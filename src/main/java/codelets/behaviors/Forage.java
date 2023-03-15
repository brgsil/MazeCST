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
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author klaus
 *
 *
 */
public class Forage extends Codelet {

    private Memory knownMO;
    private Memory selfMO;
    private Memory mapMO;
    private List<Thing> known;
    private MemoryContainer legsMO;
    private int goalX = 0, goalY = 0;
    private double creatureBasicSpeed;
    private Random rnd = new Random();
    private float wallSize = 0.5f;

    /**
     * Default constructor
     */
    public Forage(double creatureBasicSpeed) {
        this.name = "Forage";
        this.creatureBasicSpeed = creatureBasicSpeed;
    }

    @Override
    public void proc() {
        known = (List<Thing>) knownMO.getI();
        Idea cis = (Idea) selfMO.getI();
        int[][] map = (int[][]) mapMO.getI();

            List<Object> message = new ArrayList<Object>();
            List<Float> self = (List<Float>) cis.get("position").getValue();
            if (calculateDistance(self.get(0), (float)wallSize*goalY, self.get(1), (float)wallSize*goalX) < 1.50) {
                goalX = rnd.nextInt(map.length);
                goalY = rnd.nextInt(map[0].length);
            }




            Grid grid = new Grid(map, goalX, goalY);
            grid.route();
            message.add("GOTO");
            message.add((float) grid.miny * wallSize);
            message.add((float) grid.minx * wallSize);
            activation = 0.3;
            legsMO.setI(message, activation, name);
            
    }

    @Override
    public void accessMemoryObjects() {
        knownMO = (MemoryObject) this.getInput("KNOWN_FOODS");
        legsMO = (MemoryContainer) this.getOutput("LEGS");
        selfMO = (MemoryObject) this.getInput("INNER");
        mapMO = (MemoryObject) this.getInput("MAP");
    }

    @Override
    public void calculateActivation() {

    }
    private double calculateDistance(Float x1, Float x2, Float y1, Float y2) {
        return(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
    }

}
