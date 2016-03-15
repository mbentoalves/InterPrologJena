/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueryContext;

import com.hp.hpl.jena.graph.Node;

/**
 *
 * @author mba
 */
public class QC_Triple  extends QC_RuleClause  {
    private Node subject = null;
    private Node property = null;
    private Node object = null;
    
    private String ssubject = null;
    private String sproperty = null;
    private String sobject = null;
    
    
    public QC_Triple() {
        
    }
    
    public QC_Triple(Object psubject, Object pproperty, Object pobject) {
        if(psubject instanceof Node) {
            subject = (Node) psubject;
        } else if(psubject instanceof String){
            ssubject = (String) psubject;
        }
        
        if(pproperty instanceof Node) {
            property = (Node) pproperty;
        } else if(pproperty instanceof String){
            sproperty = (String) pproperty;
        }
        
        if(pobject instanceof Node) {
            object = (Node) pobject;
        } else if(pobject instanceof String){
            sobject = (String) pobject;
        }
        
    }
    
    public void setSubject(Object psubject) {
        if(psubject instanceof Node) {
            subject = (Node) psubject;
        } else if(psubject instanceof String){
            ssubject = (String) psubject;
        }      
    }
    
    public void setProperty(Object pproperty) {
        if(pproperty instanceof Node) {
            property = (Node) pproperty;
        } else if(pproperty instanceof String){
            sproperty = (String) pproperty;
        }   
    }
    
    public void setObject(Object pobject) {
        if(pobject instanceof Node) {
            object = (Node) pobject;
        } else if(pobject instanceof String){
            sobject = (String) pobject;
        }  
    }
    
    public boolean subjectIsNode () {
        return (subject != null);
    }
    
    public boolean propertyIsNode () {
        return (property != null);
    }
    
    public boolean objectIsNode () {
        return (object != null);
    }
    
    public Node getSubjectNode() {
        return subject;
    }
    
    public String getSubjectId() {
        return ssubject;
    }
    
    public Object getSubject() {
        if(subjectIsNode())
            return getSubjectNode();
        else
            return getSubjectId();
    }
    
    public Node getPropertyNode() {
        return property;
    }
    
    public String getPropertyId() {
        return sproperty;
    }
    
    public Object getProperty() {
        if(propertyIsNode())
            return getPropertyNode();
        else
            return getPropertyId();
    }
    
    public Node getObjectNode() {
        return object;
    }
    
    public String getObjectId() {
        return sobject;
    }
    
    public Object getObject() {
        if(objectIsNode())
            return getObjectNode();
        else
            return getObjectId();
    }
    
    
    public String toString() {
        String retV = "";
        
        if(subjectIsNode()) {
            retV += getSubjectNode().getURI() + " ";
        } else {
            retV += getSubjectId() + " ";
        }
        
        if(propertyIsNode()) {
            retV += getPropertyNode().getURI() + " ";
        } else {
            retV += getPropertyId() + " ";
        }
        
        if(objectIsNode()) {
            retV += getObjectNode().getURI() + " ";
        } else {
            retV += getObjectId() + " ";
        }
        
        return "(" + retV + ")";
    }
}
