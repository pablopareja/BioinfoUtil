/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.lib.bioinfo.bioinfoutil.seq;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class SeqUtil {
    
    public static String getComplementaryInverted(String sequence){
        
        StringBuilder result = new StringBuilder();
        
        for (int i = sequence.length() - 1 ; i >= 0 ; i--) {
            
            char currentChar = sequence.charAt(i);
            char valueToAppend;
            
            if(Character.isUpperCase(currentChar)){
                
                if(currentChar == 'A'){
                    valueToAppend = 'T';
                }else if(currentChar == 'T'){
                    valueToAppend = 'A';
                }if(currentChar == 'C'){
                    valueToAppend = 'G';
                }else if(currentChar == 'G'){
                    valueToAppend = 'C';
                }else{
                    valueToAppend = 'N';
                }
                        
            }else{
                
                if(currentChar == 'a'){
                    valueToAppend = 't';
                }else if(currentChar == 't'){
                    valueToAppend = 'a';
                }if(currentChar == 'c'){
                    valueToAppend = 'g';
                }else if(currentChar == 'g'){
                    valueToAppend = 'c';
                }else{
                    valueToAppend = 'n';
                }
                
            }
            
            result.append(valueToAppend);
            
        }
        
        
        return result.toString();
    }
    
}