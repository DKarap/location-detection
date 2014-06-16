package com.location_detection.core;

import com.location_detection.utils.EqualsUtil;
import com.location_detection.utils.HashCodeUtil;

public final class WrapperToken {
	private  final String token;
	private  final int tokenCode;
	private  final String type;
	private  final int typeCode;
	private  final int pos;
		
	
	private int fHashCode;

	/**
	 * Constructors
	 */
	public WrapperToken(String token,int tokenCode, int pos,String type, int typeCode){
		this.token = token;
		this.tokenCode = tokenCode;
		this.type = type;
		this.typeCode = typeCode;
		this.pos = pos;
	}
	public WrapperToken(WrapperToken otherToken){
		this.token = otherToken.token;
		this.tokenCode = otherToken.tokenCode;
		this.type = otherToken.type;
		this.typeCode = otherToken.typeCode;
		this.pos = otherToken.pos;
	}
	
	
	
	
	/**
	 * Get and Set functions
	 */
	public String getToken(){
		return token;
	}
	public String getType(){
		return type;
	}
	public int getTypeCode(){
		return typeCode;
	}
	public int getTokenCode(){
		return tokenCode;
	}
	public int getTokenPos(){
		return pos;
	}
	
	/**
	 * Token To String
	 */
	@Override
	public String toString(){
		StringBuilder tokenString = new StringBuilder();
		tokenString.append(this.token);
		return tokenString.toString();
	}
	

	/**
	 * We should use as EQUALS fields BOTH the "TokenCode" and "TokenID"
	 */
	@Override
	 public boolean equals(Object other) {
		if (this == other)
			return true;    
		if (!(other instanceof WrapperToken))
			return false;
		WrapperToken otherToken = (WrapperToken) other;
		return   EqualsUtil.areEqual(this.tokenCode,otherToken.tokenCode);
	}
	
	@Override 
	public int hashCode() {
		//this style of lazy initialization is 
	    //suitable only if the object is immutable
	    if ( fHashCode == 0) {
	      int result = HashCodeUtil.SEED;
	      result = HashCodeUtil.hash( result, this.tokenCode );
	      fHashCode = result;
	    }
	    return fHashCode;
	}
}
