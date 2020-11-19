package com.charlesgadeken.entwined.patterns.general;

import static org.junit.jupiter.api.Assertions.*;

import heronarts.lx.LX;
import org.junit.jupiter.api.Test;

class SweepPatternTest {
    @Test
    public void testBuild() {
        LX lx = new LX();
        new SweepPattern(lx);
    }
}
