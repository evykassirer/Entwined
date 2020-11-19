package com.charlesgadeken.entwined.model;

import heronarts.lx.LX;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.structure.LXBasicFixture;
import heronarts.lx.transform.LXMatrix;
import java.util.List;

public abstract class PseudoAbstractFixture extends LXModel {
    private List<LXPoint> points;

    public List<LXPoint> getPoints() {
        return points;
    }

    public void setPoints(List<LXPoint> points) {
        this.points = points;
    }


}
