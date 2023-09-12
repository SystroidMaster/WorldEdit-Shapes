package com.sk89q.worldedit.math.geom;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.shape.ArbitraryShape;

import java.util.List;

public class CombinedPoint {
    private ArbitraryShape shape;
    private List<Double> parameters;
    private Vector3 worldcoords;
    private BlockVector3 block;

    public CombinedPoint(ArbitraryShape shape, List<Double> parameters) {
        // TODO: calculate values on initialization
    }
}

