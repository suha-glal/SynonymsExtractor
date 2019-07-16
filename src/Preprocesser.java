package bingsearch;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import de.linguatools.disco.DISCO;


public class Preprocesser {
	
	
   
	private DISCO arabicDict;
	Preprocesser()
	{
//  ----------------------- Arabic Dictionary construction ----------------------
		 // path to the DISCO word space directory
        String discoDir = "C:/Users/Suha/Documents/workspace/libraries/dictionary/discoArabic";
       
        try {
			arabicDict= new DISCO(discoDir, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isValidArabicWord(String word) throws IOException
	{
		int freq=-1;

	   		
			 //retrieve the frequency of the input word
		     freq =  arabicDict.frequency(word);			
	       
	       // end if the word wasn't found in the index
	        if(freq == 0)
	        	return false;
	        else
	        	return true;
}
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet with "_SMILEHAPPY_" in place of happy emoticons and "_SMILESAD_" in place of sad emticons
	 */
	static private String replaceEmoticons(String item) {
		String result = item;
		result = result.replaceAll(":[)]+", "_HAPPY_");
		result = result.replaceAll(";[)]+", "_HAPPY_");
		result = result.replaceAll(":-[)]+", "_HAPPY_");
		result = result.replaceAll(";-[)]+", "_HAPPY_");
		result = result.replaceAll(":d", "_HAPPY_");
		result = result.replaceAll(";d", "_HAPPY_");
		result = result.replaceAll("=[)]+", "_HAPPY_");
		result = result.replaceAll(":[D]+", "_HAPPY_");
		result = result.replaceAll("X[D]+", "_HAPPY_");
		result = result.replaceAll("\\^_\\^", "_HAPPY_");
		result = result.replaceAll("=[D]+","_HAPPY_");
		result = result.replaceAll(":[(]+", "_SAD_");
		result = result.replaceAll(":-[(]+", "_SAD_");
		
		return result;
	}
	
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet without characters that are repeated more than three times
	 */
	static private String removeRepeatedCharacters(String item) {
		String result = item;
		result = item.replaceAll("(.)\\1{2,100}", "$1$1");//$1
		return result;
	}
	
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet with "STRLAUGH" in place of strings that represent a laugh
	 */
	static private String recognizeLaugh(String item) {
		String result = item;
		if(result.contains("kaak")||result.contains("kak")||result.contains("hah")||result.contains("lol")||result.contains("lool")||result.contains("هه")||result.contains("هاها")||result.contains("كاكا")||result.contains("خخ")||result.contains("هع"))
			result = "LAUGH";
		
		return result;
	}
	
	static private String languageProcessing(String str)
	{
		
		if(str.matches("[a-z]+"))
		{
			return str;
			/*
			if(isValidEnglishWord(str))
				{
				//eng.write(str+"\n");
				return str;
				}
			
			else
			{
				//String arbizi=Arabictransliteration(str);
				
			   // engArb.write(str+"-----"+arbizi+"\n");
				return "";//todo Arabic spelling check
			}
			*/
		}
			
		else if(str.matches("[ء-ي]+"))
		{
			
		   // arabic.write(str+"\n");
		return str;//todo Arabic spelling check
		}
		else if(str.matches("[a-z2-35-9]+"))
		{
			//String arbizi=Arabictransliteration(str);
			
		    //numarbiz.write(str+"-----"+arbizi+"\n");
			return "";//todo Arabic spelling check
			
		}
		else if(str.matches("[A-Z]+"))
		{
			//Tag.write(str+"\n");
		return str;
		}
		else {
			
		    //elses.write(str+"\n");
			return "";
		}
			
	}
	
	static String AlphReplacment(String word)
	{
		//word=word.replaceAll("ء"	,"ا");
		word=word.replaceAll("آ","ا");
		word=word.replaceAll("أ"	,"ا");
		word=word.replaceAll("ؤ"	,"و");
		word=word.replaceAll("إ"	,"ا");
		word=word.replaceAll("ئ"	,"ى");
		//word=word.replaceAll("ى"	,"ا");
		word=word.replaceAll("أ"	,"ا");
		return word;
	}
	
	static public String preprocess(String item) throws IOException  {
		
		String result_fin = "";
		
		String result = item;
		
		//make the sentence lower case
		result=result.toLowerCase();
		
		//remove repeated charachters
		result = removeRepeatedCharacters(result);
	
		
		String[]words=result.split("\\s");
		
		for(String word:words)
		{
		
			
			word=replaceEmoticons(word);
		    word=recognizeLaugh(word);
		    word=word.replaceAll("[^ء-غA-Zف-ي]+","");//for Arabic and English			
			word=languageProcessing(word);
			
					
			word=AlphReplacment(word);
			//word = word.replaceAll("(.)\\1{1,100}", "$1");//replace repeated char
			//word =word.replaceAll("يا", "");//replace repeated char
			//word = word.replaceAll("ما", "");//replace repeated char
			 
		 
			if(word.length()>0 )
			result_fin = result_fin +" "+word;
		}
		
	
		
		return result_fin;
		
	}
}