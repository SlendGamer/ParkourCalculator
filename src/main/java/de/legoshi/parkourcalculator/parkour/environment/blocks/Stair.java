package de.legoshi.parkourcalculator.parkour.environment.blocks;

import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Stair extends FacingBlock {

    private AxisVecTuple baseStair;

    public Stair(Vec3 vec3) {
        super(vec3);
    }

    @Override
    protected void updateBoundingBox() {
        super.updateBoundingBox();
        this.axisVecTuples.add(baseStair);
        calcBaseStair();
    }

    private void calcBaseStair() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0);
        upperEdge.addVector(1, 0.5, 1);

        Vec3 shift = new Vec3(0, 0.25, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.baseStair = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0);
        upperEdge.addVector(1, 1.0, 0.5);
        Vec3 shift = new Vec3(0, 0, 0.25);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.north = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0.5, 0, 0);
        upperEdge.addVector(1, 1.0, 1);
        Vec3 shift = new Vec3(0.25, 0, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.east = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0.5);
        upperEdge.addVector(1, 1.0, 1);
        Vec3 shift = new Vec3(0, 0, 0.25);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.south = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = getVec3().copy();
        Vec3 upperEdge = getVec3().copy();
        lowerEdge.addVector(0, 0, 0);
        upperEdge.addVector(0.5, 1.0, 1);
        Vec3 shift = new Vec3(0.25, 0, 0);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(lowerEdge, upperEdge);
        this.west = new AxisVecTuple(axisAlignedBB, shift);
    }

    @Override
    void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/stair.png");
    }
}
