package com.location_detection.domain;

import java.util.List;

import com.location_detection.core.TokenSequence;
import com.location_detection.core.WrapperToken;

/**
 * THIS IS USED ONLY FOR THE RUNWRAPPER.JAVA TEST CLASS!!!
 * @author mimis
 *
 */

public class DataRichPage {

	//The tokens of the page
	private TokenSequence tokens;
	
	//Slots - page region that doesn't exist in page template
	private List<TokenSequence> slotsTokenSequenceList;
	
	//Page Id
	private int pageId;
	
	//JOB POST INFO
	private String job_post_id;
	private String navigation_id;
	private String record_text;
	private String title;
	
	/*
	 * Extracts are a list with tokenSequences which appear between separators in the Page's Data Region
	 * 
	 * !!!!!!!!!!This is not used during page Template!!!!!!!!!
	 */
	private Extract[] extracts;
	
	/**
	 * Constructors
	 */
	public DataRichPage(TokenSequence tokens,int pageId){
		this.tokens = tokens;
		this.pageId = pageId;
		
		this.slotsTokenSequenceList = null;
		this.job_post_id = null;
		this.navigation_id = null;
		this.record_text = null;
		this.extracts = null;
	}
	
	/**
	 * Set extracts
	 */
	public void setExtracts(Extract[] extracts){			
		this.extracts = extracts;
	}
	
	/**
	 * Set slots
	 */
	public void setSlots(List<TokenSequence> slotsTokenSequenceList){			
		this.slotsTokenSequenceList = slotsTokenSequenceList;
	}
	
	
	/**
	 * Get functions
	 * @return
	 */
	public TokenSequence getPageTokenSequence(){
		return this.tokens;
	}
	public WrapperToken[] getPageTokensArray(){
		return this.tokens.getTokens();
	}
	public int getPageId(){
		return this.pageId;
	}
	public Extract[] getExtracts(){
		return this.extracts;
	}
	public List<TokenSequence> getSlots(){
		return this.slotsTokenSequenceList;
	}
	
	
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the job_post_id
	 */
	public String getJob_post_id() {
		return job_post_id;
	}

	/**
	 * @param job_post_id the job_post_id to set
	 */
	public void setJob_post_id(String job_post_id) {
		this.job_post_id = job_post_id;
	}

	/**
	 * @return the navigation_id
	 */
	public String getNavigation_id() {
		return navigation_id;
	}

	/**
	 * @param navigation_id the navigation_id to set
	 */
	public void setNavigation_id(String navigation_id) {
		this.navigation_id = navigation_id;
	}

	/**
	 * @return the record_text
	 */
	public String getRecord_text() {
		return record_text;
	}

	/**
	 * @param record_text the record_text to set
	 */
	public void setRecord_text(String record_text) {
		this.record_text = record_text;
	}


	/**
	 * Returns number of tokens that page got
	 */
	public int getPageSize(){
		return tokens.getSize();
	}
	
	/**
	 * @return true if we manage to get dynamic content, otherwise false
	 */
	public boolean gotSlots(){
		if(this.slotsTokenSequenceList == null)
			return false;
		else if(this.slotsTokenSequenceList.size() > 0)
			return true;
		return false;
	}
	
	
	/**
	 * Get the Slot with the biggest size (number of tokens)
	 * @return TokenSequence - with the biggest size
	 */
	public TokenSequence getBiggestSlot(){
		TokenSequence biggerTokenSeq = null;
		int maxSequenceSize = 0;
		for(TokenSequence tokenSeq : this.slotsTokenSequenceList){
			if(maxSequenceSize < tokenSeq.getSize()){
				maxSequenceSize = tokenSeq.getSize();
				biggerTokenSeq = tokenSeq;
			}
		}
		return biggerTokenSeq;
	}
}
