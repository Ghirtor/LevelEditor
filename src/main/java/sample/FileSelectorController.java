package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileSelectorController {
    @FXML
    private BorderPane container;

    @FXML
    private ComboBox box;

    @FXML
    private ToolBar toolbar;

    @FXML
    private Separator separator1;

    @FXML
    private Separator separator2;

    @FXML
    private Button cancel;

    @FXML
    private Button open;

    @FXML
    private ListView list;

    private FileSelector f;
    ObservableList<String> children;
    ObservableList<String> parents;

    private ChangeListener change = (a,b,c) -> {
        updateList((String) box.getValue());
        updateMenu((String) box.getValue());
    };

    @FXML
    private void initialize() {
        String directory = System.getProperty("user.dir") + File.separator;
        f = new FileSelector();
        updateList(directory);
        updateMenu(directory);
        box.setValue(directory);
        enableBoxListener();
        list.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) openButtonEvent();
        });
    }

    private void enableBoxListener() {
        box.valueProperty().addListener(change);
    }

    private void disableBoxListener() {
        box.valueProperty().removeListener(change);
    }

    private void updateList(String path) {
        ArrayList<String> listFile = f.getListFile(path);
        if (path != null && listFile != null && listFile.size() > 0) {
            ObservableList<String> c = FXCollections.observableArrayList(listFile);
            list.setItems(c);
        }
    }

    private void updateMenu(String path) {
        ArrayList<String> repParent = f.getListRepParent(path);
        if (path != null && repParent != null && repParent.size() > 0) {
            ObservableList<String> p = FXCollections.observableArrayList(repParent);
            box.setItems(p);
        }
        else {
            ObservableList<String> p = FXCollections.observableArrayList(Arrays.asList("/"));
            box.setItems(p);
        }
    }

    @FXML
    private void cancelButtonEvent() {
        Main.switchStage(Main.getMainStage());
    }

    @FXML
    private void openButtonEvent() {
        String selection = (String) list.getSelectionModel().getSelectedItem();
        if (selection != null && new File(((String) box.getValue()) + selection).isDirectory()) {
            disableBoxListener();
            String tmp = ((String) box.getValue()) + selection;
            updateList(tmp);
            updateMenu(tmp);
            box.setValue(tmp);
            enableBoxListener();
        }
        else if (selection != null && !(new File(((String) box.getValue()) + selection).isDirectory())) {
            String[] split = selection.split("\\.");
            if (split[split.length-1].equals("html")) {
                Main.sceneEditorController.parse(((String) box.getValue()) + selection);
                Main.switchStage(Main.getMainStage());
            }
        }
    }
}