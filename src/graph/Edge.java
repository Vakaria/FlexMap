package graph;
/** 
* Classe define as edges de graph
* @author vini
**/
public class Edge {

    protected int id;
    protected Vertex vertex1;
    protected Vertex vertex2;

    /** construtor da aresta @param int ID vértice1 vértice2*/
    protected Edge(int id, Vertex vertex1, Vertex vertex2) {
        this.id = id;
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    /***************** GETER AND SETTER ****************/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vertex getVertex1() {
        return vertex1;
    }

    public void setVertex1(Vertex vertex1) {
        this.vertex1 = vertex1;
    }

    public Vertex getVertex2() {
        return vertex2;
    }

    public void setVertex2(Vertex vertex2) {
        this.vertex2 = vertex2;
    }
 
}
