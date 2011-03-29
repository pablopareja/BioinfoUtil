/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.lib.bioinfo.bioinfoutil.blast;

import com.era7.lib.bioinfoxml.BlastOutput;
import com.era7.lib.bioinfoxml.Iteration;
import com.era7.lib.bioinfoxml.ProteinXML;
import com.era7.lib.bioinfoxml.ContigXML;
import com.era7.lib.bioinfoxml.Hit;
import com.era7.lib.bioinfoxml.Hsp;
import com.era7.lib.era7xmlapi.model.XMLElementException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class BlastExporter {

    public static String exportBlastXMLtoIsotigsCoverage(BlastOutput blastOutput) throws XMLElementException {

        StringBuilder stBuilder = new StringBuilder();

        stBuilder.append("<proteins>\n");

        ArrayList<Iteration> iterations = blastOutput.getBlastOutputIterations();
        //Map with isotigs/contigs per protein
        HashMap<String, ArrayList<ContigXML>> proteinContigs = new HashMap<String, ArrayList<ContigXML>>();
        //Protein info map
        HashMap<String, ProteinXML> proteinInfoMap = new HashMap<String, ProteinXML>();

        for (Iteration iteration : iterations) {
            String contigNameSt = iteration.getUniprotIdFromQueryDef();
            ContigXML contig = new ContigXML();
            contig.setId(contigNameSt);

            ArrayList<Hit> hits = iteration.getIterationHits();
            for (Hit hit : hits) {
                String proteinIdSt = hit.getHitDef().split("\\|")[1];

                ArrayList<ContigXML> contigsArray = proteinContigs.get(proteinIdSt);


                if (contigsArray == null) {
                    //Creating contigs array
                    contigsArray = new ArrayList<ContigXML>();
                    proteinContigs.put(proteinIdSt, contigsArray);
                    //Creating protein info
                    ProteinXML proteinXML = new ProteinXML();
                    proteinXML.setId(proteinIdSt);
                    proteinXML.setLength(hit.getHitLen());
                    proteinInfoMap.put(proteinIdSt, proteinXML);
                }

                ArrayList<Hsp> hsps = hit.getHitHsps();
                int hspMinHitFrom = 1000000000;
                int hspMaxHitTo = -1;

                //---Figuring out the isotig/contig positions
                for (Hsp hsp : hsps) {
                    int hspFrom = hsp.getHitFrom();
                    int hspTo = hsp.getHitTo();
//                            System.out.println("hsp = " + hsp);
//                            System.out.println("hsp.getHitFrame() = " + hsp.getHitFrame());
//                            if (hsp.getQueryFrame() < 0) {
//                                hspFrom = hsp.getHitTo();
//                                hspTo = hsp.getHitFrom();
//                            }

                    if (hspFrom < hspMinHitFrom) {
                        hspMinHitFrom = hspFrom;
                    }
                    if (hspTo > hspMaxHitTo) {
                        hspMaxHitTo = hspTo;
                    }

                    //adding hsps to contig
                    hsp.detach();
                    contig.addHsp(hsp);
                }
                //-------------------

                contig.setBegin(hspMinHitFrom);
                contig.setEnd(hspMaxHitTo);
                if (contig.getBegin() > contig.getEnd()) {
                    contig.setBegin(hspMaxHitTo);
                    contig.setEnd(hspMinHitFrom);
                }

                contigsArray.add(contig);


            }
        }

        for (String proteinKey : proteinInfoMap.keySet()) {
            //---calculating coverage and creating output xml----

            ProteinXML proteinXML = proteinInfoMap.get(proteinKey);

            ArrayList<ContigXML> contigs = proteinContigs.get(proteinKey);
            for (ContigXML contigXML : contigs) {
                proteinXML.addChild(contigXML);
            }

            proteinXML.setNumberOfIsotigs(contigs.size());

            int coveredPositions = 0;
            for (int i = 1; i <= proteinXML.getLength(); i++) {
                for (ContigXML contigXML : contigs) {
                    if (i >= contigXML.getBegin() && i <= contigXML.getEnd()) {
                        coveredPositions++;
                        break;
                    }
                }
            }

            proteinXML.setProteinCoverageAbsolute(coveredPositions);
            proteinXML.setProteinCoveragePercentage((coveredPositions * 100.0) / proteinXML.getLength());

            stBuilder.append((proteinXML.toString() + "\n"));

        }

        stBuilder.append("</proteins>\n");


        return stBuilder.toString();
    }
}
