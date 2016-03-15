/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mba
 */
public class QC_genericLib {
    public static String [] trimStringVect(String [] vect) {
        String [] retVect = new String[vect.length];
        
        for(int i=0; i<vect.length; i++) {
            retVect[i] = vect[i].trim();
        }
        
        return retVect;
    }
    
    public static String getInternalName(String clause) {
        return "@F"+getNvariables(clause)+"_"+getRuleName(clause);
    }
    
    public static String getRuleName(String clause) {
        if(clause.contains("(")) {
            return clause.substring(0, clause.indexOf("("));
        }
        else {
            return clause;
        }
    }
    
    private static int getNvariables(String rule) {
        int nV = 0;
        
        int posPOpen = rule.indexOf("(");
        int posPClose = rule.indexOf(")");
        
        if(posPOpen >= 0 && rule.substring(posPOpen+1, posPClose).trim().length()>0) {
            nV = rule.split(",").length;
        }
        
        return nV;
    }
    
    public static String[] getVars (String clause) {
        
        String trimStringVect[] = clause.trim().split(",");
        trimStringVect = trimStringVect(trimStringVect);
        
        return trimStringVect;
    }
    
    
    public static String[] getVars2 (String clause) {
        int posPOpen = clause.indexOf("(");
        int posPClose = clause.indexOf(")");
        
        if(posPOpen >=0 && clause.substring(posPOpen+1, posPClose).trim().length()>0) {
            clause = clause.substring(posPOpen+1, posPClose).trim();
            String trimStringVect[] = clause.trim().split(",");
            trimStringVect = trimStringVect(trimStringVect);
        
            return trimStringVect;
        }
        else {
            return new String[0];
        }
    }
    
    
    public static String[] getVars3 (String clause) {
        String[] tmpVars = getVars2(clause);
        int nVars = 0;
        for(int i=0; i<tmpVars.length; i++) {
            if(tmpVars[i].startsWith("?")) {
                nVars++;
            }
        }
        String retVars[] = new String[nVars];
        nVars = 0;
        for(int i=0; i<tmpVars.length; i++) {
            if(tmpVars[i].startsWith("?")) {
                retVars[nVars] = tmpVars[i];
                nVars++;
            }
        }
        
        return retVars;
    }
    
    
    public static HashMap<Integer, Node> getClauseInstValues(String query){
        HashMap<Integer, Node> retV = new HashMap<Integer, Node> ();
        
        String [] vars = getVars2(query);
        
        for(int i=0; i<vars.length; i++) {
            if(!vars[i].startsWith("?")) {
                retV.put(i+1, NodeFactory.createURI(vars[i]));
            }
        }
        
        return retV;
    }
    
    public static HashMap<Integer, String> getClauseVars(String query){
        HashMap<Integer, String> retV = new HashMap<Integer, String> ();
        
        String [] vars = getVars2(query);
        
        for(int i=0; i<vars.length; i++) {
            if(vars[i].startsWith("?")) {
                retV.put(i+1, vars[i]);
            }
        }
        
        return retV;
    }
    
    
    public static ArrayList<String> extractBodyClauses(String body) {
        ArrayList<String> retClauses = new ArrayList<String>();
        
        body = body.trim();
        
        int pBody = 0;
        while(pBody < body.length()) {
            int posClose = body.indexOf(")", pBody)+1;
            String clause = body.substring(pBody, posClose);
            retClauses.add(clause);
            pBody = posafterToken(body, posClose);
        } 
        
        return retClauses;
    }
    
    private static int posafterToken(String body, int pBody) {
        if(pBody < body.length()) {
            while(body.charAt(pBody) != ',' && body.charAt(pBody) != '.') {
                pBody++;
            }
            while(body.charAt(pBody) == ' ') {
                pBody++;
            }
        }
        return pBody+1;
    }
}
