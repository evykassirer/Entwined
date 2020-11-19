package com.charlesgadeken.entwined.interaction;

import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;

class ChannelShrubLevels{
    private CompoundParameter[] levels;
    ChannelShrubLevels(int numShrubs){
        levels = new CompoundParameter[numShrubs];
        for (int i=0; i<numShrubs; i++){
            this.levels[i] = new CompoundParameter("shrub" + i, 1);
        }
    }
    public CompoundParameter getParameter(int i){
        return this.levels[i];
    }
    public double getValue(int i){
        return this.levels[i].getValue();
    }
}
