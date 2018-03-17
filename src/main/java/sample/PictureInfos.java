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
        pictures.put("tile1", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile1", PictureInfos.picture_dir+"tiles1.png", 1, 0, 46, 47));
        pictures.put("tile2", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile2", PictureInfos.picture_dir+"tiles1.png", 65, 16, 14, 15));
        pictures.put("tile3", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile3", PictureInfos.picture_dir+"tiles1.png", 0, 128, 47, 47));
        pictures.put("tile4", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile4", PictureInfos.picture_dir+"tiles1.png", 129, 112, 46, 47));
        pictures.put("tile5", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile5", PictureInfos.picture_dir+"tiles1.png", 193, 96, 62, 63));
        pictures.put("tile6", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile6", PictureInfos.picture_dir+"tiles1.png", 1, 192, 46, 79));
        pictures.put("tile7", new Infos(Infos.TYPE_TILE, Infos.ZOOM_TILE, "tile7", PictureInfos.picture_dir+"tiles1.png", 49, 144, 78, 79));
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
    public final static int ZOOM_TILE = 3;
    private String path;
    private String type;
    private int zoom;
    private String name;
    private int x;
    private int y;
    private int w;
    private int h;

    public Infos(String type, int zoom, String name, String path) {
        this.type = type;
        this.zoom = zoom;
        this.name = name;
        this.path = path;
        x = -1;
        y = -1;
        w = -1;
        h = -1;
    }

    @Override
    public int compareTo (Infos i) {
        return name.compareTo(i.getName());
    }

    public Infos(String type, int zoom, String name, String path, int x, int y, int w, int h) {
        this.type = type;
        this.zoom = zoom;
        this.name = name;
        this.path = path;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public String getType() {
        return type;
    }

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