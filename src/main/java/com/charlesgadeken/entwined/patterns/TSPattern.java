package com.charlesgadeken.entwined.patterns;

import com.charlesgadeken.entwined.interaction.ParameterTriggerableAdapter;
import com.charlesgadeken.entwined.interaction.TreesTransition;
import com.charlesgadeken.entwined.interaction.Triggerable;
import com.charlesgadeken.entwined.model.Model;
import heronarts.lx.LX;
import heronarts.lx.mixer.LXChannel;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.pattern.LXPattern;

abstract class TSPattern extends LXPattern {
    ParameterTriggerableAdapter parameterTriggerableAdapter;
    String readableName;

    protected final Model model;

    TSPattern(LX lx) {
        super(lx);
        model = (Model)lx.getModel();
    }

    void onTriggerableModeEnabled() {
        getParameter("fade").setValue(0);

        getChannelFade().setValue(0);
        parameterTriggerableAdapter = getParameterTriggerableAdapter();
        parameterTriggerableAdapter.addOutputTriggeredListener((LXParameter parameter)->setCallRun(parameter.getValue() != 0));
        setCallRun(false);
    }

    Triggerable getTriggerable() {
        return parameterTriggerableAdapter;
    }

    CompoundParameter getChannelFade() {
        return getFaderTransition(getChannel()).get;
    }

    ParameterTriggerableAdapter getParameterTriggerableAdapter() {
        return new ParameterTriggerableAdapter(lx, getChannelFade());
    }

    void setCallRun(boolean callRun) {
        getChannel().enabled.setValue(callRun);
    }

    boolean getEnabled() {
        return getTriggerable().isTriggered();
    }

    double getVisibility() {
        return getChannel().getFader().getValue();
    }

    TreesTransition getFaderTransition(LXChannel channel) {
        return (TreesTransition) channel.getFaderTransition();
    }
}
