package de.legoshi.parkourcalculator.ai;

import de.legoshi.parkourcalculator.Application;
import de.legoshi.parkourcalculator.simulation.environment.block.ABlock;
import de.legoshi.parkourcalculator.simulation.environment.block.Air;
import de.legoshi.parkourcalculator.simulation.environment.blockmanager.BlockManager;
import de.legoshi.parkourcalculator.simulation.movement.Movement;
import de.legoshi.parkourcalculator.simulation.tick.InputTick;
import de.legoshi.parkourcalculator.simulation.tick.PlayerTickInformation;
import de.legoshi.parkourcalculator.util.AxisAlignedBB;
import de.legoshi.parkourcalculator.util.AxisVecTuple;
import de.legoshi.parkourcalculator.util.Vec3;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Bruteforcer implements Runnable {

    @Getter private ConcurrentHashMap<Vec3, List<InputTick>> ticksMap = new ConcurrentHashMap<>();
    @Getter private List<InputTick> currentFastestSolution = new ArrayList<>();
    @Setter private List<Vec3> boundaries;

    private BruteforceOptions bruteforceOptions;
    private InputGenerator inputGenerator;
    @Getter private boolean isActive;
    private long startTime;

    private int iterationCount;
    private long iterationTimeStart;

    private final MultiThreadBruteforcer instance;
    private final BlockManager blockManager;
    private final Movement movement;
    private final ABlock endBlock;

    public Bruteforcer(Application application, MultiThreadBruteforcer multiThreadBruteforcer, ABlock endBlock) {
        this.instance = multiThreadBruteforcer;
        this.movement = application.currentParkour.getMovement().clone();
        this.blockManager = application.currentParkour.getBlockManager();
        this.endBlock = endBlock;
    }

    public void addBruteforceSettings(BruteforceOptions bruteforceOptions) {
        this.bruteforceOptions = bruteforceOptions;
    }

    public void addInputGenerator(InputGenerator inputGenerator) {
        this.inputGenerator = inputGenerator;
    }

    @Override
    public void run() {
        this.clearBruteforce();
        this.findPath();
    }

    private void clearBruteforce() {
        this.iterationCount = 0;
        this.iterationTimeStart = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
        this.ticksMap.clear();
        this.currentFastestSolution.clear();
    }

    public void syncMap(ConcurrentHashMap<Vec3, List<InputTick>> map) {
        this.ticksMap = new ConcurrentHashMap<>(map);
    }

    private void findPath() {
        isActive = true;

        outer: for (int k = 0; k < bruteforceOptions.getRepetitions(); k++) {
            List<InputTick> nthEntry = getNthElement();
            for (int i = 0; i < bruteforceOptions.getNumberOfTrials(); i++) {
                if (!isActive) {
                    break outer;
                }

                List<InputTick> inputTicks = new ArrayList<>();
                int startIndex = 0;
                if (nthEntry != null) {
                    inputTicks = new ArrayList<>(nthEntry);
                    startIndex = inputTicks.size();
                }

                for (int j = 0; j < bruteforceOptions.getTicksPerTrial(); j++) {
                    inputTicks.add(inputGenerator.getNextTick());
                }

                saveInputs(boundaries, inputTicks, startIndex);
                if (bruteforceOptions.isStopOnFind() && !currentFastestSolution.isEmpty()) {
                    break outer;
                }
            }

            if (iterationTimeStart + bruteforceOptions.getIntervalDuration()*1000 < System.currentTimeMillis()) {
                iterationTimeStart = System.currentTimeMillis();
                iterationCount++;
            }
        }

        isActive = false;
    }

    // TODO: exceptions in threads
    private void saveInputs(List<Vec3> boundaries, List<InputTick> inputTicks, int startIndex) {
        List<PlayerTickInformation> playerInfo = movement.updatePath(inputTicks);
        int count = startIndex == 0 ? 0 : startIndex - 1;

        for (; count < playerInfo.size(); count++) {
            PlayerTickInformation information = playerInfo.get(count);
            Vec3 unRoundedVec = information.getPosition().copy();
            Vec3 roundedVec = getRoundedVec(unRoundedVec);

            if (!boundaries.isEmpty()) {
                if (!isInsideBoundaries(boundaries, unRoundedVec)) {
                    continue;
                }
            }

            if (!ticksMap.containsKey(roundedVec)) {
                List<InputTick> temp = new ArrayList<>(inputTicks);
                ticksMap.put(roundedVec, temp.subList(0, count));
            } else {
                List<InputTick> savedInputs = ticksMap.get(roundedVec);
                if (savedInputs.size() > count) {
                    ticksMap.put(roundedVec, inputTicks.subList(0, count));
                } else {
                    count++;
                    continue;
                }
            }

            Vec3 possibleLBPos = roundedVec.copy();
            possibleLBPos.y--;
            ABlock possibleLB = blockManager.getBlock(possibleLBPos);

            if (!(possibleLB instanceof Air) && possibleLB.getVec3().equals(endBlock.getVec3())) {
                List<InputTick> shrinkList = new ArrayList<>(inputTicks.subList(0, count));
                if ((currentFastestSolution.isEmpty() || shrinkList.size() < currentFastestSolution.size())) {
                    instance.mergeFastestSolution(shrinkList);
                    System.out.println("New Solution with " + currentFastestSolution.size() + " ticks. Found in: "
                            + ((System.currentTimeMillis() - startTime) / 1000) + "s");
                }
            }

            count++;
        }
    }

    public Vec3 getRoundedVec(Vec3 unRoundedVec) {
        double DIMENSION = bruteforceOptions.getDimension();
        double roundedX = Math.floor(unRoundedVec.x / DIMENSION) * DIMENSION;
        double roundedY = Math.floor(unRoundedVec.y / DIMENSION) * DIMENSION;
        double roundedZ = Math.floor(unRoundedVec.z / DIMENSION) * DIMENSION;

        return new Vec3(roundedX, roundedY, roundedZ);
    }

    public void cancelBruteforce() {
        if (isActive) {
            isActive = false;
        }
    }

    public boolean isInsideBoundaries(List<Vec3> boundaries, Vec3 vec) {
        for (Vec3 boundary : boundaries) {
            ABlock landBlock = blockManager.getBlock(boundary);

            for (AxisVecTuple tuple : landBlock.getAxisVecTuples()) {
                AxisAlignedBB bbBlock = tuple.getBb();
                if (vec.x >= bbBlock.minX - 0.3 && vec.x < bbBlock.maxX + 0.3 &&
                        vec.y >= bbBlock.minY && vec.y < bbBlock.maxY + 1.5 &&
                        vec.z >= bbBlock.minZ - 0.3 && vec.z < bbBlock.maxZ + 0.3) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<InputTick> getNthElement() {
        int lowestBound;
        int highestBound;

        if (bruteforceOptions.isWindowed()) {
            lowestBound = iterationCount * bruteforceOptions.getGenerateInterval();
            highestBound = lowestBound + bruteforceOptions.getGenerateInterval();
            if (iterationCount > 0) lowestBound = lowestBound - bruteforceOptions.getOverlap();

            if (!currentFastestSolution.isEmpty()) {
                lowestBound = Math.max(0, currentFastestSolution.size() - bruteforceOptions.getGenerateInterval());
                highestBound = currentFastestSolution.size();
            }
        } else {
            highestBound = 0;
            for (List<InputTick> value : ticksMap.values()) {
                if (value.size() > highestBound) {
                    highestBound = value.size();
                }
            }
            lowestBound = highestBound - Math.min(highestBound, bruteforceOptions.getRecTicks());
        }

        List<InputTick> selectedValue = null;
        Random random = new Random();

        List<List<InputTick>> eligibleValues = new ArrayList<>();
        for (List<InputTick> value : ticksMap.values()) {
            if (isWithinBounds(value.size(), lowestBound, highestBound)) {
                eligibleValues.add(value);
            }
        }

        if (!eligibleValues.isEmpty()) {
            int randomIndex = random.nextInt(eligibleValues.size());
            selectedValue = eligibleValues.get(randomIndex);
        }

        if (selectedValue == null) {
            int highestValue = 0;
            for (List<InputTick> value : ticksMap.values()) {
                if (value.size() > highestValue) {
                    highestValue = value.size();
                }
            }
            if (highestValue <= lowestBound && iterationCount > 0) {
                iterationTimeStart = System.currentTimeMillis();
                iterationCount--;
            }
        }

        return selectedValue;
    }

    private boolean isWithinBounds(int size, int lowestBound, int highestBound) {
        return size >= lowestBound && size <= highestBound;
    }

    public void setCurrentFastestSolution(List<InputTick> solution) {
        this.currentFastestSolution = new ArrayList<>(solution);
    }

}
