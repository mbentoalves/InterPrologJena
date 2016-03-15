/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import com.declarativa.interprolog.PrologEngine;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;

/**
 *
 * @author mba
 */
public class IPBuiltin extends PrologCall{

    private String predicateName = "";
    
    public IPBuiltin(String XSBloc, String fprologfile, String predicateName) {
        super(XSBloc, fprologfile);
        this.predicateName = predicateName;
    }

    public IPBuiltin(PrologEngine engine, String fprologfile, String predicateName) {
        super(engine, fprologfile);
        this.predicateName = predicateName;
    }
    
    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        Node[] args2 = new Node[length+1];
        args2[0] = NodeFactory.createLiteral(predicateName);

        for(int i=0; i<length;i++){
            args2[i+1] = args[i];
        }
        return super.bodyCall(args2, args2.length, context);
    }
}
