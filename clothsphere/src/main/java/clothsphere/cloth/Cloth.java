package clothsphere.cloth;

import javafx.geometry.Point3D;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;
import static java.lang.Math.sqrt;

public class Cloth extends MeshView {
    private final List<ClothPoint> points = new ArrayList<>();
    private TriangleMesh mesh; //сетка из треугольников

    public final PhongMaterial material = new PhongMaterial();

    //масса каждой точки
    public double perPointMass = 1.0f;
    //сила растягивания
    public  double stretchStrength = 0.5;
    //сила сдвига
    public double shearStrength = 0.75;
    //сила скручивания
    public double bendStrength = 0.8;

    public Cloth(int divsX, int divsY, double width, double height){

        this.buildMesh(divsX, divsY, width, height, true, true);
    }

    //https://github.com/FXyz/FXyzLib/blob/master/src/org/fxyz/shapes/complex/cloth/ClothMesh.java
    private void buildMesh(int divsX, int divsY, double width, double height, boolean shear, boolean bend) {
       points.clear();

        mesh = new TriangleMesh();

        float minX = (float) (-width / 2f),
                maxX = (float) (width / 2f),
                minY = (float) (-height / 2f),
                maxY = (float) (height / 2f);

        int sDivX = (divsX - 1), sDivY = (divsY - 1);

        //Отношение кол-ва сегментов к высоте/ширине
        double xDist = (width / divsX),
                yDist = (height / divsY);

        //с помощью циклов вычисляем каждый сегмент
        for(int Y = 0; Y <= sDivY; Y++){

            float currY = (float) Y / sDivY;
            float fy = (1 - currY) * minY + currY * maxY;

            for(int X = 0; X <=sDivX; X++){
                float currX = (float) X / sDivX;
                float fx = (1 - currX) * minX + currY * maxX;

                //Создаем точку в пространстве x, y, z
                //коордианту z - делаем фиксированной выше сферы
                ClothPoint p = new ClothPoint(this, perPointMass, fx, fy, 300);

                if(((Y < 5) && (X == 0)) || (Y > sDivX - 5) && (X == 0)){
                    p.initMass = random() * perPointMass * 2 + 0.5;
                    p.mass = p.initMass;
                }

                if(X != 0) {
                    p.attatchTo((points.get(points.size() - 1)), xDist, stretchStrength);
                }
                if(Y != 0) {
                    p.attatchTo((points.get((Y - 1) * (divsX) + X)), yDist, stretchStrength);
                }

                points.add(p);
                //добавляем точку в сетку
                mesh.getPoints().addAll(p.position.x, p.position.y, p.position.z);
                //добавляем координаты текстуры
                mesh.getTexCoords().addAll(currX, currY);
            }
        }

        //Связи сдвига
        if (shear) {
            for (int Y = 0; Y <= sDivY; Y++) {
                for (int X = 0; X <= sDivX; X++) {
                    ClothPoint p = points.get(Y * divsX + X);
                    // top left(xy) to right(xy + 1)
                    if (X < (divsX - 1) && Y < (divsY - 1)) {
                        p.attatchTo((points.get(((Y + 1) * (divsX) + (X + 1)))), sqrt((xDist * xDist) + (yDist * yDist)), shearStrength);
                    }
                    // index(xy) to left(x - 1(y + 1))
                    if (Y != 0 && X != (divsX - 1)) {
                        p.attatchTo((points.get(((Y - 1) * divsX + (X + 1)))), sqrt((xDist * xDist) + (yDist * yDist)), shearStrength);
                    }
                }
            }
        }

        //Связи скручивания
        if (bend) {
            for (int Y = 0; Y <= sDivY; Y++) {
                for (int X = 0; X <= sDivX; X++) {
                    ClothPoint p = points.get(Y * divsX + X);
                    //skip every other
                    if (X < (divsX - 2)) {
                        p.attatchTo((points.get((Y * divsX + (X + 2)))), xDist * 2, shearStrength);
                    }
                    if (Y < (divsY - 2)) {
                        p.attatchTo((points.get((Y + 2) * divsX + X)), xDist * 2, shearStrength);
                    }
                    p.setOldPosition(p.position);
                }
            }
        }
        //добавляем грани каждого треугольника
        for(int Y = 0; Y < sDivY; Y++){
            for(int X = 0; X < sDivX; X++){

                int p00 = Y * (sDivX + 1) + X;
                int p01 = p00 + 1;
                int p10 = p00 + (sDivX + 1);
                int p11 = p10 + 1;

                int tc00 = Y * (sDivX + 1) + X;
                int tc01 = tc00 + 1;
                int tc10 = tc00 + (sDivX + 1);
                int tc11 = tc10 + 1;

                //один сегмент состоит и двух треугольников
                mesh.getFaces().addAll(p00, tc00, p10, tc10, p11, tc11);
                mesh.getFaces().addAll(p11, tc11, p01, tc01, p00, tc00);
            }
        }

        setMesh(mesh);
        setMaterial(material);
    }
}
