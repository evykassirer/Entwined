package com.charlesgadeken.entwined.interaction;

import heronarts.lx.parameter.CompoundParameter;

public class ChannelTreeLevels{
    private CompoundParameter[] levels;
    ChannelTreeLevels(int numTrees){
        levels = new CompoundParameter[numTrees];
        for (int i=0; i<numTrees; i++){
            this.levels[i] = new CompoundParameter("tree" + i, 1);
        }
    }
    public CompoundParameter getParameter(int i){
        return this.levels[i];
    }
    public double getValue(int i){
        return this.levels[i].getValue();
    }
}