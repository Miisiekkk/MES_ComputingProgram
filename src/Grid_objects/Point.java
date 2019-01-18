package Grid_objects;

/**
 * Created by miisiekkk on 2018-01-16.
 */
public class Point {
    private double x;   //eta
    private double y;   //psi

    private double xW;
    private double yW;

    public Point(double x, double y, double xW, double yW) {
        this.x = x;
        this.y = y;
        this.xW = xW;
        this.yW = yW;
    }
    public Point(double x, double y){
        this.x = x;
        this.y = y;
        this.xW = 1.;
        this.yW = 1.;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getxW() {
        return xW;
    }

    public void setxW(double xW) {
        this.xW = xW;
    }

    public double getyW() {
        return yW;
    }

    public void setyW(double yW) {
        this.yW = yW;
    }
}