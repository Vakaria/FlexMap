package graph;
import java.util.ArrayList;

/** 
* Classe que define o vertex do graph
* @author vini
**/
public class Vertex {

    protected int id;
    protected ArrayList<Edge> adjacencies;
	
    /** Contrutor passando novo ID da aresta @param int ID*/
    protected Vertex(int id) {
        this.id = id;
        this.adjacencies = new ArrayList<Edge>();
    }
    
    /** Metodo que recebe um vertice e uma aresta e retorna o 
       vertice que está conectado a outra ponta da aresta. */
    public Vertex getNextVertex(Edge edge) {
        
        Vertex auxVertex = edge.getVertex1();

        if(this.equals(auxVertex)) {
            auxVertex = edge.getVertex2();
        }

        return auxVertex;
    }

    /***************** GETER AND SETTER ****************/

    public ArrayList<Edge> getAdjacencies() {
        return adjacencies;
    }

    public void setAdjacencies(ArrayList<Edge> adjacencies) {
        this.adjacencies = adjacencies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
