package com.charlesgadeken.entwined.patterns.general;

import com.charlesgadeken.entwined.Funcs;
import com.charlesgadeken.entwined.model.BaseCube;
import com.charlesgadeken.entwined.model.Geometry;
import com.charlesgadeken.entwined.patterns.TSPattern;
import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.parameter.LXParameter;

@LXCategory(LXCategory.TEST)
public class SweepPattern extends TSPattern {
    final SinLFO speedMod = new SinLFO(3000, 9000, 5400);
    final SinLFO yPos = new SinLFO(model.yMin, model.yMax, speedMod);
    final SinLFO width = new SinLFO("WIDTH", 2 * Geometry.FEET, 20 * Geometry.FEET, 19000);
    final SawLFO offset = new SawLFO(0, Funcs.TWO_PI, 9000);

    final BoundedParameter amplitude =
            new BoundedParameter("AMP", 10 * Geometry.FEET, 0, 20 * Geometry.FEET);
    final BoundedParameter speed = new BoundedParameter("SPEED", 1, 0, 3);
    final BoundedParameter height = new BoundedParameter("HEIGHT", 0, -300, 300);
    final SinLFO amp = new SinLFO(0, amplitude, 5000);

    SweepPattern(LX lx) {
        super(lx);
                addModulator(speedMod).start();
                addModulator(yPos).start();
                addModulator(width).start();

                addParameter("foo", amplitude);
                addParameter("foo", speed);
                addParameter("foo", height);

                addModulator(amp).start();
                addModulator(offset).start();
    }

        public void onParameterChanged(LXParameter parameter) {
            super.onParameterChanged(parameter);
            if (parameter == speed) {
                float speedVar = 1 / speed.getValuef();
                speedMod.setRange(9000 * speedVar, 5400 * speedVar);
            }
        }

    public void run(double deltaMs) {
                for (BaseCube cube : model.baseCubes) {
                    float yp =
                            yPos.getValuef()
                                    + amp.getValuef()
                                            * Funcs.sin((cube.cx - model.cx) * .01f +
         offset.getValuef());
                    colors[cube.index] =
                            LX.hsb(
                                    ( //lx.getBaseHuef() + // TODO(meawoppl) Qua?
                                            Funcs.abs(cube.x - model.cx) * .2f
                                                    + cube.cz * .1f
                                                    + cube.cy * .1f)
                                            % 360,
                                    Funcs.constrain(Funcs.abs(cube.transformedY - model.cy), 0,
         100),
                                    Funcs.max(
                                            0,
                                            100
                                                    - (100 / width.getValuef())
                                                            * Funcs.abs(
                                                                    cube.cy - yp -
         height.getValuef())));
                }
    }
}
