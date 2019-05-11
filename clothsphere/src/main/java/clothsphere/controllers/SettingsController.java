package clothsphere.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import clothsphere.App;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    public App app;

    @FXML
    public Spinner spSphereRadius;
    @FXML
    public Spinner spClothWidth;
    @FXML
    public Spinner spClothHeight;
//    @FXML
//    public Spinner spDivX;
//    @FXML
//    public Spinner spDivY;

    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }

    //Инициализация контроллера
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Пречисляем все спиннеры
        Spinner[] list = {spSphereRadius, spClothWidth, spClothHeight};

        //Присваиваем редактор, который при редактирвоании спинера приваивает корректное значение
        for (Spinner spinner : list)
            spinner.focusedProperty().addListener((s, ov, nv) -> {
                commitEditorText(spinner);
            });
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
