package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class OpenStage extends Stage {
    private EventHandler<WindowEvent> closeEvent = (t) -> {
        Main.switchStage(Main.getMainStage());
    };

    public OpenStage() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/fileSelector.fxml"));
            setTitle("Open");
            Scene scene = new Scene(root);
            setScene(scene);
            if (!Main.cssPath.equals("")) {
                URL cssURL = getClass().getResource(Main.cssPath);
                if (Main.cssURL != null) {
                    scene.getStylesheets().add(cssURL.toExternalForm());
                    Main.cssURL = cssURL;
                }
            }
            this.setOnCloseRequest(closeEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main extends Application {
    public static Stage active;
    public static Stage mainStage;
    public static Controller sceneEditorController;
    public static String cssPath = "";
    public static URL cssURL = null;
    public static Database database;
    public static AbstractDAOFactory factory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DATABASE_DAO_FACTORY);

    public static Database getDatabase() {
        return database;
    }

    public static Stage getActive() {
        return active;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void switchStage(Stage s) {
        if (s == mainStage)
            active.close();
        else
            active.hide();
        active = s;
        active.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        database = new Database();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sceneEditor.fxml"));
        primaryStage.setTitle("Level editor");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        database.execScript(Database.create_table);
        DAO<StyleSheet> sDAO = Main.factory.getStyleSheetDAO(database);
        StyleSheet s = sDAO.findAll();
        if (s != null) {
            Main.cssPath = s.getPath();
            Main.cssURL = getClass().getResource(Main.cssPath);
        }
        sceneEditorController.setSelectedStyle();
        if (!Main.cssPath.equals("")) {
            URL cssURL = getClass().getResource(Main.cssPath);
            if (cssURL != null) {
                scene.getStylesheets().add(cssURL.toExternalForm());
                Main.cssURL = cssURL;
            }
        }
        active = primaryStage;
        mainStage = primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                database.logout();
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
