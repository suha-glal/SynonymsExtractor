package bingsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContextsMatch implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String word;
	double meanDocFreq=0;
	double stdDocFreq=0;	
	ArrayList<ContextWord> ContextsMatchList=new ArrayList<ContextWord>();
}
