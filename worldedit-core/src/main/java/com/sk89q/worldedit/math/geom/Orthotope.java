package com.sk89q.worldedit.math.geom;

public interface Orthotope {
    public Axis getSplittingAxis();
    public boolean isSplit();
    public boolean split(Axis axis, AdaptiveParameterGrid grid);
    public Orthotope[] getChildren();
}