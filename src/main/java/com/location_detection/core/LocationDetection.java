package com.location_detection.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.cybozu.labs.langdetect.LangDetectException;
import com.google.common.collect.ImmutableList;
import com.location_detection.domain.DataRichPage;
import com.location_detection.domain.Location;
import com.location_detection.domain.RowDataBaseResultMap;
import com.location_detection.domain.Semantic;
import com.location_detection.utils.MySqlHelper;
import com.machine_learning.utils.Helper;



public class LocationDetection implements Callable<String> {
	private final static List<String>  ieLocationExtractionKeywords = ImmutableList.of("filiaal","werkgebied","contact","adress","adres",
			"provincie","land","lokatie","location","locations","city","country","place","locatie","plaats","standplaats","regio",
			"vestiging","vestigingsplaats","niederlassung","based","located","area","state","province","town","info","information","werklocatie");	
	private final static int ieNumberOfAlphaNumericTokensToCheckForLocationKeyword = 5;
	private final static int ieMinimumLengtThatALocationCanHave = 3;
	private final static int ieBoostingValueForLocationInRecordOrTitleText = 8;
	//boost locations with prefix a location keyword
	private final static int ieBoostingLocationWithAprefixLocationKeyword = 4;
	//boost semantic with all caps or a starting Capital
	private final static int ieBoostingValueForLocationWithAllCapitalOrStartingCapitalChar = 2;

    
	private final Tokenizer tokenizer;
	private final String databaseName;
	private final String databaseTableName;
	private final String usr;
	private final String psw;
	private final List<RowDataBaseResultMap> job_list;
	private final Connection connectionToUpdate;
	private final String fatal_errors_out;
	
	/**
	 * Constructor
	 * @throws LangDetectException 
	 */
	public LocationDetection(Connection connectionToUpdate,List<RowDataBaseResultMap> job_list,Tokenizer tokenizer, String databaseName,String databaseTableName,String usr,String psw,String fatal_errors_out) throws LangDetectException{
		this.connectionToUpdate = connectionToUpdate;
		this.tokenizer = tokenizer;
		this.job_list = job_list;
		this.databaseName = databaseName;
		this.databaseTableName = databaseTableName;
		this.usr=usr;
		this.psw=psw;
		this.fatal_errors_out = fatal_errors_out;
	}
	

