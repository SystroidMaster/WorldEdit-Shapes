package com.sk89q.worldedit.math.geom;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.shape.ArbitraryShape;
import com.sk89q.worldedit.world.block.ParametricBlockData;

import java.util.List;
import java.util.function.Function;

public class CombinedPoint {
    private final ParametricBlockData data;

    public CombinedPoint(AdaptiveParameterGrid grid, double[] parameters) {
        this.data = grid.getBlockCalculator().apply(parameters);
        // register calculated ParametricBlockData with the AdaptiveParameterGrid
        grid.addBlock(data);
    }
    public ParametricBlockData getBlockData() {
        return data;
    }
}

