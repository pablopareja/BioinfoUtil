/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.lib.bioinfo.bioinfoutil.gephi;

import com.era7.lib.bioinfoxml.ProteinXML;
import com.era7.lib.bioinfoxml.gexf.AttValueXML;
import com.era7.lib.bioinfoxml.gexf.AttValuesXML;
import com.era7.lib.bioinfoxml.gexf.AttributeXML;
import com.era7.lib.bioinfoxml.gexf.AttributesXML;
import com.era7.lib.bioinfoxml.gexf.EdgeXML;
import com.era7.lib.bioinfoxml.gexf.GexfXML;
import com.era7.lib.bioinfoxml.gexf.GraphXML;
import com.era7.lib.bioinfoxml.gexf.NodeXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizColorXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizPositionXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizSizeXML;
import com.era7.lib.bioinfoxml.go.GoAnnotationXML;
import com.era7.lib.bioinfoxml.go.GoTermXML;
import com.era7.lib.era7xmlapi.model.XMLElementException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GephiExporter {

    public static double DEFAULT_GO_SIZE = 5.0;
    public static double DEFAULT_PROTEIN_SIZE = 20.0;

    public static String exportGoAnnotationToGexf(GoAnnotationXML goAnnotationXML,
            VizColorXML proteinColor,
            VizColorXML goColor,
            Boolean proportionalSize) throws XMLElementException {


        StringBuilder stBuilder = new StringBuilder();

        StringBuilder nodesXMLStBuilder = new StringBuilder("<nodes>\n");
        StringBuilder edgesXMLStBuilder = new StringBuilder("<edges>\n");

        int edgesIdCounter = 1;

        stBuilder.append("<" + GexfXML.TAG_NAME + ">\n");
        stBuilder.append("<" + GraphXML.TAG_NAME + " defaultedgetype=\"directed\">\n");

        AttributesXML attributesXML = new AttributesXML();
        attributesXML.setClass(AttributesXML.NODE_CLASS);
        AttributeXML idAttributeXML = new AttributeXML();
        idAttributeXML.setId("0");
        idAttributeXML.setTitle("ID");
        idAttributeXML.setType("string");
        attributesXML.addAttribute(idAttributeXML);
        AttributeXML nameAttributeXML = new AttributeXML();
        nameAttributeXML.setId("1");
        nameAttributeXML.setTitle("Name");
        nameAttributeXML.setType("string");
        attributesXML.addAttribute(nameAttributeXML);

        stBuilder.append((attributesXML.toString() + "\n"));


        List<GoTermXML> goTerms = goAnnotationXML.getAnnotatorGoTerms();
        Element proteinAnnotations = goAnnotationXML.getProteinAnnotations();
        List<Element> proteins = proteinAnnotations.getChildren(ProteinXML.TAG_NAME);


        //-----go terms-------------
        for (GoTermXML goTerm : goTerms) {
            NodeXML nodeXML = new NodeXML();
            nodeXML.setId(goTerm.getId());
            nodeXML.setLabel(goTerm.getGoName());
            nodeXML.setColor(new VizColorXML((Element) goColor.asJDomElement().clone()));
            //nodeXML.setSize(new VizSizeXML((Element) goSize.asJDomElement().clone()));

            //---------size---------------------
            if (proportionalSize) {
                nodeXML.setSize(new VizSizeXML(goTerm.getAnnotationsCount() * 5.0));
            } else {
                nodeXML.setSize(new VizSizeXML(DEFAULT_GO_SIZE));
            }

            //---------position--------------------
            nodeXML.setPosition(new VizPositionXML(0, 0, 0));

            AttValuesXML attValuesXML = new AttValuesXML();
            AttValueXML nameAttValue = new AttValueXML();
            nameAttValue.setFor(1);
            nameAttValue.setValue(goTerm.getGoName());
            attValuesXML.addAttValue(nameAttValue);
            nodeXML.setAttvalues(attValuesXML);

            nodesXMLStBuilder.append((nodeXML.toString() + "\n"));
        }

        //-----------proteins-------------
        for (Element protElem : proteins) {

            ProteinXML proteinXML = new ProteinXML(protElem);
            NodeXML nodeXML = new NodeXML();
            nodeXML.setId(proteinXML.getId());
            nodeXML.setLabel(proteinXML.getId());
            nodeXML.setColor(new VizColorXML((Element) proteinColor.asJDomElement().clone()));
            nodeXML.setSize(new VizSizeXML(DEFAULT_PROTEIN_SIZE));
            //---------position--------------------
            nodeXML.setPosition(new VizPositionXML(0, 0, 0));

            AttValuesXML attValuesXML = new AttValuesXML();
            AttValueXML nameAttValue = new AttValueXML();
            nameAttValue.setFor(1);
            nameAttValue.setValue(proteinXML.getId());
            attValuesXML.addAttValue(nameAttValue);
            nodeXML.setAttvalues(attValuesXML);

            nodesXMLStBuilder.append((nodeXML.toString() + "\n"));

            //----edges----
            List<GoTermXML> proteinTerms = new ArrayList<GoTermXML>();
            List<GoTermXML> bioTerms = proteinXML.getBiologicalProcessGoTerms();
            List<GoTermXML> cellTerms = proteinXML.getCellularComponentGoTerms();
            List<GoTermXML> molTerms = proteinXML.getMolecularFunctionGoTerms();
            if (bioTerms != null) {
                proteinTerms.addAll(bioTerms);
            }
            if (cellTerms != null) {
                proteinTerms.addAll(cellTerms);
            }
            if (molTerms != null) {
                proteinTerms.addAll(molTerms);
            }

            for (GoTermXML goTermXML : proteinTerms) {
                EdgeXML edge = new EdgeXML();
                edge.setId(String.valueOf(edgesIdCounter++));
                edge.setTarget(proteinXML.getId());
                edge.setSource(goTermXML.getId());
                edge.setType(EdgeXML.DIRECTED_TYPE);

                edgesXMLStBuilder.append((edge.toString() + "\n"));
            }

        }


        stBuilder.append((nodesXMLStBuilder.toString() + "</nodes>\n"));
        stBuilder.append((edgesXMLStBuilder.toString() + "</edges>\n"));

        stBuilder.append("</" + GraphXML.TAG_NAME + ">\n");
        stBuilder.append("</" + GexfXML.TAG_NAME + ">\n");

        return stBuilder.toString();
    }
}
