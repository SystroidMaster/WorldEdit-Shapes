package com.sk89q.worldedit.math.geom;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.shape.ArbitraryShape;
import com.sk89q.worldedit.world.block.SafeBlockData;

import java.util.ArrayList;
import java.util.List;


public class AdaptiveParameterGrid {
    private ArbitraryShape shape;
    private int numDims;
    private List<Vector2> parameterLimits;
    private Orthotope root;
    private ArrayList<SafeBlockData> blocks;

    public AdaptiveParameterGrid(ArbitraryShape shape, List<Vector2> parameterLimits, List<Integer> numStartSplittingLevels) {
        this.shape = shape;
        this.numDims = parameterLimits.size();
        this.parameterLimits = parameterLimits;

        this.blocks = new ArrayList<SafeBlockData>();

        switch (numDims) {
            case 1:
                CombinedPoint minPoint = new CombinedPoint(shape,List.of(parameterLimits.get(0).getX()));
                CombinedPoint maxPoint = new CombinedPoint(shape,List.of(parameterLimits.get(0).getZ()));
                root = new Line(minPoint,maxPoint,Axis.X);
                break;
            case 2:
                root = new Rect();
                break;
            case 3:
                root = new RectangularCuboid();
                break;
        }
        // TODO: build starting grid
    }

    public ArrayList<SafeBlockData> getBlocksAdaptive() {


        return blocks;
    }

    private void adaptRecursively(Orthotope current) {

    }
}

