package com.location_detection.lexicon;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.machine_learning.utils.Helper;

public class LexiconHash implements LexiconInterface{
	private BiMap<String, Integer> lexicon = HashBiMap.create();
	private String lexiconFile = "INPUT/lexicon.txt";
	private int initialLexiconSize; // We use it in order to update the lexicon
	
	
	/**
	 * Constructor
	 * 	If Lexicon exist then import to the Map and also keep the size of the existed lexicon on order to update only the new tokens
	 */
	public LexiconHash() {
		//If the lexicon file exist then import lexicon from the file
		if(existLexicon())
			getLexiconFromFile();
		else
			this.initialLexiconSize = 0;
	}
	
	/**
	 * Constructor
	 * @param lexiconFile
	 */
	public LexiconHash(String lexiconFile) {
		this.lexiconFile = lexiconFile;
		//If the lexicon file exist then import lexicon from the file
		if(existLexicon())
			getLexiconFromFile();
		else
			this.initialLexiconSize = 0;
	}
	
	/**
	 * 
	 * @return lexicon filename
	 */
	public String getLexiconFileName(){
		return this.lexiconFile;
	}
	/**
	 * Get Token's String
	 * @param int Token code
	 * @return String representation of the token code
	 */
	public String getToken(int code){
		return lexicon.inverse().get(code);
	}
	
	/**
	 * Given the Token String return its code
	 * @param String token
	 * @return int - token's code
	 */
	public int getTokenCode(String token){
		synchronized (lexicon) {
			
			if(lexicon.containsKey(token))
				return lexicon.get(token);
			else{
				lexicon.put(token, lexicon.size());
				return lexicon.size()-1;
			}
		}
	}
	
	/**
	 * Checks if the lexicon file exists in order to create a new one
	 * @return
	 */
	public boolean existLexicon(){
		File lexFile = new File(lexiconFile);
		return lexFile.exists();
	}
	
	/**
	 * Get and Import the existed Lexicon from the lexicon file
	 */
	public void getLexiconFromFile(){
		try{
    		// Open the file that is the first 
		    // command line parameter
		    FileInputStream fstream = new FileInputStream(lexiconFile);
		    
		    // Get the object of DataInputStream
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    int code=0;
		    while ((strLine = br.readLine()) != null)	{
		    	lexicon.put(strLine.trim(),code++);
		    }
		    
		    //Close the input stream
		    in.close();
		    //Initial Lexicon Size
		    this.initialLexiconSize = lexicon.size();
    	}catch (Exception e){//Catch exception if any
		      System.err.println("Error getLexiconFromFile: " + e.getMessage());
		}
	}
	
	/**
	 * Write Lexicon to the given File
	 * @param lexiconFile
	 * @throws IOException
	 */
	public void writeLexiconToFile() throws IOException{
		for(int tokenCode = initialLexiconSize; tokenCode < this.lexicon.size(); tokenCode++)
	    	Helper.writeToFile(lexiconFile,lexicon.inverse().get(tokenCode)+"\n", true);
	}
	
	
}
