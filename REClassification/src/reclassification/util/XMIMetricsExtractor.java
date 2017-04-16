/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reclassification.util;

import com.sdmetrics.model.MetaModel;
import com.sdmetrics.model.MetaModelElement;
import com.sdmetrics.model.Model;
import com.sdmetrics.model.ModelElement;
import com.sdmetrics.model.XMIReader;
import com.sdmetrics.model.XMITransformations;
import com.sdmetrics.util.XMLParser;
import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author truongh
 */
public class XMIMetricsExtractor {
    private MetaModel metaModel; // metamodel definition to use
    private XMITransformations trans; // XMI tranformation
    private File xmiFile;
    private Model model;
    private XMIReader xmiReader;
    private XMLParser parser;
    
    // ====== Features =======
    private int noCls = 0;
    
    private double avgRelCls = 0.0;
    private boolean ext2SameDir = false;
    
    // orphan classes
    private boolean extOrpCls = false;
    private int noOrpCls = 0;
    private double avgOrpCls = 0.0;

    // cardinality
    private boolean extCard = false;
    
    // parameters of operation
    private boolean extOperPara = false;
    private int noPara = 0;
    private double avgParaOper = 0.0;
    
    // attributes
    private int noAttr = 0;
    private int maxAttrCls = 0;
    private double avgAttrCls = 0;
    
    // operations
    private int noOper = 0;
    private int maxOperCls = 0;
    private double avgOperCls = 0;
    
    // associations
    private int noAssociation = 0;
    private double avgAssocCls = 0;
    private int noAssocType = 0;
    
    // =======================
    
    public MetaModel getMetaModel() {
        return metaModel;
    }

    public Model getModel() {
        return model;
    }

    public int getNoCls() {
        return noCls;
    }

    public double getAvgRelCls() {
        return avgRelCls;
    }

    public boolean isExt2SameDir() {
        return ext2SameDir;
    }

    public boolean isExtOrpCls() {
        return extOrpCls;
    }

    public int getNoOrpCls() {
        return noOrpCls;
    }

    public boolean isExtCard() {
        return extCard;
    }

    public boolean isExtOperPara() {
        return extOperPara;
    }

    public double getAvgParaOper() {
        return avgParaOper;
    }

    public int getNoAttr() {
        return noAttr;
    }

    public int getMaxAttrCls() {
        return maxAttrCls;
    }

    public double getAvgAttrCls() {
        return avgAttrCls;
    }

    public int getNoOper() {
        return noOper;
    }

    public int getMaxOperCls() {
        return maxOperCls;
    }

    public double getAvgOperCls() {
        return avgOperCls;
    }
    
    public int getNoAssociation() {
        return noAssociation;
    }

    public int getNoAssocType() {
        return noAssocType;
    }
    
    public double getAvgAssocCls() {
        return avgAssocCls;
    }
    
    public int getNoPara() {
        return noPara;
    }
    
    public double getAvgOrpCls() {
        return avgOrpCls;
    }
    
    public XMIMetricsExtractor(File xmiFile, String metaModelURL, String xmiTransURL){
        try { 
            parser = new XMLParser(); 
            metaModel = new MetaModel(); 
            parser.parse(metaModelURL, metaModel.getSAXParserHandler()); 

            trans=new XMITransformations(metaModel); 
            parser.parse(xmiTransURL, trans.getSAXParserHandler()); 

            model = new Model(metaModel); 
            xmiReader = new XMIReader(trans, model); 
            parser.parse(xmiFile.getAbsolutePath(), xmiReader);
            
            String[] filters = { "#.java", "#.javax", "#.org.xml" };
            model.setFilter(filters, false, true);
            
            calculateFeatures();
        } 
        catch(Exception e) { 
            System.out.println("Errors occured when trying to parse the file " + xmiFile.getName());
        } 
    }
    
