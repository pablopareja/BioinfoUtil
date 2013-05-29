/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.lib.bioinfo.bioinfoutil.uniprot;

import com.era7.lib.bioinfoxml.PredictedGene;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author ppareja
 */
public class UniprotProteinRetreiver {

    public static String URL_UNIPROT = "http://www.uniprot.org/uniprot/";

    public static PredictedGene getUniprotDataFor(PredictedGene gene, boolean withSequence) throws Exception {


        String columnsParameter = "protein names,organism,comment(FUNCTION),ec,interpro,go,pathway,families,keywords,length,subcellular locations,citation,genes,go-id,domains,length";
        if(withSequence){
            columnsParameter += ",sequence";
        }

        PostMethod post = new PostMethod(URL_UNIPROT);
        post.addParameter("query", "accession:" + gene.getAnnotationUniprotId());
        post.addParameter("format", "tab");
        post.addParameter("columns", columnsParameter);

        // execute the POST
        String response = null;
        HttpClient client = new HttpClient();
        do {
            System.out.println("Performing POST request...");
            int status = client.executeMethod(post);
            InputStream inStream = post.getResponseBodyAsStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

            //la primera linea me la salto que son las cabeceras
            reader.readLine();
            response = reader.readLine();
            if (response == null) {
                System.out.println("There was no response, trying again....");
            }else{
                String tempLine = null;
                while((tempLine = reader.readLine()) != null){
                    response += "\n" + tempLine;
                }
            }
        } while (response == null);


        int maxI = 16;
        if(withSequence){
            maxI = 17;
        }

        String[] columns = response.split("\t");
//        System.out.println("columns = " + columns);
//        System.out.println("columns.length = " + columns.length);

        for (int i = 0; i < maxI; i++) {
            String currentValue = "";
//            int index = response.indexOf("\t");
//            if (i < 11) {
//                currentValue = response.substring(0, index);
//            } else {
//                currentValue = response.replaceFirst("\t", "");
//            }

            currentValue = columns[i];

            //System.out.println("i = " + i + " currentValue = " +  currentValue);
            switch (i) {
                case 0:
                    gene.setProteinNames(currentValue);
                    break;
                case 1:
                    gene.setOrganism(currentValue);
                    break;
                case 2:
                    gene.setCommentFunction(currentValue);
                    break;
                case 3:
                    gene.setEcNumbers(currentValue);
                    break;
                case 4:
                    gene.setInterpro(currentValue);
                    break;
                case 5:
                    gene.setGeneOntology(currentValue);
                    break;
                case 6:
                    gene.setPathway(currentValue);
                    break;
                case 7:
                    gene.setProteinFamily(currentValue);
                    break;
                case 8:
                    gene.setKeywords(currentValue);
                    break;
                case 9:
                    gene.setLength(Integer.parseInt(currentValue));
                    break;
                case 10:
                    gene.setSubcellularLocations(currentValue);
                    break;
                case 11:
                    gene.setPubmedId(currentValue);
                    break;
                case 12:
                    gene.setGeneNames(currentValue);
                    break;
                case 13:
                    gene.setGeneOntologyId(currentValue);
                    break;
                case 14:
                    gene.setDomains(currentValue);
                    break;
                case 15:
                    gene.setLength(Integer.parseInt(currentValue));
                    break;
                case 16:
                    gene.setSequence(currentValue.replaceAll(" ", ""));
                    break;

            }

//            response = response.substring(index);
//            if (i != 10) {
//                response = response.replaceFirst("\t", "");
//            }

        }

        //pongo como accession el unipot id
        gene.setAccession(gene.getAnnotationUniprotId());

        return gene;
    }
}
