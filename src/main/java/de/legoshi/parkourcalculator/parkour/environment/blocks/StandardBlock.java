package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class StandardBlock extends ABlock {

    public StandardBlock(Vec3 vec3) {
        super(vec3);
    }

    @Override
    void updateBoundingBox() {
        Vec3 lowerEdge = getVec3();
        Vec3 upperEdge = getVec3().copy();
        upperEdge.addVector(1.0, 1.0, 1.0);

        Vec3 shift = new Vec3(0, 0, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        AxisVecTuple axisVecTuple = new AxisVecTuple(axisAlignedBB, shift);
        this.axisVecTuples = new ArrayList<>();
        this.axisVecTuples.add(axisVecTuple);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/grass_block.png");
    }

}
