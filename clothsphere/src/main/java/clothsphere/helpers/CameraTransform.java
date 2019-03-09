//package clothsphere.helpers;
//import javafx.scene.Group;
//import javafx.scene.transform.Rotate;
//import javafx.scene.transform.Scale;
//import javafx.scene.transform.Translate;
//
///**
// * Вспомогательный класс трансформации камеры, наследник группы
// */
//public class CameraTransform extends Group {
//
//    /**
//     *  Точка расположения камеры
//     */
//    public Translate t  = new Translate();
//    /**
//     *  Точка вращения
//     */
//    public Translate p  = new Translate();
//
//
//    /**
//     * Вращение по осям x,y,z
//     */
//    public Rotate rx = new Rotate();
//    { rx.setAxis(Rotate.X_AXIS); }
//    public Rotate ry = new Rotate();
//    { ry.setAxis(Rotate.Y_AXIS); }
//    public Rotate rz = new Rotate();
//    { rz.setAxis(Rotate.Z_AXIS); }
//
//    /**
//     * Масштаб
//     */
//    public Scale s = new Scale();
//
//    public CameraTransform() {
//        super();
//        getTransforms().addAll(t, rz, ry, rx, s);
//    }
//
//    public void setTranslate(double x, double y, double z) {
//        t.setX(x);
//        t.setY(y);
//        t.setZ(z);
//    }
//
//    public void setTranslate(double x, double y) {
//        t.setX(x);
//        t.setY(y);
//    }
//
//    public void setTx(double x) { t.setX(x); }
//    public void setTy(double y) { t.setY(y); }
//    public void setTz(double z) { t.setZ(z); }
//
//    public void setRotate(double x, double y, double z) {
//        rx.setAngle(x);
//        ry.setAngle(y);
//        rz.setAngle(z);
//    }
//
//    public void setRotateX(double x) { rx.setAngle(x); }
//    public void setRotateY(double y) { ry.setAngle(y); }
//    public void setRotateZ(double z) { rz.setAngle(z); }
//    public void setRx(double x) { rx.setAngle(x); }
//    public void setRy(double y) { ry.setAngle(y); }
//    public void setRz(double z) { rz.setAngle(z); }
//
//    public void setScale(double scaleFactor) {
//        s.setX(scaleFactor);
//        s.setY(scaleFactor);
//        s.setZ(scaleFactor);
//    }
//
//    public void setScale(double x, double y, double z) {
//        s.setX(x);
//        s.setY(y);
//        s.setZ(z);
//    }
//
//    public void setSx(double x) { s.setX(x); }
//    public void setSy(double y) { s.setY(y); }
//    public void setSz(double z) { s.setZ(z); }
//
//    /**
//     * Установка точки вращения
//     */
//    public void setPivot(double x, double y, double z) {
//        p.setX(x);
//        p.setY(y);
//        p.setZ(z);
//    }
//
//    /**
//     * Сброс параметров трансформации камеры
//     */
//    public void reset() {
//        t.setX(0.0);
//        t.setY(0.0);
//        t.setZ(0.0);
//        rx.setAngle(0.0);
//        ry.setAngle(0.0);
//        rz.setAngle(0.0);
//        s.setX(1.0);
//        s.setY(1.0);
//        s.setZ(1.0);
//        p.setX(0.0);
//        p.setY(0.0);
//        p.setZ(0.0);
//    }
//}

package clothsphere.helpers;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public  class CameraTransform extends Group{
    public Translate t = new Translate();
    public  Translate p = new Translate();

    public Rotate rx = new Rotate();
    {
        rx.setAxis(Rotate.X_AXIS);
    }
    public Rotate ry = new Rotate();
    {
        ry.setAxis(Rotate.Y_AXIS);
    }
    public Rotate rz = new Rotate();
    {
        rz.setAxis(Rotate.Z_AXIS);
    }

    public  Scale scale = new Scale();

    public CameraTransform(){
        super();
        getTransforms().addAll(t, rz, ry, rx, scale);
    }

    public  void setTranslate(double x, double y, double z){
        t.setX(x);
        t.setY(y);
        t.setZ(z);
    }

    public void setTranslate(double x, double y){
        t.setX(x);
        t.setY(y);
    }

    public void setTx(double x){
        t.setX(x);
    }

    public void setTy(double y){
        t.setY(y);
    }

    public void setTz(double z){
        t.setZ(z);
    }

    public  void setRotate(double x, double y, double z){
        rx.setAngle(x);
        ry.setAngle(y);
        rx.setAngle(z);
    }

    public void setRotateX(double x) { rx.setAngle(x); }
    public void setRotateY(double y) { ry.setAngle(y); }
    public void setRotateZ(double z) { rz.setAngle(z); }
    public void setRx(double x) { rx.setAngle(x); }
    public void setRy(double y) { ry.setAngle(y); }
    public void setRz(double z) { rz.setAngle(z); }

    public void setScale(double scaleFactor) {
        scale.setX(scaleFactor);
        scale.setY(scaleFactor);
        scale.setZ(scaleFactor);
    }

    public void setScale(double x, double y, double z) {
        scale.setX(x);
        scale.setY(y);
        scale.setZ(z);
    }

    public void setSx(double x) { scale.setX(x); }
    public void setSy(double y) { scale.setY(y); }
    public void setSz(double z) { scale.setZ(z); }

    public void setPivot(double x, double y, double z) {
        p.setX(x);
        p.setY(y);
        p.setZ(z);
    }

    public void reset() {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        rx.setAngle(0.0);
        ry.setAngle(0.0);
        rz.setAngle(0.0);
        scale.setX(1.0);
        scale.setY(1.0);
        scale.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
    }
}
