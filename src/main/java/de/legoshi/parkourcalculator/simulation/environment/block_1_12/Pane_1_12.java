package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.gui.debug.menu.BlockSettings;
import de.legoshi.parkourcalculator.simulation.environment.block.FacingBlock;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class Pane_1_12 extends FacingBlock {

    public Pane_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.PANE.get());
        setSpecularColor(BlockColors.IRON_SPEC.get());
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/glass_pane.png");
    }

    @Override
    public void updateBoundingBox() {
        this.axisVecTuples = new ArrayList<>();

        calcBase();

        if (BlockSettings.isNorth()) calcNorth();
        if (BlockSettings.isEast()) calcEast();
        if (BlockSettings.isSouth()) calcSouth();
        if (BlockSettings.isWest()) calcWest();
    }

    @Override
    protected void calcNorth() {
        Vec3 lowerEdge = new Vec3(0.4375, 0, 0);
        Vec3 upperEdge = new Vec3(0.5625, 1, 0.5);
        Vec3 shift = new Vec3(0.4375, 0, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcWest() {
        Vec3 lowerEdge = new Vec3(0.5, 0, 0.4375);
        Vec3 upperEdge = new Vec3(1, 1, 0.5625);
        Vec3 shift = new Vec3(0.25, 0, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcSouth() {
        Vec3 lowerEdge = new Vec3(0.4375, 0, 0.5);
        Vec3 upperEdge = new Vec3(0.5625, 1, 1);
        Vec3 shift = new Vec3(0.4375, 0, 0.25);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcEast() {
        Vec3 lowerEdge = new Vec3(0, 0, 0.4375);
        Vec3 upperEdge = new Vec3(0.5, 1, 0.5625);
        Vec3 shift = new Vec3(0.25, 0, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBase() {
        Vec3 lowerEdge = new Vec3(0.4375, 0, 0.4375);
        Vec3 upperEdge = new Vec3(0.5625, 1, 0.5625);
        Vec3 shift = new Vec3(0.4375, 0, 0.4375);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    protected void calcBaseFlip() {

    }

}
