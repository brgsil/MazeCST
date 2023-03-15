/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.behaviors;

import WS3DCoppelia.model.Thing;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ia941
 */
public class GoToClosestJewel extends Codelet {

    private Memory closestJewelMO;
    private Memory selfInfoMO;
    private Memory leafletsMO;
    private Memory mapMO;
    private MemoryContainer legsMO;
    private double creatureBasicSpeed;
    private float reachDistance;
    private double minFuel;
    private float wallSize = 0.5f;

    public GoToClosestJewel(double creatureBasicSpeed, float reachDistance, double minFuel) {
        this.creatureBasicSpeed = creatureBasicSpeed;
        this.reachDistance = reachDistance;
        this.name = "GoToClosestJewel";
        this.minFuel = minFuel;
    }

    @Override
    public void accessMemoryObjects() {
        closestJewelMO = (MemoryObject) this.getInput("CLOSEST_LEAFLET_JEWEL");
        selfInfoMO = (MemoryObject) this.getInput("INNER");
        leafletsMO = (MemoryObject) this.getInput("LEAFLETS");
        legsMO = (MemoryContainer) this.getOutput("LEGS");
        mapMO = (MemoryObject) this.getInput("MAP");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        Thing closestJewel = (Thing) closestJewelMO.getI();
        Idea cis = (Idea) selfInfoMO.getI();
        Idea leaflets = (Idea) leafletsMO.getI();
        int[][] map = (int[][]) mapMO.getI();

        List<Object> message = new ArrayList<Object>();
        if (closestJewel != null && ((float) cis.get("fuel").getValue()) >= minFuel) {

            double jewelX = 0;
            double jewelY = 0;
            try {
                jewelX = closestJewel.getPos().get(0);
                jewelY = closestJewel.getPos().get(1);

            } catch (Exception e) {
                e.printStackTrace();
            }

            double selfX = ((List<Float>) cis.get("position").getValue()).get(0);
            double selfY = ((List<Float>) cis.get("position").getValue()).get(1);

            Point2D pJewel = new Point();
            pJewel.setLocation(jewelX, jewelY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pJewel);
            if (distance > reachDistance) { //Go to it
                int j = (int) Math.floor((jewelX) / wallSize);
                int i = (int) Math.floor((jewelY) / wallSize);

                Grid grid = new Grid(map, i, j);
                grid.route();

                message.add("GOTO");
                message.add((float) grid.miny * wallSize);
                message.add((float) grid.minx * wallSize);
                activation = 0.9;

            } else {//Stop
                message.add("GOTO");
                message.add((float) jewelX);
                message.add((float) jewelY);
                activation = 0.5;
            }
            legsMO.setI(message, activation, name);
            
        } else {
            activation = 0.0;
            message.add("");
            legsMO.setI(message, activation, name);
        }
    }

}
