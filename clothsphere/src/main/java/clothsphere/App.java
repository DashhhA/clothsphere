
package  clothsphere;

import clothsphere.helpers.CameraTransform;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.*;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;



public class App extends Application{

    private  PerspectiveCamera camera;

    public Sphere sphere;
    public int sphereRadius;
    public int sphereDivisions;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private final CameraTransform cameraTransform = new CameraTransform();

    public  static  final  int WIDTH = 1000;
    public static  final  int HEIGHT = 600;

    public  static  void  main(String [] args){
        launch(args);
    }

    public void setSceneDefaults(){
        sphereRadius = 345;
        sphereDivisions = 50;
    }

    public  void createCamera(){
        camera = new PerspectiveCamera(true);
        cameraTransform.setTranslate(0,0,0);
        cameraTransform.getChildren().addAll(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(1000000.0);
        camera.setFieldOfView(42);
        camera.setVerticalFieldOfView(true);

        cameraTransform.rx.setAngle(250);
        camera.setTranslateZ(-1500);
    }

    public void setSphereMaterial(){
        double MAP_WIDTH = 1000 / 2d;
        double MAP_HEIGHT = 500 / 2d;

        String DIFFUSE_MAP =
                "https://raw.githubusercontent.com/devingfx/DUnitearth/master/images/earthmap1k.jpg";

        //https://www.programcreek.com/java-api-examples/?class=javafx.scene.paint.PhongMaterial&method=setDiffuseMap
        PhongMaterial earthMaterial = new PhongMaterial();
               earthMaterial.setDiffuseMap(new Image(
                DIFFUSE_MAP,
                MAP_WIDTH,
                MAP_HEIGHT,
                true,
                true
        )
        );

               sphere.setMaterial(earthMaterial);
    }

    //https://github.com/FXyz/FXyzLib/blob/master/src/org/fxyz/tests/ClothMeshTest.java
    public void setSceneLight(){
        PointLight light = new PointLight(Color.LIGHTSKYBLUE);

        light.translateXProperty().bind(camera.translateXProperty());
        light.translateYProperty().bind(camera.translateYProperty());
        light.translateZProperty().bind(camera.translateZProperty());
    }

    public void setSceneControl(Scene scene){
        scene.setOnMousePressed((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        //Определяет функцию, вызываемую при выполнении пользователем действия прокрутки.
        //http://qaru.site/questions/5764547/how-to-set-axis-triad-at-fixed-position-on-screen-in-javafx
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double modifier = 10.0;
                double modifierFactor = 0.1;
                double z = camera.getTranslateZ();
                double newZ = z + event.getDeltaY() * modifierFactor* modifier;
                camera.setTranslateZ(newZ);
            }
        });

        scene.setOnMouseDragged((MouseEvent me) -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            double modifier = 10.0;
            double modifierFactor = 0.1;

            if (me.isControlDown()) {
                modifier = 0.1;
            }
            if (me.isShiftDown()) {
                modifier = 50.0;
            }
            if (me.isPrimaryButtonDown()) {
                cameraTransform.rz.setAngle(((cameraTransform.ry.getAngle() - mouseDeltaX*modifierFactor*modifier*2.0) % 360 + 540) % 360 - 180);  // +
                cameraTransform.rx.setAngle(((cameraTransform.rx.getAngle() - mouseDeltaY*modifierFactor*modifier*2.0) % 360 + 540) % 360 - 180);  // -
            }
            else if (me.isSecondaryButtonDown()) {
                cameraTransform.ry.setAngle(((cameraTransform.ry.getAngle() - mouseDeltaX*modifierFactor*modifier*2.0) % 360 + 540) % 360 - 180);
            }
    });
    }



    @Override
    public  void start(Stage primartStage){

        setSceneDefaults();

        createCamera();

        setSceneLight();;

        sphere = new Sphere(sphereRadius, sphereDivisions);

        setSphereMaterial();
        Group root = new Group(sphere);

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setCamera(camera);

        scene.setCamera(camera);

        sphere.translateXProperty().set(WIDTH / 2);
        sphere.translateYProperty().set(HEIGHT/2);

        Stop[] stops = new Stop[]{new Stop(0, Color.BLACK), new Stop(1, Color.SKYBLUE)};
        LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        scene.setFill(lg);

        setSceneControl(scene);

        primartStage.setTitle("Падение ткани на сферу");

        primartStage.setScene(scene);

        primartStage.show();
    }
}