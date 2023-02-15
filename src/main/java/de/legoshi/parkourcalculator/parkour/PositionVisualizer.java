package de.legoshi.parkourcalculator.parkour;

import de.legoshi.parkourcalculator.gui.DebugScreen;
import de.legoshi.parkourcalculator.parkour.simulator.MovementEngine;
import de.legoshi.parkourcalculator.parkour.simulator.PlayerTickInformation;
import de.legoshi.parkourcalculator.parkour.tick.InputTick;
import de.legoshi.parkourcalculator.parkour.tick.InputTickManager;
import de.legoshi.parkourcalculator.util.Vec3;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PositionVisualizer implements Observer {

    private final MovementEngine movementEngine;
    private final InputTickManager inputTickManager;
    private final Group group;

    public ArrayList<Sphere> spheres = new ArrayList<>();
    public ArrayList<Cylinder> lines = new ArrayList<>();

    private double lastX;
    private double lastY;
    private double lastZ;

    public PositionVisualizer(Group group, MovementEngine movementEngine, InputTickManager inputTickManager) {
        this.inputTickManager = inputTickManager;
        this.movementEngine = movementEngine;
        this.group = group;
    }

    public void generatePlayerPath() {
        ArrayList<PlayerTickInformation> playerTI = getUpdatedPlayerPos();
        ArrayList<Vec3> playerPos = new ArrayList<>();
        playerTI.forEach(pti -> playerPos.add(pti.getPosition()));

        group.getChildren().clear();

        spheres = new ArrayList<>();
        lines = new ArrayList<>();

        int posCounter = 0;
        for (Vec3 pos : playerPos) {
            Sphere sphere = new Sphere(0.03);
            sphere.setTranslateX(pos.x);
            sphere.setTranslateY(pos.y*-1);
            sphere.setTranslateZ(pos.z);
            spheres.add(sphere);
            int finalPosCounter = posCounter;
            sphere.setOnMouseClicked((event) -> onMouseClick(event, finalPosCounter));
            sphere.setOnMouseDragged((event) -> onMouseClick(event, finalPosCounter));
            group.getChildren().add(sphere);
            posCounter++;
        }

        for (int i = 0; i < playerPos.size() - 1; i++) {
            Point3D startPoint = new Point3D(playerPos.get(i).x, playerPos.get(i).y*-1, playerPos.get(i).z);
            Point3D endPoint = new Point3D(playerPos.get(i+1).x, playerPos.get(i+1).y*-1, playerPos.get(i+1).z);
            Cylinder cylinder = createCylinder(startPoint, endPoint);
            lines.add(cylinder);
            group.getChildren().add(cylinder);
        }
        group.setOnMouseClicked(this::onMouseReleaseClick);
        group.setOnMouseDragged(this::onMouseDrag);
        group.setOnMouseDragExited(this::onMouseReleaseClick);
    }

    public PlayerTickInformation calcLastTick() {
        ArrayList<InputTick> playerInputs = inputTickManager.getInputTicks();
        return movementEngine.getLastTick(playerInputs);
    }

    public ArrayList<PlayerTickInformation> getUpdatedPlayerPos() {
        ArrayList<InputTick> playerInputs = inputTickManager.getInputTicks();
        return movementEngine.updatePath(playerInputs);
    }

    private void onMouseClick(MouseEvent event, int tickPos) {
        if (!(event.getTarget() instanceof Sphere sphere)) return;
        PhongMaterial white = new PhongMaterial();
        white.setDiffuseColor(Color.WHITE);
        for (Sphere s : spheres) s.setMaterial(white);
        sphere.setMaterial(new PhongMaterial(Color.RED));
        for (Observer observer : inputTickManager.getObservers()) {
            if (observer instanceof DebugScreen) {
                ((DebugScreen) observer).updateTickClick(tickPos);
            }
        }
    }

    // move around the player path
    private void onMouseDrag(MouseEvent event) {
        if (lastX == 0 && lastZ == 0) {
            this.lastX = event.getScreenX();
            this.lastZ = event.getScreenY();
        }

        Vec3 updatedStartPos = movementEngine.player.getStartPos().copy();
        updatedStartPos.z = updatedStartPos.z + (event.getScreenX() - lastX)/100;
        updatedStartPos.x = updatedStartPos.x + (event.getScreenY() - lastZ)/100;
        movementEngine.player.setStartPos(updatedStartPos);

        System.out.println(updatedStartPos);

        this.lastX = event.getScreenX();
        this.lastZ = event.getScreenY();

        generatePlayerPath();
    }

    private void onMouseReleaseClick(MouseEvent event) {
        this.lastX = 0;
        this.lastZ = 0;
    }

    private Cylinder createCylinder(Point3D startP, Point3D endP) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = endP.subtract(startP);
        double height = diff.magnitude();

        Point3D mid = endP.midpoint(startP);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01, height);
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
        return line;
    }

    @Override
    public void update(Observable o, Object arg) {
        generatePlayerPath();
    }
}
