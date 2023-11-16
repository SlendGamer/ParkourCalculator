package de.legoshi.parkourcalculator.simulation.environment.blockmanager;

import de.legoshi.parkourcalculator.simulation.environment.block.*;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.scene.shape.Box;

import java.util.*;

public abstract class BlockManager implements Observer, Cloneable {

    private static final int OFFSET = 250; // Adjust this based on the expected range of x, y, z
    private static final int SIZE = 500; // This should be at least 2 * OFFSET

    public ABlock[][][] blocks = new ABlock[SIZE][SIZE][SIZE];
    public HashMap<Box, ABlock> boxBlocks = new HashMap<>();

    public ABlock currentBlock = new StandardBlock();
    public List<ABlock> registeredBlocks = new ArrayList<>();
    public List<ABlock> allBlocks = new ArrayList<>();

    // other methods and fields remain unchanged

    private int toInternalIndex(int coordinate) {
        return coordinate + OFFSET;
    }

    public ABlock getBlock(Vec3 vec3) {
        return getBlock((int) vec3.x, (int) vec3.y, (int) vec3.z);
    }

    public ABlock getBlock(int x, int y, int z) {
        try {
            ABlock block = blocks[toInternalIndex(x)][toInternalIndex(y)][toInternalIndex(z)];
            return block == null ? Air.getInstance() : block;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null; // or handle differently
        }
    }

    public void addBlock(ABlock block) {
        int x = toInternalIndex((int) block.getVec3().x);
        int y = toInternalIndex((int) block.getVec3().y);
        int z = toInternalIndex((int) block.getVec3().z);

        try {
            blocks[x][y][z] = block;
        } catch (ArrayIndexOutOfBoundsException e) {
            // handle the case where the block is outside the array bounds
        }

        allBlocks.add(block);
        block.getBoxesArrayList().forEach(box -> boxBlocks.put(box, block));
    }

    public void removeBlock(ABlock block) {
        int x = toInternalIndex((int) block.getVec3().x);
        int y = toInternalIndex((int) block.getVec3().y);
        int z = toInternalIndex((int) block.getVec3().z);

        try {
            blocks[x][y][z] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            // handle the case where the block is outside the array bounds
        }

        block.getBoxesArrayList().forEach(box -> boxBlocks.remove(box));
        allBlocks.remove(block);
    }
    
    public List<ABlock> getAllBlocks() {
        return allBlocks;
    }
    
    public ABlock getBlockFromBox(Box box) {
        return boxBlocks.get(box);
    }

    @Override
    public void update(Observable o, Object arg) {
        List<Object> objects = (List<Object>) arg;
        if (objects.get(0).equals("add")) {
            addBlock((ABlock) objects.get(1));
        } else {
            removeBlock((ABlock) objects.get(1));
        }
    }

    public void updateCurrentBlock(ABlock aBlock) {
        currentBlock = aBlock;
    }

    public void clear() {
        allBlocks.clear();
        boxBlocks.clear();
        blocks = new ABlock[SIZE][SIZE][SIZE];
    }

    @Override
    public BlockManager clone() {
        try {
            return (BlockManager) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
