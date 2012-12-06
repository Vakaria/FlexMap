package io;
import aig.*;
import kcutter.*;

import java.io.*;
import java.util.*;
/**
 * Classe para geração de logs dos circuitos mapeados
 * @author Julio Saraçol
 **/

public class Logs
{
/**Método que gera descrição eqn a partir de um AIG*/
   public static String AigtoEqn(Aig myAig)
  {
            
            String outString ="###############EQN GERADO#########################\n";
            String inputSymbol    = "pi_";
            String outputSymbol   = "po_";
            String defInputsEqn   = "INORDER = ";
            String defOutputsEqn  = "OUTORDER = ";
            TreeMap<String,String> symbolsEqn     = new TreeMap<String,String>();
            ArrayList<String>      primaryInput   = new ArrayList<String>();
            
            for(int i=0;i<myAig.getI();i++){
                String name = myAig.getInputsAig()[i][0];
                if(!primaryInput.contains(name)){
                  defInputsEqn = defInputsEqn +" "+ inputSymbol+ name;
                  primaryInput.add(name);
                  symbolsEqn.put(name,inputSymbol+ name);
               }
            }
            defInputsEqn +=";";
            for(int i=0;i<myAig.getO();i++){ 
                NodeAig output = myAig.getVertexName(myAig.getOutputsAig()[i][0]); 
                if(symbolsEqn.get(outputSymbol+output.getName()) == null) 
                {
                    if((Integer.parseInt(output.getName())%2) != 0)
                    {
                        if(!primaryInput.contains(String.valueOf(Integer.parseInt(output.getName())-1))) //testa se é entrada<->saida invertida
                            symbolsEqn.put(outputSymbol+output.getName(),"!["+(Integer.parseInt(output.getName())-1)+"]");    // o_3=![2]
                        else
                            symbolsEqn.put(outputSymbol+output.getName(),"!["+inputSymbol+(Integer.parseInt(output.getName())-1)+"]"); // o_3=![2]
                    }
                    else
                    {
                        if(!primaryInput.contains(output.getName()))
                            symbolsEqn.put(outputSymbol+output.getName(),"["+output.getName()+"]");     // o_2=[2]
                        else
                            symbolsEqn.put(outputSymbol+output.getName(),"["+inputSymbol+output.getName()+"]"); // o_2= i_2
                    }  
                    defOutputsEqn = defOutputsEqn +" "+ outputSymbol+output.getName(); 
                }
            }
            defOutputsEqn += ";";      
            outString += defInputsEqn+"\n"+defOutputsEqn+"\n";
            
            Iterator<Map.Entry<String,String>> iteratorSymbol = symbolsEqn.entrySet().iterator();
            while(iteratorSymbol.hasNext())
            {
                    Map.Entry<String,String> current = iteratorSymbol.next();
                    if(current.getKey().contains("po_"))
                        outString += current.getKey()+" = "+current.getValue()+";\n";
                    else
                        outString += "["+current.getKey()+"]"+" = "+current.getValue()+";\n";
            } 
            //-------------------inputs-Outputs---------------------------------
            bfsNodeAigVisitorAigtoEqn bfsEqn = new bfsNodeAigVisitorAigtoEqn();
            for(NodeAig output: myAig.getNodeOutputsAig())
              output.accept(bfsEqn);  
            outString += bfsEqn.getEqnDescription();   
            return outString;
   }  
   /**Método que gera eqn para arquivo de saída*/
   public static void LogsWriteEqn(String eqn,String fileLog)throws FileNotFoundException, IOException
   {
        if(!fileLog.contains(".eqn"))
        {
                System.out.println("ARQUIVO DE SAIDA ERRADO");
                System.exit(-1);
        }
        File outFile = new File(fileLog);
        FileOutputStream buffStart;
        
        outFile.setWritable(true);
        buffStart = new FileOutputStream(outFile,true);
        buffStart.write(eqn.getBytes());
        buffStart.write('\n');
        buffStart.close();
        System.out.println("#Log Eqn Executado com Sucesso");
   }
   /**Método que gera descrição eqn a partir de um AIG*/
   public static String coveringToEqn(Aig myAig, Map<NodeAig,AigCut> covering) throws FileNotFoundException 
   {
       ArrayList<NodeAig> nodesPath = new ArrayList<NodeAig>();
       String outString ="###############EQN GERADO#########################\n";
       String inputSymbol    = "pi_";
       String outputSymbol   = "po_";
       String defInputsEqn   = "INORDER = ";
       String defOutputsEqn  = "OUTORDER = ";
       TreeMap<String,String> symbolsEqn     = new TreeMap<String,String>();
       ArrayList<String>      primaryInput   = new ArrayList<String>();
       //--inputs and outputs--------------------------------------------------     
       for(int i=0;i<myAig.getI();i++){
                String name = myAig.getInputsAig()[i][0];
                if(!primaryInput.contains(name)){
                  defInputsEqn = defInputsEqn +" "+ inputSymbol+ name;
                  primaryInput.add(name);
                  symbolsEqn.put(name,inputSymbol+ name);
               }
          }
        defInputsEqn +=";";
        for(int i=0;i<myAig.getO();i++){ 
           NodeAig output = myAig.getVertexName(myAig.getOutputsAig()[i][0]); 
           if(symbolsEqn.get(outputSymbol+output.getName()) == null) 
           {
               if((Integer.parseInt(output.getName())%2) != 0)
               {
                   if(!primaryInput.contains(String.valueOf(Integer.parseInt(output.getName())-1))) //testa se é entrada<->saida invertida
                     symbolsEqn.put(outputSymbol+output.getName(),"!["+(Integer.parseInt(output.getName())-1)+"]");    // o_3=![2]
                   else
                      symbolsEqn.put(outputSymbol+output.getName(),"!["+inputSymbol+(Integer.parseInt(output.getName())-1)+"]"); // o_3=![2]
               }
               else
               {
                   if(!primaryInput.contains(output.getName()))
                      symbolsEqn.put(outputSymbol+output.getName(),"["+output.getName()+"]");     // o_2=[2]
                   else
                      symbolsEqn.put(outputSymbol+output.getName(),"["+inputSymbol+output.getName()+"]"); // o_2= i_2
               }  
               defOutputsEqn = defOutputsEqn +" "+ outputSymbol+output.getName(); 
           }
        }
        defOutputsEqn += ";";      
        outString += defInputsEqn+"\n"+defOutputsEqn+"\n";
        Iterator<Map.Entry<String,String>> iteratorSymbol = symbolsEqn.entrySet().iterator();
        while(iteratorSymbol.hasNext())
        {
           Map.Entry<String,String> current = iteratorSymbol.next();
           if(current.getKey().contains("po_"))
                outString += current.getKey()+" = "+current.getValue()+";\n";
           else
                outString += "["+current.getKey()+"]"+" = "+current.getValue()+";\n";
        }  
        //--inputs and outputs--------------------------------------------------            
        String outString1="";
        for(Map.Entry<NodeAig,AigCut> cell: covering.entrySet())
        {
            if((!cell.getKey().isInput())&&(!nodesPath.contains(cell.getKey())))
            {
                if(!(((Integer.parseInt(cell.getKey().getName())%2) != 0)&&(cell.getValue().size()==1)))
                {
                  dfsNodeAigVisitorCutToEqn dfsEqn = new dfsNodeAigVisitorCutToEqn(cell.getValue());
                  if((Integer.parseInt(cell.getKey().getName())%2) != 0)
                  {
                      if(!nodesPath.contains(cell.getKey().getParents().get(0)))
                      {
                        nodesPath.add(cell.getKey().getParents().get(0));
                        cell.getKey().getParents().get(0).accept(dfsEqn);
                        outString1 += "["+(String.valueOf(Integer.parseInt(cell.getKey().getName())-1))+"]="+dfsEqn.getEqnDescription(cell.getKey().getParents().get(0))+";\n";
                      }
                  }
                  else
                  {
                      if(!nodesPath.contains(cell.getKey()))
                      {
                        nodesPath.add(cell.getKey());
                        cell.getKey().accept(dfsEqn);
                        outString1 += "["+cell.getKey().getName()+"]="+dfsEqn.getEqnDescription(cell.getKey())+";\n";
                      }
                  }
                }
            }
        }
        outString+=outString1;
        System.out.println(outString);
        return outString;
  }    
  //*-------------------------For Kcuts---------------------------------------
  public static void LogsAigKcuts(String fileLog, CutterK cutterK)throws FileNotFoundException, IOException
  {
        File outFile = new File(fileLog);
        FileOutputStream buffStart;
        
        outFile.setWritable(true);       
        buffStart = new FileOutputStream(outFile,true);
        String out = "#############KCUTS###################\nSize Cut K= "+cutterK.getLimit()+"\n"+"Cuts:\n";
        buffStart.write(out.getBytes());
        String nodes = "Node:";
        Iterator<Map.Entry<NodeAig,Set<AigCut>>> node = cutterK.getCuts().entrySet().iterator();
        while(node.hasNext())
        {
           Map.Entry<NodeAig,Set<AigCut>> cuts = node.next();
           buffStart.write(nodes.getBytes());
           buffStart.write(cuts.getKey().getName().getBytes());
           buffStart.write('\n');
           for(AigCut singleCut: cuts.getValue())
             buffStart.write(singleCut.getBytesForLog().getBytes());
        }
        nodes = "########################################";
        buffStart.write(nodes.getBytes());
        buffStart.close();
        System.out.println("#Log Kcuts Executado com Sucesso");
  }
  