    private void calculateFeatures(){
        // calculate number of classes
        noCls = getNoElements("class");
        // calculate number of attributes
        noAttr = getNoElements("attribute");
        // calculate number of operations
        noOper = getNoElements("operation"); 
        // calculate number of parameter of operation
        noPara = getNoElements("parameter");
        // existence of params in operation signature
        if(noPara >0) 
            extOperPara = true;
        else 
            extOperPara = false;
            
        // calculate number of association
        noAssociation = getNoElements("association");
        
        // orphan classes
        noOrpCls = getOrphanClasses();
        extOrpCls = (noOrpCls > 0);

        if (noCls > 0) {
            // average number of attribute
            avgAttrCls = noAttr / (1.0 * noCls);
            // average number of operation
            avgOperCls = noOper / (1.0 * noCls);
            // average number of association per class
            avgAssocCls = noAssociation / (1.0 * noCls);
            // average number of orphan class over all classes
            avgOrpCls = noOrpCls / (1.0 * noCls);
        }
        // average number of parameters per operation
        if (noOper > 0) {
            avgParaOper = noPara / (1.0 * noOper);
        }
        
        // calculate max number of attribute per class
        maxAttrCls = getMaxNoElementPerClass("attribute");  
        // calculate max number of operation per class
        maxOperCls = getMaxNoElementPerClass("operation"); 
        
        noAssocType = 0;
        // calculate number of generalization
        int noGeneralization = getNoElements("generalization");
        if (noGeneralization > 0) {
            noAssocType++;
        }
        // calculate number of realization
        int noRealization = getNoElements("realization");
        if (noRealization > 0) {
            noAssocType++;
        }
        // calculate number of aggregation
        int noAggregation = getNoAggregation();
        if (noAggregation > 0) {
            noAssocType++;
        }
    }
    
    private int getOrphanClasses(){
        int noOrphanCls =0;
        //iterate over all model element types in the metamodel
        for (MetaModelElement type : metaModel) {
            //System.out.println("Elements of type: " + type.getName());
            if (type.getName().matches("class")){        
                // iterate over all model elements of the current type
                List<ModelElement> elements = model.getAcceptedElements(type);
                for (ModelElement me : elements) {
                    Collection<ModelElement> assoc = me.getRelations("associationendtype");
                    if (assoc != null){
                        System.out.println("Class: " + me.getFullName() + " contains " + assoc.size() + " association");
                    } else {
                        noOrphanCls++;
                        System.out.println("Class: " + me.getFullName() + " is an island class");
                    }
                }
            }	
        }
        return noOrphanCls;
    }
    
    private int getNoElements(String elementType) {
        int elementCount = 0;
        
        //iterate over all model element types in the metamodel
        for (MetaModelElement type : metaModel) {
            //System.out.println("Elements of type: " + type.getName());
            // iterate over all model elements of the current type
            List<ModelElement> elements = model.getAcceptedElements(type);
            for (ModelElement me : elements) {
                if (me.getType().getName().matches(elementType)){
                  elementCount++;    
                }
            }
        }	
        return elementCount;
    }
    
    private int getMaxNoElementPerClass(String elementType){
        int elementMax =0;
        int count = 0;
        //iterate over all model element types in the metamodel
        for (MetaModelElement type : metaModel) {
            //System.out.println("Elements of type: " + type.getName());
            if (type.getName().matches("class")){        
                // iterate over all model elements of the current type
                List<ModelElement> elements = model.getAcceptedElements(type);
                for (ModelElement me : elements) {
                    //System.out.println("  Class: " + me.getFullName() + " ");
                    Collection<ModelElement> ownedElements = me.getOwnedElements();
                    if (ownedElements != null){
                        for (ModelElement modelE:ownedElements){
                            if (modelE.getType().getName().matches(elementType)){
                                count ++;
                                //System.out.println(reqAttrOps + ":" + modelE.getName());
                            }
                        }
                        if (count != 0 && elementMax < count)
                            elementMax = count;
                        count =0;
                    }    
                }
            }	
        }
        return elementMax;
    }
    
    private int getNoAggregation(){
        String elementType = "property";
        int numAggregation = 0;

        //iterate over all model element types in the metamodel
        for (MetaModelElement type : metaModel) {
                //System.out.println("Elements of type: " + type.getName());
                if (type.getName().matches(elementType)){        
                // iterate over all model elements of the current type
                List<ModelElement> elements = model.getAcceptedElements(type);
                for (ModelElement me : elements) {
                    //System.out.println("  Element: " + me.getFullName() + " ");

                    // write out the value of each attribute of the element
                    Collection<String> attributeNames = type.getAttributeNames();
                    for (String attr : attributeNames) {
                        if (attr.matches("aggregation")){
                            if (me.getPlainAttribute(attr).matches("shared")){
                                numAggregation++;
                            }

                        }
                    }
                }
            }
        }	
        return numAggregation;
    }
}
