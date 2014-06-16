package com.location_detection.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.ImmutableList;
import com.location_detection.domain.Token;
import com.location_detection.lexicon.LexiconInterface;
//import com.machine_learning.utils.Helper;

/**
 * Just include the new constructor and new Tokenize method to the Wraper tokenizer
 * @author mimis
 *TODO use character id and not char-To support all Unicode characters, including supplementary characters, use the isLetter(int) method.
 */
public final class Tokenizer {
	
    public final static List<String>  ieInvalidHtmlTagsKeywords = ImmutableList.of("<option","<a","<button");	
	private final static List<String> tokenTypeList = ImmutableList.of("HTML","PUNCT","ALPHANUMERIC","ALPHANUMERIC_NUMBER","ALPHABETIC_CAPS","ALPHABETIC_LOWER","ALPHABETIC_ALLCAPS");

	private final LexiconInterface lexicon;
	private final boolean ignoreLowerCase = false;
	private final int minWrapperTokenLength = 1;
	private final int maxWrapperTokenLength = 30;
	private final  int N_Gramm_Size;
	private boolean convertHTMLtoASCII = true;
	private final Token start_sentence_symbolToken;
	private final Token end_sentence_symbolToken;
	
	
	
	/**
	 * @param lexicon
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Tokenizer(LexiconInterface lexicon)  {
		this.lexicon = lexicon;
		this.N_Gramm_Size=1;
		this.start_sentence_symbolToken = new Token("",lexicon.getTokenCode("S-T-A-R-T"));
		this.end_sentence_symbolToken = new Token("",lexicon.getTokenCode("E-N-D"));
	}
	
	
	
	
	/**
	 * @return the lexicon
	 */
	public LexiconInterface getLexicon() {
		return lexicon;
	}

	/**
	 * @return the start_sentence_symbolToken
	 */
	public Token getStart_sentence_symbolToken() {
		return start_sentence_symbolToken;
	}

	/**
	 * @return the end_sentence_symbolToken
	 */
	public Token getEnd_sentence_symbolToken() {
		return end_sentence_symbolToken;
	}

	/**
	 * @return the nGrammSize
	 */
	public  int getnGrammSize() {
		return N_Gramm_Size;
	}




	/**
	 * Return tokenized version of the given String= List of WrapperToken instead of the default Token Class
	 * alphanumerics, or any single punctuation character. Doesn't separate
	 * alphanumerics(i.e. "aa12" = "aa12")
	 * 
	 * keeps HTML TAGS, except of style and script!!!!
	 * @param string
	 * @return List<Token> with tokens
	 */
	public TokenSequence getHtmlPageTokenSequence(String string,boolean translateTokensToId,boolean convertToLowerCase) {
		//List to save the extracted tokens  		
		List<WrapperToken> tokens = new ArrayList<WrapperToken>();

		//If given String is empty then return the empty token sequence
		if(string == null || string.equals("") || string.matches("\\s*"))
			return new TokenSequence(tokens.toArray(new WrapperToken[]{}));
		
		//Convert Html to Ascci, i.e. '&lt;' to '<'
		if (convertHTMLtoASCII)
			string = StringEscapeUtils.unescapeHtml4(string);

			
		//Character Index
		int index = 0;
		//Token Positions
		int position = 0;
		while (index < string.length()) {
			//get current character
			char ch = string.charAt(index);
			
			//Is WhiteSPace so continue to the next character by increasing the character index!
			if (Character.isWhitespace(ch)) {
				index++;
			} 
			
			//Is AlphaNumeric so get The token till you find a character that is not a character or Number
			else if (Character.isLetter(ch) || Character.isDigit(ch)) {
				StringBuilder buf = new StringBuilder();
				while (index < string.length() && (Character.isLetter(string.charAt(index)) || Character.isDigit(string.charAt(index)))) {
					buf.append(string.charAt(index));
					index++;
				}
				String token = buf.toString();
				if(addWrapperToken(tokens,token,position,"",translateTokensToId,convertToLowerCase))
					position++;
			} 
			
			//IS HTML OR PUNCTUATION 
			else {
				//If we want to save punctuation
					
				/*
				 * If character is '<' then probably is an HTML tag
				 * We continue to append the tokens till we find the token '>'
				 * TODO 1. this is not completely right because there is the case where the character is only the '<' less than symbol and not HTML tag
				 * 			Solution: if we got a another character which the symbol less than '<' then the  first symbol is just a symbol and not HTML TAG
				 * 						Keep the index of the first less than symbol
				 * 		2. Also Here we keep the whole HTML TAG as one Token - What if the token size exceed the max allowed token size
				 * 			@see addToken(); In case that the token is html then we dont check its max or min size
				 */
				StringBuilder buf = new StringBuilder();
				if(ch=='<'){
					while (index < string.length()){
						char chTemp = string.charAt(index);
						//We skip whitespaces....may this produces problem
//						if(Character.isWhitespace(chTemp)){
//							index++;
//							continue;
//						}
						if(chTemp == '>'){
							buf.append(chTemp);
							index++;
							break;
						}
						buf.append(chTemp);
						index++;
					}
					
					
					//SAVE HTML token if it is NOT script tag
					String token = buf.toString().replaceAll("\\s+", " ");
					if(!token.toLowerCase().startsWith("<script") && !token.toLowerCase().startsWith("<style")){
						if(addWrapperToken(tokens,token,position,"HTML",translateTokensToId,convertToLowerCase))
							position++;
					}
					
					
					/*
					 * ELSE find the whole SCRIPT and skip it 
					 */	
					else{
						StringBuilder buf2 = new StringBuilder();
						while (index < string.length()){
							char chTemp = string.charAt(index);
							if(Character.isWhitespace(chTemp)){
								index++;
								continue;
							}
							if(chTemp == '>'){
								//if end with </script
								if(buf2.toString().toLowerCase().endsWith("</script") || buf2.toString().toLowerCase().endsWith("</style")){
									index++;
									break;
								}
							}
							buf2.append(chTemp);
							index++;
						}
					}
				}
				else{
					//SAVE PUNCT token
					addWrapperToken(tokens,Character.toString(ch),position,"PUNCT",translateTokensToId,convertToLowerCase);
					index++;
					position++;
				}
			}
		}
		return new TokenSequence(tokens.toArray(new WrapperToken[]{}));
	}

