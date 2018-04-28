package sample;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;

import java.io.InputStream;

public class Component extends ImageView {
    private int x;
    private int y;
    private Infos infos;

    public Component(int x, int y, Infos infos, Image i) {
        super(i);
        this.infos = infos;
        setX(x);
        setY(y);
        Rectangle2D rect = infos.displayArea();
        if (rect == null) {
            setFitHeight(infos.getZoom() * this.getImage().getHeight());
            setFitWidth(infos.getZoom() * this.getImage().getWidth());
        }
        else {
            setViewport(rect);
            setFitHeight(infos.getZoom() * rect.getHeight());
            setFitWidth(infos.getZoom() * rect.getWidth());
        }
    }

    public Infos getInfos() {
        return infos;
    }
}

class Character extends Component {
    public Character(int x, int y, Infos infos, Image i) {
        super(x, y, infos, i);
    }

    public String toHtml(int level) {
        String html = "";
        for (int i = 0; i < level; i++) html+="\t";
        html+="<div class=\"character\">\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"x\">"+Integer.toString((int) this.getX())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"y\">"+Integer.toString((int) this.getY())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceX\">"+Integer.toString(getInfos().getX())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceY\">"+Integer.toString(getInfos().getY())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceWidth\">"+Integer.toString(getInfos().getW())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceHeight\">"+Integer.toString(getInfos().getH())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"zoom\">"+Integer.toString(getInfos().getZoom())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"path\">"+getInfos().getPath()+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"name\">"+getInfos().getName()+"</span>\n";
        for (int i = 0; i < level; i++) html+="\t";
        html+="</div>\n";
        return html;
    }
}

class Tile extends Component {
    public Tile(int x, int y, Infos infos, Image i) {
        super(x, y, infos, i);
    }

    public String toHtml(int level) {
        String html = "";
        for (int i = 0; i < level; i++) html+="\t";
        html+="<div class=\"tile\">\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"type\">"+getInfos().getSubtype()+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"x\">"+Integer.toString((int) this.getX())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"y\">"+Integer.toString((int) this.getY())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceX\">"+Integer.toString(getInfos().getX())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceY\">"+Integer.toString(getInfos().getY())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceWidth\">"+Integer.toString(getInfos().getW())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"sourceHeight\">"+Integer.toString(getInfos().getH())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"zoom\">"+Integer.toString(getInfos().getZoom())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"path\">"+getInfos().getPath()+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"name\">"+getInfos().getName()+"</span>\n";
        for (int i = 0; i < level; i++) html+="\t";
        html+="</div>\n";
        return html;
    }
}

class Background extends Component {
    public Background(int x, int y, Infos infos, Image i) {
        super(x, y, infos, i);
    }

    public String toHtml(int level) {
        String html = "";
        for (int i = 0; i < level; i++) html+="\t";
        html+="<div class=\"background\">\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"width\">"+Integer.toString((int) getFitWidth())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"height\">"+Integer.toString((int) getFitHeight())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"zoom\">"+Integer.toString(getInfos().getZoom())+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"path\">"+getInfos().getPath()+"</span>\n";
        for (int i = 0; i < level+1; i++) html+="\t";
        html+="<span class=\"name\">"+getInfos().getName()+"</span>\n";
        for (int i = 0; i < level; i++) html+="\t";
        html+="</div>\n";
        return html;
    }
}