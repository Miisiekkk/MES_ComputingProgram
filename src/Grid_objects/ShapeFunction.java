package Grid_objects;

/**
 * Created by miisiekkk on 2018-01-19.
 */
public class ShapeFunction {
    public static double shapeFunction1(double psi, double eta){return 0.25 * ((1 - eta) * (1 - psi));}
    public static double shapeFunction2(double psi, double eta){return 0.25 * ((1 - eta) * (1 + psi));}
    public static double shapeFunction3(double psi, double eta){return 0.25 * ((1 + eta) * (1 + psi));}
    public static double shapeFunction4(double psi, double eta){return 0.25 * ((1 + eta) * (1 - psi));}

    //Psi
    public static double sfDerivativePSI1(double eta){
        return -0.25 * (1 - eta);
    }
    public static double sfDerivativePSI2(double eta) {
        return 0.25 * (1 - eta);
    }
    public static double sfDerivativePSI3(double eta){
        return 0.25 * (1 + eta);
    }
    public static double sfDerivativePSI4(double eta){
        return -0.25 * (1 + eta);
    }

    //Eta
    public static double sfDerivativeETA1(double psi){
        return -0.25 * (1 - psi);
    }
    public static double sfDerivativeETA2(double psi) {
        return -0.25 * (1 + psi);
    }
    public static double sfDerivativeETA3(double psi){
        return 0.25 * (1 + psi);
    }
    public static double sfDerivativeETA4(double psi){
        return 0.25 * (1 - psi);
    }
}