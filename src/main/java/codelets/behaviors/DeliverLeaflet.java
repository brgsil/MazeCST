/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.behaviors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author bruno
 */
public class DeliverLeaflet extends Codelet {

    private Memory leafletsMO;
    private MemoryContainer handsMO;

    
    public DeliverLeaflet(){
        this.name = "DeliverLeaflet";
    }
    
    @Override
    public void accessMemoryObjects() {
        leafletsMO = (MemoryObject) this.getInput("LEAFLETS");
        handsMO = (MemoryContainer) this.getOutput("HANDS");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        Idea leaflets = (Idea) leafletsMO.getI();

        for (Idea l1 : leaflets.getL()) {
            boolean completed = true;
            for (Idea child : l1.getL()) {
                if (child.getType() == 0) {
                    double miss = ((int) child.get("NEED").getValue()) - ((int) child.get("HAS").getValue());
                    if (miss > 0) {
                        completed = false;
                    }
                }
            }
            List<Object> message = new ArrayList<Object>();
            if (completed && !((boolean) l1.get("COMPLETED").getValue())) {
                l1.get("COMPLETED").setValue(true);
                message.add("DELIVER");
                message.add(l1.get("ID").getValue());
                activation = 1.0;
                handsMO.setI(message, activation, name);
                break;
            } else {
                message.add("");
                activation = 0.0;
                handsMO.setI(message, activation, name);
            }
        }

    }

}
