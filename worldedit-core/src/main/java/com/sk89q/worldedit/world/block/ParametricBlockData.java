package com.sk89q.worldedit.world.block;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;

public class ParametricBlockData {
    private double[] parameters;
    private Vector3 worldcoords;
    private BlockVector3 position;
    private BaseBlock material;

    public ParametricBlockData(double[] parameters, Vector3 worldcoords, BlockVector3 position, BaseBlock material) {
        this.parameters = parameters;
        this.worldcoords = worldcoords;
        this.position = position;
        this.material = material;
    }

    public double[] getParameters() {
        return parameters;
    }
    public Vector3 getWorldcoords() {
        return worldcoords;
    }
    public BlockVector3 getPosition() {
        return position;
    }
    public BaseBlock getMaterial() {
        return material;
    }
}