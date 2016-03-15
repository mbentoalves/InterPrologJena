/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext2;

import static IP_Jena.GenericLib.generatekey;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author mba
 */
public class QC2_rule {
    private HashMap<String, String> genkeys;
    private HashMap<String, String> genExternalkeys;
    private HashMap<Integer, QC2_triple> triples;
    private int tripleID = 0;
    public QC2_rule() {
        genkeys = new HashMap<String, String>();
        genExternalkeys = new HashMap<String, String>();
        triples = new HashMap <Integer, QC2_triple> ();        
    }
    
    public void addTriple(Object pSubject, Object pProperty, Object pObject){
        triples.put((++tripleID), new QC2_triple(pSubject, pProperty, pObject));
    }
    
    public String getExternalVarKey(String k) {
        String v = getVarKey(k);
        genExternalkeys.put(k, v);
        return v;
    }
    
    public String getVarKey(String k) {
        if(!genkeys.containsKey(k)){
             String v = generatekey(10);
            while(genkeys.containsValue(v)){
                v = generatekey(10);
            }
            genkeys.put(k, v);
        }
        return genkeys.get(k);
    }
    
    public boolean isVar(String v) {
        return genkeys.containsValue(v);
    }
    
    public QC2_triple getTriple(int t) {
        if(triples.containsKey(t)){
            return triples.get(t);
        }
        else {
            return null;
        }
    }
    
    public HashMap<String, String> getKeys() {
        return genkeys;
    }
    
    public HashMap<String, String> getExternalKeys() {
        return genExternalkeys;
    }
    
    public String toString(){
        String msg = "";
        
        for(Entry<Integer, QC2_triple> entry : triples.entrySet()){
            msg+=entry.getValue().toString()+" ;; ";
        }
        
        return msg;
    }
}
