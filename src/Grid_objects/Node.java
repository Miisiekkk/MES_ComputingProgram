package Grid_objects;

/**
 * Created by miisiekkk on 2018-01-16.
 */

public class Node {
    private double x;
    private double y;
    private double result;
    private boolean status;
    private final int uid;
    private double temp;
    private double tInf;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
        uid = -1;
    }

    public Node(double x, double y, final int uid, boolean status, double initialTemp) {
        this.x = x;
        this.y = y;
        this.uid = uid;
        this.status = status;
        this.temp = initialTemp;
    }

    public Node(double x, double y, final int uid, boolean status, double initialTemp, double tInf) {
        this.x = x;
        this.y = y;
        this.uid = uid;
        this.status = status;
        this.temp = initialTemp;
        this.tInf = tInf;
    }

    public Node(Point point){
        this.x = point.getX();
        this.y = point.getY();
        uid = -1;
        status = false;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public int getUid() {
        return uid;
    }

    public double gettInf() {
        return tInf;
    }

    public void settInf(double tInf) {
        this.tInf = tInf;
    }
}