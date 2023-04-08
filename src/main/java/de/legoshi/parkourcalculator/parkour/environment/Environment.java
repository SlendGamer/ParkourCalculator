package de.legoshi.parkourcalculator.parkour.environment;

import de.legoshi.parkourcalculator.parkour.environment.blocks.*;
import de.legoshi.parkourcalculator.parkour.environment.blocks.ABlock;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Environment implements Observer {

    public static ABlock currentBlock = new StandardBlock();
    public static ArrayList<ABlock> registeredBlocks = new ArrayList<>();
    public static ArrayList<ABlock> aBlocks = new ArrayList<>();

    public Environment() {
        registeredBlocks.add(new StandardBlock());
        registeredBlocks.add(new Enderchest());
        registeredBlocks.add(new Pane());
        registeredBlocks.add(new Cake());
        registeredBlocks.add(new Stair());
        registeredBlocks.add(new Ladder());
        registeredBlocks.add(new Vine());
    }

    public static void updateCurrentBlock(ABlock aBlock) {
        currentBlock = aBlock;
    }

    public void addBlock(ABlock block) {
        aBlocks.add(block);
    }

    public void removeBlock(ABlock block) {
        aBlocks.remove(block);
    }

    public ArrayList<AxisAlignedBB> getAllBBs() {
        ArrayList<AxisAlignedBB> boundingBoxes = new ArrayList<>();
        for (ABlock aBlock : aBlocks) {
            for (AxisVecTuple axisVecTuple : aBlock.axisVecTuples) {
                boundingBoxes.add(axisVecTuple.getBb());
            }
        }
        return boundingBoxes;
    }

    public static ABlock getBlock(double x, double y, double z) {
        for (ABlock aBlock : aBlocks) {
            if (aBlock.getVec3().x == x && aBlock.getVec3().y == y && aBlock.getVec3().z == z) {
                return aBlock;
            }
        }
        return new Air();
    }

    @Override
    public void update(Observable o, Object arg) {
        ArrayList<Object> objects = (ArrayList<Object>) arg;
        if (objects.get(0).equals("add")) addBlock((ABlock) objects.get(1));
        else removeBlock((ABlock) objects.get(1));
    }
}
