package Grid_objects;

/**
 * Created by miisiekkk on 2018-01-19.
 */
import FEM_Main.GlobalData;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    private List<Node> ND = null;       //lista węzłów w siatce
    private List<Element> EL = null;    //lista elementów w siatce
    private GlobalData globalData;
    private int nh, ne;

    private static Grid grid = null;

    public Grid(GlobalData globalData) {
        this.globalData = globalData;
        nh = globalData.getNodeNumber();
        ne = globalData.getNe();

        ND = new ArrayList<>(nh);
        EL = new ArrayList<>(ne);
    }

    public void generateGrid() {
        //generowanie siatki, wyliczanie

        //wyliczenie zmiany wysokości i szerekości w zależności od ilości węzłów
        double dx = globalData.getDx();
        double dy = globalData.getDy();

        for (int i = 0; i < globalData.getWidthNodesNumber(); ++i)
            for (int j = 0; j < globalData.getHeightNodesNumber(); ++j) {
                double x = i * dx;
                double y = j * dy;
                boolean status = false;
                if(x == 0.0 || x == globalData.getB()){
                    status = true;
                    //System.out.println("x: " + x + " y: " + y + " status: " + Boolean.toString(status));
                    if(x == 0.0){
                        ND.add(new Node(x, y, i * globalData.getHeightNodesNumber() + j, status, globalData.getT0(), globalData.gettInf_l()));
                    } else if (x == globalData.getB()){
                        ND.add(new Node(x, y, i * globalData.getHeightNodesNumber() + j, status, globalData.getT0(), globalData.gettInf_r()));
                    } else {
                        ND.add(new Node(x,y,i * globalData.getHeightNodesNumber() + j, status, globalData.getT0(), globalData.gettInf()));
                    }
                } else {
                    ND.add(new Node(x, y, i * globalData.getHeightNodesNumber() + j, status, globalData.getT0(), globalData.gettInf()));
                }
        }
        //tworzenie listy elementów
        for (int i = 0; i < globalData.getWidthNodesNumber() - 1; ++i) {
            for (int j = 0; j < globalData.getHeightNodesNumber() - 1; ++j) {       //idzie po wysokości, kopnieta siatka
                int[] tab = new int[4];

                tab[0] = j + i * globalData.getHeightNodesNumber();
                tab[3] = tab[0] + 1;
                tab[1] = j + (i + 1) * globalData.getHeightNodesNumber();
                tab[2] = tab[1] + 1;

                Node[] nodes = new Node[4];
                int z = 0;

                ///////////////tu mi sie nie podoba
                double maxX = 0.;

                for (int nodeId : tab) {
                    nodes[z] = ND.get(nodeId);
                    if (nodes[z].getX() > maxX) {
                        maxX = nodes[z].getX();
                    }
                    z++;
                }

                Element tempElement;
                if (nodes[0].getX() == 0.) {
                    tempElement = new Element(tab, nodes, materialForX(maxX), nodes[0].gettInf());

                } else {
                    tempElement = new Element(tab, nodes, materialForX(maxX), nodes[1].gettInf());
                }
                //////////////////
                EL.add(tempElement);

                /*
                System.out.print("Nodes tab \n\n");
                System.out.print(nodes[0].getUid()+ " ");
                System.out.print(nodes[3].getUid());
                System.out.print("\n");
                System.out.print(nodes[1].getUid()+ " ");
                System.out.print(nodes[2].getUid());


                System.out.print("\n");
                System.out.print("IDarray tab \n");
                System.out.print(tab[0]+ " ");
                System.out.print(tab[3]);
                System.out.print("\n");
                System.out.print(tab[1]+ " ");
                System.out.print(tab[2]);
                System.out.print("\n");
                System.out.print("\n");
                */
            }
        }
    }

    public void setND(List<Node> ND) {
        this.ND = ND;
    }

    public void setEL(List<Element> EL) {
        this.EL = EL;
    }

    public List getND() {
        return ND;
    }

    public List getEL() {
        return EL;
    }

    public boolean setTemps(double[] temps) {
        if (temps.length == ND.size()) {
            for (int i = 0; i < globalData.getHeightNodesNumber(); i++) {
                ND.get(i).setTemp(temps[i]);
            }
            return true;
        }
        return false;
    }

    public void printNodesTemps() {
        int nodeIter = 0;
        for (int i = globalData.getWidthNodesNumber() - 1; i >= 0; i--) {
            for (int j = 0; j < globalData.getHeightNodesNumber(); j++) {
                double temp = ND.get(j * globalData.getHeightNodesNumber() + i).getTemp();
                System.out.printf("%.4f\t", temp);
            }
            System.out.println("");
        }
        System.out.println("\n\n");
    }

    public double[][] tempsAsArray() {
        double temps[][] = new double[globalData.getWidthNodesNumber()][globalData.getWidthNodesNumber()];
        int ii = 0;
        for (int i = globalData.getWidthNodesNumber() - 1; i >= 0; i--) {
            for (int j = 0; j < globalData.getHeightNodesNumber(); j++) {
                temps[ii][j] = ND.get(j * globalData.getHeightNodesNumber() + i).getTemp();
            }
            ii++;
        }
        return temps;
    }

    private Material materialForX(double x) {
        //return globalData.getMaterials().get(3);

        if (x <= 0.) {
            return globalData.getMaterials().get(0);
        } else if (x > 0.3 && x <= 0.35) {
            return globalData.getMaterials().get(1);
        } else{
            return globalData.getMaterials().get(2);
        }
    }
}
