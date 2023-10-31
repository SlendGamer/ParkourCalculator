package de.legoshi.parkourcalculator.simulation;

import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager_1_8;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.movement.Movement_1_8;
import de.legoshi.parkourcalculator.simulation.player.Player;
import de.legoshi.parkourcalculator.simulation.player.Player_1_8;

public class Parkour_1_8 extends Parkour {

    public Parkour_1_8() {
        this.player = new Player_1_8(DEFAULT_START, DEFAULT_VELOCITY, START_YAW);
        this.blockManager = new BlockManager_1_8();
        this.movement = new Movement_1_8(player, blockManager);
    }
    
    public Parkour_1_8(Player player, BlockManager blockManager, Movement movement) {
        this.player = player;
        this.blockManager = blockManager;
        this.movement = movement;
    }
    
    @Override
    public Parkour clone() {
        Player_1_8 clonePlayer = (Player_1_8) player.clone();
        BlockManager_1_8 cloneBlockManager = (BlockManager_1_8) blockManager.clone();
        Movement_1_8 cloneMovement = new Movement_1_8(clonePlayer, cloneBlockManager);
        return new Parkour_1_8(clonePlayer, cloneBlockManager, cloneMovement);
    }
    
}
