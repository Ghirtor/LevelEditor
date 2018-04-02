package sample;

import javafx.geometry.Rectangle2D;

import java.util.*;

import static java.util.Collections.*;

public class PictureInfos {
    public static String picture_dir = "/pictures/";
    public static HashMap<String, Infos> pictures = new HashMap<String, Infos>();
    public static int backgroundCount = 7;
    static {
        pictures.put(PictureInfos.picture_dir+"character.png", new Infos(Infos.TYPE_CHARACTER, Infos.ZOOM_CHARACTER, Infos.NAME_CHARACTER, PictureInfos.picture_dir+"character.png", 0, 0, 25, 48));
        pictures.put("tile1", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile01", PictureInfos.picture_dir+"tiles1.png", 3, 8, 42, 37));
        pictures.put("tile2", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile02", PictureInfos.picture_dir+"tiles1.png", 65, 16, 14, 15));
        pictures.put("tile3", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile03", PictureInfos.picture_dir+"tiles1.png", 64, 64, 18, 15));
        pictures.put("tile4", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile04", PictureInfos.picture_dir+"tiles1.png", 94, 64, 20, 15));
        pictures.put("tile5", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile05", PictureInfos.picture_dir+"tiles1.png", 126, 64, 18, 15));
        pictures.put("tile6", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile06", PictureInfos.picture_dir+"tiles1.png", 82, 48, 12, 79));
        pictures.put("tile7", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile07", PictureInfos.picture_dir+"tiles1.png", 210, 96, 28, 61));
        pictures.put("tile8", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile08", PictureInfos.picture_dir+"tiles1.png", 238, 112, 18, 29));
        pictures.put("tile9", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile09", PictureInfos.picture_dir+"tiles1.png", 192, 112, 18, 29));
        pictures.put("tile10", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile10", PictureInfos.picture_dir+"tiles1.png", 209, 142, 30, 17));
        pictures.put("tile11", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile11", PictureInfos.picture_dir+"tiles1.png", 113, 110, 14, 17));
        pictures.put("tile12", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile12", PictureInfos.picture_dir+"tiles1.png", 66, 160, 16, 45));
        pictures.put("tile13", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile13", PictureInfos.picture_dir+"tiles1.png", 95, 160, 16, 45));
        pictures.put("tile14", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile14", PictureInfos.picture_dir+"tiles1.png", 65, 190, 17, 17));
        pictures.put("tile15", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile15", PictureInfos.picture_dir+"tiles1.png", 94, 190, 17, 17));
        pictures.put("tile16", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile16", PictureInfos.picture_dir+"tiles1.png", 81, 254, 46, 18));
        pictures.put("tile17", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile17", PictureInfos.picture_dir+"tiles1.png", 83, 240, 42, 45));
        pictures.put("laser", new Infos(Infos.TYPE_LASER, Infos.ZOOM_LASER, "laser", PictureInfos.picture_dir+"laser.png", 1, 11, 14, 53));
        pictures.put("decoration", new Infos(Infos.TYPE_DECORATION, Infos.ZOOM_DECORATION, "decoration", PictureInfos.picture_dir+"decoration.png", 1, 1, 14, 15));
        pictures.put("endlevel", new Infos(Infos.TYPE_ENDLEVEL, Infos.ZOOM_ENDLEVEL, "endlevel", PictureInfos.picture_dir+"endlevel_turning_off.png", 80, 0, 16, 16));
        for (int i = 1; i <= backgroundCount; i++)
            pictures.put(PictureInfos.picture_dir+"background"+i+".bmp", new Infos(Infos.TYPE_BACKGROUND, Infos.ZOOM_BACKGROUND, "background"+i, PictureInfos.picture_dir+"background"+i+".bmp"));
    }

    public static ArrayList<Infos> Filter(String type) {
        ArrayList<Infos> paths = new ArrayList<Infos>();
        for (String s : pictures.keySet())
            if (pictures.get(s).getType() == type) paths.add(pictures.get(s));
        Collections.sort(paths);
        return paths;
    }
}

class Infos implements Comparable<Infos> {
    public final static String TYPE_CHARACTER = "CHARACTER";
    public final static int ZOOM_CHARACTER = 3;
    public final static String NAME_CHARACTER = "character";
    public final static String TYPE_BACKGROUND = "BACKGROUND";
    public final static int ZOOM_BACKGROUND = 1;
    public final static String TYPE_TILE = "TILE";
    public final static String TYPE_LASER = "LASER";
    public final static String TYPE_DECORATION = "DECORATION";
    public final static String TYPE_ENDLEVEL = "ENDLEVEL";
    public final static int ZOOM_TILE = 3;
    public final static int ZOOM_LASER = 4;
    public final static int ZOOM_DECORATION = 3;
    public final static int ZOOM_ENDLEVEL = 4;
    private String path;
    private String type;
    private String subtype;
    private int zoom;
    private String name;
    private int x;
    private int y;
    private int w;
    private int h;

    public Infos(String type, int zoom, String name, String path) {
        this.type = type.equals(TYPE_BACKGROUND)||type.equals(TYPE_CHARACTER)||type.equals(TYPE_TILE)?type:TYPE_TILE;
        this.zoom = zoom;
        this.name = name;
        this.path = path;
        x = -1;
        y = -1;
        w = -1;
        h = -1;
        subtype = type;
    }

    @Override
    public int compareTo (Infos i) {
        return name.compareTo(i.getName());
    }

    public Infos(String type, int zoom, String name, String path, int x, int y, int w, int h) {
        this.type = type.equals(TYPE_BACKGROUND)||type.equals(TYPE_CHARACTER)||type.equals(TYPE_TILE)?type:TYPE_TILE;
        this.zoom = zoom;
        this.name = name;
        this.path = path;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        subtype = type;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() { return subtype; }

    public int getZoom() {
        return zoom;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getW() { return w; }

    public int getH() { return h; }

    public Rectangle2D displayArea() {
        if (w == -1 || h == -1) return null;
        else return new Rectangle2D(x, y, w, h);
    }
}