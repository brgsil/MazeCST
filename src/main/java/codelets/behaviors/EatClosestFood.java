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
import java.util.concurrent.CopyOnWriteArrayList;

public class EatClosestFood extends Codelet {

    private Memory closestAppleMO;
    private Memory innerSenseMO;
    private Memory knownMO;
    private float reachDistance;
    private MemoryContainer handsMO;
    Thing closestApple;
    Idea cis;
    List<Thing> known;

    public EatClosestFood(float reachDistance) {
        setTimeStep(50);
        this.reachDistance = reachDistance;
        this.name = "EatClosestApple";
    }

    @Override
    public void accessMemoryObjects() {
        closestAppleMO = (MemoryObject) this.getInput("CLOSEST_FOOD");
        innerSenseMO = (MemoryObject) this.getInput("INNER");
        handsMO = (MemoryContainer) this.getOutput("HANDS");
        knownMO = (MemoryObject) this.getOutput("KNOWN_FOODS");
    }

    @Override
    public void proc() {
        String appleName = "";
        closestApple = (Thing) closestAppleMO.getI();
        cis = (Idea) innerSenseMO.getI();
        known = (List<Thing>) knownMO.getI();
        //Find distance between closest apple and self
        //If closer than reachDistance, eat the apple

        if (closestApple != null) {
            double appleX = 0;
            double appleY = 0;
            try {
                appleX = closestApple.getPos().get(0);
                appleY = closestApple.getPos().get(1);
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            double selfX = ((List<Float>) cis.get("position").getValue()).get(0);
            double selfY = ((List<Float>) cis.get("position").getValue()).get(1);

            Point2D pApple = new Point();
            pApple.setLocation(appleX, appleY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pApple);
            List<Object> message = new ArrayList<Object>();
            if (distance <= reachDistance) { //eat it						
                message.add("EATIT");
                message.add(closestApple);
                activation = 1.0;
                handsMO.setI(message, activation, name);
                DestroyClosestApple();
            } else {
                activation = 0.0;
                message.add(" ");
                handsMO.setI(message, activation, name);
            }

        } else {
            List<Object> message = new ArrayList<Object>();
            activation = 0.0;
            message.add(" ");
            handsMO.setI(message, activation, name);
        }
        //System.out.println("Before: "+known.size()+ " "+known);

        //System.out.println("After: "+known.size()+ " "+known);
        //System.out.println("EatClosestApple: "+ handsMO.getInfo());	
    }

    @Override
    public void calculateActivation() {

    }

    public void DestroyClosestApple() {
        int r = -1;
        int i = 0;
        synchronized (known) {
            CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
            for (Thing t : known) {
                if (closestApple != null) {
                    if (t.getId() == closestApple.getId()){
                        r = i;
                    }
                }
                i++;
            }
            if (r != -1) {
                known.remove(r);
            }
            closestApple = null;
        }
    }

}
