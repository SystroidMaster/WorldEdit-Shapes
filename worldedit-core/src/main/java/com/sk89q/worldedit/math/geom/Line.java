package com.sk89q.worldedit.math.geom;

public class Line implements Orthotope{
    private CombinedPoint minPoint, maxPoint;
    private boolean isSplit;
    private Line minChild, maxChild;
    private Axis orientation;

    public Line(CombinedPoint minPoint, CombinedPoint maxPoint, Axis orientation) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.orientation = orientation;

        this.isSplit = false;
        this.minChild = null;
        this.maxChild = null;
    }

    public boolean isSplit() {
        return isSplit;
    }
    public boolean split() {
        if (isSplit) {
            return false;
        }
        //...
        return true;
    }
}

