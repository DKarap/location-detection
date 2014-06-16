package com.location_detection.domain;

import java.util.Map;

/**
 * Map Class for the ROW DataBase Select Result 
 * Contains a map with the column label and value its data in the database
 * @author mimis
 *
 */
public final class RowDataBaseResultMap {
	
	private final Map<String,String> rowResultsMap;
	
	public RowDataBaseResultMap(Map<String,String> rowResultsMap){
		this.rowResultsMap = rowResultsMap;
	}
	
	/**
	 * Get the Map of the DB results
	 * @return
	 */
	public Map<String,String> getRowMap(){
		return this.rowResultsMap;
	}
	
	
	/**
	 * Get Map value of the given key label name
	 * @return String - the map value
	 */
	public String getLabelValue(String labelName){
		if(this.rowResultsMap.containsKey(labelName))
			return this.rowResultsMap.get(labelName);
		else
			return null;
	}
	
	/**
	 * Add to rowResultsMap the given rowResultsMap
	 * @param rowResultsMap
	 */
	public void addRowDataBaseResultMap(Map<String,String> rowResultsMap){
		this.rowResultsMap.putAll(rowResultsMap);
	}
	
	/**
	 * 
	 * @param value
	 * @return true if the map contains the given value,otherwise false
	 */
	public boolean containsValue(String value){
		if(this.rowResultsMap.containsValue(value))
			return true;
		else
			return false;
	}
	
	/**
	 * 
	 * @param key
	 * @return true if the map contains the given key,otherwise false
	 */
	public boolean containsKey(String key){
		if(this.rowResultsMap.containsKey(key))
			return true;
		else
			return false;
	}
	
	
	/**
	 * Add the given values into the map
	 * @param key
	 * @param value
	 */
	public void put(String key,String value){
		this.rowResultsMap.put(key, value);
	}

}
