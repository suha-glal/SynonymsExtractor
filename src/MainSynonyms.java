
package bingsearch;





import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.google.code.bing.search.client.BingSearchClient;
import com.google.code.bing.search.client.BingSearchServiceClientFactory;
import com.google.code.bing.search.client.BingSearchClient.SearchRequestBuilder;
import com.google.code.bing.search.schema.AdultOption;
import com.google.code.bing.search.schema.SearchOption;
import com.google.code.bing.search.schema.SearchRequest;
import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.SourceType;
import com.google.code.bing.search.schema.relatedsearch.RelatedSearchResult;
import com.google.code.bing.search.schema.web.WebResult;
import com.google.code.bing.search.schema.web.WebSearchOption;

/**
 * The Class RelatedSearchSample.
 */
public class MainSynonyms {

    /** The Constant APPLICATION_KEY_OPTION. */
    private static final String APPLICATION_KEY = "APPLICATION_KEY_OPTION_FROM_BING_SEARCH_ENGINE";
    private static final  String [] mean={"","معناها","يعني","بمعنى","تعني","معنى"};
   
    private static final ArrayList<String> words= FileReadToString.File2ArrString("mywords.txt");
    private static final Preprocesser Pro= new Preprocesser();
    private static final List meanlist= new List();
    /**
     * The main method.
     * 
     * @param args the arguments
     * @throws IOException 
     * @throws LockObtainFailedException 
     * @throws CorruptIndexException 
     */
	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException
	{
		
		//CollectContextData();
	 /*   ArrayList<SynonymContexts> SynonymsContextsArr=PrepareContextData();
		ArrayList<IndexedSynonymContexts> idxSynCx=indexContextWords(SynonymsContextsArr);
		SynonymsContextsArr=null;
		SetMSArabic( idxSynCx);
	    PrintIndexedSyCox (idxSynCx);
	    ArrayList <ContextsMatch>  conMatchArr=ContextMatch(idxSynCx);
	 
	   //-------Save it to memeory-------------------				
       SaveContextsMatchToMemory(conMatchArr);*/
       //----------Read Contxet Match Array From Memeory------------
     ArrayList <ContextsMatch> conMatchArr=ReadContextMatchArrayFromMemory();
	//----------Purn an nessesary words---------------		
     PurnContextMatchList(conMatchArr);
	//----------print Contexts Matched----------------
    PrintContextMatch(conMatchArr);
		
		
	
		
	}
	private static void SaveContextsMatchToMemory(ArrayList <ContextsMatch>  conMatchArr ) 
	{
		File file = new File("contextmatch.in");
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(file);
			ObjectOutputStream sout = new ObjectOutputStream(fout);  
			sout.writeObject( conMatchArr );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
			
	}
	private static ArrayList <ContextsMatch> ReadContextMatchArrayFromMemory()
	{
		File file = new File("contextmatch.in");
		ArrayList <ContextsMatch>fileObj=null ;
		FileInputStream f=null;
		ObjectInputStream s=null;
		try {
			f = new FileInputStream(file);
		    s = new ObjectInputStream(f);  
			fileObj = (ArrayList<ContextsMatch>)s.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
		try {
			f.close();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return fileObj;
	}
	private static ArrayList <ContextsMatch>  ContextMatch(ArrayList<IndexedSynonymContexts> idxSynCx)
	{
		ArrayList <ContextsMatch> conMatchArr= new ArrayList <ContextsMatch>();
		int forme=1;
		for( IndexedSynonymContexts idnsc:idxSynCx)
		{
			ContextsMatch cMatch= new ContextsMatch();
			cMatch.word=idnsc.word;
			
			ArrayList<String> AllcontextWords= new ArrayList<String>();
			//add words of all patterns 
			for(int i=0;i<idnsc.idxContextsList.size();i++)
			{
			for(ContextWord word:idnsc.idxContextsList.get(i))
			{
				AllcontextWords.add(word.cxWord);
			}
			}
			//eliminate dublicate
			HashSet<String> h = new HashSet<String>(AllcontextWords);
			AllcontextWords.clear();
			AllcontextWords.addAll(h);
			h=null;
			// match the context
			
			for(int c=0;c<AllcontextWords.size();c++)
			{
			ContextWord cw= new ContextWord(AllcontextWords.get(c));
			
			for(int i=0;i<idnsc.idxContextsList.size();i++)
			{
				ArrayList<String> strArr=getStringsList((ArrayList<ContextWord>) idnsc.idxContextsList.get(i));
				int idx=strArr.indexOf(AllcontextWords.get(c));
				if(idx!=-1)
				{
					
					cw.cxMatchNo++;
					cw.docFreq=cw.docFreq+idnsc.idxContextsList.get(i).get(idx).docFreq;
				}
			
			}//i
			cMatch.ContextsMatchList.add(cw);
			}//c
			
			Collections.sort(cMatch.ContextsMatchList, new ContextComparetor());
			conMatchArr.add(cMatch);
			System.out.println( forme);
			forme++;
		}	
		
		return conMatchArr;
		
	}
	private static void PurnContextMatchList(ArrayList <ContextsMatch> conMatchArr)
	{
		
		for(ContextsMatch cmItem:conMatchArr)
		{
			ArrayList<Integer> docfreqArr= new ArrayList<Integer>();
			for(ContextWord cw:cmItem.ContextsMatchList)
			{
				docfreqArr.add(cw.docFreq);
			}
			
			cmItem.meanDocFreq=Mean(docfreqArr);
			cmItem.stdDocFreq=STD(cmItem.meanDocFreq,docfreqArr);
		}
		
		for(ContextsMatch cmItem:conMatchArr)
		{
			
			for(ContextWord cw:cmItem.ContextsMatchList)
			{
				if(cw.docFreq==cmItem.meanDocFreq)
				//if(cw.docFreq>(cmItem.meanDocFreq+(0.2*cmItem.stdDocFreq))||cw.docFreq<(cmItem.meanDocFreq-(0.2*cmItem.stdDocFreq)))
				{
					cw.candidate=false;
				}
			}
			
		}
		
	}
	private static double Mean(ArrayList<Integer> docfreqArr)
	{
		double meanval=0;
		for(Integer f:docfreqArr)
		{
			meanval=meanval+f;
		}
		
		return(meanval/docfreqArr.size());
	}
	private static double STD(double mean,ArrayList<Integer> docfreqArr)
	{
		final int n = docfreqArr.size(); 
	// calculate the sum of squares 
	double sum = 0; 
	for(Integer f:docfreqArr)
	{ 
	final double v = f - mean; 
	sum += v * v; 
	} 
	// Change to ( n - 1 ) to n if you have complete data instead of a sample. 
	return Math.sqrt( sum /  n ); 
		
	}
	private static void PrintContextMatch(ArrayList <ContextsMatch> conMatchArr)
	{
		WriteFile wf= new WriteFile("data/ContextMatchWithPurning2.doc",false);//ContextMatchWithPurning2
		for(ContextsMatch cMobj:conMatchArr)
		{
			wf.write("-----------------------"+ cMobj.word+"-----------------------------");
			for(ContextWord cw:cMobj.ContextsMatchList)
			{
				if(cw.cxMatchNo>=3&& cw.candidate==true)
				wf.write(cw.cxWord+"$"+cw.cxMatchNo+"#"+cw.docFreq);
								
			}
		}
		wf.close();
	}
	private static ArrayList<String> getStringsList(ArrayList<ContextWord> cwArr)
	{
		ArrayList<String> strArr= new ArrayList<String>();
		for(ContextWord cw:cwArr)
		{
			strArr.add(cw.cxWord);
		}
		return strArr;
	}
	private static void SetMSArabic(ArrayList<IndexedSynonymContexts> idxSynCx) throws IOException
	{
		for( IndexedSynonymContexts idnsc:idxSynCx)
		{
			for(int i=0;i<idnsc.idxContextsList.size();i++)
			{
			for(ContextWord word:idnsc.idxContextsList.get(i))
			{
				word.MSArabic=Pro.isValidArabicWord(word.cxWord);
			}
			}
			
		
		}
	}
	private static void correctSpelling(ArrayList<IndexedSynonymContexts> idxSynCx)
	{
		
	}
	private static void PrintIndexedSyCox (ArrayList<IndexedSynonymContexts> idxSynCx)
	{
		//-------------------------Sample out after indexing------------------------------------------//
		
		WriteFile wf= new WriteFile("data/Bsampleindx.doc",false);
		
		for( IndexedSynonymContexts idnsc:idxSynCx)
		{
			wf.write(idnsc.word);
			for(int i=0;i<idnsc.idxContextsList.size();i++)
			{
				
			wf.write("---------------"+mean[i]+"-------------------");
			wf.write(idnsc.idxContextsList.get(i).size()+"");
			
			for(ContextWord word:idnsc.idxContextsList.get(i))
			{
				wf.write(word.docFreq+"#"+word.cxWord+"#"+word.MSArabic);
			}
			}//for i
			
					
			
		}
		wf.close();
		
	}
	private static void CollectContextData()
	{
		//-------------------------Configuration-----------------------------------------//
				BingSearchServiceClientFactory factory = BingSearchServiceClientFactory.newInstance();
				BingSearchClient client = factory.createBingSearchClient();
				SearchRequestBuilder builder = client.newSearchRequestBuilder();
				builder.withAppId(APPLICATION_KEY);
				builder.withSourceType(SourceType.WEB);
				builder.withVersion("2.0");
	  //-------------------------Collect Context Data----------------------------------/
				
										
				for(String word:words)
				{
				
					for (int i=0;i<mean.length;i++)
					{
						String query=word+" "+mean[i];
						
						builder.withQuery(query);
						builder.withWebRequestCount(50L);
						builder.withWebRequestOffset(0L);
						WriteFile wf= new WriteFile("data/"+query+".txt",false);
						SearchResponse response = client.search(builder.getResult());
						Long total=response.getWeb().getTotal();
						Long offset=0L, limit,counter=0L;
						
						if(total>=1000)
							limit=20L;
						else
							limit= (total/50);
						
							while(counter<limit)
							{
								for (WebResult result : response.getWeb().getResults())
								{
								       
								        wf.write(result.getDescription());
								      
								}//for results
								
								offset=offset+51;
								builder.withWebRequestOffset(offset);
							    response = client.search(builder.getResult());
								counter++;
								wait(170);
							}//while
						
						 //close output file
					    wf.close();
					}//for mean
				}//for words
	}
	
	private static ArrayList<SynonymContexts>  PrepareContextData()
	{
		//----------------------------------Read Context Data-------------------------------//
		
		
		ArrayList<SynonymContexts> SynonymsContextsArr= new ArrayList<SynonymContexts> ();
		for(String word:words)
		{
			SynonymContexts sc= new SynonymContexts();
			 sc.word=word;
			 
				String query=word+" "+mean[0];
				sc.Context.add(FileReadToString.File2ArrString("data/"+query+".txt"));
				
				query=word+" "+mean[1];
				sc.Context.add(FileReadToString.File2ArrString("data/"+query+".txt"));
				
				query=word+" "+mean[2];
				sc.Context.add(FileReadToString.File2ArrString("data/"+query+".txt"));
				
				query=word+" "+mean[3];
				sc.Context.add(FileReadToString.File2ArrString("data/"+query+".txt"));
				
				query=word+" "+mean[4];
				sc.Context.add(FileReadToString.File2ArrString("data/"+query+".txt"));
				
				query=word+" "+mean[5];
				sc.Context.add(FileReadToString.File2ArrString("data/"+query+".txt"));
				
				
				SynonymsContextsArr.add(sc);
		}//for words
		
				
	//------------------------------------Clean Context Data------------------------------------------			
		for( SynonymContexts sc:SynonymsContextsArr)
		{
			for(int i=0;i<sc.Context.size();i++)
			PreprocessArrStr(sc.Context.get(i));
			
		}
	
		/*-----------------------------Sample clean Context-----------------------------------------
		WriteFile wf= new WriteFile("data/sampleclean.doc",false);
		for( SynonymContexts sc:SynonymsContextsArr)
		{
			wf.write(sc.word);
			for (String s:sc.wordalone)
			{
				
				wf.write(s);
			}
			wf.write("-------------------------------------------------------------------------");
			
			
		}
		wf.close();
		*/
		return SynonymsContextsArr;
	
}
	private static ArrayList<IndexedSynonymContexts>indexContextWords(ArrayList<SynonymContexts> SynonymsContextsArr) throws CorruptIndexException, LockObtainFailedException, IOException
	{
		//-------------------------------------Index Context Words---------------------------------------//
		ArrayList<IndexedSynonymContexts> idxSynCx= new ArrayList<IndexedSynonymContexts> ();
			for( SynonymContexts sc:SynonymsContextsArr)
			{
				IndexedSynonymContexts idsc= new IndexedSynonymContexts();
				
				idsc.word=sc.word;
				for(int i=0;i<sc.Context.size();i++)
				{
				ArrayList<ContextWord> temArr=getContextWords(sc.Context.get(i));
				//Collections.sort(temArr, new ContextComparetor());
			  //  idsc.idxContextsList.add( temArr.subList(0,21));
				idsc.idxContextsList.add( temArr);
				}
				
				idxSynCx.add(idsc);
				
			}
			
			return  idxSynCx;
	}
	private static void PreprocessArrStr(ArrayList<String> contexts) 
	    { 
	    	
			 int i=0;
	    
	    	for(String context:contexts)
	    	
	    	{ 
	    		
	    		String cleantxt;
				try {
					cleantxt = Pro.preprocess(context);
						    			
		    		contexts.set(i, cleantxt);
		    	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		
	    		i++;
	    		
	    	}
	    	
	    	
	    }
 private static ArrayList<ContextWord> getContextWords(ArrayList<String> contexts) throws CorruptIndexException, LockObtainFailedException, IOException
	    {
	    
	    //This analyzer implements light-stemming as specified by: Light Stemming for Arabic Information Retrieval http://www.mtholyoke.edu/~lballest/Pubs/arab_stem05.pdf
	  //Analyzer analyzer = new ArabicAnalyzer( Version.LUCENE_35);//,new File("ArabicStopWords.txt")
	  Analyzer analyzer= new StandardAnalyzer(Version.LUCENE_35);
	  ArrayList<String>stopWords=FileReadToString.ArrStringFromFile("stopwords.txt");
		  String [] meanlist={"معناها","يعني","بمعنى","تعني","معنى"};
		  ArrayList<String> meanarr= new ArrayList<String> ();
		  Collections.addAll( meanarr,meanlist);
		  
	        // 1. create the index
	        Directory index = new RAMDirectory();

	       
	     
	      IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
	        IndexWriter w = new IndexWriter(index,config); 
	        
	        for(String str:contexts)
			{     
	            addDoc(w,str);
	        }

	        w.close();
	        
	        IndexReader reader= IndexReader.open(index);

	        
	        	//unique terms in the context 
	        	 TermEnum terms = reader.terms();
	        	 ArrayList<ContextWord>ContextWords= new ArrayList<ContextWord>();
	              
	              //set unique terms as attributes
	        while( terms.next())
	        		{
		        	String tempterm=terms.term().text();
		        	int freq=reader.docFreq(terms.term());
		        	
	        	  if(tempterm.length()>2 && 
	        			  stopWords.contains(tempterm)==false &&  meanarr.contains(tempterm)==false)
	        	  {
	        		
	        		  ContextWords.add(new ContextWord(tempterm,freq));
		            	
	        		  
	        	  }//if
	        	
		            	 	           
		            }//while
	      
	      
	       reader.close();
	      return ContextWords; 
	       
	    }
	    
	 private static void addDoc(IndexWriter w, String value) throws IOException {
	        Document doc = new Document();
	        doc.add(new Field("title", value, Field.Store.YES,Field.Index.ANALYZED));//Field.Index.ANALYZED
	        w.addDocument(doc);
	      }
	private static void wait(int amount)
	{
		try{
			  //do what you want to do before sleeping
			  Thread.currentThread().sleep(amount);//sleep for 1000 ms= 1sec
			  //do what you want to do after sleeptig
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
    
	
}
