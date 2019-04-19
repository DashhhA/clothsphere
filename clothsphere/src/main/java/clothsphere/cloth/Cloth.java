package clothsphere.cloth;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.random;
import static java.lang.Math.sqrt;

/**
 * Ткань
 */
public class Cloth extends MeshView {

    /**
     * Точки такни
     */
    public final List<ClothPoint> points = new ArrayList<>();

    /**
     *  Таймер симуляции
     */
    public ClothTimer timer;

    /**
     * Сетка из треугольников
     */
    private TriangleMesh mesh;

    /**
     * Материал ткани
     */
    public final PhongMaterial material = new PhongMaterial();

    /**
     * Масса каждой точки
     */
    public double perPointMass = 1.0f;

    /**
     * Сила растягивания
     */
    public  double stretchStrength = 0.5;

    /**
     * Сила сдвига
     */
    public  double shearStrength = 0.75;

    /**
     * Сила скручивания
     */
    public  double bendStrength = 0.8;

    /**
     * Точность расчета связей
     */
    public  int constraitsAccuracy = 8;

    /**
     * Конструктор ткани с параметрами
     *
     * @param divsX делений по X оси
     * @param divsY делений па Y оси
     * @param width ширина
     * @param height высота
     */
    public Cloth(int divsX, int divsY, double width, double height) {

        //Создаем таймер
        this.timer = new ClothTimer(this);

        //Строим сетку
        this.buildMesh(divsX, divsY, width, height, true, true);

    }

    /**
     * Построить сетку
     *
     * @param divsX делений по X оси
     * @param divsY делений па Y оси
     * @param width ширина
     * @param height высота
     * @param shear исползовать связи сдвига
     * @param bend исползовать связи скручивания
     */
    public void buildMesh(int divsX, int divsY, double width, double height, boolean shear, boolean bend) {

        //Очищаем список точек
        points.clear();
        //Создаем новый обьект javafx TriangleMesh
        mesh = new TriangleMesh();

        //Мин и макс координаты по X и Y, такань помещается в центре
        float minX = (float) (-width / 2f),
                maxX = (float) (width / 2f),
                minY = (float) (-height / 2f),
                maxY = (float) (height / 2f);

        //Число сегментов уменьшаем для удобства на 1
        int sDivX = (divsX - 1), sDivY = (divsY - 1);

        //Отношение кол-ва сегментов к высоте/ширине
        double xDist = (width / divsX),
                yDist = (height / divsY);

        //С помощью циклов вычисляем каждый сегмент
        for (int Y = 0; Y <= sDivY; Y++) {

            float currY = (float) Y / sDivY;
            float fy = (1 - currY) * minY + currY * maxY;

            for (int X = 0; X <= sDivX; X++) {

                float currX = (float) X / sDivX;
                float fx = (1 - currX) * minX + currX * maxX;

                //Создаем точку в пространстве x, y, z
                //коордианту z - делаем фиксированной выше сферы
                ClothPoint p = new ClothPoint(this, perPointMass, fx, fy, 300 );

                /* В некоторых сегментах делаем массу немного отличной, чтобы ткань немного деформировалась */
                if (((Y < 5) && (X == 0)) || ((Y > sDivY - 5) && X == 0)) {
                    p.initMass = random()*perPointMass*2 + 0.5;
                    p.mass = p.initMass;
                }

                // Основные связи
                if (X != 0) {
                    p.attatchTo((points.get(points.size() - 1)), xDist, stretchStrength);
                }
                if (Y != 0) {
                    p.attatchTo((points.get((Y - 1) * (divsX) + X)), yDist, stretchStrength);
                }

                //Добавляем точку в список
                points.add(p);
                //добавляем точку в сетку
                mesh.getPoints().addAll(p.position.x, p.position.y, p.position.z);
                //добавляем координаты текстуры
                mesh.getTexCoords().addAll(currX, currY);
            }
        }


        //Свзязи сдвига
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

        //Свзязи скручивания
        if (bend) {
            for (int Y = 0; Y <= sDivY; Y++) {
                for (int X = 0; X <= sDivX; X++) {
                    ClothPoint p = points.get(Y * divsX + X);
                    //skip every other
                    if (X < (divsX - 2)) {
                        p.attatchTo((points.get((Y * divsX + (X + 2)))), xDist * 2, bendStrength);
                    }
                    if (Y < (divsY - 2)) {
                        p.attatchTo((points.get((Y + 2) * divsX + X)), xDist * 2, bendStrength);
                    }
                    p.setOldPosition(p.position);
                }
            }
        }

        //добавляем грани каждого треугольника
        for (int Y = 0; Y < sDivY; Y++) {
            for (int X = 0; X < sDivX; X++) {

                int p00 = Y * (sDivX + 1) + X;
                int p01 = p00 + 1;
                int p10 = p00 + (sDivX + 1);
                int p11 = p10 + 1;

                int tc00 = Y * (sDivX + 1) + X;
                int tc10 = tc00 + (sDivX + 1);
                int tc01 = tc00 + 1;
                int tc11 = tc10 + 1;

                //один сегмент состоит из двух треугольников
                mesh.getFaces().addAll(p00, tc00, p10, tc10, p11, tc11);
                mesh.getFaces().addAll(p11, tc11, p01, tc01, p00, tc00);
            }
        }

        //Назначаем triangleMesh
        setMesh(mesh);
        //Назначаем материал ткани
        setMaterial(material);

    }

    /**
     * Обновляем точки сетки (обьекта Mesh)
     */
    public void updatePoints() {
        float[] pts = this.points.stream()
                .flatMapToDouble(wp -> {
                    return wp.position.getCoordinates();
                })
                .collect(() -> new FloatCollector(this.points.size() * 3), FloatCollector::add, FloatCollector::join)
                .toArray();

        mesh.getPoints().setAll(pts, 0, pts.length);
    }

    /**
     * Вспомогательный класс для сбора координат в массив float[]
     */
    public class FloatCollector {

        private float[] curr=new float[64];
        private int size;


        public FloatCollector(int initialSize){
            if(curr.length<initialSize){
                curr= Arrays.copyOf(curr, initialSize);
            }
        }
        public void add(double d) {
            if(curr.length == size){
                curr = Arrays.copyOf(curr, size*2);
            }
            curr[size++] = (float)d;
        }

        public void join(FloatCollector other) {
            if(size + other.size > curr.length) {
                curr = Arrays.copyOf(curr, size + other.size);
            }
            System.arraycopy(other.curr, 0, curr, size, other.size);
            size += other.size;
        }

        public float[] toArray() {
            if(size != curr.length){
                curr = Arrays.copyOf(curr, size);
            }
            return curr;
        }
    }

}