	/**
	 * SAVE  TEMPLATE TOKEN
	 * @param tokens
	 * @param token
	 * @param position
	 * @param tokenType - HTML , PUNCT or "" in case taht is alphanumeric
	 * @return
	 */
	private boolean addWrapperToken(List<WrapperToken> tokens,String token, int position,String tokenType,boolean translateTokensToId, boolean convertToLowerCase){
		
		
		//In case that is aplhanumeric we found its typeCOde and then is type from the map
		Integer tokenTypeCode = 0;
		if(tokenType.equals("")){
			tokenTypeCode = alphanumericTypeCode(token);
			tokenType = tokenTypeList.get(tokenTypeCode);
		}
		
		if(!ignoreLowerCase && convertToLowerCase)
			token = token.toLowerCase();
		
		//translate token to token Id
		int tokenCode = -1;
		if(translateTokensToId)
			tokenCode = lexicon.getTokenCode(token);

		if(tokenType.equals("PUNCT") || tokenType.equals("HTML")){
			tokens.add(new WrapperToken(token,tokenCode,position,tokenType,tokenTypeList.indexOf(tokenType)));
			return true;
		}
		else if (token.length() >= minWrapperTokenLength && token.length() <= maxWrapperTokenLength){// && !stopList.contains(token.toLowerCase())){
			tokens.add(new WrapperToken(token,tokenCode,position,tokenType,tokenTypeCode));
			return true;
		} 
		else
			return false;
	}


	/**
	 * Returns a string with the type(s) of the given alphanumeric String
	 * Alphabetic or Number
	 * Alphabetic => Caps || AllCaps || Lower
	 * @param Aplanumeric String
	 * @return Int - typeCode of the given string 
	 */
	private Integer alphanumericTypeCode(String alphaNum){
		
		
		
		//Alphanumeric 
		if(alphaNum.matches(".*\\w+?.*") && alphaNum.matches(".*\\d+?.*"))
			return tokenTypeList.indexOf("ALPHANUMERIC");
		
		//Number
		else if(alphaNum.matches("\\d+?"))
			return tokenTypeList.indexOf("ALPHANUMERIC_NUMBER");
		
		//All caps
		else if(isAllUpper(alphaNum))
			return tokenTypeList.indexOf("ALPHABETIC_ALLCAPS");
		
		//Only the first character is on Uppercase
		else if(Character.isUpperCase(alphaNum.charAt(0)))
			return tokenTypeList.indexOf("ALPHABETIC_CAPS");
		
		//Lower case
		else
			return tokenTypeList.indexOf("ALPHABETIC_LOWER");
	}


	
	
	/**
	 * 
	 * @param tokenSequence
	 * @return new tokensequence without not valid tags text
	 */
	public static TokenSequence cleanUpInvalidTagsText(TokenSequence tokenSequence){
		List<WrapperToken> newTokens = new ArrayList<WrapperToken>();
		int newIndex=-1;
		WrapperToken[] tokenArray = tokenSequence.getTokens();
		for(int i=0;i<tokenArray.length;i++){
			newIndex=i;
			WrapperToken token = tokenArray[i];
			if(token.getTypeCode() == 0){
				for(String invalidTag:ieInvalidHtmlTagsKeywords){
					if(token.getToken().startsWith(invalidTag)){
						newIndex = getIndexOfNextHtmlTag(i+1, tokenArray);
						break;
					}
				}
			}
			if(newIndex != i)
				i = newIndex-1;
			else
				newTokens.add(token);
		}
		return new TokenSequence(newTokens.toArray(new WrapperToken[]{}));
	}
	
	/**
	 * @param startIndex
	 * @param tokenArray
	 * @return the index of the next HTML tag token
	 */
	private static int getIndexOfNextHtmlTag(int startIndex,WrapperToken[] tokenArray){
		int htmlTagIndex = startIndex;
		for(int i=startIndex;i<tokenArray.length;i++){
			WrapperToken token = tokenArray[i];
			if(token.getTypeCode() == 0){
				htmlTagIndex = i;
				break;
			}
		}
		return htmlTagIndex;
	}


	private static boolean isAllUpper(String s) {
	    for(char c : s.toCharArray()) {
	       if(Character.isLetter(c) && Character.isLowerCase(c)) {
	           return false;
	        }
	    }
	    return true;
	}

}