  public static void LogsAigKcuts(String fileLog, CutterK cutterK,NodeAig nodeSelect)throws FileNotFoundException, IOException
  {
        File outFile = new File(fileLog);
        FileOutputStream buffStart;
        
        outFile.setWritable(true);       
        buffStart = new FileOutputStream(outFile,true);
        String out = "#############KCUTS###################\nCut K= "+cutterK.getLimit()+"\n";
        buffStart.write(out.getBytes());
        String nodes = "Node:";
        Set<AigCut> cuts = cutterK.getCuts().get(nodeSelect);
        buffStart.write(nodes.getBytes());
        buffStart.write(nodeSelect.getName().getBytes());
        buffStart.write('\n');
        for(AigCut singleCut: cuts)
             buffStart.write(singleCut.getBytesForLog().getBytes());
        nodes = "########################################";
        buffStart.write(nodes.getBytes());
        buffStart.close();
        System.out.println("#Log Kcuts Executado com Sucesso");
  }
  
  public static void LogsAigKcuts(String fileLog, CutterKCutsLibrary cutterKLibrary)throws FileNotFoundException, IOException
  {
        File outFile = new File(fileLog);
        FileOutputStream buffStart;
        
        outFile.setWritable(true);       
        buffStart = new FileOutputStream(outFile,true);
        String out = "#############KCUTS###################\nSize Cut K= "+cutterKLibrary.getLimit()+"\n"+"Cuts:\n";
        buffStart.write(out.getBytes());
        String nodes = "Node:";
        Iterator<Map.Entry<NodeAig,Set<AigCutBrc>>> node = cutterKLibrary.getCutsBrc().entrySet().iterator();
        while(node.hasNext())
        {    
           Map.Entry<NodeAig,Set<AigCutBrc>> cuts = node.next();
           buffStart.write(nodes.getBytes());
           buffStart.write(cuts.getKey().getName().getBytes());
           buffStart.write('\n');
           for(AigCut singleCut: cuts.getValue())
             buffStart.write(singleCut.getBytesForLog().getBytes());
        }
        nodes = "########################################";
        buffStart.write(nodes.getBytes());
        buffStart.write(cutterKLibrary.logs.getBytes());
        buffStart.close();
        System.out.println("#Log Kcuts Library Executado com Sucesso");
  }
  
  public static void LogsAigKcuts(String fileLog, CutterKCutsLibrary cutterKLibrary,NodeAig nodeSelect)throws FileNotFoundException, IOException
  {
        File outFile = new File(fileLog);
        FileOutputStream buffStart;
        
        outFile.setWritable(true);       
        buffStart = new FileOutputStream(outFile,true);
        String out = "#############KCUTS###################\nCut K= "+cutterKLibrary.getLimit()+"\n";
        buffStart.write(out.getBytes());
        String nodes = "Node:";
        Set<AigCutBrc> cuts = cutterKLibrary.getCutsBrc().get(nodeSelect);
        buffStart.write(nodes.getBytes());
        buffStart.write(nodeSelect.getName().getBytes());
        buffStart.write('\n');
        for(AigCutBrc singleCut: cuts)
             buffStart.write(singleCut.getBytesForLog().getBytes());
        nodes = "########################################";
        buffStart.write(nodes.getBytes());
        buffStart.close();
        System.out.println("#Log Kcuts Library Executado com Sucesso");
  }
  //*---------------------------------------------------------------------------    
  
}