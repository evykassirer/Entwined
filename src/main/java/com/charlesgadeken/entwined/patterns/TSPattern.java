package com.charlesgadeken.entwined.patterns;

import com.charlesgadeken.entwined.interaction.ParameterTriggerableAdapter;
import com.charlesgadeken.entwined.interaction.Triggerable;
import com.charlesgadeken.entwined.model.Model;
import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.pattern.LXPattern;

public abstract class TSPattern extends LXPattern {
    ParameterTriggerableAdapter parameterTriggerableAdapter;
    String readableName;

    protected final Model model;

    public TSPattern(LX lx) {
        super(lx);
        model = (Model) lx.getModel();
    }

    void onTriggerableModeEnabled() {
        getParameter("fade").setValue(0);

        getChannelFade().setValue(0);
        parameterTriggerableAdapter = getParameterTriggerableAdapter();
        parameterTriggerableAdapter.addOutputTriggeredListener(
                (LXParameter parameter) -> setCallRun(parameter.getValue() != 0));
        setCallRun(false);
    }

    Triggerable getTriggerable() {
        return parameterTriggerableAdapter;
    }

    LXParameter getChannelFade() {
        return getParameter("fade");
    }

    ParameterTriggerableAdapter getParameterTriggerableAdapter() {
        return new ParameterTriggerableAdapter(lx, getParameter("fade"));
    }

    void setCallRun(boolean callRun) {
        getChannel().enabled.setValue(callRun);
    }

    boolean getEnabled() {
        return getTriggerable().isTriggered();
    }

    double getVisibility() {
        return getParameter("fade").getValue();
    }
}
