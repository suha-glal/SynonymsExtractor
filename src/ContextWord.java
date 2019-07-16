package bingsearch;

import java.io.Serializable;

public class ContextWord implements Serializable{
	String cxWord;
	int docFreq;
	boolean MSArabic=false;
	int cxMatchNo;
	boolean candidate=true;
	 ContextWord (String s, int f)
	 {
		cxWord=s;
		docFreq=f;
		
		 cxMatchNo=0;
	 }
	 ContextWord (String s)
	 {
	 cxWord=s;
	 docFreq=0;
	 cxMatchNo=0; 
	 }

}
