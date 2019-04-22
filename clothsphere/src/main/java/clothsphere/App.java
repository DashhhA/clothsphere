package clothsphere;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import clothsphere.cloth.Cloth;
import clothsphere.helpers.CameraTransform;

/**
 * Главный класс приложения.
 */
public class App extends Application {

    /**
     *  Камера перспективы
     */
    private PerspectiveCamera camera;

    /**
     *  Трансформация камеры
     */
    private final CameraTransform cameraTransform = new CameraTransform();

    /**
     * Сфера
     */
    public Sphere sphere;

    /**
     * Ткань
     */
    public Cloth cloth;


    /**
     * Радиус сферы
     */
    public int sphereRadius;

    /**
     * Разделы сферы (детализация)
     */
    public int sphereDivisions;

    /**
     * Ширина ткани
     */
    public int clothWidth;

    /**
     * Высота ткани
     */
    public int clothHeight;

    /**
     * Деления ткани по X
     */
    public int clothDivX;

    /**
     * Деления ткани по Y
     */
    public int clothDivY;

    /**
     * Координаты мыши, для управления сценой
     */
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    /**
     * Конструктор
     * @param args параметры командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Устанавливаем значения по умолчанию
     */
    public void setSceneDefaults() {
        sphereRadius = 145;
        sphereDivisions = 50;

        clothWidth = 800;
        clothHeight = 800;
        clothDivX = 20;
        clothDivY = 20;
    }

    /**
     * Создание и настройка камеры
     */
    public void createCamera() {
        camera = new PerspectiveCamera(true);
        cameraTransform.setTranslate(0, 0, 0);
        cameraTransform.getChildren().addAll(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(1000000.0);
        camera.setFieldOfView(42);
        camera.setVerticalFieldOfView(true);

        //поворачиваем кармеру находящуюсь в точке 0 0 0 на 250 градусов по оси X
        cameraTransform.rx.setAngle(250);

        //перемещаем камеру по координате Z на -1500
        camera.setTranslateZ(-1500);
    }

    /**
     * Материал сферы, ресурсы загружаем по сети
     */
    public void setSphereMaterial() {

        double MAP_WIDTH  = 1000 / 2d;
        double MAP_HEIGHT = 500 / 2d;

        String DIFFUSE_MAP =
                "https://raw.githubusercontent.com/devingfx/DUnitearth/master/images/earthmap1k.jpg";

        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(
                new Image(
                        DIFFUSE_MAP,
                        MAP_WIDTH,
                        MAP_HEIGHT,
                        false,
                        true
                )
        );

        sphere.setMaterial(
                earthMaterial
        );
    }

    /**
     * Добавялем точку света
     */
    public void setSceneLight() {

        PointLight light = new PointLight(Color.LIGHTSKYBLUE);
        //cameraTransform.getChildren().add(light);
        light.translateXProperty().bind(camera.translateXProperty());
        light.translateYProperty().bind(camera.translateYProperty());
        light.translateZProperty().bind(camera.translateZProperty());
    }

    /**
     * Управление сценой, назначаем события для сцены
     * @param scene сцена
     */
    public void setSceneControl(Scene scene) {

        /**
         * При нажатии мыши запоминаем позицию курсора на сцене
         */
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        /**
         * Меняем координату положения камеры по Z, для приближения или отдаления с помощью колеса мыши
         */
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double modifier = 10.0;
                double modifierFactor = 0.1;
                double z = camera.getTranslateZ();
                double newZ = z + event.getDeltaY() * modifierFactor * modifier;
                camera.setTranslateZ(newZ);
            }
        });

        /**
         *  При перемещении курсора мыши с нажатой кнопкой
         */
        scene.setOnMouseDragged((MouseEvent me) -> {

            //Меняем старые и новые координаты
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();

            //Вычисляем разницу старых и новых координат
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            //Множетили перемещения, для определния скорости вращения
            double modifier = 10.0;
            double modifierFactor = 0.1;

            //Если зажата клавиша CTRL уменьшаем множитель
            if (me.isControlDown()) {
                modifier = 0.1;
            }
            //Если зажата клавиша SHIFT увеличиваем множитель
            if (me.isShiftDown()) {
                modifier = 50.0;
            }

            //Нажата левая кнопка мыши
            if (me.isPrimaryButtonDown()) {
                //Устанавливаем Z и X оси вращения камеры
                cameraTransform.rz.setAngle(((cameraTransform.rz.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180); // -
            } else
                //Нажата правая кнопка мыши
                if (me.isSecondaryButtonDown()) {
                    //Устанавливаем вращение вокруг Y оси камеры
                    cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180);
                }
        });

    }
    /**
     * Запуск приложение, главное окно (класс Stage)
     * @param primaryStage главное окно
     */
    @Override
    public void start(Stage primaryStage) {

        //Значения по умолчанию
        setSceneDefaults();

        createCamera();

        setSceneLight();

        //Создаем сферу
        sphere = new Sphere(sphereRadius, sphereDivisions);
        //Установим материал сферы, чтобы различать вращение
        setSphereMaterial();

        //Создаем ткань
        cloth = new Cloth(sphere, clothDivX, clothDivY, clothWidth, clothHeight);
        cloth.setDrawMode(DrawMode.LINE);
        cloth.setCullFace(CullFace.NONE);
        cloth.material.setSpecularPower(2);

        //Добавляем сферу и ткань в корневую группу
        Group root = new Group(cloth, sphere);

        //Добавляем сцену, и включем в нее пустой Group, устанавливаем ширину, высоту
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);

        //Назначаем камеру
        scene.setCamera(camera);

        //Делаем фон линейным градиентом, от черного к небесно-голубому
        Stop[] stops = new Stop[]{new Stop(0, Color.BLACK), new Stop(1, Color.SKYBLUE)};
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        scene.setFill(lg);

        //Назначаем события для управления сценой
        setSceneControl(scene);

        //Заголовок главного окна
        primaryStage.setTitle("Clothsphere");

        //Устанавливаем сцену для главного окна
        primaryStage.setScene(scene);

        //Показываем окно
        primaryStage.show();

        //Заупскаем симуляцию при старте
        cloth.timer.start();

    }
}

