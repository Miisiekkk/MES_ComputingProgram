package FEM_Main;

/**
 * Created by miisiekkk on 2018-01-20.
 */
import Grid_objects.*;
import Jama.Matrix;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import java.io.FileReader;

public class GlobalData {
    private double H;   //wysokosc siatki
    private double B;   //szerokosc siatki
    private double dy;
    private double dx;
    private int nH;         //liczba wezlow na wysokosci
    private int nB;         //liczba wezlow na szerokosci
    private int nh;         //liczba wezlow
    private int ne;         //liczba elementow
    private double t0;      //temperatura poczatkowa
    private double tau;     //czas trwania calego procesu
    private double dTau;    //zmiana czasu
    private double tInf;    //stala temperatura otoczenia
    private double tInf_l;
    private double tInf_r;
    private double alfa;    //wsp wymiany ciepla
    private double c;       //cieplo wlasciwe
    private double k;       //wsp. przewodzenia ciepla
    private double ro;      //gestosc materialu

    private Matrix hCurrentMatrix;    //macierz sztywnosci obecna (dla konkretnego elementow), wsp, układu równan H
    private Matrix hGlobalMatrix;     //macierz globalna współczynnikow układu równań H
    private double[] pCurrentMatrix;  //wektor obciazen obecny(dla konkretnego elementu) prawej czesci ukladu rownan P
    private double[] pGlobal;   //wektor globalny P(cały ten z c/dt)
    private Matrix dndEta;
    private Matrix dndXi;
    private Matrix shapeFunc;
    private Element newElement;      //element lokalny

    private transient List<Material> materials;
    private static GlobalData globalData;

    private Grid grid;

    public GlobalData() {

    }

    public GlobalData(boolean x){
        GlobalData dataGetter = this.dataReader();
        if(dataGetter != null) {
            this.B = dataGetter.getB();
            this.H = dataGetter.getH();
            this.nB = dataGetter.getWidthNodesNumber();
            this.nH = dataGetter.getHeightNodesNumber();
            this.tInf = dataGetter.gettInf();
            this.t0 = dataGetter.getT0();
            this.c = dataGetter.getC();
            this.tau = dataGetter.getTau();
            this.dTau = dataGetter.getdTau();
            this.k = dataGetter.getK();
            this.ro = dataGetter.getRo();
            this.alfa = dataGetter.getAlfa();
            this.tInf_l = dataGetter.gettInf_l();
            this.tInf_r = dataGetter.gettInf_r();


            this.setNh(this.getHeightNodesNumber() * this.getWidthNodesNumber());
            this.setNe((this.getWidthNodesNumber() - 1) * (this.getHeightNodesNumber() - 1));
            this.setDx(this.getB() / (this.getWidthNodesNumber() - 1));
            this.setDy(this.getH() / (this.getHeightNodesNumber() - 1));

            this.hCurrentMatrix = null;
            this.pCurrentMatrix = null;
            this.hGlobalMatrix = new Matrix(this.nh, this.nh);
            this.pGlobal = new double[this.nh];
            for (double elem: pGlobal) elem = 0.;


            derivativesMatrixdEtadPsi();
            localElementCreator();
            generateMaterials();

            grid = new Grid(this);
            grid.generateGrid();
        }
    }

