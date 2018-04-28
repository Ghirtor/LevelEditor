package sample;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;

public class Controller {
    private final int BACKGROUND = 0; // status corresponding to choose background mode
    private final int TILE = 1; // status corresponding to choose tile mode
    private final int SELECTANDMOVE = 2; // statuc corresponding to select/move mode
    private final int CHARACTER = 3; // status corresponding to choose character mode

    private final int STYLE_DEFAULT = 0; // value corresponding to default css style
    private final int STYLE_DARK = 1; // value corresponding to dark css style

    private final String DARK_STYLE_PATH = "/css/darkStyle.css"; // path for css dark style file

    @FXML
    private SplitPane splitPane;
    @FXML
    AnchorPane drawArea;
    @FXML
    AnchorPane menuArea;
    @FXML
    ScrollPane scrollPane;
    @FXML
    private RadioButton selectAndMove;
    @FXML
    private RadioButton background;
    @FXML
    private RadioButton tile;
    @FXML
    private RadioButton character;
    @FXML
    private ComboBox<Infos> comboBox;
    @FXML
    private Button delete;
    @FXML
    private Button clone;
    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private TextField minX;
    @FXML
    private TextField minY;
    @FXML
    private TextField maxX;
    @FXML
    private TextField maxY;
    @FXML
    private TextField title;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem exportHtml;
    @FXML
    private MenuItem about;
    @FXML
    private RadioMenuItem defaultStyle;
    @FXML
    private RadioMenuItem darkStyle;
    ToggleGroup group = new ToggleGroup(); // group all radioButtons
    ToggleGroup styleGroup = new ToggleGroup(); // group all radioButtons
    private ArrayList<Character> characters; // list of characters on the draw area
    private ArrayList<Tile> tiles; // list of tiles on draw area
    private ArrayList<Background> backgrounds; // list of backgrounds on draw area
    private int status; // status corresponding to the radio button of group selected
    private double lastClickedX; // last clicked point x in select/move mode usefull to delete an object or clone it
    private double lastClickedY; // last clicked point y in select/move mode usefull to delete an object or clone it
    private Component lastSelected; // last selected component
    double orgSceneX;
    double orgSceneY;
    private final int default_scene_width = 100;
    private final int default_scene_height = 100;
    int sceneWidth = default_scene_width;
    int sceneHeight = default_scene_height;
    private ArrayList<Line> borders;
    private int borderWidth = 10;
    private DropShadow selectionEffect; // effect applied on selected component

    private void updateList() {
        ArrayList<Infos> list = new ArrayList<Infos>();
        switch (status) {
            case BACKGROUND:
                list = PictureInfos.Filter(Infos.TYPE_BACKGROUND);
                break;
            case TILE:
                list = PictureInfos.Filter(Infos.TYPE_TILE);
                break;
            case CHARACTER:
                list = PictureInfos.Filter(Infos.TYPE_CHARACTER);
                break;
            default: break;
        }
        ObservableList<Infos> c = FXCollections.observableArrayList(list);
        comboBox.setItems(c);
    }

    // event when an object is moved
    private EventHandler<MouseEvent> catchEvent = (t) -> {
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();
        if (t.getSource() instanceof Character) {
            Character tmp = (Character) (t.getSource());
            tmp.toFront();
        }
        else {
            Tile tmp = (Tile) (t.getSource());
            tmp.toFront();
            int ind = tiles.indexOf(tmp);
            for (int i = ind + 1; i < tiles.size(); i++) {
                tiles.set(i-1, tiles.get(i));
                tiles.set(i, tmp);
            }
            for (Character c : characters)
                c.toFront();
        }
        drawBorders();
    };

    // event when an object is moved
    private EventHandler<MouseEvent> releaseEvent = (t) -> {
        double offsetX = t.getSceneX() - orgSceneX;
        double offsetY = t.getSceneY() - orgSceneY;
        if (t.getSource() instanceof Character) {
            Character tmp = (Character) (t.getSource());
            tmp.setX(tmp.getX() + offsetX);
            tmp.setY(tmp.getY() + offsetY);
        }
        else {
            Tile tmp = (Tile) (t.getSource());
            tmp.setX(tmp.getX() + offsetX);
            tmp.setY(tmp.getY() + offsetY);
        }
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();
        drawBorders();
    };

