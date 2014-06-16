package com.location_detection.domain;

import com.location_detection.utils.EqualsUtil;
import com.location_detection.utils.HashCodeUtil;

/**
 * With No too much details Token
 * @author mimis
 *
 */
public class Token {
	private  String token;
	private  int tokenCode;
	private int tokenFrequency = 0;
	private int tokenDocFrequency = 0;
	private double multinomialLikelihood;
	private double binomialLikelihood;
	
	private int fHashCode;

	/**
	 * Constructors
	 */
	public Token(String token,int tokenCode){
		this.token = token;
		this.tokenCode = tokenCode; 
	}
	//Import token
	public Token(String token,int tokenCode,int tokenFrequency,int tokenDocFrequency,double multinomialLikelihood,double binomialLikelihood){
		this.token = token;
		this.tokenCode = tokenCode;
		this.tokenFrequency = tokenFrequency;
		this.tokenDocFrequency = tokenDocFrequency;
		this.multinomialLikelihood = multinomialLikelihood;
		this.binomialLikelihood = binomialLikelihood;
	}
	
	
	
	/**
	 * Get and Set functions
	 */
	
	public int getTokenCode(){
		return this.tokenCode;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getTermFrequency(){
		return this.tokenFrequency;
	}
	public int getDocFrequency(){
		return this.tokenDocFrequency;
	}
	public double getMultinomialLikelihood(){
		return this.multinomialLikelihood;
	}
	public double getBinomialLikelihood(){
		return this.binomialLikelihood;
	}
	
	
	/**
	 * Increase token's frequency by one
	 */
	public void increaseFrequency(){
		this.tokenFrequency++;
	}
	public void increaseDocFrequency(){
		this.tokenDocFrequency++;
	}
	
	/**
	 * Assign Multinomial likelihood to token
	 * P(t|Feature_level) = t_frequency / total Term Frequency
	 * @param likelihood
	 */
	public void assignMultinomialLikelihood(double multinomialLikelihood){
		this.multinomialLikelihood = multinomialLikelihood;
	}
	
	/**
	 * Assign Binomial likelihood to token
	 * P(t|Feature_level) = t_Doc_frequency / total_number_of_docs
	 * @param likelihood
	 */
	public void assignBinomialLikelihood(double binomialLikelihood){
		this.binomialLikelihood = binomialLikelihood;
	}
	
	/**
	 * TODO I THINK WE USE THIS FUNCTION FOR VERY CRUCIAL PART ..WE SHOULD CHANGE ITS NAME,..
	 * Override toString
	 */
	@Override
	public String toString(){
		String toString = tokenCode+"\t"+tokenFrequency + "\t"+tokenDocFrequency +"\t"+multinomialLikelihood+"\t"+binomialLikelihood;
		return toString;
	}
	
	/**
	 * We should use as EQUALS fields BOTH the "TokenCode" and "TokenID"
	 */
	@Override
	 public boolean equals(Object other) {
		if (this == other)
			return true;    
		if (!(other instanceof Token))
			return false;
		Token otherToken = (Token) other;
		return   EqualsUtil.areEqual(tokenCode,otherToken.tokenCode);
	}
	
	@Override 
	public int hashCode() {
		//this style of lazy initialization is 
	    //suitable only if the object is immutable
	    if ( fHashCode == 0) {
	      int result = HashCodeUtil.SEED;
	      result = HashCodeUtil.hash( result, tokenCode );
	      fHashCode = result;
	    }
	    return fHashCode;
	}
}
