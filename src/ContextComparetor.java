package bingsearch;

import java.util.Comparator;

public class ContextComparetor implements Comparator<ContextWord>{
 
    @Override
    public int compare(ContextWord o1, ContextWord o2) {
        return (o1.docFreq>o2.docFreq ? -1 : (o1.docFreq==o2.docFreq ? 0 : 1));
    }
}