package com.sk89q.worldedit.math.geom;

import com.sk89q.worldedit.math.Vector3;

public class Rect implements Orthotope {
    private Line leftLine, rightLine, lowerLine, upperLine;
    private boolean isSplit;
    private Rect minChild, maxChild;
    private final Axis orientationRight, orientationUp; // perpendicular axis in 3D space
    private Axis splittingAxis;

    public Rect(Line leftLine, Line rightLine, Line lowerLine, Line upperLine, Axis orientationRight, Axis orientationUp) {
        this.leftLine = leftLine;
        this.rightLine = rightLine;
        this.lowerLine = lowerLine;
        this.upperLine = upperLine;

        this.orientationRight = orientationRight;
        this.orientationUp = orientationUp;

        this.isSplit = false;
        this.minChild = null;
        this.maxChild = null;
    }
    @Override
    public Axis getSplittingAxis() {
        /*
        //BlockVector3 distances = maxPoint.getBlockData().getPosition().subtract(minPoint.getBlockData().getPosition()).abs();
        Vector3 distances = maxPoint.getBlockData().getWorldcoords().subtract(minPoint.getBlockData().getWorldcoords()).abs();
        // TODO: maybe do the following more efficiently
        double maxDistance = Math.max(distances.getX(),Math.max(distances.getY(), distances.getZ()));
        // TODO: adjust value to compare against?
        if (maxDistance > 0.5) {
            if (maxDistance == distances.getX()) {
                return Axis.X;
            } else if (maxDistance == distances.getZ()) {
                return Axis.Y;
            } else {
                return Axis.Z;
            }
        }
        */
        double rightMax = Math.max(leftLine.length(), rightLine.length());
        double upMax = Math.max(lowerLine.length(), upperLine.length());
        // TODO: adjust constant value to compare against or the whole algorithm to determine whether Rect is small enough
        if (Math.max(rightMax, upMax) > 0.5) {
            return rightMax >= upMax ? orientationUp : orientationRight;
        }
        return Axis.NOAXIS;
    }

    @Override
    public boolean isSplit() {
        return isSplit;
    }

    @Override
    public boolean split(Axis axis, AdaptiveParameterGrid grid) {
        if (!(axis == orientationRight || axis == orientationUp)) {
            return false;
        } else if (isSplit) {
            // TODO: save splittingDirection?
            // is already split but in the other direction => delegate splitting to children
            // TODO: maybe simply destroy structure and get new children by splitting in the new direction?
            minChild.split(axis, grid);
            maxChild.split(axis, grid);
        } else if (axis == orientationRight) {
            lowerLine.split(axis, grid);
            upperLine.split(axis, grid);
            Line[] lowerLines = lowerLine.getChildren();
            Line[] upperLines = upperLine.getChildren();
            Line splittingLine = new Line(lowerLines[0].getMaxPoint(), upperLines[0].getMaxPoint(), orientationUp);
            minChild = new Rect(leftLine, splittingLine, lowerLines[0], upperLines[0], orientationRight, orientationUp);
            maxChild = new Rect(splittingLine, rightLine, lowerLines[1], upperLines[1], orientationRight, orientationUp);
            isSplit = true;
        } else if (axis == orientationUp) {
            leftLine.split(axis, grid);
            rightLine.split(axis, grid);
            Line[] leftLines = leftLine.getChildren();
            Line[] rightLines = rightLine.getChildren();
            Line splittingLine = new Line(leftLines[0].getMaxPoint(), rightLines[0].getMaxPoint(), orientationRight);
            minChild = new Rect(leftLines[0], rightLines[0], lowerLine, splittingLine, orientationRight, orientationUp);
            maxChild = new Rect(leftLines[1], rightLines[1], splittingLine, upperLine, orientationRight, orientationUp);
            isSplit = true;
        }
        return true;
    }

    @Override
    public Rect[] getChildren() {
        return new Rect[] {minChild, maxChild};
    }


}

