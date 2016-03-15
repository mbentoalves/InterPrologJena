/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import com.hp.hpl.jena.graph.Node;
import java.util.ArrayList;

/**
 *
 * @author mba
 */
public interface QC_BuiltInInterface {
    public boolean bodyCall(ArrayList<Node> args);
}
