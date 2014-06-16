package com.location_detection.lexicon;

import java.io.IOException;

public interface LexiconInterface {
	
	
	
	/**
	 * Get Token's String
	 * @param int Token code
	 * @return String representation of the token code
	 */
	public String getToken(int code);
	
	/**
	 * Given the Token String return its code
	 * @param String token
	 * @return int - token's code
	 */
	public int getTokenCode(String token);	
	/**
	 * Checks if the lexicon file exists in order to create a new one
	 * @return
	 */
	public boolean existLexicon();
	
	/**
	 * Get and Import the existed Lexicon from the lexicon file
	 */
	public void getLexiconFromFile();
	
	/**
	 * Write Lexicon to the given File
	 * @param lexiconFile
	 * @throws IOException
	 */
	public void writeLexiconToFile()throws IOException;


}
