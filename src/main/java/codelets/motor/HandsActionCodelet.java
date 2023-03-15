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

package codelets.motor;


import WS3DCoppelia.model.Agent;
import WS3DCoppelia.model.Thing;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 *  Hands Action Codelet monitors working storage for instructions and acts on the World accordingly.
 *  
 * @author klaus
 *
 */


public class HandsActionCodelet extends Codelet{

	private MemoryContainer handsMO;
	private String previousHandsAction="";
        private Agent agent;
        private Random r = new Random();
        static Logger log = Logger.getLogger(HandsActionCodelet.class.getCanonicalName());

	public HandsActionCodelet(Agent nc) {
                agent = nc;
                this.name = "HandsActionCodelet";
	}
	
        @Override
	public void accessMemoryObjects() {
		handsMO=(MemoryContainer)this.getInput("HANDS");
	}
	public void proc() {
            
                List<Object> action = (List<Object>) handsMO.getI();
                String command = (String) action.get(0);

		if(!command.equals("") && (!command.equals(previousHandsAction))){
			if(command.equals("PICKUP")){
                            Thing object =  (Thing) action.get(1);
                            agent.sackIt(object);
                            log.info("Sending Put In Sack command to agent:****** "+object.getTypeName()+"**********");							
                                //							}
                        }
                        if(command.equals("EATIT")){
                            Thing object = (Thing) action.get(1);
                            agent.eatIt(object);
                                log.info("Sending Eat command to agent:****** "+object.getTypeName()+"**********");							
                        }
//					if(action.equals("BURY")){
//                                                try {
//                                                 agent.hideIt(objectName);
//                                                } catch (Exception e) {
//                                                    
//                                                }
//						log.info("Sending Bury command to agent:****** "+objectName+"**********");							
//					}
                        if(command.equals("DELIVER")){
                                int leafletId = (int) action.get(1);
                                agent.deliver(leafletId);
                                log.info("Sending Deliver command to agent:****** Leaflt"+leafletId+"**********");							
                        }
                }
		previousHandsAction = command;
		
	}//end proc

    @Override
    public void calculateActivation() {
        
    }


}
