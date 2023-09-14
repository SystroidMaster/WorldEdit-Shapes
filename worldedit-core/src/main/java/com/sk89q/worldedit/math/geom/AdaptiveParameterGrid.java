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
                Line left = new Line(leftLower, leftUpper, Axis.Z);
                Line right = new Line(rightLower, rightUpper, Axis.Z);
                Line lower = new Line(leftLower, rightLower, Axis.X);
                Line upper = new Line(leftUpper, rightUpper, Axis.X);
                root = new Rect(left, right, lower, upper, Axis.X, Axis.Z);
                break;
            case 3:
                root = new RectangularCuboid();
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

