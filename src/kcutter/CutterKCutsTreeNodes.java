package kcutter;
import aig.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe que aplica os K-cuts com limite em TreeNodes
 * @author Julio Saraçol
 */
public final class CutterKCutsTreeNodes extends CutterK
{

    public CutterKCutsTreeNodes(Aig aig, int limit)throws Exception{
        super(aig, limit);
        computeAllCuts();
    }

    public CutterKCutsTreeNodes(Aig aig, NodeAig nodeCurrent, int limit) throws CloneNotSupportedException, Exception{
        super(aig, limit);
        if(nodeCurrent == null){
            System.out.println("NODO NÃO EXISTE NO CIRCUITO");
            System.exit(-1);
        }
        computeAllCuts(nodeCurrent);
    }
    
    @Override
    protected void computeAllCuts() 
    {
        for(NodeAig nodeCurrent : aig.getAllNodesAig())
           try {
            computeKcuts(nodeCurrent);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(CutterKCutsTreeNodes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void computeAllCuts(NodeAig nodeCurrent) throws CloneNotSupportedException 
    {
        computeKcuts(nodeCurrent);
    }

    private Set<AigCut> computeKcuts(NodeAig nodeCurrent) throws CloneNotSupportedException 
    {
        
        if(this.cuts.containsKey(nodeCurrent))
            return Collections.unmodifiableSet(this.cuts.get(nodeCurrent)); 
        if((nodeCurrent.isInput())||((nodeCurrent.isOutput())&&(nodeCurrent.getParents().isEmpty()))) //constant
        {
            Set<AigCut> kcut = new HashSet<AigCut>();
            kcut.add(new AigCut(nodeCurrent));
            this.cuts.put(nodeCurrent,kcut);
            return null;
        }
        else
        {
            if((nodeCurrent.isOutput())&&(nodeCurrent.getParents().size() == 1)) //inverter
            {
               if(!this.cuts.containsKey(nodeCurrent.getParents().get(0)))
                   computeKcuts(nodeCurrent.getParents().get(0));
                Set<AigCut> clone =  new HashSet<AigCut>();
                for(AigCut father: this.cuts.get(nodeCurrent.getParents().get(0)))
                    clone.add(father.clone());
                this.cuts.put(nodeCurrent,clone);
                this.cuts.get(nodeCurrent).add(new AigCut(nodeCurrent.getParents().get(0)));                   
            }
            else
            {
               Set<AigCut> k1,k2;
               if(nodeCurrent.getParents().get(0).getChildren().size() == 1)
               {
                   if(!this.cuts.containsKey(nodeCurrent.getParents().get(0)))
                      computeKcuts(nodeCurrent.getParents().get(0));
                   k1 = this.cuts.get(nodeCurrent.getParents().get(0));
               }
               else
               {
                   k1 = new HashSet<AigCut>();
                   k1.add(new AigCut(nodeCurrent.getParents().get(0)));                  
               }
               if(nodeCurrent.getParents().get(1).getChildren().size() == 1)
               {
                   if(!this.cuts.containsKey(nodeCurrent.getParents().get(1)))
                      computeKcuts(nodeCurrent.getParents().get(1));
                   k2 = this.cuts.get(nodeCurrent.getParents().get(1));
               }
               else
               {
                   k2 = new HashSet<AigCut>();
                   k2.add(new AigCut(nodeCurrent.getParents().get(1)));                                     
               }
               
               Set<AigCut> combined = combineIrredundant(nodeCurrent,k1, k2);
               //combined.add(new AigCut(nodeCurrent));
               if(this.cuts.containsKey(nodeCurrent))
                   this.cuts.get(nodeCurrent).addAll(combined);
               else
                   this.cuts.put(nodeCurrent, combined);
            }
            return null;
        }
    }
}
