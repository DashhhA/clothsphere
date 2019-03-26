package clothsphere.cloth;

import javafx.geometry.Point3D;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Cloth extends MeshView {
    private TriangleMesh mesh; //сетка из треугольников

    public final PhongMaterial material = new PhongMaterial();

    public Cloth(int divsX, int divsY, double width, double height){
        this.buildMesh(divsX, divsY, width, height);
    }

    private void buildMesh(int divsX, int divsY, double width, double height) {
        mesh = new TriangleMesh();

        float minX = (float) (-width / 2f),
                maxX = (float) (width / 2f),
                minY = (float) (-height / 2f),
                maxY = (float) (height / 2f);

        int sDivX = (divsX - 1), sDivY = (divsY - 1);

        //с помощью циклов вычисляем каждый сегмент
        for(int Y = 0; Y <= sDivY; Y++){

            float currY = (float) Y / sDivY;
            float fy = (1 - currY) * minY + currY * maxY;

            for(int X = 0; X <=sDivX; X++){
                float currX = (float) X / sDivX;
                float fx = (1 - currX) * minX + currY * maxX;

                Point3D p = new Point3D(fx, fy, 300);//точка

                //добавляем точку в сетку
                mesh.getPoints().addAll((float) p.getX(),(float) p.getY(), (float) p.getZ());
                //добавляем координаты текстуры
                mesh.getTexCoords().addAll(currX, currY);
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
