package com.location_detection.domain;

import java.util.List;

public class Location {
	
	private final String text;
	private final List<String> countryList;
	
	
	/**
	 * @param text
	 * @param countryList
	 */
	public Location(String text, List<String> countryList) {
		super();
		this.text = text;
		this.countryList = countryList;
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @return the countryList
	 */
	public List<String> getCountryList() {
		return countryList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Location [text=" + text + ", countryList=" + countryList.toString() + "]";
	}
}
