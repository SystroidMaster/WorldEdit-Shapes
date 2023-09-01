package com.sk89q.worldedit.world.block;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.block.BaseBlock;

public class SafeBlockData {
    private Vector3 position;
    private BaseBlock material;

    public SafeBlockData(Vector3 position, BaseBlock material) {
        this.position = position;
        this.material = material;
    }

    public Vector3 getPosition() {
        return position;
    }

    public BaseBlock getMaterial() {
        return material;
    }
}