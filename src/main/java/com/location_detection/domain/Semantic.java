package com.location_detection.domain;

import java.util.List;


public class Semantic {
	
	private final String semanticString;//the semantic itself
	private final String source; //record,detail
	private final String method; //db(geo database), keyword(location keywords)
	private final boolean isLocation; //is true if this location exist in the db
	private final int tokenType;
	private final int score;
	//this is the list of locations that the current semantic String got; is used only for location semantics
	private List<Location> locationList;
	private final boolean gotPrefixLocationKeyword;
	
	public Semantic(String semanticString,String source,String method, boolean isLocation,int score){
		this.semanticString = semanticString.trim();
		this.source = source;
		this.method = method;
		this.isLocation = isLocation;
		this.score = score;
		this.locationList = null;
		this.tokenType = -1;
		this.gotPrefixLocationKeyword = false;
	}

	public Semantic(String semanticString,String source,String method, boolean isLocation,int score,List<Location> locationList){
		this.semanticString = semanticString.trim();
		this.source = source;
		this.method = method;
		this.isLocation = isLocation;
		this.score = score;
		this.locationList = locationList;
		this.tokenType = -1;
		this.gotPrefixLocationKeyword = false;
	}
	
	public Semantic(String semanticString,String source,String method, boolean isLocation,int score,List<Location> locationList,int tokenType,boolean gotPrefixLocationKeyword){
		this.semanticString = semanticString.trim();
		this.source = source;
		this.method = method;
		this.isLocation = isLocation;
		this.score = score;
		this.locationList = locationList;
		this.tokenType = tokenType;
		this.gotPrefixLocationKeyword = gotPrefixLocationKeyword;
	}
	
	
	/**
	 * @return the gotPrefixLocationKeyword
	 */
	public boolean isGotPrefixLocationKeyword() {
		return gotPrefixLocationKeyword;
	}

	/**
	 * @return the tokenType
	 */
	public int getTokenType() {
		return tokenType;
	}

	/**
	 * @return the locationList
	 */
	public List<Location> getLocationList() {
		return locationList;
	}

	/**
	 * @param locationList the locationList to set
	 */
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @return the location
	 */
	public String getSemanticString() {
		return semanticString;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @return the isLocation
	 */
	public boolean isLocation() {
		return isLocation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Semantic [semanticString=" + semanticString + ", source="
				+ source + ", locationList=" + locationList.toString() + "]";
	}


}
