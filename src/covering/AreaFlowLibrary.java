/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package covering;

import FlexMap.CostAreaFlow;
import aig.Aig;
import aig.NodeAig;
import io.Logs;
import java.io.FileNotFoundException;
import kcutter.*;
import library.*;
import java.util.*;

/**
 * Classe que estende ArreaFlow utilizando apenas K-cuts com Matching na biblioteca
 * @author Julio Saraçol
 */
public class AreaFlowLibrary
{
     protected Aig                   myAig;
    protected Integer               sizeCut;
    protected CutterKCutsLibrary     kcuts;
    protected CostAreaFlow          function;
    protected Map<NodeAig, AigCutBrc>  covering;
    protected Map<NodeAig, AigCutBrc>  bestCut;
    protected Map<NodeAig,Float>    tableArea;
    protected Map<NodeAig,Integer>  levelNode;

    public AreaFlowLibrary(Aig myAig, int size, CutterKCutsLibrary cutterK,CostAreaFlow function) 
    {
        this.myAig      = myAig;
        this.sizeCut    = size;
        this.kcuts      = cutterK;
        this.function   = function;
        this.covering   = new HashMap<NodeAig, AigCutBrc>();
        this.bestCut    = new HashMap<NodeAig, AigCutBrc>();
        this.tableArea  = new HashMap<NodeAig, Float>();
        this.levelNode  = new HashMap<NodeAig, Integer>();
        System.out.print("********* START AREAFLOW LIBRARY ******");
        mapAreaFlow();
        //covering();
    }
    //** Método que aplica a cobertura baseado no AreaFlow
    protected void mapAreaFlow() 
    {
        dfsAigVisitorAreaGetLevel dfs = new dfsAigVisitorAreaGetLevel(this.levelNode);
        for(NodeAig node: myAig.getNodeInputsAig())
            this.levelNode.put(node,1);
        for(NodeAig node: myAig.getNodeOutputsAig())
        {
           if((node.isOutput())&&(node.getParents().isEmpty())) //constant
               this.levelNode.put(node,1);
           else
               node.accept(dfs);
        }
        for(NodeAig node: kcuts.getCutsBrc().keySet())
        {
          System.out.println(node.getName());  
          getBestArea(node);
        }
    }
    //**Método que dado um Nodo gera os cortes, calcula áreas e seleciona melhor área
    protected  void getBestArea(NodeAig nodeActual)
    {
        if(this.bestCut.containsKey(nodeActual))
          return;
        Map<AigCutBrc,Float> tableCost     = new HashMap<AigCutBrc, Float>();
        Set<AigCutBrc> cuts                = kcuts.getCutsBrc().get(nodeActual);
        Iterator<AigCutBrc> iterator       = cuts.iterator();
        do
        {          
           AigCutBrc cut = iterator.next(); 
           if(nodeActual.isInput())
           {
              bestCut.put(nodeActual, cut);
              tableArea.put(nodeActual,(float)0);              
              break;
           }
           else
           {
              float cost  = sumCost(cut,nodeActual);
              tableCost.put(cut, (Float)cost);              
           }           
        }while(iterator.hasNext()); //contabiliza areas
        if(!nodeActual.isInput())
            choiceBestArea(nodeActual,tableCost);
        System.out.print(" BestArea Nodo: "+nodeActual.getName()+
         " Custo: "+tableArea.get(nodeActual)+
         " Profundidade: "+levelNode.get(nodeActual)+
         " Corte:");
        bestCut.get(nodeActual).showCut();
    }
    //**Método contabiliza a área do Cut
    protected float sumCost(AigCutBrc cut, NodeAig nodeActual) 
    {
        float input     =0;
        int output      =0;
        if(nodeActual.getChildren().isEmpty())
            output =1;//fanouts
        else
            output = nodeActual.getChildren().size(); //fanouts
        if(cut.size()==1) //corte unitário sempre 1+entradas
        {
          for(NodeAig node: nodeActual.getParents())
          {
            if(!tableArea.containsKey(node))
                getBestArea(node);
            input+=tableArea.get(node);
          }
          return this.function.eval(1,this.levelNode.get(nodeActual),0,input, output,0);  //área do corte 1
        }
        for(NodeAig node:cut)
        {
            if(!tableArea.containsKey(node))
                getBestArea(node);
            input+=tableArea.get(node);
        }
        return this.function.eval(1,this.levelNode.get(nodeActual),0,input, output, 0);
    }
    //**Método faz a melhor escolha entre os cortes do Nodo
    protected void choiceBestArea(NodeAig nodeActual, Map<AigCutBrc, Float> tableCost) 
    {
        AigCutBrc cut        = null;
        AigCutBrc cutBest    = bestCost(tableCost);
        Set<AigCutBrc> cuts  = kcuts.getCutsBrc().get(nodeActual); 
        Iterator<AigCutBrc> iterator = cuts.iterator();
        do
        {    
            cut = iterator.next();
            //System.out.println("trabalhando com o nodo :"+nodeActual.getName()+" e corte :");
            //cut.showCut();
            if(((tableCost.get(cut)) <= (tableCost.get(cutBest))))
              if(cut.size() >= cutBest.size())
                if(sumLevel(cut,nodeActual) >= sumLevel(cutBest,nodeActual)) //compara a profundidade em relação ao circuito
                  cutBest = cut; 
                
        }while(iterator.hasNext());
        bestCut.put(nodeActual,cutBest);      
        tableArea.put(nodeActual, (tableCost.get(cutBest)));
    }
    //**Método contabiliza a profundidade utilizando bfs
    protected Integer sumLevel(AigCutBrc cut, NodeAig nodeActual)
    {
        if((nodeActual.isOutput())&&(nodeActual.getParents().isEmpty())) //constant
            return 1;
        bfsAigVisitorAreaSumLevel bfs = new bfsAigVisitorAreaSumLevel(levelNode, cut);
        nodeActual.accept(bfs);
        return bfs.getLevel();
    }
    //**Método que aplica a cobertura baseado em bfs utilizando as areas calculadas
    protected void covering()
    {
//        bfsAigVisitorAreaCovering bfs = new bfsAigVisitorAreaCovering(this);
//        for(NodeAig nodeActual: myAig.getNodeOutputsAig())
//        {
//            if(!this.covering.containsKey(nodeActual))
//            {
//                this.covering.put(nodeActual, this.bestCut.get(nodeActual));
//                nodeActual.accept(bfs);     
//            }
//        }
//        boolean signalOk = true;
//        while(signalOk == true)
//        {
//          Map<NodeAig,AigCutBrc> list = new HashMap<NodeAig, AigCutBrc>();
//          signalOk = false;  
//          for(Map.Entry<NodeAig,AigCutBrc> element: this.covering.entrySet())
//              for(NodeAig node: element.getValue().getCut())
//                if((!node.isInput())&&(!this.covering.containsKey(node)))
//                {
//                    list.put(node, this.bestCut.get(node));
//                    signalOk = true;
//                }
//          this.covering.putAll(list);
//        }
    }
    //**Método para visualização da cobetura final
    public void showCovering()
    {
        Iterator<Map.Entry<NodeAig,AigCutBrc>> iterator =  this.covering.entrySet().iterator();
        System.out.println("################# AREA FLOW ################################");
        do
        {
            Map.Entry<NodeAig,AigCutBrc> element = iterator.next();
            System.out.print("Covering Nodo: "+element.getKey().getName()+
                           " Custo: "+tableArea.get(element.getKey())+
                           " Profundidade: "+levelNode.get(element.getKey())+
                           " Corte:");
            element.getValue().showCut();
        }while(iterator.hasNext());
        System.out.println("############################################################");
    }
    