    private GlobalData dataReader(){
        try {
            Gson gson = new Gson();
            GlobalData gettingData = gson.fromJson(new FileReader(System.getProperty("user.dir") + "/data/data.txt"), GlobalData.class);
            return gettingData;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    private void derivativesMatrixdEtadPsi(){
        Point[] points = new Point[4];
        points[0] = new Point(-0.577, -0.577);
        points[1] = new Point(0.577, -0.577);
        points[2] = new Point(0.577, 0.577);
        points[3] = new Point(-0.577, 0.577);

        dndEta = new Matrix(4, 4);
        for(int i = 0; i < 4; i++){ //pochodna fukcji ksztaltu po eta
            dndEta.set(i,0, ShapeFunction.sfDerivativeETA1(points[i].getX()));
            dndEta.set(i,1, ShapeFunction.sfDerivativeETA2(points[i].getX()));
            dndEta.set(i,2, ShapeFunction.sfDerivativeETA3(points[i].getX()));
            dndEta.set(i,3, ShapeFunction.sfDerivativeETA4(points[i].getX()));
        }

        dndXi = new Matrix(4, 4); //pochodna fukcji ksztaltu po xi
        for(int i = 0; i < 4; i++){
            dndXi.set(i,0, ShapeFunction.sfDerivativePSI1(points[i].getY()));
            dndXi.set(i,1, ShapeFunction.sfDerivativePSI2(points[i].getY()));
            dndXi.set(i,2, ShapeFunction.sfDerivativePSI3(points[i].getY()));
            dndXi.set(i,3, ShapeFunction.sfDerivativePSI4(points[i].getY()));
        }
        shapeFunc = new Matrix(4,4); //wartosci fukncji ksztaltu dla objetosci (dla kazdego z 4 puntkow 4 fukncje ksztaltu)
        for(int i = 0; i < 4; i++){
            shapeFunc.set(i,0, ShapeFunction.shapeFunction1(points[i].getX(), points[i].getY()));
            shapeFunc.set(i,1, ShapeFunction.shapeFunction2(points[i].getX(), points[i].getY()));
            shapeFunc.set(i,2, ShapeFunction.shapeFunction3(points[i].getX(), points[i].getY()));
            shapeFunc.set(i,3, ShapeFunction.shapeFunction4(points[i].getX(), points[i].getY()));
        }
    }

    private void generateMaterials(){
        Material concrete = new Material("Aerated Concrete", 840, 600, 0.21, 4.397);
        Material styrofoam = new Material("Styrofoam", 1460, 20, 0.043, 4.397);
        Material concrete_2 = new Material("Aerated Concrete", 840, 600, 0.21, 3.780);

        materials = new ArrayList<>(2);
        materials.add(0, concrete);
        materials.add(1, styrofoam);
        materials.add(2,concrete_2);

    }

    private void localElementCreator(){
        Node[] nodesLocal = new Node[4];
        Point [] integrationPts = new Point[4]; //lokalne pkt całkowania po objetosci
        integrationPts[0] = new Point(-0.577, -0.577);
        integrationPts[1] = new Point(0.577, -0.577);
        integrationPts[2] = new Point(0.577, 0.577);
        integrationPts[3] = new Point(-0.577, 0.577);

        for (int i = 0; i < nodesLocal.length; i++) nodesLocal[i] = new Node(integrationPts[i]);

        int [] tempIDS = new int[]{0,0,0,0};
        newElement = new Element(tempIDS, nodesLocal);

        Surface [] surfacesTab = new Surface[4]; //lokalne punkty calkowania po powierzchni
        surfacesTab[0] = new Surface(new Node(new Point(-1.0, 0.577)), new Node(new Point(-1.0, -0.577)));
        surfacesTab[1] = new Surface(new Node(new Point(-0.577, -1)), new Node(new Point(0.577, -1.)));
        surfacesTab[2] = new Surface(new Node(new Point(1.0, -0.577)), new Node(new Point(1.0, 0.577)));
        surfacesTab[3] = new Surface(new Node(new Point(0.577, 1.0)), new Node(new Point(-0.577, 1.0)));

        newElement.setSurfaces(surfacesTab);

        for(int i = 0; i < 4; i++){
            double [][] shapeFvals = new double[2][4];
            for(int j = 0; j < 2; j++){
                shapeFvals[j][0] = ShapeFunction.shapeFunction1(surfacesTab[i].getSurfaceNodes()[j].getX(), surfacesTab[i].getSurfaceNodes()[j].getY());
                shapeFvals[j][1] = ShapeFunction.shapeFunction2(surfacesTab[i].getSurfaceNodes()[j].getX(), surfacesTab[i].getSurfaceNodes()[j].getY());
                shapeFvals[j][2] = ShapeFunction.shapeFunction3(surfacesTab[i].getSurfaceNodes()[j].getX(), surfacesTab[i].getSurfaceNodes()[j].getY());
                shapeFvals[j][3] = ShapeFunction.shapeFunction4(surfacesTab[i].getSurfaceNodes()[j].getX(), surfacesTab[i].getSurfaceNodes()[j].getY());
            }
            surfacesTab[i].setShapefValues(shapeFvals);
        }
    }





    public void compute(){
        for(int i = 0; i < pGlobal.length; i++) pGlobal[i] = 0.;
        hGlobalMatrix = new Matrix(nh, nh);

        double[] nodeX = new double[4];   //wspolrzednie wezla z elemetu(globalne wartosci)
        double[] nodeY = new double[4];   //wspolrzedne wezla z elementu, globalne wartosci
        double[] dNdx = new double[4];  //przechowywanie tego co chcemy obliczyc
        double[] dNdy = new double[4];  //przechowywanie tego co chcemy obliczyc
        double[] initialTemps = new double[4];  //temp poczatkowa ktora sie zmienia przy iteracji
        double tempInterpolated = 0.; //temperatura początkowa z węzłów zinterpolowana do konkretnego punktu całkowania,

        int id;     // id elementu globalnego np 0 5 6 1, następnie id powierzchnii
        double detJ = 0.;
        double ijMatrixC; // element I j  c
        Jacobian jacobianForPoints;


        for(int elemIter = 0; elemIter < ne; elemIter++){  //iteracja po wszystkich elementach siatki
            Element actualElement = (Element)(grid.getEL().get(elemIter));
            Material actualMaterial = actualElement.getMaterial();
            hCurrentMatrix = new Matrix(4,4);
            pCurrentMatrix = new double[]{0.,0.,0.,0.};

            for(int i = 0; i < 4; i++){     //wyciagamy dane elementu z elementow w siatce
                id = actualElement.getIDArray()[i];
                nodeX[i] = ((Node)(grid.getND().get(id))).getX();
                nodeY[i] = ((Node)(grid.getND().get(id))).getY();
                initialTemps[i] = ((Node)(grid.getND().get(id))).getTemp();
            }

            for (int integrationPoint_Iteration = 0; integrationPoint_Iteration < 4; integrationPoint_Iteration++){
                    //petla po punktach calkowania w danym elemencie
                jacobianForPoints = new Jacobian(integrationPoint_Iteration, nodeX, nodeY, dndEta, dndXi);
                tempInterpolated = 0;

                for(int i = 0; i < 4; i++){     //obliczanie wektoru dni po dy i dx
                    dNdx[i] = jacobianForPoints.getFinalJacobian().get(0,0) * dndXi.get(integrationPoint_Iteration, i)
                            + jacobianForPoints.getFinalJacobian().get(0,1) * dndEta.get(integrationPoint_Iteration, i);

                    dNdy[i] = jacobianForPoints.getFinalJacobian().get(1, 0) * dndXi.get(integrationPoint_Iteration, i)
                            + jacobianForPoints.getFinalJacobian().get(1, 1) * dndEta.get(integrationPoint_Iteration, i);

                    //interpolacja temp
                    // t = N1*t1 * ... *N4*t4
                    tempInterpolated += initialTemps[i] * this.shapeFunc.get(integrationPoint_Iteration, i);
                }

                detJ = Math.abs(jacobianForPoints.getDet());    //dla dv

                for(int i = 0; i < 4; i++){//bo 4 funkcje kształtu a mnozenie jest ransponowane [N]*[N]^T
                    for(int j = 0; j < 4; j++){
                        ijMatrixC = actualMaterial.getC() * actualMaterial.getRo() * shapeFunc.get(integrationPoint_Iteration, i)  * shapeFunc.get(integrationPoint_Iteration, j) * detJ;
                        double tempVal = hCurrentMatrix.get(i, j) + actualMaterial.getK() * (dNdx[i] * dNdx[j] + dNdy[i] * dNdy[j])* detJ + ijMatrixC / dTau;
                        hCurrentMatrix.set(i, j, tempVal);
                        tempVal = pCurrentMatrix[i] + ijMatrixC / dTau * tempInterpolated;
                        pCurrentMatrix[i] = tempVal;
                    }
                }
            }

            //warunki brzegowe
            for(int surface_iteration = 0; surface_iteration < actualElement.getNodesOfBorders(); surface_iteration++) {
                id = actualElement.getIDOfBordersSurfaces().get(surface_iteration);
                Surface surface = actualElement.getSurfaceOfId(id);
                double currentAlfa = surface.getAlfa();


                detJ = Math.sqrt(Math.pow((surface.getSurfaceNodes()[0].getX() - surface.getSurfaceNodes()[1].getX()), 2)   //wyznacznik dla ds
                        + Math.pow((surface.getSurfaceNodes()[0].getY() - surface.getSurfaceNodes()[1].getY()), 2)) / 2.0;

                //nakladanie warunku brzegowego
                for (int i = 0; i < 2; i++) {       //2 punkty calkowania na poweirzchni
                    for (int j = 0; j < 4; j++) {       //4 bo transponowane
                        for (int k = 0; k < 4; k++) {
                            double tempVal = hCurrentMatrix.get(j, k);
                            tempVal += currentAlfa * newElement.getSurfaces()[id].getShapefValues()[i][j]
                                    * newElement.getSurfaces()[id].getShapefValues()[i][k] * detJ;
                            hCurrentMatrix.set(j, k, tempVal);
                        }
                        pCurrentMatrix[j] += currentAlfa * surface.gettInf() * newElement.getSurfaces()[id].getShapefValues()[i][j] * detJ;
                        //System.out.println(surface.gettInf());
                    }
                }
            }
            //agregacja (wpisanie do macierzy globalncyh)
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < 4; j++){
                    int first = actualElement.getIDArray()[i];
                    int second = actualElement.getIDArray()[j];
                    double tempValue = hGlobalMatrix.get(first, second) + hCurrentMatrix.get(i,j);
                    hGlobalMatrix.set(first, second, tempValue);
                }
                pGlobal[actualElement.getIDArray()[i]] += pCurrentMatrix[i];
            }
        }
    }



