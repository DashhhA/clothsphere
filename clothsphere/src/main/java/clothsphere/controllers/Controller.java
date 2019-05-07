package clothsphere.controllers;

import clothsphere.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    public App app;

    @FXML
    private Button btnInfo;
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnPause;
    @FXML
    private Button btnRestart;

    @Override
    public void initialize (URL location, ResourceBundle resources){
    //стиль кнопок
        String style = "" +
                "-fx-pref-width:100px;" +
                "-fx-padding: 5px;" +
                "-fx-text-fill: black;" +
                "-fx-background-color: #00cccc;"  +
                "-fx-border-width: 1px; " +
                "-fx-border-color:#6699ff;";

        //присваиваем стиль всем кнопкам
        btnInfo.setStyle(style);
        btnSettings.setStyle(style);
        btnStart.setStyle(style);
        btnPause.setStyle(style);
        btnRestart.setStyle(style);
    }

    @Override
    protected void finalize() throws Throwable{
        super.finalize();
    }
}
