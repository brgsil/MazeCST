/*****************************************************************************
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
 *****************************************************************************/

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
 * @author klaus
 *
 */
public class ClosestFoodDetector extends Codelet {

	private Memory knownMO;
	private Memory closestFoodMO;
	private Memory innerSenseMO;
	
        private List<Thing> known;

	public ClosestFoodDetector() {
            this.name = "ClosestAppleDetector";
	}


	@Override
	public void accessMemoryObjects() {
		this.knownMO=(MemoryObject)this.getInput("KNOWN_FOODS");
		this.innerSenseMO=(MemoryObject)this.getInput("INNER");
		this.closestFoodMO=(MemoryObject)this.getOutput("CLOSEST_FOOD");	
	}
	@Override
	public void proc() {
                Thing closest_food=null;
                known = Collections.synchronizedList((List<Thing>) knownMO.getI());
                Idea cis = (Idea) innerSenseMO.getI();
                synchronized(known) {
		   if(known.size() != 0){
			//Iterate over objects in vision, looking for the closest apple
                        CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                        for (Thing t : myknown) {
				if(t.isFood()){ //Then, it is a food
                                        if(closest_food == null){    
                                                closest_food = t;
					}
                                        else {
                                                float px = ((List<Float>) cis.get("position").getValue()).get(0);
                                                float py = ((List<Float>) cis.get("position").getValue()).get(1);
						double Dnew = calculateDistance(t.getPos().get(0), px, t.getPos().get(1), py);
                                                double Dclosest= calculateDistance(closest_food.getPos().get(0), px, closest_food.getPos().get(1), py);
						if(Dnew<Dclosest){
                                                        closest_food = t;
						}
					}
				}
			}
                        
                        if(closest_food!=null){    
				if(closestFoodMO.getI() == null || !closestFoodMO.getI().equals(closest_food)){
                                      closestFoodMO.setI(closest_food);
				}
				
			}else{
				//couldn't find any nearby apples
                                closest_food = null;
                                closestFoodMO.setI(closest_food);
			}
		   }
                   else  { // if there are no known apples closest_apple must be null
                        closest_food = null;
                        closestFoodMO.setI(closest_food);
		   }
                }
	}//end proc

@Override
        public void calculateActivation() {
        
        }
        
        private double calculateDistance(Float x1, Float x2, Float y1, Float y2) {
            return(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
        }

}
