package edu.claflin.finder.logic.cygrouper;

 /* This class was written by Evyatar Saias & Ariel Sari on Nov 19, 2019. */
public class CygrouperEdge {

    public String source;
    public String target;

    public CygrouperEdge(String s, String t)
    {
        this.source = s;
        this.target = t;
    }

    @Override
    public String toString() {
        return "{"+this.source + "," + this.target+"}";
    }



}
