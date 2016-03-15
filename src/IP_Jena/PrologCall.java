/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IP_Jena;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.TermModel;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDBaseStringType;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDBaseNumericType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.reasoner.rulesys.builtins.BaseBuiltin;
import com.hp.hpl.jena.sparql.util.NodeToLabelMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
/**
 *
 * @author mba
 */
public class PrologCall extends BaseBuiltin {
    private IPConnPrologJenaContext vIPConnPrologJena;
    private NodeToLabelMap bnodes = new NodeToLabelMap() ;
    
    public PrologCall(String XSBloc, String fprologfile) {
        this.vIPConnPrologJena = new IPConnPrologJenaContext();
        this.vIPConnPrologJena.init(XSBloc, fprologfile);
    }
     
    public PrologCall(PrologEngine engine, String fprologfile) {
        this.vIPConnPrologJena = new IPConnPrologJenaContext();
        this.vIPConnPrologJena.init(engine, fprologfile);
    }
    
    @Override
    public String getName() {
        return "PrologCall";
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        vIPConnPrologJena.setContext(context);

        ArrayList<String> vars = new ArrayList<>();
        HashMap<Integer, Integer> posVar = new HashMap<>();
        String clause1 = "(_PrologPredicate_(_Parameters_), L=[true_Variables_];L=[false]), buildTermModel(L,TM)";

        String clause2 = "[TM]";
        String prologPredicate;
        if(args[0].isLiteral()) {
            prologPredicate = args[0].getLiteral().toString();
        }
        else {
            prologPredicate = args[0].toString();
        }
        String parameters = "";
        clause1 = clause1.replace("_PrologPredicate_", prologPredicate);
        String variables = "";
        for(int i=1; i<length;i++) {
            Node n = getArg(i, args, context);
            if(!n.isVariable()) {
                if(n.isURI()) {
                    parameters += (parameters.length()>0 ? "," : "") + "'" + n.toString() +"'";
                }
                else if(n.isLiteral()) {
                    if(n.getLiteralDatatype() instanceof XSDBaseStringType) {
                        parameters += (parameters.length()>0 ? "," : "") + "'" + n.toString() +"'";
                    }
                    else if(n.getLiteralDatatype() instanceof XSDBaseNumericType) {
                        parameters += (parameters.length()>0 ? "," : "") + n.toString().replace(",", ".");
                    }
                }
                else if(n.isBlank()){
                    parameters += (parameters.length()>0 ? "," : "") + "'"+ bnodes.asString(n)+ "'";
                }
            }
            else {
                String var = "V" + randomString(4);
                while(vars.contains(var)) {
                    var = "V" + randomString(4);
                }
                vars.add(var);
                posVar.put(vars.indexOf(var), i);
                parameters += (parameters.length()>0 ? "," : "") + var ;
                variables += "," + var;
            }    
        }
            
        clause1 = clause1.replace("_Parameters_", parameters);
        clause1 = clause1.replace("_Variables_", variables);
        
        Object [] bindings = vIPConnPrologJena.run(clause1, clause2);
        
        TermModel list = (TermModel)bindings[0];
        boolean retValue = list.getChild(0).toString().compareTo("true")==0;
        
        if(retValue) {
            list = (TermModel) list.getChild(1);
            int p=0;
            while(list.getChildCount()>0) {
                String vNode = list.getChild(0).toString();
       
                Node retNode;
                URI uri;
                try {
                    uri = new URI(vNode);
                } catch (URISyntaxException ex) {
                    uri = null;
                }
                if (uri != null) {
                    retNode = NodeFactory.createURI(vNode);
                }
                else {            
                    RDFDatatype rdfdatatype = NodeFactory.getType(vNode);
                    retNode = NodeFactory.createLiteral(vNode, rdfdatatype);
                }
                context.getEnv().bind(args[posVar.get(p)], retNode);
                list = (TermModel) list.getChild(1);
                p++;
            }
        }
        
        //System.out.println(clause1);
        //System.out.println((retValue ? "true" : "false"));
        
        return retValue;
                
    }
    
    public void release() {
        vIPConnPrologJena.close();
    }
        
    public void close() {
        vIPConnPrologJena.close();
    }

    private String randomString( int len ) 
    {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        String retString = "";
        
        
       for( int i = 0; i < len; i++ ) {
           retString += AB.charAt( rnd.nextInt(AB.length()) );
       }

       return retString;
    }


}
