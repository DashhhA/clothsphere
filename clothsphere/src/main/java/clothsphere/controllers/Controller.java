package clothsphere.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import clothsphere.App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

/**
 * Контроллер главного окна
 */
public class Controller implements Initializable {

    //Ссыолка на класс приложения
    public App app;

    //Кнопки управления
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

    //Инициализация Контроллера при загрузке fxml
    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
    protected void finalize() throws Throwable {
        super.finalize();
    }

    //Корневой элемент
    @FXML
    private BorderPane root;

    /**
     * Создаем диалог опредленного типа
     * @param alertType тип диалога
     * @return
     */
    private Alert createAlert( Alert.AlertType alertType) {
        Alert aboutDialog = new Alert(alertType);
        aboutDialog.initOwner(root.getScene().getWindow());
        aboutDialog.initStyle(StageStyle.UTILITY);
        aboutDialog.setHeaderText(null);
        aboutDialog.setGraphic(null);
        return aboutDialog;
    }

    /**
     * Установить значени спиннера
     * @param spinner
     * @param text
     * @param <T>
     */
    private <T> void setSpinnerValue(Spinner<T> spinner,String text) {
        SpinnerValueFactory<T> vf = spinner.getValueFactory();
        if (vf != null) {
            StringConverter<T> converter = vf.getConverter();
            try {
                T value = converter.fromString(text);
                vf.setValue(value);
            } catch (Exception ex) {};

        }
    }

    /**
     * Информация о разработчике
     * @param event
     * @throws IOException
     */
    @FXML
    private void btnInfoClick(ActionEvent event) throws IOException {

        //Создаем диалог
        Alert aboutDialog = createAlert(INFORMATION);

        //Заголовок окна
        aboutDialog.setTitle("Information" );

        //Загружаем about.fxml
        Node content = FXMLLoader.load(getClass().getResource("/about.fxml"));

        DialogPane pane = aboutDialog.getDialogPane();

        //Размешаем в центре диалога
        pane.setContent(content);

        //Показываем диалог
        aboutDialog.showAndWait();
    }

    /**
     * Настройки программы
     * @param event
     * @throws IOException
     */
    @FXML
    private void btnSettingsClick(ActionEvent event) throws IOException {

        Alert settingsDialog = createAlert(CONFIRMATION);

        settingsDialog.setTitle("Settings" );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings.fxml"));
        Node content = loader.load();

        SettingsController ctrl = loader.getController();
        DialogPane pane = settingsDialog.getDialogPane();

        pane.setContent(content);
        //SpinnerValueFactory valueFactory = ctrl.spDivY.getValueFactory();

        setSpinnerValue(ctrl.spSphereRadius,""+app.sphereRadius);
//        setSpinnerValue(ctrl.spDivX,""+app.clothDivX);
//        setSpinnerValue(ctrl.spDivY,""+app.clothDivY);
        setSpinnerValue(ctrl.spClothWidth,""+app.clothWidth);
        setSpinnerValue(ctrl.spClothHeight,""+app.clothHeight);

        settingsDialog.showAndWait();

        if (settingsDialog.getResult() == ButtonType.OK) {
            app.sphereRadius = (int) ctrl.spSphereRadius.getValue();
//            app.clothDivX = (int) ctrl.spDivX.getValue();
//            app.clothDivY = (int) ctrl.spDivY.getValue();
            app.clothWidth = (int) ctrl.spClothWidth.getValue();
            app.clothHeight = (int) ctrl.spClothHeight.getValue();

            app.changeSettings();
        }
    }

    /**
     * Запуск симуляции
     * @param event
     */
    @FXML
    private void btnStartClick(ActionEvent event){
        app.cloth.startSimulation();
    }

    /**
     * Пауза симуляции
     * @param event
     */
    @FXML
    private void btnPauseClick(ActionEvent event){
        app.cloth.pauseSimulation();
    }

    /**
     * Сброс симуляции
     * @param event
     */
    @FXML
    private void btnRestartClick(ActionEvent event){
        app.cloth.resetSimulation();
    }




}

