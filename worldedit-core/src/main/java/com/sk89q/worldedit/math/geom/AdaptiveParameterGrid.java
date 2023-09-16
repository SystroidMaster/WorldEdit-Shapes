package com.sk89q.worldedit.math.geom;

import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.shape.ArbitraryShape;
import com.sk89q.worldedit.world.block.ParametricBlockData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class AdaptiveParameterGrid {
    private Function<double[], ParametricBlockData> blockCalculator;
    private int numDims;
    private Orthotope root;
    private ArrayList<ParametricBlockData> blocks;

    public AdaptiveParameterGrid(Function<double[], ParametricBlockData> blockCalculator, Vector2[] parameterLimits) {
        this.blockCalculator = blockCalculator;
        this.numDims = parameterLimits.length;

        this.blocks = new ArrayList<ParametricBlockData>();

        switch (numDims) {
            case 1:
                CombinedPoint minPoint = new CombinedPoint(this, new double[]{parameterLimits[0].getX()});
                CombinedPoint maxPoint = new CombinedPoint(this, new double[]{parameterLimits[0].getZ()});
                root = new Line(minPoint,maxPoint,Axis.X);
                break;
            case 2:
                CombinedPoint leftLower = new CombinedPoint(this, new double[]{parameterLimits[0].getX(), parameterLimits[1].getX()});
                CombinedPoint leftUpper = new CombinedPoint(this, new double[]{parameterLimits[0].getX(), parameterLimits[1].getZ()});
                CombinedPoint rightLower = new CombinedPoint(this, new double[]{parameterLimits[0].getZ(), parameterLimits[1].getX()});
                CombinedPoint rightUpper = new CombinedPoint(this, new double[]{parameterLimits[0].getZ(), parameterLimits[1].getZ()});
                Line left = new Line(leftLower, leftUpper, Axis.Y);
                Line right = new Line(rightLower, rightUpper, Axis.Y);
                Line lower = new Line(leftLower, rightLower, Axis.X);
                Line upper = new Line(leftUpper, rightUpper, Axis.X);
                root = new Rect(left, right, lower, upper, Axis.X, Axis.Y);
                break;
            case 3:
                CombinedPoint[][][] points = new CombinedPoint[2][2][2];
                int[] indices = {0,1};
                for (int i : indices) {
                    for (int j : indices) {
                        for (int k : indices) {
                            double[] parameters = new double[3];
                            parameters[0] = (i==0 ? parameterLimits[0].getX() : parameterLimits[0].getZ());
                            parameters[1] = (j==0 ? parameterLimits[1].getX() : parameterLimits[1].getZ());
                            parameters[2] = (k==0 ? parameterLimits[2].getX() : parameterLimits[2].getZ());
                            points[i][j][k] = new CombinedPoint(this, parameters);
                        }
                    }
                }
                Line e0x0 = new Line(points[0][0][0], points[0][1][0],Axis.Y);
                Line e0x1 = new Line(points[0][0][1], points[0][1][1],Axis.Y);
                Line e1x0 = new Line(points[1][0][0], points[1][1][0],Axis.Y);
                Line e1x1 = new Line(points[1][0][1], points[1][1][1],Axis.Y);

                Line e00x = new Line(points[0][0][0], points[0][0][1],Axis.Z);
                Line e01x = new Line(points[0][1][0], points[0][1][1],Axis.Z);
                Line e10x = new Line(points[1][0][0], points[1][0][1],Axis.Z);
                Line e11x = new Line(points[1][1][0], points[1][1][1],Axis.Z);

                Line ex00 = new Line(points[0][0][0], points[1][0][0],Axis.X);
                Line ex01 = new Line(points[0][0][1], points[1][0][1],Axis.X);
                Line ex10 = new Line(points[0][1][0], points[1][1][0],Axis.X);
                Line ex11 = new Line(points[0][1][1], points[1][1][1],Axis.X);

                Rect f0xx = new Rect(e0x0, e0x1, e00x, e01x, Axis.Z,Axis.Y);
                Rect f1xx = new Rect(e1x0, e1x1, e10x, e11x, Axis.Z,Axis.Y);

                Rect fxx0 = new Rect(e0x0, e1x0, ex00, ex10, Axis.X,Axis.Y);
                Rect fxx1 = new Rect(e0x1, e1x1, ex01, ex11, Axis.X,Axis.Y);

                Rect fx0x = new Rect(e00x, e10x, ex00, ex01, Axis.X,Axis.Z);
                Rect fx1x = new Rect(e01x, e11x, ex10, ex11, Axis.X,Axis.Z);

                root = new RectangularCuboid(f0xx, f1xx, fxx0, fxx1, fx0x, fx1x);
                break;
        }
    }

    public Function<double[], ParametricBlockData> getBlockCalculator() {
        return blockCalculator;
    }

    public void addBlock(ParametricBlockData block) {
        blocks.add(block);
    }

    public ArrayList<ParametricBlockData> getBlocksAdaptive(int[] numMinSplittingLevels, int maxRecursionDepth) {
        adaptRecursively(root, numMinSplittingLevels, new int[numMinSplittingLevels.length], maxRecursionDepth, 0);
        return blocks;
    }

    private void adaptRecursively(Orthotope current, int[] numMinSplittingLevels, int[] currentSplittingLevels, int maxRecursionDepth, int currentRecursionDepth) {
        Axis splittingAxis = current.getSplittingAxis();

        // Determine whether current Orthotope should be subdivided
        // TODO: do this more efficiently / in a shorter way?
        boolean shouldSplit = false;
        if (((splittingAxis != Axis.NOAXIS) && (currentRecursionDepth<maxRecursionDepth))) {
            shouldSplit = true;
        } else if (splittingAxis == Axis.NOAXIS) {
            for (int i = 0; i<numDims; i++) {
                if (currentSplittingLevels[i] < numMinSplittingLevels[i]) {
                    splittingAxis = Axis.values()[i];
                    shouldSplit = true;
                    break;
                }
            }
        }

        if (shouldSplit) {
            // Perform the actual splitting
            current.split(splittingAxis, this);
            int[] newSplittingLevels = currentSplittingLevels.clone();
            newSplittingLevels[splittingAxis.ordinal()] += 1;
            for (Orthotope child : current.getChildren()) {
                adaptRecursively(child, numMinSplittingLevels, newSplittingLevels, maxRecursionDepth, currentRecursionDepth + 1);
                // System.out.println("In Recursion, depth " + String.valueOf(currentRecursionDepth) + ", splitting level " + String.valueOf(currentSplittingLevels[0]));
            }
        }
    }
}

