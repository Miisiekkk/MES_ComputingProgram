package Grid_objects;

import Grid_objects.Node;

/**
 * Created by miisiekkk on 2018-01-19.
 */
public class Surface {
    private final Node[] surfaceNodes;
    private double [][] shapefValues;
    private double alfa;
    private double tInf;

    public Surface(Node node1, Node node2, double alfa, double tInf) {
        this.surfaceNodes = new Node[2];
        this.surfaceNodes[0] = node1;
        this.surfaceNodes[1] = node2;
        this.alfa = alfa;
        this.tInf = tInf;
    }

    public Surface(Node node1, Node node2) {
        this.surfaceNodes = new Node[2];
        this.surfaceNodes[0] = node1;
        this.surfaceNodes[1] = node2;
    }


    public void setShapefValues(double[][] shapefValues) {
        this.shapefValues = shapefValues;
    }

    public double[][] getShapefValues() {
        return shapefValues;
    }

    public Node[] getSurfaceNodes() {
        return surfaceNodes;
    }

    public double getAlfa() {
        return alfa;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public double gettInf() {
        return tInf;
    }

    public void settInf(double tInf) {
        this.tInf = tInf;
    }
}