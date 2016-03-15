/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QC_BuiltInFunctions;

import QueryContext.QC_BuiltInInterface;
import com.hp.hpl.jena.graph.Node;
import java.util.ArrayList;

/**
 *
 * @author mba
 */
public class notEqual implements QC_BuiltInInterface {

    @Override
    public boolean bodyCall(ArrayList<Node> args) {
        boolean allEquals = true;
        
        if(args.size()>1) {
            int prev = 0;
            int act = 1;
            while(allEquals && act<args.size()) {
                if(args.get(prev).isURI() && args.get(act).isURI()) {
                    allEquals = (args.get(prev).getURI().compareTo(args.get(act).getURI())==0);
                }
                else allEquals = false;
                if(allEquals) {
                    prev++;
                    act++;
                }
            }
        }
        
        return !allEquals;
    }
    
}
