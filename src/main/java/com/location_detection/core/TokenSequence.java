package com.location_detection.core;

import java.util.Arrays;
import java.util.TreeMap;


public final class TokenSequence {
	private final WrapperToken[] tokens;
	
	/**
	 * CONSTRUCTORS
	 */
	public TokenSequence(WrapperToken[] tokens){
		this.tokens = tokens;
	}
	
	/**
	 * Get Page's token
	 * @return List with page's tokens
	 */
	public WrapperToken[] getTokens(){
		return  tokens; 
	}
	
	/**
	 * Get first token
	 * @return the first token of the list
	 */
	public WrapperToken getFirstElement(){
		return  tokens[0]; 
	}
	
	/**
	 * Get the page's number of tokens that contain
	 * @return Page Size
	 */
	public int getSize(){
		return tokens.length;
	}
	
	
	/**
	 * Check if Contains List
	 */
	public boolean containsTokens(WrapperToken[] sequence){	
		WrapperToken beginTokenInSequence = sequence[0];
		int sequenceLength = sequence.length;
		int tokensLength = tokens.length;
		
		for(int i = 0;i<tokensLength;i++){
			if(tokens[i].equals(beginTokenInSequence)){
				
				WrapperToken[] tempTokenArray = new WrapperToken[sequenceLength];
				int index = 0;
				
				for(int y = i;y < i+sequenceLength;y++){
					if(y<tokensLength)
						tempTokenArray[index++] = tokens[y];
					else
						break;
				}
				
				if(Arrays.equals(tempTokenArray, sequence))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Count number of occurences of the given token sequence in Page
	 * @param WrapperToken sequence
	 */
	public int countSequenceInPage(WrapperToken[] sequence){
		int countHits = 0;
		int sequenceLength = sequence.length;
		WrapperToken beginTokenInSequence = sequence[0];
		int tokensLength = tokens.length;
		for(int i = 0;i<tokens.length;i++){
			if(tokens[i].equals(beginTokenInSequence)){
			
				WrapperToken[] tempTokenArray = new WrapperToken[sequenceLength];
				int index = 0;
				for(int y = i;y < i + sequenceLength;y++){
					if(y<tokensLength)
						tempTokenArray[index++] = tokens[y];
					else
						break;
				}
				if(Arrays.equals(tempTokenArray, sequence))
					countHits++;
			}
		}
		return countHits;
	}	
	
	/**
	 * Return the start/end indexes of the templates in the page
	 * @param templateTokens
	 * @return
	 */
	public void IndexesOfTokenSequence(WrapperToken[] templateTokens,TreeMap<Integer,Integer> allTemplatePos){
		int sequenceLength = templateTokens.length;
		WrapperToken beginTokenInSequence = templateTokens[0];
		int tokensLength = this.tokens.length;
		
		for(int i = 0;i<this.tokens.length;i++){
			if(this.tokens[i].equals(beginTokenInSequence)){
				int startIndex = i;
				WrapperToken[] tempTokenArray = new WrapperToken[sequenceLength];
				int index = 0;
				for(int y = i;y < i + sequenceLength;y++){
					if(y<tokensLength)
						tempTokenArray[index++] = this.tokens[y];
					else
						break;
				}
				if(Arrays.equals(tempTokenArray, templateTokens))
					allTemplatePos.put(startIndex, startIndex + sequenceLength);
			}
		}		
	}
	

	/**
	 * @return true if the sequence got only HTML tags
	 */
	public boolean isHTMLtag(){
		boolean isHtmlSeq = false;
		for(WrapperToken token : this.tokens)
			if(token.getType().equals("HTML"))
				isHtmlSeq = true;		
		return isHtmlSeq;
	}
	
	
	
	/**
	 * Override toString method
	 */
	public String toStringAndCode(){
		StringBuilder builder = new StringBuilder();
		for(WrapperToken token : this.tokens){
			builder.append(token.getToken()+" "+token.getTokenCode()+" ");
		}
		return builder.toString();
	}
	public String toStringAllInfo(){
		StringBuilder builder = new StringBuilder();
		for(WrapperToken token : this.tokens){
			builder.append(token.toString());
		}
		return builder.toString();
	}
	
	public String toStringOnlyAlphanumeric(){
		StringBuilder builder = new StringBuilder();
		for(WrapperToken token : this.tokens){
			if(token.getType().equals("PUNCT") || token.getType().equals("HTML"))
				builder.append(token.getToken()+" ");
		}
		return builder.toString();
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for(WrapperToken token : this.tokens){
			builder.append(token.getToken()+" ");
		}
		return builder.toString();
	}	
}
