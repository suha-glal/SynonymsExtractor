package bingsearch;

import java.util.Comparator;

public class ContextMatchComparetor implements Comparator<ContextWord>{
 
    @Override
    public int compare(ContextWord o1, ContextWord o2) {
        return (o1.cxMatchNo>o2.cxMatchNo ? -1 : (o1.cxMatchNo==o2.cxMatchNo? 0 : 1));
    }
}