    public Grid getGrid() {
        return grid;
    }

    public double getTau() {
        return tau;
    }

    public void setTau(double tau) {
        this.tau = tau;
    }

    public double getdTau() {
        return dTau;
    }

    public void setdTau(double dTau) {
        this.dTau = dTau;
    }

    public double gettInf() {
        return tInf;
    }

    public void settInf(double tInf) {
        this.tInf = tInf;
    }

    public double getAlfa() {
        return alfa;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getRo() {
        return ro;
    }

    public void setRo(double ro) {
        this.ro = ro;
    }

    public Matrix gethCurrentMatrix() {
        return hCurrentMatrix;
    }

    public void sethCurrentMatrix(Matrix hCurrentMatrix) {
        this.hCurrentMatrix = hCurrentMatrix;
    }

    public Matrix gethGlobalMatrix() {
        return hGlobalMatrix;
    }

    public void sethGlobalMatrix(Matrix hGlobalMatrix) {
        this.hGlobalMatrix = hGlobalMatrix;
    }

    public double[] getpCurrentMatrix() {
        return pCurrentMatrix;
    }

    public void setpCurrentMatrix(double[] pCurrentMatrix) {
        this.pCurrentMatrix = pCurrentMatrix;
    }

    public double[] getpGlobal() {
        return pGlobal;
    }

    public void setpGlobal(double[] pGlobal) {
        this.pGlobal = pGlobal;
    }

    public void setDndEta(Matrix dndEta) {
        this.dndEta = dndEta;
    }

    public void setDndXi(Matrix dndXi) {
        this.dndXi = dndXi;
    }

    public double getT0() {
        return t0;
    }

    public void setT0(double t0) {
        this.t0 = t0;
    }

    public Matrix getDndEta() {
        return dndEta;
    }

    public Matrix getDndXi() {
        return dndXi;
    }

    public double getH() {
        return H;
    }

    public void setH(double h) {
        H = h;
    }

    public double getB() {
        return B;
    }

    public void setB(double b) {
        B = b;
    }

    public int getHeightNodesNumber() {
        return nH;
    }

    public void setnH(int nH) {
        this.nH = nH;
    }

    public int getWidthNodesNumber() {
        return nB;
    }

    public void setnB(int nB) {
        this.nB = nB;
    }

    public int getNodeNumber() {
        return nh;
    }

    public void setNh(int nh) {
        this.nh = nh;
    }

    public int getNe() {
        return ne;
    }

    public void setNe(int ne) {
        this.ne = ne;
    }

    public double getDy() {
        return dy;
    }

    private void setDy(double dy) {
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    private void setDx(double dx) {
        this.dx = dx;
    }
    public double gettInf_l() {
        return tInf_l;
    }

    public void settInf_l(double tInf_l) {
        this.tInf_l = tInf_l;
    }

    public double gettInf_r() {
        return tInf_r;
    }

    public void settInf_r(double tInf_r) {
        this.tInf_r = tInf_r;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}