package com.charlesgadeken.entwined.bpm;

import heronarts.lx.LX;
import heronarts.lx.LXEngine;
import heronarts.lx.mixer.LXChannel;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXListenableNormalizedParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.pattern.LXPattern;
import java.util.ArrayList;
import java.util.List;

public class BPMTool {

    private final LX lx;

    final BooleanParameter tapTempo = new BooleanParameter("Tap");
    final BooleanParameter nudgeUpTempo = new BooleanParameter("Nudge +");
    final BooleanParameter nudgeDownTempo = new BooleanParameter("Nudge -");

    final String[] bpmLabels = {"SIN", "SAW", "TRI", "QD", "SQR"};
    final DiscreteParameter tempoLfoType = new DiscreteParameter("Tempo LFO", bpmLabels.length);

    final String[] beatLabels = {"1", "\u00bd", "\u00bc", "1/16"}; // 1, 1/2, 1/4, 1/16
    final DiscreteParameter beatType = new DiscreteParameter("Beat", beatLabels.length);
    final double[] beatScale = {1, 2, 4, 16};

    final BooleanParameter addTempoLfo = new BooleanParameter("Add Tempo LFO");
    final BooleanParameter clearAllTempoLfos = new BooleanParameter("Clear All Tempo LFOs");

    private LXChannel currentActiveChannel = null;
    private final List<BPMParameterListener> parameterListeners =
            new ArrayList<BPMParameterListener>();
    private final List<BPMParameterListener> masterEffectParameterListeners =
            new ArrayList<BPMParameterListener>();

    private final ParameterModulatorController[] modulatorControllers;
    final ParameterModulationController modulationController;

    BPMTool(LX lx, LXListenableNormalizedParameter[] effectKnobParameters) {
        this.lx = lx;

        ParameterModulatorControllerFactory factory = new ParameterModulatorControllerFactory();

        modulatorControllers =
                new ParameterModulatorController[] {
                    factory.makeSinLFOController(),
                    factory.makeSawLFOController(),
                    factory.makeTriangleLFOController(),
                    factory.makeQuadraticEnvelopeController(),
                    factory.makeSquareLFOController()
                };

        modulationController = new ParameterModulationController(lx, modulatorControllers);

        addActionListeners(effectKnobParameters);
    }

    public void addActionListeners(final LXListenableNormalizedParameter[] effectKnobParameters) {

        tapTempo.addListener(
                new LXParameterListener() {
                    public void onParameterChanged(LXParameter parameter) {
                        if (tapTempo.isOn()) {
                            lx.engine.tempo.tap();
                        }
                    }
                });

        nudgeUpTempo.addListener(
                new LXParameterListener() {
                    public void onParameterChanged(LXParameter parameter) {
                        if (nudgeUpTempo.isOn()) {
                            lx.engine.tempo.adjustBpm(1);
                        }
                    }
                });

        nudgeDownTempo.addListener(
                new LXParameterListener() {
                    public void onParameterChanged(LXParameter parameter) {
                        if (nudgeDownTempo.isOn()) {
                            lx.engine.tempo.adjustBpm(-1);
                        }
                    }
                });

        addTempoLfo.addListener(
                new LXParameterListener() {
                    public void onParameterChanged(LXParameter parameter) {
                        if (addTempoLfo.isOn()) {
                            watchPatternParameters(
                                    lx.engine.getFocusedChannel().getActivePattern());
                            watchMasterEffectParameters(effectKnobParameters);
                        } else {
                            unwatchPatternParameters();
                            unwatchMasterEffectParameters();
                        }
                    }
                });

        clearAllTempoLfos.addListener(
                new LXParameterListener() {
                    public void onParameterChanged(LXParameter parameter) {
                        if (clearAllTempoLfos.isOn()) {
                            modulationController.unbindAllParameters();
                        }
                    }
                });

        watchEngine(lx.engine);
    }

    private final LXChannel.AbstractListener bindPatternParametersListener =
            new LXChannel.AbstractListener() {
                @Override
                public void patternDidChange(LXChannel channel, LXPattern pattern) {
                    watchPatternParameters(pattern);
                }
            };

    private void watchEngine(final LXEngine engine) {
        engine.focusedChannel.addListener((parameter) -> watchDeck(engine.getFocusedChannel()));
        watchDeck(engine.getFocusedChannel());
    }

    private void watchDeck(LXChannel channel) {
        if (this.currentActiveChannel != channel) {
            if (this.currentActiveChannel != null) {
                this.currentActiveChannel.removeListener(this.bindPatternParametersListener);
            }
            this.currentActiveChannel = channel;
            this.currentActiveChannel.addListener(this.bindPatternParametersListener);
        }
        watchPatternParameters(channel.getActivePattern());
    }

    private void watchPatternParameters(LXPattern pattern) {
        unwatchPatternParameters();
        if (addTempoLfo.isOn()) {
            for (LXParameter parameter : pattern.getParameters()) {
                if (parameter instanceof LXListenableNormalizedParameter) {
                    parameterListeners.add(
                            new BPMParameterListener(
                                    this, (LXListenableNormalizedParameter) parameter));
                }
            }
        }
    }

    private void unwatchPatternParameters() {
        for (BPMParameterListener parameterListener : parameterListeners) {
            parameterListener.stopListening();
        }
        parameterListeners.clear();
    }

    private void watchMasterEffectParameters(LXListenableNormalizedParameter[] parameters) {
        for (LXListenableNormalizedParameter parameter : parameters) {
            masterEffectParameterListeners.add(new BPMParameterListener(this, parameter));
        }
    }

    private void unwatchMasterEffectParameters() {
        for (BPMParameterListener parameterListener : masterEffectParameterListeners) {
            parameterListener.stopListening();
        }
        masterEffectParameterListeners.clear();
    }

    public void bindParameter(
            LXListenableNormalizedParameter parameter, double minValue, double maxValue) {
        modulationController.bindParameter(
                getSelectedModulatorController(),
                parameter,
                minValue,
                maxValue,
                getSelectedModulatorScale());
    }

    private ParameterModulatorController getSelectedModulatorController() {
        return modulatorControllers[tempoLfoType.getValuei()];
    }

    private double getSelectedModulatorScale() {
        return beatScale[beatType.getValuei()];
    }
}
