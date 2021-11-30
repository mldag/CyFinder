/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.claflin.finder.algo.clustering.struct.fast_greedy_struct;


import java.util.List;

import edu.claflin.finder.logic.Node;

/**
 *
 * @author Cesar Martin
 */
public class VectorValue 
{
    private List<Node> community;
    private double a;
    
    public VectorValue(List<Node> c) 
    {
        this.community = c;
        this.a = 0;
    }
    
    public VectorValue(List<Node> c, double a) 
    {
        this.community = c;
        this.a = a;
    }

    /**
     * @return the community
     */
    public List<Node> getCommunity ()
    {
        return community;
    }

    /**
     * @return the a
     */
    public double getA ()
    {
        return a;
    }

    /**
     * @param a the a to set
     */
    public void setA ( double a )
    {
        this.a = a;
    }
    
    @Override
    public String toString() 
    {
        return "a: " + this.a + " " + this.community + " ";
    }
}
