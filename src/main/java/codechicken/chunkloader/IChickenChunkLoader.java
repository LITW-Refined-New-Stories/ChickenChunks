package codechicken.chunkloader;

import java.util.Collection;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import codechicken.lib.vec.BlockCoord;

public abstract interface IChickenChunkLoader {

    public String getOwner();

    public Object getMod();

    public World getWorld();

    public BlockCoord getPosition();

    public void deactivate();

    public void activate();

    public Collection<ChunkCoordIntPair> getChunks();
}