    // allow drag and drop on objects when clicking on select/move mode
    private EventHandler<MouseEvent> enableDragAndDropEvent = (t) -> {
        enableDragAndDrop();
    };

    // disable drag and drop on objects when clicking on not select/move mode
    private EventHandler<MouseEvent> disableDragAndDropEvent = (t) -> {
        disableDragAndDrop();
    };

    // set scene width
    private EventHandler<KeyEvent> widthSelection = (t) -> {
        if (t.getCode() == KeyCode.ENTER) {
            try {
                sceneWidth = Integer.parseInt(width.getText());
                drawArea.setMinSize(sceneWidth, sceneHeight);
                drawArea.setMaxSize(sceneWidth, sceneHeight);
                drawArea.setPrefSize(sceneWidth, sceneHeight);
                drawBackground();
                drawBorders();
            } catch (NumberFormatException e){
                width.setText(Integer.toString(sceneWidth));
            }
        }
    };

    // set scene height
    private EventHandler<KeyEvent> heightSelection = (t) -> {
        if (t.getCode() == KeyCode.ENTER) {
            try {
                sceneHeight = Integer.parseInt(height.getText());
                drawArea.setMinSize(sceneWidth, sceneHeight);
                drawArea.setMaxSize(sceneWidth, sceneHeight);
                drawArea.setPrefSize(sceneWidth, sceneHeight);
                drawBackground();
                drawBorders();
            } catch (NumberFormatException e){
                height.setText(Integer.toString(sceneHeight));
            }
        }
    };

    // set selected component minX
    private EventHandler<KeyEvent> minXSelection = (t) -> {
        if (lastSelected != null && t.getCode() == KeyCode.ENTER) {
            try {
                int number = Integer.parseInt(minX.getText());
                if (number < 0 || number > sceneWidth) throw new NumberFormatException();
                lastSelected.setX(Integer.parseInt(minX.getText()));
                maxX.setText(Integer.toString((int) (lastSelected.getX() + lastSelected.getFitWidth())));
                drawBorders();
            } catch (NumberFormatException e){
                minX.setText(Integer.toString((int) lastSelected.getX()));
            }
        }
    };

    // set selected component minY
    private EventHandler<KeyEvent> minYSelection = (t) -> {
        if (lastSelected != null && t.getCode() == KeyCode.ENTER) {
            try {
                int number = Integer.parseInt(minY.getText());
                if (number < 0 || number > sceneHeight) throw new NumberFormatException();
                lastSelected.setY(Integer.parseInt(minY.getText()));
                maxY.setText(Integer.toString((int) (lastSelected.getY() + lastSelected.getFitHeight())));
                drawBorders();
            } catch (NumberFormatException e){
                minY.setText(Integer.toString((int) lastSelected.getY()));
            }
        }
    };

    // set selected component maxX
    private EventHandler<KeyEvent> maxXSelection = (t) -> {
        if (lastSelected != null && t.getCode() == KeyCode.ENTER) {
            try {
                int number = Integer.parseInt(maxX.getText());
                if (number < 0 || number - lastSelected.getFitWidth() > sceneWidth) throw new NumberFormatException();
                lastSelected.setX(Integer.parseInt(maxX.getText()) - lastSelected.getFitWidth());
                minX.setText(Integer.toString((int) (lastSelected.getX())));
                drawBorders();
            } catch (NumberFormatException e){
                maxX.setText(Integer.toString((int) (lastSelected.getX() + lastSelected.getFitWidth())));
            }
        }
    };

    // set selected component maxY
    private EventHandler<KeyEvent> maxYSelection = (t) -> {
        if (lastSelected != null && t.getCode() == KeyCode.ENTER) {
            try {
                int number = Integer.parseInt(maxY.getText());
                if (number < 0 || number - lastSelected.getFitHeight() > sceneHeight) throw new NumberFormatException();
                lastSelected.setY(Integer.parseInt(maxY.getText()) - lastSelected.getFitHeight());
                minY.setText(Integer.toString((int) (lastSelected.getY())));
                drawBorders();
            } catch (NumberFormatException e){
                maxY.setText(Integer.toString((int) (lastSelected.getY() + lastSelected.getFitHeight())));
            }
        }
    };

