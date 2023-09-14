package com.sk89q.worldedit.math.geom;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;

public class Line implements Orthotope{
    //TODO: replace CombinedPoint with bare ParametricBlockData?
    private final CombinedPoint minPoint;
    private final CombinedPoint maxPoint;
    private boolean isSplit;
    private Line minChild, maxChild;
    private final Axis orientation;

    public Line(CombinedPoint minPoint, CombinedPoint maxPoint, Axis orientation) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.orientation = orientation;

        this.isSplit = false;
        this.minChild = null;
        this.maxChild = null;
    }

    @Override
    public boolean isSplit() {
        return isSplit;
    }

    @Override
    public boolean split(Axis axis, AdaptiveParameterGrid grid) {
        if (isSplit || (axis != orientation)) {
            return false;
        } else {
            double[] newParameters = minPoint.getBlockData().getParameters().clone(); // Cloning is important to be able to change it
            // TODO: Check whether newParameters has required length?
            int axisIndex = axis.ordinal();
            newParameters[axisIndex] = (newParameters[axisIndex] + maxPoint.getBlockData().getParameters()[axisIndex]) / 2;
            CombinedPoint newPoint = new CombinedPoint(grid, newParameters);
            // Build two new Lines
            minChild = new Line(minPoint, newPoint, orientation);
            maxChild = new Line(newPoint, maxPoint, orientation);
            isSplit = true;
        }
        return true;
    }

    @Override
    public Axis getSplittingAxis() {
        // TODO: adjust value to compare against?
        return length() <= 0.5 ? Axis.NOAXIS : orientation;
    }

    @Override
    public Line[] getChildren() {
        return new Line[] {minChild, maxChild};
    }

    public CombinedPoint getMinPoint() {
        return minPoint;
    }

    public CombinedPoint getMaxPoint() {
        return maxPoint;
    }

    public double length() {
        return minPoint.getBlockData().getWorldcoords().distance(maxPoint.getBlockData().getWorldcoords());
    }
}

