package Grid_objects;

/**
 * Created by miisiekkk on 2018-02-09.
 */
public class Material {
    private String name;
    private double c;
    private double k;
    private double alfa;
    private double ro;


    public Material(String name, double c, double ro, double k, double alfa) {
        this.name = name;
        this.c = c;
        this.ro = ro;
        this.k = k;
        this.alfa = alfa;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getRo() {
        return ro;
    }

    public void setRo(double ro) {
        this.ro = ro;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getAlfa() {
        return alfa;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }
}