    @FXML
    private void initialize() {
        Main.sceneEditorController = this;
        splitPane.setDividerPositions(menuArea.getPrefWidth());
        menuArea.setMaxWidth(menuArea.getPrefWidth());
        selectionEffect = new DropShadow( 20, Color.AQUA );
        lastSelected = null;
        width.setText(Integer.toString(sceneWidth));
        height.setText(Integer.toString(sceneHeight));
        borders = new ArrayList<Line>();
        drawBorders();
        drawArea.setMinSize(sceneWidth, sceneHeight);
        drawArea.setMaxSize(sceneWidth, sceneHeight);
        drawArea.setPrefSize(sceneWidth, sceneHeight);
        Callback<ListView<Infos>, ListCell<Infos>> factory = lv -> new ListCell<Infos>() {
            @Override
            protected void updateItem(Infos item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBox.setCellFactory(factory);
        comboBox.setButtonCell(factory.call(null));

        background.addEventHandler(MouseEvent.MOUSE_CLICKED, disableDragAndDropEvent); // if we are not in select/move mode we don't want to move an object
        selectAndMove.addEventHandler(MouseEvent.MOUSE_CLICKED, enableDragAndDropEvent); // we are in select/move so we can move an object
        tile.addEventHandler(MouseEvent.MOUSE_CLICKED, disableDragAndDropEvent); // if we are not in select/move mode we don't want to move an object
        character.addEventHandler(MouseEvent.MOUSE_CLICKED, disableDragAndDropEvent); // if we are not in select/move mode we don't want to move an object
        lastClickedX = -1;
        lastClickedY = -1;
        disableButtons();
        characters = new ArrayList<Character>();
        tiles = new ArrayList<Tile>();
        backgrounds = new ArrayList<Background>();

        defaultStyle.setToggleGroup(styleGroup);
        darkStyle.setToggleGroup(styleGroup);
        if (Main.cssPath.equals(""))
            defaultStyle.setSelected(true);
        else
            darkStyle.setSelected(true);

        selectAndMove.setToggleGroup(group);
        character.setToggleGroup(group);
        tile.setToggleGroup(group);
        background.setToggleGroup(group);

        width.addEventHandler(KeyEvent.KEY_PRESSED, widthSelection);
        height.addEventHandler(KeyEvent.KEY_PRESSED, heightSelection);

        minX.addEventHandler(KeyEvent.KEY_PRESSED, minXSelection);
        minY.addEventHandler(KeyEvent.KEY_PRESSED, minYSelection);
        maxX.addEventHandler(KeyEvent.KEY_PRESSED, maxXSelection);
        maxY.addEventHandler(KeyEvent.KEY_PRESSED, maxYSelection);

        exportHtml.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Main.switchStage(new SaveStage());
            }
        });

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Main.switchStage(new OpenStage());
            }
        });

        selectAndMove.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                status = SELECTANDMOVE;
                updateList();
                enableButtons();
            }
        });

        character.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                status = CHARACTER;
                updateList();
                disableButtons();
                minX.setText("");
                maxX.setText("");
                minY.setText("");
                maxY.setText("");
            }
        });

        tile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                status = TILE;
                updateList();
                disableButtons();
                minX.setText("");
                maxX.setText("");
                minY.setText("");
                maxY.setText("");
            }
        });

        background.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                status = BACKGROUND;
                updateList();
                disableButtons();
                minX.setText("");
                maxX.setText("");
                minY.setText("");
                maxY.setText("");
            }
        });

        background.setSelected(true);
        status = BACKGROUND;
        updateList();

        drawArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switch (status) {
                    case SELECTANDMOVE:
                        setLastClickedPosition(event.getX(), event.getY());
                        break;
                    case BACKGROUND:
                        drawBackground();
                        break;
                    default:
                        drawPicture(event.getX(), event.getY(), comboBox.getValue());
                        break;
                }
            }
        });

        delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                delete();
            }
        });

        clone.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                duplicate();
            }
        });

        // change selected style to default style
        defaultStyle.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (Main.cssURL != null) {
                    Main.getMainStage().getScene().getStylesheets().remove(Main.cssURL.toExternalForm());
                    Main.cssURL = null;
                    Main.cssPath = "";
                    Main.getDatabase().execScript(Database.delete_rows);
                }
            }
        });

        // change selected style to dark style
        darkStyle.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (Main.cssURL == null) {
                    Main.cssPath = DARK_STYLE_PATH;
                    Main.cssURL = getClass().getResource(Main.cssPath);
                    Main.getMainStage().getScene().getStylesheets().add(Main.cssURL.toExternalForm());
                    Main.getDatabase().execScript(Database.insert_row);
                }
            }
        });
    }

    // set selected style sheet in options
    public void setSelectedStyle() {
        if (Main.cssPath.equals(""))
            defaultStyle.setSelected(true);
        else
            darkStyle.setSelected(true);
    }

    // draw selected background;
    public void drawBackground() {
        String backgroundPath = "";
        Infos backgroundInfos = null;
        for (Background b : backgrounds) {
            backgroundPath = b.getInfos().getPath();
            backgroundInfos = b.getInfos();
            drawArea.getChildren().remove(b);
        }
        backgrounds.clear();
        if (comboBox.getValue() != null && status == BACKGROUND) {
            Image image = new Image(this.getClass().getResourceAsStream(comboBox.getValue().getPath()));
            for (int i = 0; i <= sceneWidth; i += comboBox.getValue().getZoom() * image.getWidth()-1) {
                for (int j = 0; j <= sceneHeight; j += comboBox.getValue().getZoom() * image.getHeight()-1) {
                    drawPicture(i, j, comboBox.getValue());
                }
            }
        }
        else if (backgroundPath != "") {
            Image image = new Image(this.getClass().getResourceAsStream(backgroundPath));
            for (int i = 0; i <= sceneWidth; i += backgroundInfos.getZoom() * image.getWidth()) {
                for (int j = 0; j <= sceneHeight; j += backgroundInfos.getZoom() * image.getHeight()) {
                    drawPicture(i, j, backgroundInfos);
                }
            }
        }
    }

    // draw a picture
    private void drawPicture(double x, double y, Infos infos) {
        if (infos != null) {
            int drawX = (int) x;
            int drawY = (int) y;
            switch (infos.getType()) {
                case Infos.TYPE_CHARACTER:
                    if (characters.size() == 0) {
                        Character c = new Character(drawX, drawY, infos, new Image(this.getClass().getResourceAsStream(infos.getPath())));
                        drawArea.getChildren().add(c);
                        characters.add(c);
                        character.setDisable(true);
                    }
                    break;
                case Infos.TYPE_BACKGROUND:
                    Background b = new Background(drawX, drawY, infos, new Image(this.getClass().getResourceAsStream(infos.getPath())));
                    drawArea.getChildren().add(b);
                    backgrounds.add(b);
                    b.toBack();
                    break;
                case Infos.TYPE_TILE:
                    Tile t = new Tile(drawX, drawY, infos, new Image(this.getClass().getResourceAsStream(infos.getPath())));
                    drawArea.getChildren().add(t);
                    tiles.add(t);
                    for (Tile tile : tiles)
                        t.toBack();
                    for (Background back : backgrounds)
                        back.toBack();
                default: break;
            }
            drawBorders();
        }
    }

    // draw a line vertically
    private void drawLineV(double x, double y, double width, Paint p) {
        Line l = new Line(x + borderWidth / 2, y, x + borderWidth / 2, y + width - borderWidth / 2);
        l.setStrokeWidth(10);
        l.setStroke(p);
        l.setFill(p);
        drawArea.getChildren().addAll(l);
        borders.add(l);
    }

    // draw a line horizontally
    private void drawLineH(double x, double y, double width, Paint p) {
        Line l = new Line(x, y + borderWidth / 2, x + width + borderWidth / 2, y + borderWidth / 2);
        l.setStrokeWidth(10);
        l.setStroke(p);
        l.setFill(p);
        drawArea.getChildren().addAll(l);
        borders.add(l);
    }

    private void drawBorders() {
        for (Line l : borders) drawArea.getChildren().remove(l);
        borders.clear();
        drawLineV(sceneWidth, 0, sceneHeight, Color.RED);
        drawLineH(0, sceneHeight, sceneWidth + 0, Color.RED);
    }

    // remember last clicked point when we are in select and move mode to know what object to delete or clone
    private void setLastClickedPosition(double x, double y) {
        if (lastSelected != null) lastSelected.setEffect(null);
        lastClickedX = x;
        lastClickedY = y;
        boolean selection = false;
        for (Character c : characters) {
            if (lastClickedX >= c.getX() && lastClickedX <= c.getX() + c.getFitWidth() && lastClickedY >= c.getY() && lastClickedY <= c.getY() + c.getFitHeight()) {
                lastSelected = c;
                selection = true;
                break;
            }
        }
        if (!selection) {
            for (Tile t : tiles) {
                if (lastClickedX >= t.getX() && lastClickedX <= t.getX() + t.getFitWidth() && lastClickedY >= t.getY() && lastClickedY <= t.getY() + t.getFitHeight()) {
                    lastSelected = t;
                    selection = true;
                    break;
                }
            }
        }
        if (selection) {
            minX.setText(Integer.toString((int) lastSelected.getX()));
            minY.setText(Integer.toString((int) lastSelected.getY()));
            maxX.setText(Integer.toString((int) (lastSelected.getX() + lastSelected.getFitWidth())));
            maxY.setText(Integer.toString((int)(lastSelected.getY() + lastSelected.getFitHeight())));
            lastSelected.setEffect(selectionEffect);
        }
        else {
            minX.setText("");
            minY.setText("");
            maxX.setText("");
            maxY.setText("");
        }
    }

    private boolean delete() {
        if (lastSelected == null) return false;
        else if (lastSelected instanceof Character) {
            drawArea.getChildren().remove(lastSelected);
            characters.remove(lastSelected);
            character.setDisable(false);
        }
        else {
            drawArea.getChildren().remove(lastSelected);
            tiles.remove(lastSelected);
        }
        lastClickedX = -1;
        lastClickedY = -1;
        lastSelected = null;
        return true;
    }

    // clone selected object
    private boolean duplicate() {
        if (lastClickedX == -1 || lastClickedY == -1 || lastSelected == null || !(lastSelected instanceof Tile)) return false;
        drawPicture(lastClickedX, lastClickedY, lastSelected.getInfos());
        enableDragAndDrop(tiles.get(tiles.size()-1));
        lastClickedX = -1;
        lastClickedY = -1;
        return true;
    }

    // disable buttons if we are not in select/move mode
    private void disableButtons() {
        delete.setDisable(true);
        clone.setDisable(true);
        lastClickedX = -1;
        lastClickedY = -1;
        if (lastSelected != null) lastSelected.setEffect(null);
        lastSelected = null;
    }

    // enable buttons if we are in select/move mode
    private void enableButtons() {
        delete.setDisable(false);
        clone.setDisable(false);
    }

    private void enableDragAndDrop(Component c) {
        c.addEventHandler(MouseEvent.MOUSE_PRESSED, catchEvent);
        c.addEventHandler(MouseEvent.MOUSE_DRAGGED, releaseEvent);
    }

    private void disableDragAndDrop(Component c) {
        c.removeEventHandler(MouseEvent.MOUSE_PRESSED, catchEvent);
        c.removeEventHandler(MouseEvent.MOUSE_DRAGGED, releaseEvent);
    }

    // enable drag and drop if we are in select/move mode
    private void enableDragAndDrop() {
        for (Character c : characters)
            enableDragAndDrop(c);
        for (Tile r : tiles)
            enableDragAndDrop(r);
    }

    // disable drag and drop if we are not in select/move mode
    private void disableDragAndDrop() {
        for (Character c : characters)
            disableDragAndDrop(c);
        for (Tile r : tiles)
            disableDragAndDrop(r);
    }

    private void reset() {
        background.setSelected(true);
        status = BACKGROUND;
        drawArea.getChildren().clear();
        characters.clear();
        tiles.clear();
        backgrounds.clear();
        sceneWidth = default_scene_width;
        sceneHeight = default_scene_height;
        minX.setText("");
        maxX.setText("");
        minY.setText("");
        maxY.setText("");
        drawBorders();
    }

    // generate an html file corresponding to the scene
    public void htmlGenerator(String path) {
        int level = 2;
        try {
            String filename = title.getText().trim().equals("")?Long.toString(System.currentTimeMillis())+".html":title.getText()+".html";
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path, filename)));
            writer.write("<!DOCTYPE html>\n<html>\n");
            writer.write("\t<head>\n\t\t<title>" + filename.substring(0, filename.length()-5) + "</title>\n\t</head>\n\t<body>\n");
            writer.write("\t\t<div class=\"scene\">\n" +
                    "\t\t\t<span class=\"width\">" + sceneWidth + "</span>\n" +
                    "\t\t\t<span class=\"height\">" + sceneHeight + "</span>\n" +
                    "\t\t</div>\n");
            String html = "";
            if (backgrounds.size() > 0) html+=backgrounds.get(0).toHtml(level);
            for (Character c : characters) html+=c.toHtml(level);
            for (Tile t : tiles) html+=t.toHtml(level);
            writer.write(html);
            writer.write("\t</body>\n</html>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // parse an html file using Jsoup
    public void parse(String toParse) {
        try {
            reset();
            Document doc = Jsoup.parse(new File(toParse), "UTF-8", "");
            Element title = doc.select("title").first();
            this.title.setText(title.text());
            Element scene = doc.select("div.scene").first();
            Element sceneW = scene.select("span.width").first();
            Element sceneH = scene.select("span.height").first();
            sceneWidth = Integer.parseInt(sceneW.text());
            sceneHeight = Integer.parseInt(sceneH.text());
            Element background = doc.select("div.background").first();
            Element backgroundZoom = background.select("span.zoom").first();
            Element backgroundPath = background.select("span.path").first();
            Element backgroundName = background.select("span.name").first();
            Image imageBack = new Image(this.getClass().getResourceAsStream(backgroundPath.text()));
            for (int i = 0; i <= sceneWidth; i += Integer.parseInt(backgroundZoom.text()) * imageBack.getWidth()) {
                for (int j = 0; j <= sceneHeight; j += Integer.parseInt(backgroundZoom.text()) * imageBack.getHeight()) {
                    drawPicture(i, j, new Infos(Infos.TYPE_BACKGROUND, Integer.parseInt(backgroundZoom.text()), backgroundName.text(), backgroundPath.text()));
                }
            }
            drawArea.setMinSize(sceneWidth, sceneHeight);
            drawArea.setMaxSize(sceneWidth, sceneHeight);
            drawArea.setPrefSize(sceneWidth, sceneHeight);
            height.setText(Integer.toString(sceneHeight));
            width.setText(Integer.toString(sceneWidth));
            drawBackground();
            Elements chars = doc.select("div.character");
            for (Element e : chars) {
                Element charX = e.select("span.x").first();
                Element charY = e.select("span.y").first();
                Element charSourceX = e.select("span.sourceX").first();
                Element charSourceY = e.select("span.sourceY").first();
                Element charSourceWidth = e.select("span.sourceWidth").first();
                Element charSourceHeight = e.select("span.sourceHeight").first();
                Element charZoom = e.select("span.zoom").first();
                Element charPath = e.select("span.path").first();
                Element charName = e.select("span.name").first();
                drawPicture(Integer.parseInt(charX.text()), Integer.parseInt(charY.text()), new Infos(Infos.TYPE_CHARACTER, Integer.parseInt(charZoom.text()), charName.text(), charPath.text(), Integer.parseInt(charSourceX.text()), Integer.parseInt(charSourceY.text()), Integer.parseInt(charSourceWidth.text()), Integer.parseInt(charSourceHeight.text())));
            }
            Elements tls = doc.select("div.tile");
            for (int i = tls.size()-1; i >= 0; i--) {
                Element TileType = tls.get(i).select("span.type").first();
                Element TileX = tls.get(i).select("span.x").first();
                Element TileY = tls.get(i).select("span.y").first();
                Element TileSourceX = tls.get(i).select("span.sourceX").first();
                Element TileSourceY = tls.get(i).select("span.sourceY").first();
                Element TileSourceWidth = tls.get(i).select("span.sourceWidth").first();
                Element TileSourceHeight = tls.get(i).select("span.sourceHeight").first();
                Element TileZoom = tls.get(i).select("span.zoom").first();
                Element TilePath = tls.get(i).select("span.path").first();
                Element TileName = tls.get(i).select("span.name").first();
                drawPicture(Integer.parseInt(TileX.text()), Integer.parseInt(TileY.text()), new Infos(TileType.text(), Integer.parseInt(TileZoom.text()), TileName.text(), TilePath.text(), Integer.parseInt(TileSourceX.text()), Integer.parseInt(TileSourceY.text()), Integer.parseInt(TileSourceWidth.text()), Integer.parseInt(TileSourceHeight.text())));
            }
            drawBorders();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