    //** Método de acesso a cobetura gerada no formato do objeto Covering*/
    public CoveringAreaFlow getCovering() {
        Map<NodeAig,Set<NodeAig>> finalCov = new HashMap<NodeAig, Set<NodeAig>>();
        for(Map.Entry<NodeAig,AigCutBrc> cut : this.covering.entrySet())
            finalCov.put(cut.getKey(), cut.getValue().getCut());
        CoveringAreaFlow finalCovering = new CoveringAreaFlow(finalCov,tableArea);
        return finalCovering;
    }

    //** Método de acesso aos cortes da cobetura gerada
    public Map<NodeAig, AigCutBrc> getCoveringCuts() {
        return Collections.unmodifiableMap(covering);
    }
    
    //**Método que seleciona o menor custo possivel melhor Cut*/
    protected AigCutBrc bestCost(Map<AigCutBrc, Float> tableCost) 
    {
        AigCutBrc best = null;
        for(Map.Entry<AigCutBrc,Float> nodes: tableCost.entrySet())
        {
            if(best == null)
                best = nodes.getKey();
            else
                if(tableCost.get(best) > nodes.getValue())
                    best = nodes.getKey();
        }
        return best;            
    }
    
//    public String getEqn() throws FileNotFoundException
//    {
//       String eqn = Logs.coveringToEqn(myAig, getCoveringCuts());
//       return eqn;
//    }

    //**Método que retorna os melhores cortes de cada nodo em formato baseado na classe cobertura*/
    public Map<NodeAig, Set<NodeAig>> getBestCut()
    {
        Map<NodeAig, Set<NodeAig>> bestsCut = new HashMap<NodeAig, Set<NodeAig>>();
        for(Map.Entry<NodeAig,AigCutBrc> cuts : this.bestCut.entrySet())
        {
            Set<NodeAig> cut = new HashSet<NodeAig>();
            cut.addAll(cuts.getValue().getCut());
            bestsCut.put(cuts.getKey(),cut);
        }
        return Collections.unmodifiableMap(bestsCut);
    }
}