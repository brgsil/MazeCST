/** ***************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 **************************************************************************** */
package codelets.behaviors;

import WS3DCoppelia.model.Thing;
import java.awt.Point;
import java.awt.geom.Point2D;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.ArrayList;
import java.util.List;

public class GoToClosestFood extends Codelet {

    private Memory closestAppleMO;
    private Memory selfInfoMO;
    private MemoryContainer legsMO;
    private Memory mapMO;
    private double creatureBasicSpeed;
    private float reachDistance;
    private double minFuel;
    private float wallSize = 0.5f;

    public GoToClosestFood(double creatureBasicSpeed, float reachDistance, double minFuel) {
        this.creatureBasicSpeed = creatureBasicSpeed;
        this.reachDistance = reachDistance;
        this.minFuel = minFuel;
        this.name = "GoToClosestFood";
    }

    @Override
    public void accessMemoryObjects() {
        closestAppleMO = (MemoryObject) this.getInput("CLOSEST_FOOD");
        selfInfoMO = (MemoryObject) this.getInput("INNER");
        legsMO = (MemoryContainer) this.getOutput("LEGS");
        mapMO = (MemoryObject) this.getInput("MAP");
    }

    @Override
    public void proc() {
        // Find distance between creature and closest apple
        //If far, go towards it
        //If close, stops

        Thing closestApple = (Thing) closestAppleMO.getI();
        Idea cis = (Idea) selfInfoMO.getI();
        int[][] map = (int[][]) mapMO.getI();

        List<Object> message = new ArrayList<Object>();
        if (closestApple != null && ((float) cis.get("fuel").getValue()) < minFuel) {
            double appleX = 0;
            double appleY = 0;
            try {
                appleX = closestApple.getPos().get(0);
                appleY = closestApple.getPos().get(1);

            } catch (Exception e) {
                e.printStackTrace();
            }

            double selfX = ((List<Float>) cis.get("position").getValue()).get(0);
            double selfY = ((List<Float>) cis.get("position").getValue()).get(1);

            Point2D pApple = new Point();
            pApple.setLocation(appleX, appleY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pApple);
            if (distance > reachDistance) { //Go to it
                int j = (int) Math.floor((appleX) / wallSize);
                int i = (int) Math.floor((appleY) / wallSize);

                Grid grid = new Grid(map, i, j);
                grid.route();

                message.add("GOTO");
                message.add((float) grid.miny * wallSize);
                message.add((float) grid.minx * wallSize);
                activation = 0.9;

            } else {//Stop
                message.add("GOTO");
                message.add((float) appleX);
                message.add((float) appleY);
                activation = 0.5;
            }
            legsMO.setI(message, activation, name);

        } else {
            activation = 0.0;
            message.add("");
            legsMO.setI(message, activation, name);
        }

    }//end proc

    @Override
    public void calculateActivation() {

    }

}
