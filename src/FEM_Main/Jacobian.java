package FEM_Main;

import Jama.Matrix;

/**
 * Created by miisiekkk on 2018-01-17.
 */

public class Jacobian {
    private Matrix J; //macierz jakobiego
    private Matrix jInverted; //odwrocona macierz jacobiego
    private int pCalkowania; // ktory punkt calkowania 0 || 1 || 2 || 3
    private Matrix dndEta;
    private Matrix dndPsi;
    private double det; //jacobian

    //Jakobian obliczany jest dla każdego
    //punktu całkowania osobno.

    public Jacobian(int pktCalk, double x[], double y[], Matrix dndEta, Matrix dndPsi){
        this.dndEta = dndEta;
        this.dndPsi = dndPsi;
        this.pCalkowania = pktCalk;

        J = new Matrix(2,2); //macierz jakobiego

        double dxDpsi = this.dndPsi.get(pCalkowania, 0) * x[0]      //4 funkcje kształtu dla każdego punktu(jakobian)
                + this.dndPsi.get(pCalkowania, 1) * x[1]
                + this.dndPsi.get(pCalkowania, 2) * x[2]
                + this.dndPsi.get(pCalkowania, 3) * x[3];

        double dyDpsi = this.dndPsi.get(pCalkowania, 0) * y[0]
                + this.dndPsi.get(pCalkowania, 1) * y[1]
                + this.dndPsi.get(pCalkowania, 2) * y[2]
                + this.dndPsi.get(pCalkowania, 3) * y[3];

        double dxDeta = this.dndEta.get(pCalkowania, 0) * x[0]
                + this.dndEta.get(pCalkowania, 1) * x[1]
                + this.dndEta.get(pCalkowania, 2) * x[2]
                + this.dndEta.get(pCalkowania, 3) * x[3];

        double dyDeta = this.dndEta.get(pCalkowania, 0) * y[0]
                + this.dndEta.get(pCalkowania, 1) * y[1]
                + this.dndEta.get(pCalkowania, 2) * y[2]
                + this.dndEta.get(pCalkowania, 3) * y[3];


        J.set(0,0,dxDpsi);
        J.set(0,1,dyDpsi);
        J.set(1,0,dxDeta);
        J.set(1,1,dyDeta);

        det = J.det();

        jInverted = J.transpose();
        jInverted.set(0,1, jInverted.get(0,1)*(-1.0));
        jInverted.set(1,0, jInverted.get(1,0)*(-1.0));
    }

    public Matrix getFinalJacobian(){
        return jInverted.times(1/det);
    }

    public Matrix getjInverted() {
        return jInverted;
    }

    public double getDet() {
        return det;
    }

}