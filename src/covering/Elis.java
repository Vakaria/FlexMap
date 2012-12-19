package covering;

import aig.*;
import tree.*;
import FlexMap.Algorithms;

import java.util.*;
/**
 * Classe que aplica a cobertura de acordo com  algoritmo da Elis (Correia,2005)
 * @author Julio Saraçol
 */
public class Elis 
{

    protected Trees trees;
    protected Integer s;
    protected Integer p;
    
    public Elis(Trees trees, Integer s, Integer p)
    {
        this.s = s;
        this.p = p;
        this.trees = trees;
    }
    
    
}
