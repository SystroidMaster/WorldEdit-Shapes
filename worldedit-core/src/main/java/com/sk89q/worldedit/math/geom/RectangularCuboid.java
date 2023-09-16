package com.sk89q.worldedit.math.geom;

import java.util.Collections;
import java.util.List;

public class RectangularCuboid implements Orthotope {
    private Rect left, right, front, back, bottom, top;
    private boolean isSplit;
    private RectangularCuboid minChild, maxChild;

    public RectangularCuboid(Rect left, Rect right, Rect front, Rect back, Rect bottom, Rect top) {
        this.left = left;
        this.right = right;
        this.front = front;
        this.back = back;
        this.bottom = bottom;
        this.top = top;

        this.isSplit = false;
    }
    @Override
    public Axis getSplittingAxis() {
        double xMax = Collections.max(List.of(front.getLowerLine().length(), front.getUpperLine().length(), back.getLowerLine().length(), back.getUpperLine().length()));
        double zMax = Collections.max(List.of(left.getLowerLine().length(), left.getUpperLine().length(), right.getLowerLine().length(), right.getUpperLine().length()));
        double yMax = Collections.max(List.of(left.getLeftLine().length(), left.getRightLine().length(), right.getLeftLine().length(), right.getRightLine().length()));
        // TODO: adjust constant value to compare against or the whole algorithm to determine whether RectangularCuboid is small enough
        // TODO: use Map instead of List?
        List<Double> extents = List.of(xMax, yMax, zMax);
        double maximumExtent = Collections.max(extents);
        if (maximumExtent > 0.5) {
            return switch (extents.indexOf(maximumExtent)){
                case 0: yield Axis.X;
                case 1: yield Axis.Y;
                case 2: yield Axis.Z;
                default: yield Axis.NOAXIS;
            };
        }
        return Axis.NOAXIS;
    }

    @Override
    public boolean isSplit() {
        return isSplit;
    }

    @Override
    public boolean split(Axis axis, AdaptiveParameterGrid grid) {
        if (isSplit || axis==Axis.NOAXIS) {
            return false;
        } else {
            switch (axis) {
                case X -> {
                    front.split(Axis.X, grid);
                    back.split(Axis.X, grid);
                    bottom.split(Axis.X, grid);
                    top.split(Axis.X, grid);
                    Rect[] frontFaces = front.getChildren();
                    Rect[] backFaces = back.getChildren();
                    Rect[] bottomFaces = bottom.getChildren();
                    Rect[] topFaces = top.getChildren();
                    Rect splittingFace = new Rect(frontFaces[0].getRightLine(), backFaces[0].getRightLine(), bottomFaces[0].getRightLine(), topFaces[0].getRightLine(), Axis.Z, Axis.Y);
                    minChild = new RectangularCuboid(left, splittingFace, frontFaces[0], backFaces[0], bottomFaces[0], topFaces[0]);
                    maxChild = new RectangularCuboid(splittingFace, right, frontFaces[1], backFaces[1], bottomFaces[1], topFaces[1]);
                    isSplit = true;
                }
                case Z -> {
                    left.split(Axis.Z, grid);
                    right.split(Axis.Z, grid);
                    bottom.split(Axis.Z, grid);
                    top.split(Axis.Z, grid);
                    Rect[] leftFaces = left.getChildren();
                    Rect[] rightFaces = right.getChildren();
                    Rect[] bottomFaces = bottom.getChildren();
                    Rect[] topFaces = top.getChildren();
                    Rect splittingFace = new Rect(leftFaces[0].getRightLine(), rightFaces[0].getRightLine(), bottomFaces[0].getUpperLine(), topFaces[0].getUpperLine(), Axis.X, Axis.Y);
                    minChild = new RectangularCuboid(leftFaces[0], rightFaces[0], front, splittingFace, bottomFaces[0], topFaces[0]);
                    maxChild = new RectangularCuboid(leftFaces[1], rightFaces[1], splittingFace, back, bottomFaces[1], topFaces[1]);
                    isSplit = true;
                }
                case Y -> {
                    left.split(Axis.Y, grid);
                    right.split(Axis.Y, grid);
                    front.split(Axis.Y, grid);
                    back.split(Axis.Y, grid);
                    Rect[] leftFaces = left.getChildren();
                    Rect[] rightFaces = right.getChildren();
                    Rect[] frontFaces = front.getChildren();
                    Rect[] backFaces = back.getChildren();
                    Rect splittingFace = new Rect(leftFaces[0].getUpperLine(), rightFaces[0].getUpperLine(), frontFaces[0].getUpperLine(), backFaces[0].getUpperLine(), Axis.X, Axis.Z);
                    minChild = new RectangularCuboid(leftFaces[0], rightFaces[0], frontFaces[0], backFaces[0], bottom, splittingFace);
                    maxChild = new RectangularCuboid(leftFaces[1], rightFaces[1], frontFaces[1], backFaces[1], splittingFace, top);
                    isSplit = true;
                }
            }
        }
        return true;
    }

    @Override
    public RectangularCuboid[] getChildren() {
        return new RectangularCuboid[] {minChild, maxChild};
    }
}
