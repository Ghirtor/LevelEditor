package sample;

import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class FileSelector extends DialogPane {

    private String directory = null ;
    private String file = null ;
    private ArrayList<String> listFile;
    private ArrayList<String> listRepParent;

    public FileSelector() {
        super();
        listRepParent = new ArrayList<>();
        listFile = new ArrayList<>();
    }



    // return arraylist of parents of file Path
    public ArrayList<String> getListRepParent(String path){

        String actualPath = "";
        File dir = new File(path) ;
        if (!dir.exists() || !dir.isDirectory()) return null ;

        directory = dir.getAbsolutePath() ;
        //  System.out.println(directory) ;
        file = null ;

        String[] files = dir.list();
        if (files!=null) {
            for(int i=0; i<files.length; i++) {
                File f = new File(path, files[i]);
                if (f.isDirectory()) files[i] = files[i]+ File.separator ;
                //        System.out.format("  file: %s\n",files[i]) ;
            }
        }

        //System.out.format("  parentdir: %s\n",File.separator) ;
        listRepParent.clear();
        String[] dirs = path.split(Pattern.quote(File.separator)) ;
        for (String p : dirs) {
            actualPath += p + File.separator;
            // if (p.equals("")) continue ;
            listRepParent.add(actualPath);
            // System.out.format("  parentdir: %s\n",p) ;
        }

        return listRepParent;
    }

    // return arraylist of files present in directory Path
    public ArrayList<String> getListFile(String path){

        File dir = new File(path) ;

        if (!dir.exists() || !dir.isDirectory()) return null ;

        directory = dir.getAbsolutePath() ;
        file = null ;

        String[] files = dir.list() ;
        if (files!=null) {
            listFile.clear();
            for(int i=0; i<files.length; i++) {
                File f = new File(path, files[i]);

                if (f.isDirectory()) {
                    files[i] = files[i]+ File.separator ;
                    listFile.add(f.getName()+ File.separator);
                } else if(f.isFile()){
                    listFile.add(f.getName());
                }
                // System.out.format("  file: %s\n",files[i]) ;
            }
        }

        return listFile;
    }






}