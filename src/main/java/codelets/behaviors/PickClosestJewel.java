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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author ia941
 */
public class PickClosestJewel extends Codelet {

	private Memory closestJewelMO;
	private Memory innerSenseMO;
        private Memory knownMO;
	private float reachDistance;
	private MemoryContainer handsMO;
        Thing closestJewel;
        Idea cis;
        List<Thing> known;

	public PickClosestJewel(float reachDistance) {
		this.reachDistance=reachDistance;
                this.name = "PickClosestJewel";
	}

	@Override
	public void accessMemoryObjects() {
		closestJewelMO=(MemoryObject)this.getInput("CLOSEST_JEWEL");
		innerSenseMO=(MemoryObject)this.getInput("INNER");
		handsMO=(MemoryContainer)this.getOutput("HANDS");
                knownMO = (MemoryObject)this.getOutput("KNOWN_JEWELS");
	}

	@Override
	public void proc() {
                String jewelName="";
                closestJewel = (Thing) closestJewelMO.getI();
                cis = (Idea) innerSenseMO.getI();
                known = (List<Thing>) knownMO.getI();
		
		if(closestJewel != null)
		{
			float jewelX=0;
			float jewelY=0;
			try {
				jewelX=closestJewel.getPos().get(0);
				jewelY=closestJewel.getPos().get(1);
                                

			} catch (Exception e) {
				e.printStackTrace();
			}

			float selfX=((List<Float>) cis.get("position").getValue()).get(0);
			float selfY=((List<Float>) cis.get("position").getValue()).get(1);

			Point2D pJewel = new Point();
			pJewel.setLocation(jewelX, jewelY);

			Point2D pSelf = new Point();
			pSelf.setLocation(selfX, selfY);

			double distance = calculateDistance(selfX, selfY, jewelX, jewelY);
			List<Object> message=new ArrayList<Object>();
			if(distance<=reachDistance){						
                                message.add("PICKUP");
                                message.add(closestJewel);
                                activation=1.0;
                                handsMO.setI(message, activation, name);
                                DestroyClosestJewel();
                        }else{
                                activation=0.0;
                                message.add("");
                                handsMO.setI(message, activation, name);
                        }

		}else{
                        activation=0.0;
                        List<Object> message=new ArrayList<Object>();
                        message.add("");
                        handsMO.setI(message, activation, name);
		}

	}
        
        @Override
        public void calculateActivation() {
        
        }
        
        public void DestroyClosestJewel() {
           int r = -1;
           int i = 0;
           synchronized(known) {
             CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);  
             for (Thing t : known) {
              if (closestJewel != null) 
                 if (t.getId() == closestJewel.getId()) r = i;
              i++;
             }   
             if (r != -1) known.remove(r);
             closestJewel = null;
           }
        }
        
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }

}
