package bingsearch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
class FileReadToString
{
 public static  ArrayList<String> File2ArrString(String filename)
  {
	 ArrayList<String> list= new ArrayList<String> ();
  try{
  // Open the file that is the first 
 // command line parameter
  FileInputStream fstream = new FileInputStream(filename);
  // Get the object of DataInputStream
  DataInputStream in = new DataInputStream(fstream);
  BufferedReader br = new BufferedReader(new InputStreamReader(in));
  String strLine="";
  int c;
  //Read File Line By Line
  while ((strLine = br.readLine()) != null)   {
	 
	 list.add(strLine);
 
  }
  //Close the input stream
  in.close();
  
    }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }
  
 
  return list;
  }

public static  ArrayList<String>  ArrStringFromFile ( String fileName )
{
	ArrayList<String> list= new ArrayList<String> ();

   
    try
    {
       
       
        // create a buffered reader
        File file = new File ( fileName );
        FileInputStream fileInputStream = new FileInputStream ( file );
        InputStreamReader inputStreamReader = new InputStreamReader ( fileInputStream, "UTF-16" );

        //If the bufferedReader is not big enough for a file, I should change the size of it here
        BufferedReader bufferedReader = new BufferedReader ( inputStreamReader, 20000 );

        // read in the text a line at a time
        String part;
        StringBuffer word = new StringBuffer ( );
        while ( ( part = bufferedReader.readLine ( ) ) != null )
        {
            // add spaces at the end of the line
            part = part + "  ";

            // for each line
            for ( int index = 0; index < part.length ( ) - 1; index ++ )
            {
                // if the character is not a space, append it to a word
                if ( ! ( Character.isWhitespace ( part.charAt ( index ) ) ) )
                {
                    word.append ( part.charAt ( index ) );
                }
                // otherwise, if the word contains some characters, add it
                // to the vector
                else
                {
                    if ( word.length ( ) != 0 )
                    {
                    	 list.add( word.toString ( ) );
                        word.setLength ( 0 );
                    }//if
                }//else
            }//for
        }//while

      

        // destroy the buffered reader
        bufferedReader.close ( );
	        fileInputStream.close ( );

        
       
    }//try
    catch ( Exception exception )
    {
      //  JOptionPane.showMessageDialog ( arabicStemmerGUI, "Could not open '" + fileName + "'.", " Error ", JOptionPane.ERROR_MESSAGE );
      
    }
   return list;
}
}