	@Override
	public String call() throws NumberFormatException, Exception  {
		//get the mySql connection from the GEO DB
		Connection geoDBconn = MySqlHelper.getMySqlConnection(databaseName,usr,psw);
		
		try {
			assignLocationToJobs(geoDBconn);
		} catch (Exception e) {
			try {
				e.printStackTrace();
				Helper.writeToFile(fatal_errors_out, e.getMessage()+"\n", true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		for(RowDataBaseResultMap jobrecordMap : job_list){
			System.out.println("Title:"+jobrecordMap.getLabelValue("title")+"\tLocation:"+jobrecordMap.getLabelValue("location"));
			update_job_location(connectionToUpdate, Integer.parseInt(jobrecordMap.getLabelValue("id")) , jobrecordMap.getLabelValue("location"));
		}
	    

		
		//Close Mysql Connection to GEoDB
		MySqlHelper.closeMySqlConnection(geoDBconn);
		return null;
	}
	
	public static void update_job_location(Connection connection, int id, String location) throws Exception{
		String sql = "UPDATE semantic "
				   + "SET location = ? "
			       + " WHERE id = " + id;
		
		PreparedStatement pst = null;		
		try{
	        pst = connection.prepareStatement(sql);	        
	        pst.setString(1, location);	
	        pst.executeUpdate();	        
		}catch(Exception e){
			throw e;
		}finally{
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	

	
	/**
	 * Detect and Assign to the given jobs into the DB the Semantic field(UNKNOWN if it is not possible to be detected)
	 * 
	 * @param job_records
	 * @param geoDBconn
	 * @param tokenizer
	 * @throws Exception 
	 */
	private  void assignLocationToJobs(Connection geoDBconn) throws Exception{
		
		for(RowDataBaseResultMap job_record : job_list){

			
			DataRichPage currentDataRichPage = getDataRichPage(job_record, 1, tokenizer);
			
			/*
			 * 1.GET LOCATION CANDIDATES from Title,RecordText,DetailPageText
			 */
			List<Semantic> locationCandidateList = getCandidateLocationObjects(geoDBconn, currentDataRichPage, this.tokenizer);


			/*
			 * 2.FIND MOST PROBABLE LOCATION: SAVE THE best Location if we manage to find one,otherwise the "UNKNOWN" string
			 */
			if(locationCandidateList !=null && !locationCandidateList.isEmpty()){
				String country = getMostProbableCountryFromSemanticList(locationCandidateList);					
				job_record.put("location", country);						
			}else
			    job_record.put("location", "UNKNOWN");

		}			
	}
	
	/**
	 * 1.GET LOCATION CANDIDATES  from Title,RecordText,DetailText
	 * @param geoDBconn
	 * @param dataRichPage
	 * @return list of candidate Semantic objects from the predefined sources(recordText,detailPageText), otherwise an empty list
	 * @throws Exception 
	 */
	private  List<Semantic> getCandidateLocationObjects(Connection geoDBconn,DataRichPage dataRichPage,Tokenizer tokenizer) throws Exception{
		
		List<Semantic> locationCandidateList = new ArrayList<Semantic>();
		
		/*
		 * 1.Get Location from Title
		 */
		if(dataRichPage.getTitle() != null && !dataRichPage.getTitle().isEmpty()){
			TokenSequence tokenSequence = this.tokenizer.getHtmlPageTokenSequence(dataRichPage.getTitle(), true,true);
			List<Semantic> semanticsList = getCandidateLocationsFromText(geoDBconn, tokenSequence.getTokens(),"title","db");
			if(semanticsList != null)
				locationCandidateList.addAll(semanticsList);
		}
		
		
		
		/*
		 * 2.GET Semantic FROM RECORD TEXT
		 * 	1)search each record for Semantic entities via GEO DB
		 *  2)search for Semantic keywords in the records and then extract the next or the same record after the Semantic keyword
		 */
		if(dataRichPage.getRecord_text() != null && !dataRichPage.getRecord_text().isEmpty()){		
			TokenSequence tokenSequence = this.tokenizer.getHtmlPageTokenSequence(dataRichPage.getRecord_text(), true,true);
			List<Semantic> semanticsList = getCandidateLocationsFromText(geoDBconn, tokenSequence.getTokens(),"record","db");
			if(semanticsList != null)
				locationCandidateList.addAll(semanticsList);
		}
	
		
		// 3.GET Semantic FROM DETAIL PAGE 
		if(dataRichPage.getPageTokenSequence() != null){
			//remove text that exist between invalid Html Tags(a,optioon,button)
			TokenSequence cleanTokenSequence =  Tokenizer.cleanUpInvalidTagsText(dataRichPage.getPageTokenSequence());
			List<Semantic> semanticsList = getCandidateLocationsFromText(geoDBconn, cleanTokenSequence.getTokens(),"detail","db");
			if(semanticsList != null)
				locationCandidateList.addAll(semanticsList);

		}		
		return  locationCandidateList;
	}
	
	
	
	

	
	
	/**
	 * 2.GET BEST LOCATION CANDIDATE
	 * @param locationCandidateList list of semantic; each semantic can have multiple locations strings
	 * @return the country which occurs more than the others between the semantics
	 */
	public static String getMostProbableCountryFromSemanticList(List<Semantic> locationCandidateList){
		String country = null;
		Map<String, Integer> countryCount = new HashMap<String, Integer>();		

		for(Semantic sem:locationCandidateList){
			List<Location> locList = sem.getLocationList();
			String sem_string = sem.getSemanticString();
			int sem_type = sem.getTokenType();
			String sem_source = sem.getSource();//record,title,detail_page
			boolean sem_prefixIsLocationKeyword = sem.isGotPrefixLocationKeyword();	  

			//Boosting specific features ,except the semantics with COuntry Codes(length>2)
			int boost = 0; 
			if(sem_string.length() > 2){
				//boost semantics from 'record' or 'title' 
				if (sem_source.equals("record") || sem_source.equals("title"))
					boost += ieBoostingValueForLocationInRecordOrTitleText;
				
				//boost locations with prefix a location keyword
				if(sem_prefixIsLocationKeyword)
					boost +=  ieBoostingLocationWithAprefixLocationKeyword;

				//boost semantic with all caps or a starting Capital
				if(sem_type == 6 || sem_type == 4)
					boost +=  ieBoostingValueForLocationWithAllCapitalOrStartingCapitalChar;				
			}
			//Boosting Country Codes with a location keyword prefixe
			if(sem_string.length() == 2){
				if(sem_prefixIsLocationKeyword)
					boost +=  ieBoostingLocationWithAprefixLocationKeyword;				
			}
			
			//TODO check for co occurences of candidates;especially locations names with country codes which is very high candidate
			
			//save occurences..
			if(locList != null){
				for(Location loc:locList){
					for(String c:loc.getCountryList()){
						Integer count = countryCount.get(c);          
						countryCount.put(c, (count==null) ? 1+boost : count+1+boost);
					}
				}
			}
		}
		
		//Normalize counts from the same country
		Map<String, Integer> countryCount2 = normalizeCountsFromSameCountry(countryCount);
		
		//sort by count
		Map<String, Integer> countryCount3 = Helper.sortByValue(countryCount2);

		/**
		 *TODO Get the best one...What do we do with the duplications????
		 */
		for(Map.Entry<String, Integer> entry:countryCount3.entrySet()){
			country = entry.getKey();
			break;
		}
		return country;
	}
	
	
	
	
	/**
	 * We check each token for Location:
	 * 	1)if is a country code => US,DE,GR...
	 * 	2)is a country name - UNIGRAM+BIGRAM
	 * 	3)is a city name - UNIGRAM+BIGRAM
	 * 	
	 * 
	 * Save features:
	 * 	1)TokenType(html,punct,all caps,number)
	 *  2)Source of text
	 *  3)if there is a Location keyword before
	 * @param con
	 * @param WrapperTokenList
	 * @return list of detected semantics; a semantic is the text that is a Location in this case and 
	 * @throws Exception 
	 */
	public  List<Semantic> getCandidateLocationsFromText(Connection con, WrapperToken[] WrapperTokenList,String sourceOfText, String method) throws Exception{
		if(WrapperTokenList == null || WrapperTokenList.length==0)
			return null;
		//list to save the different semantics
		List<Semantic> semanticList = new ArrayList<Semantic>();
		List<Location> locationList = null;
		
		for(int i=0;i<WrapperTokenList.length;i++){
			WrapperToken current_token = WrapperTokenList[i];
			String current_tokenString = current_token.getToken();
			int currentTokenTypeCode = current_token.getTypeCode();
			int currentTokenlength = current_tokenString.length();

			//check the current code if is not an HTML(0) or PUNCT(1) or 
			if(currentTokenTypeCode != 0 && currentTokenTypeCode != 1 && currentTokenlength > 1){
				locationList = new ArrayList<Location>();		

				//*US,GR,GE => check for code country names: if token got all capital(ALPHABETIC_ALLCAPS==6) and length two words  
				if(currentTokenTypeCode==6 && currentTokenlength == 2){
					 //* QUERY the geo db with the given text and on the given field and save results to given locationList
					getCountriesFromDBFromGivenText(con, current_tokenString, locationList, "country_code");
				}
				//check for country name or cities if the length is valid
				else if(currentTokenlength >= ieMinimumLengtThatALocationCanHave){
					getCountriesFromDBFromGivenText(con, current_tokenString, locationList, "NAME");
					getCountriesFromDBFromGivenText(con, current_tokenString, locationList, "COUNTRY");
					//check bi-grams if exist next token and is not an html or punctuantion and has good length
					if(i+1 < WrapperTokenList.length && WrapperTokenList[i+1].getTypeCode() != 0 && WrapperTokenList[i+1].getTypeCode() != 1 && WrapperTokenList[i+1].getToken().length() >= ieMinimumLengtThatALocationCanHave){
						List<Location> bi_gram_locationList = new ArrayList<Location>();		
						String bi_gram = current_tokenString + " " + WrapperTokenList[i+1].getToken();
						getCountriesFromDBFromGivenText(con, bi_gram, bi_gram_locationList, "NAME");
						getCountriesFromDBFromGivenText(con, bi_gram, bi_gram_locationList, "COUNTRY");	
						if(!bi_gram_locationList.isEmpty()){
							//check if there is a Location Keyword before the semantic
							boolean gotPrefixLocationKeyword = gotLocationKeywordPrefix(i-1, WrapperTokenList);
							semanticList.add(new Semantic(bi_gram,sourceOfText,method,true, -1, bi_gram_locationList,currentTokenTypeCode,gotPrefixLocationKeyword));	
						}
					}
				}
				//save possible location if we manage to found
				if(!locationList.isEmpty()){
					//check if there is a Location Keyword before the semantic
					boolean gotPrefixLocationKeyword = gotLocationKeywordPrefix(i-1, WrapperTokenList);
					semanticList.add(new Semantic(current_tokenString,sourceOfText,method,true,-1,locationList,currentTokenTypeCode,gotPrefixLocationKeyword));	
				}
			}			
		}
		return semanticList;
	}
    	
	
	
		

	
	/**
	 * QUERY the geo db with the given text and on the given field and save results to given locationList
	 * @param con
	 * @param current_text
	 * @param locationList
	 * @param fieldToCheck (country_code,NAME,COUNTRY)
	 * @return true if the given text is a location,false otherwise
	 */
	private  boolean getCountriesFromDBFromGivenText(Connection con, String current_text,List<Location> locationList,String fieldToCheck)throws Exception{
		boolean isLocation = false;
		List<RowDataBaseResultMap> rowDBresultMap = null;
		if(fieldToCheck.equals("NAME")){
			String query = "SELECT distinct(COUNTRY),name FROM " + databaseTableName + "  where " + fieldToCheck + "  LIKE '"+current_text+"'";
			rowDBresultMap = MySqlHelper.executeQuerytoDB(con, query, Arrays.asList("country","name"));
		}
		else{
			String query = "SELECT distinct(COUNTRY) FROM " + databaseTableName + "  where " + fieldToCheck + "  LIKE '"+current_text+"'  OR COUNTRY2  LIKE '"+current_text+"'";
			rowDBresultMap = MySqlHelper.executeQuerytoDB(con, query, Arrays.asList("country"));
		}
		
		List<String> countriesList = new ArrayList<String>();
		if(rowDBresultMap !=null && !rowDBresultMap.isEmpty()){
			for(RowDataBaseResultMap row: rowDBresultMap){
				String country_city = row.getLabelValue("country");
				country_city +=  row.getLabelValue("name") != null ? "-" + row.getLabelValue("name") : "";
				countriesList.add(country_city);
			}
			locationList.add(new Location(current_text,countriesList));
			isLocation = true;
		}
		return isLocation;
	}
	
	
	/**
	 * Normalize candidates: Sum up thes counts of candidates with a location with the counts of candiadtes with the same Country
			Germany			=>10
			Germany	essen	=>3
			
		  After Normalization:
			Germany	essen	=>13
			Germany			=>10
	 * @param countryCount
	 */
	private static Map<String, Integer> normalizeCountsFromSameCountry(Map<String, Integer> countryCount){
		Map<String, Integer> onlyCountriesCounts = new HashMap<String, Integer>();		
		//get locations with only COuntries counts
		for(Map.Entry<String, Integer> entry:countryCount.entrySet()){						
			if(entry.getKey().indexOf("-") == -1){
				onlyCountriesCounts.put(entry.getKey(), entry.getValue());
			}
		}
		
		//sum up counts with the same country
		for(Map.Entry<String, Integer> entry:countryCount.entrySet()){			
			String location = entry.getKey();
			if(location.indexOf("-") != -1){
				String locArray[] = location.split("-");
				Integer ONLYCOUNTRYCount = onlyCountriesCounts.get(locArray[0]);
				if(ONLYCOUNTRYCount != null){
					countryCount.put(location, entry.getValue() + ONLYCOUNTRYCount);
				}
			}
		}
		return countryCount;
	}

	
	/**
	 * Search Backwards(for a given ammount of alphanumeric tokens) for Location keywords..
	 * @param lastIndex
	 * @param WrapperTokenList
	 * @return true if there is a Location keyword,otherwise false
	 */
	private static boolean gotLocationKeywordPrefix(int lastIndex, WrapperToken[] WrapperTokenList){		
		int countChecks = 0;
		for(int i=lastIndex;i>=0;i--){
			String token = WrapperTokenList[i].getToken();
			int tokenTypeCode = WrapperTokenList[i].getTypeCode();
			//iff its an alphanumeric
			if(tokenTypeCode != 0 && tokenTypeCode != 1 && token.length() > 1){
				if(ieLocationExtractionKeywords.contains(token)){
					return true;
				}
				//if we exceed the maximum umber of checking break
				if(countChecks++ >= ieNumberOfAlphaNumericTokensToCheckForLocationKeyword)
					break;
			}
		}
		return false;
	}

	

	public static DataRichPage getDataRichPage(RowDataBaseResultMap record,int pageId, Tokenizer tokenizer){
		String id = record.getLabelValue("id");
		String nav_id = record.getLabelValue("wrapper_id");
		String recordText = record.getLabelValue("recordText");
		String title = record.getLabelValue("title");

		
		//we need to translate the tokens into their IDS; so the second parameter must be true
		TokenSequence tokenSequence =  tokenizer.getHtmlPageTokenSequence(record.getLabelValue("source_code"),true,true);
		//add page to the list
		DataRichPage detailPage = new DataRichPage(tokenSequence,pageId);
		detailPage.setJob_post_id(id);
		detailPage.setNavigation_id(nav_id);
		detailPage.setRecord_text(recordText);
		detailPage.setTitle(title);
		
		return detailPage;
	}

}
