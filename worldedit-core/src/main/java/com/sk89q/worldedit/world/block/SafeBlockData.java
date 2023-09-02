package com.sk89q.worldedit.world.block;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;

public class SafeBlockData {
    private BlockVector3 position;
    private BaseBlock material;

    public SafeBlockData(BlockVector3 position, BaseBlock material) {
        this.position = position;
        this.material = material;
    }

    public BlockVector3 getPosition() {
        return position;
    }

    public BaseBlock getMaterial() {
        return material;
    }
}