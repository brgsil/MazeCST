/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.perception;

import WS3DCoppelia.model.Thing;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author bruno
 */
public class MapStructuringCodelet extends Codelet {

    private Memory knownFoods;
    private Memory knownJewels;
    private Memory knownBricks;
    private Memory selfMO;
    private Memory mapMO;
    private float wallSize = 0.5f;

    public MapStructuringCodelet() {
        this.name = "MapStructuringCodelet";
    }

    @Override
    public void accessMemoryObjects() {
        knownBricks = (MemoryObject) this.getInput("KNOWN_BRICKS");
        mapMO = (MemoryObject) this.getOutput("MAP");
        selfMO = (MemoryObject) this.getInput("INNER");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        List<Thing> bricks = Collections.synchronizedList((List<Thing>) knownBricks.getI());
        int[][] map = (int[][]) mapMO.getI();
        Idea cis = (Idea) selfMO.getI();

        synchronized (bricks) {
            if (bricks.size() != 0) {
                synchronized (map) {
                    CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(bricks);
                    for (Thing t : myknown) {

                        List<Float> pos = t.getPos();
                        int j = (int) Math.floor(pos.get(0) / wallSize);
                        int i = (int) Math.floor(pos.get(1) / wallSize);

                        map[i][j] = -1;

                    }
                }
            }

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == 1) {
                        map[i][j] = 0;
                    }
                }
            }
            List<Float> self = (List<Float>) cis.get("position").getValue();
            int j = (int) Math.floor((self.get(0) + wallSize/2) / wallSize);
            int i = (int) Math.floor((self.get(1) + wallSize/2) / wallSize);
            map[i][j] = 1;

            String line = "map    ";
            for (int l = 0; l < map.length; l++) {
                line += "-";
            }
            for (int l = 0; l < map.length; l++) {
                for (int m = 0; m < map[l].length; m++) {
                    line += map[l][m] == -1 ? "█" : (map[l][m] == 1 ? "o" : " ");
                }
                line += "|\nmap    |";
            }
            for (int l = 0; l < map.length; l++) {
                line += "-";
            }
            //System.out.println(line);
        }

    }
}
