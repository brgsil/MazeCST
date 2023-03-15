/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.sensors;

import WS3DCoppelia.model.Agent;
import WS3DCoppelia.model.Bag;
import WS3DCoppelia.model.Leaflet;
import WS3DCoppelia.util.Constants.JewelTypes;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
/**
 *
 * @author ia941
 */
public class LeafletSense extends Codelet{
    
    private Agent agent;
    private Memory leafletSenseMO;
    private Idea leaflets;
    
    public LeafletSense(Agent nc){
        agent = nc;
        this.name = "LeafletSense";
    }

    @Override
    public void accessMemoryObjects() {
        leafletSenseMO = (MemoryObject) this.getOutput("LEAFLETS");
        leaflets = (Idea) leafletSenseMO.getI();
    }

    @Override
    public void calculateActivation() {
        
    }

    @Override
    public void proc() {
        Bag bag = agent.getBag();
        int id = 1;
        Leaflet[] leafletList = agent.getLeaflets();
        for (Leaflet leaflet : leafletList){
            //Get leaflet or created if does not exist
            Idea leafletIdea = leaflets.get(String.format("LEAFLET_%d", id));
            if (leafletIdea == null){
                leafletIdea = leaflets.add(new Idea(String.format("LEAFLET_%d", id), null, 0));
                for (JewelTypes jewel : JewelTypes.values()){
                    Idea colorIdea = new Idea(jewel.typeName(), null, 0);
                    colorIdea.add(new Idea("NEED", 0, 1));
                    colorIdea.add(new Idea("HAS", 0, 1));
                    leafletIdea.add(colorIdea);
                }
                leafletIdea.add(new Idea("COMPLETED", false, 1));
                leafletIdea.add(new Idea("ID", false, 1));
            }

            for (JewelTypes jewel : JewelTypes.values()){
                Idea colorIdea = leafletIdea.get(jewel.typeName());
                colorIdea.get("NEED").setValue(leaflet.getRequiredAmountOf(jewel));
                colorIdea.get("HAS").setValue(bag.getTotalCountOf(jewel));
            }  

            leafletIdea.get("ID").setValue(leaflet.getId());

            id += 1;
        }
    }
    
}
