
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

import WS3DCoppelia.WS3DCoppelia;
import WS3DCoppelia.model.Agent;
import WS3DCoppelia.util.Constants;
import WS3DCoppelia.util.Constants.BrickTypes;
import WS3DCoppelia.util.ResourceGenerator;
import co.nstant.in.cbor.CborException;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import support.ResourcesGenerator;

/**
 *
 * @author rgudwin
 */
public final class Environment {
    
    public WS3DCoppelia world;
    public Agent creature = null;
    private final ResourceGenerator rg;
    private float worldH;
    private float worldW;
    public final float WALL_SIZE = 0.5f;

    public Environment() {
        world = new WS3DCoppelia(8,8);
        worldH = world.getWorldHeigth();
        worldW = world.getWorldWidth();
        
        creature = world.createAgent(0.1f,0.1f);
        
        generateMaze(world);
        rg = new ResourceGenerator(world, 2, world.getWorldWidth(), world.getWorldHeigth());
        //rg.disableJewel();
        rg.start();
        
        try {
            world.startSimulation();
        } catch (IOException ex) {
            Logger.getLogger(Demo.Environment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CborException ex) {
            Logger.getLogger(Demo.Environment.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Robot " + creature.getName() + " is ready to go.");
    }

    //Gera um labirinto por divisão recursiva
    private void generateMaze(WS3DCoppelia world) {
        int h = (int) (worldH / WALL_SIZE);
        int w = (int) (worldW / WALL_SIZE);
        //Mapa em grid do ambiente indicando locais com parede (célula ocupada)
        boolean[][] walls = new boolean[h][w];

        walls = divideChambers(walls, 0, 0, h, w);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                System.out.print(walls[i][j] ? "x" : " ");
            }
            System.out.println("");
        }
        for (int i = 1; i < h - 1; i++) {
            for (int j = 1; j < w - 1; j++) {
                if (walls[i][j]) {
//                    insertWall(world, i, j, i, j+1, ratio);                    
                    //Up
                    if (walls[i][j + 1]) {
                        insertWall(world, i, j, i, j + 1);
                    }
//                    //Down
//                    if (walls[i][j-1]){
//                        insertWall(world, i, j, i, j-1, ratio);
//                    }
//                    //Left
//                    if (walls[i-1][j]){
//                        insertWall(world, i-1, j, i, j, ratio);
//                    }
                    //Rigth
                    if (walls[i + 1][j]) {
                        insertWall(world, i + 1, j, i, j);
                    }

                }
            }
        }

    }

    private boolean[][] divideChambers(boolean[][] walls, int startH, int startW, int endH, int endW) {
        Random rnd = new Random();

        if ((endH - startH) >= 3 && (endW - startW) >= 3) {

            //Intersecção das duas linhas que dividem o ambiente em 4 câmaras
            //Garantindo que nenhuma parede estrá na borda do ambiente
            int divH = rnd.nextInt(endH - startH - 2) + startH + 1;
            int divW = rnd.nextInt(endW - startW - 2) + startW + 1;

            //Criação das paredes definidas pelas duas linhas
            for (int i = startH; i < endH; i++) {
                walls[i][divW] = true;
            }
            for (int i = startW; i < endW; i++) {
                walls[divH][i] = true;
            }

            //Cria uma abertura em 3 das quatro paredes formadas
            int noOpenning = rnd.nextInt(4);
            int i1 = rnd.nextInt(divH - startH) + startH;
            int j1 = rnd.nextInt(divW - startW) + startW;
            int i2 = rnd.nextInt(endH - divH - 1) + divH + 1;
            int j2 = rnd.nextInt(endW - divW - 1) + divW + 1;
            if (noOpenning != 0) {
                walls[i1][divW] = false;
            }
            if (noOpenning != 1) {
                walls[divH][j1] = false;
            }
            if (noOpenning != 2) {
                walls[i2][divW] = false;
            }
            if (noOpenning != 3) {
                walls[divH][j2] = false;
            }

            //Subdivisão das 4 câmeras geradas
            walls = divideChambers(walls, startH, startW, divH, divW);
            walls = divideChambers(walls, divH, divW, endH, endW);
            walls = divideChambers(walls, startH, divW, divH, endW);
            walls = divideChambers(walls, divH, startW, endH, divW);

            //Reabertura
            if (noOpenning != 0) {
                walls[i1][divW + 1] = false;
                walls[i1][divW - 1] = false;
            }
            if (noOpenning != 1) {
                walls[divH + 1][j1] = false;
                walls[divH - 1][j1] = false;
            }
            if (noOpenning != 2) {
                walls[i2][divW + 1] = false;
                walls[i2][divW - 1] = false;
            }
            if (noOpenning != 3) {
                walls[divH + 1][j2] = false;
                walls[divH - 1][j2] = false;
            }

        }
        return walls;
    }

    private void insertWall(WS3DCoppelia world, int y1, int x1, int y2, int x2) {
        float wallWidth = 0.05f;
        world.createBrick(BrickTypes.RED_BRICK, 
                x1 * WALL_SIZE - wallWidth, 
                y1 * WALL_SIZE - wallWidth, 
                x1 * WALL_SIZE + wallWidth, 
                y1 * WALL_SIZE + wallWidth);
        world.createBrick(BrickTypes.RED_BRICK, 
                x2 * WALL_SIZE - wallWidth, 
                y2 * WALL_SIZE - wallWidth, 
                x2 * WALL_SIZE + wallWidth, 
                y2 * WALL_SIZE + wallWidth);
        world.createBrick(BrickTypes.RED_BRICK, 
                x1 * WALL_SIZE - wallWidth, 
                y1 * WALL_SIZE - wallWidth,  
                x2 * WALL_SIZE + wallWidth, 
                y2 * WALL_SIZE + wallWidth);

    }
}
