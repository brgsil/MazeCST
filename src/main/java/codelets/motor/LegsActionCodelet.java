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

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 *  Legs Action Codelet monitors working storage for instructions and acts on the World accordingly.
 *  
 * @author klaus
 *
 */

public class LegsActionCodelet extends Codelet{

	private MemoryContainer legsActionMO;
	private float previousTargetx=0;
	private float previousTargety=0;
	private String previousLegsAction="";
        private Agent agent;
        double old_angle = 0;
        int k=0;
        static Logger log = Logger.getLogger(LegsActionCodelet.class.getCanonicalName());

	public LegsActionCodelet(Agent nc) {
		agent = nc;
                this.name = "LegsActionCodelet";
	}
	
	@Override
	public void accessMemoryObjects() {
		legsActionMO=(MemoryContainer)this.getInput("LEGS");
	}
	
	@Override
	public void proc() {
            
                List<Object> action = (List<Object>) legsActionMO.getI();
                Random r = new Random();
		
                String command = (String) action.get(0);
		if(!command.equals("") ){
			
                    if(command.equals("FORAGE") && !command.equals(previousLegsAction)){
                                   agent.rotate();     
                        }
                    else if(command.equals("GOTO")){
                           float targetx=(float) action.get(1);
                           float targety=(float) action.get(2);
                           if (targetx != previousTargetx || targety != previousTargety){
                            agent.moveTo(targetx, targety);
                            previousTargetx = targetx;
                            previousTargety = targety;
                           }

                    } else {
                         agent.stop();
                    }
                    previousLegsAction = command;
                }
        
	}//end proc

    @Override
    public void calculateActivation() {
        
    }


}
