package com.sk89q.worldedit.math.geom;

public class RectangularCuboid implements Orthotope {

    @Override
    public Axis getSplittingAxis() {
        return Axis.NOAXIS;
    }

    @Override
    public boolean isSplit() {
        return false;
    }

    @Override
    public boolean split(Axis axis, AdaptiveParameterGrid grid) {
        return false;
    }

    @Override
    public Orthotope[] getChildren() {
        return new Orthotope[0];
    }
}
