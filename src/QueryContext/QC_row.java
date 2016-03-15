/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import com.hp.hpl.jena.graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author mba
 */
public class QC_row  {
    private HashMap<String, Node> row = new HashMap<String, Node>();
    
    public QC_row(){
        
    }
    
    public void insertValue(String field, Node Value) {
        row.put(field, Value);
    }

    public void insertValue(QC_row row) {
        for(String field : row.getRow().keySet()) {
            insertValue(field, row.getValue(field));
        }
    }
    
    public boolean hasField(String field) {
        return row.containsKey(field);
    }
    
    public int getNFields() {
        return row.size();
    }
    
    public Node getValue(String field) {
        if(hasField(field)){
            return row.get(field);
        }
        else return null;
    }
    
    public HashMap<String, Node> getRow() {
        return row;
    }
    
    public String toString() {
        String ret = "";
        
        for(String f : row.keySet()) {
            ret += f + ": "+row.get(f).toString();
        }
        
        return ret;
    }


    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if(object instanceof QC_row){
            QC_row otherRow = (QC_row) object;
            if(getNFields() == otherRow.getNFields()) {
                sameSame = true;
                Object fields[] =row.keySet().toArray();
                int i=0;
                while(sameSame && i<fields.length) {
                    String field = (String) fields[i];
                    sameSame = (getValue(field).toString().compareTo(otherRow.getValue(field).toString())==0);
                    if (sameSame) i++;
                }
            }
        }

        return sameSame;
    }
    
}
