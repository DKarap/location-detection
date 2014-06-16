package com.location_detection.domain;

import com.location_detection.core.TokenSequence;
import com.location_detection.core.WrapperToken;


public class Extract {
	
	private TokenSequence extracts;
	//Number of detail pages that this extract exist
	private  int detailCount;
	//Number of list pages that this extract exist
	private  int listCount;
	//START TOKEN SEPARATOR OF EXTRACT
	private WrapperToken startSeparator;
	//END TOKEN SEPARATOR OF EXTRACT
	private WrapperToken endSeparator;
	
	/**
	 * Constructor
	 * @param extracts
	 */
	public Extract(TokenSequence extracts,WrapperToken startSeparator,WrapperToken endSeparator){
		this.extracts = extracts;
		this.startSeparator = startSeparator;
		this.endSeparator = endSeparator;
		this.detailCount = 0;
		this.listCount = 0;
	}
	
	/**
	 * Get Fucntion
	 */
	public TokenSequence getExtracts(){
		return extracts;
	}
	public WrapperToken getStartSeparator(){
		return startSeparator;
	}
	public WrapperToken getEndSeparator(){
		return endSeparator;
	}
	
	
	
	/**
	 * Number of appearance of the extract in detail pages
	 * @return
	 */
	public int getDetailCount(){
		return detailCount;
	}
	public int getListCount(){
		return listCount;
	}
	
	/**
	 * Increase number of detailCounts
	 */
	public void increaseDetailCount(){
		detailCount++;
	}
	/**
	 * Increase number of listCounts
	 */
	public void increaseListCount(){
		listCount++;
	}
	
	
	public boolean isEqual(Extract otherExtract){
		TokenSequence otherTokenSeq = otherExtract.getExtracts();
		WrapperToken[] otherTokenArray = otherTokenSeq.getTokens();
		
		if(otherTokenSeq.getSize() == extracts.getSize()){
			WrapperToken[] tokens = extracts.getTokens();
			for(int i=0; i < tokens.length; i++)
				if(!tokens[i].equals(otherTokenArray[i]))
					return false;		
			return true;
		}
		else
			return false;
	}
	/**
	 * Get Text of extract
	 */
	public String getText(){
		return extracts.toString();
	}
	
	/**
	 * Overide toString
	 */
	@Override
	public String toString(){
		StringBuilder buf = new StringBuilder();
		buf.append( extracts.toString());
		//buf.append( extracts.toString()+"\tlistCount:" + listCount+ "\tDetailCount:" + detailCount);
		buf.append("\n");
		return buf.toString();
	}
	public String toStringAll(){
		StringBuilder buf = new StringBuilder();
		buf.append( extracts.toString() +startSeparator +"\t" + endSeparator +"\tlistCount:" + listCount+ "\tDetailCount:" + detailCount);
		buf.append("\n");
		return buf.toString();
	}
}
