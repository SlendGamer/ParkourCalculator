package de.legoshi.parkourcalculator.simulation.environment.block_1_12;

import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.util.BlockColors;
import de.legoshi.parkourcalculator.util.ImageHelper;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Bed_1_12 extends ABlock {

    public Bed_1_12(Vec3 vec3) {
        super(vec3);
    }

    @Override
    public void updateColor() {
        setMaterialColor(BlockColors.BED.get());
        setSpecularColor(BlockColors.WOOD.get());
    }

    @Override
    public void updateBoundingBox() {
        Vec3 lowerEdge = new Vec3(0, 0, 0);
        Vec3 upperEdge = new Vec3(1, 0.5625, 1);
        Vec3 shift = new Vec3(0, 0.21875, 0);
        this.axisVecTuples.add(constructBlock(lowerEdge, upperEdge, shift));
    }

    @Override
    public void updateImage() {
        this.image = new ImageHelper().getImageFromURL("/images/bed.png");
    }

    @Override
    public void onLanded(Player player) {
        if (player.isSNEAK()) {
            super.onLanded(player);
        } else if (player.velocity.y < 0.0D) {
            player.velocity.y = -player.velocity.y * 0.6600000262260437D;
        }
    }

}
