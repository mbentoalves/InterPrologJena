/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext2;

import com.hp.hpl.jena.graph.Node;

/**
 *
 * @author mba
 */
public class QC2_triple {
    private Object subject;
    private Object property;
    private Object object;
    
    public QC2_triple(Object subject, Object property, Object object){
        this.subject = subject;
        this.property = property;
        this.object = object;
    }
    
    public Object getSubject() {
        return subject;
    }

    public Object getProperty() {
        return property;
    }
        
    public Object getObject() {
        return object;
    }
    
    public String toString() {
        String msg = "";
        
        msg += getMsg(subject)+";"+getMsg(property)+";"+getMsg(object);
        
        return msg;
    }
    
    private String getMsg(Object n){
        if(n instanceof Node) {
            return ((Node) n).toString();
        }
        else {
            return (String) n;
        }
    }
}
