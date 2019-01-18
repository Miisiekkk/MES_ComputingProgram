package Grid_objects;

/**
 * Created by miisiekkk on 2018-01-17.
 */

import Grid_objects.Material;
import Grid_objects.Node;
import Grid_objects.Surface;

import java.util.LinkedList;
import java.util.List;

public class Element {

    private int [] IDArray = null;
    private Node[] nodes = null;
    private Surface[] surfaces = null;
    private int nodesOfBorders = 0;
    private List<Integer> IDOfBordersSurfaces = null;
    private Material material;
    private double tInf;


    public Element(int[] IDArray, Node[] nodes) {
        this.IDArray = IDArray;
        this.nodes = nodes;
        IDOfBordersSurfaces = new LinkedList<>();

        surfaces = new Surface[4];
        surfaces[0] = new Surface(nodes[3], nodes[0]);
        surfaces[1] = new Surface(nodes[0], nodes[1]);
        surfaces[2] = new Surface(nodes[1], nodes[2]);
        surfaces[3] = new Surface(nodes[2], nodes[3]);

        for(Surface surface: surfaces)  //czy powierzchnia jest na brzegu (ofc po wezlach)
            if(surface.getSurfaceNodes()[0].isStatus() && surface.getSurfaceNodes()[1].isStatus()) nodesOfBorders++;

        for(int i = 0; i < 4; i++)
            if(surfaces[i].getSurfaceNodes()[0].isStatus() && surfaces[i].getSurfaceNodes()[1].isStatus()) IDOfBordersSurfaces.add(i);
    }

    public Element(int[] IDArray, Node[] nodes, Material material, double tInf) {
        this.IDArray = IDArray;
        this.nodes = nodes;
        IDOfBordersSurfaces = new LinkedList<>();
        this.material = material;
        this.tInf = tInf;

        surfaces = new Surface[4];
        surfaces[0] = new Surface(nodes[3], nodes[0], this.material.getAlfa(), this.tInf);
        surfaces[1] = new Surface(nodes[0], nodes[1], this.material.getAlfa(), this.tInf);
        surfaces[2] = new Surface(nodes[1], nodes[2], this.material.getAlfa(), this.tInf);
        surfaces[3] = new Surface(nodes[2], nodes[3], this.material.getAlfa(), this.tInf);

        for(Surface surface: surfaces)  //czy powierzchnia jest na brzegu (ofc po wezlach)
            if(surface.getSurfaceNodes()[0].isStatus() && surface.getSurfaceNodes()[1].isStatus()) nodesOfBorders++;

        for(int i = 0; i < 4; i++)
            if(surfaces[i].getSurfaceNodes()[0].isStatus() && surfaces[i].getSurfaceNodes()[1].isStatus()) IDOfBordersSurfaces.add(i);
        System.out.println("x: " + this.nodes[1].getX() + " y: " + this.nodes[1].getX() + "material: " + this.material.getName() + " tinf: " + tInf);
    }

    public int getNodesOfBorders() {
        return nodesOfBorders;
    }

    public int[] getIDArray() {
        return IDArray;
    }

    public void setIDArray(int[] IDArray) {
        this.IDArray = IDArray;
    }

    public void setIdOfIndex(int index, int id){
        IDArray[index] = id;
    }

    public List<Integer> getIDOfBordersSurfaces() {
        return IDOfBordersSurfaces;
    }

    public Surface[] getSurfaces() {
        return surfaces;
    }

    public Surface getSurfaceOfId(int id){
        if(id >= 0 && id <= surfaces.length) return surfaces[id];
        else return null;
    }

    public void setSurfaces(Surface [] surface){
        surfaces = surface;
    }

    public Material getMaterial() {
        return material;
    }
}