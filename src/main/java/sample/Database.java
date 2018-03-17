package sample;

import org.hsqldb.cmdline.SqlFile;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

class DAOFactory extends AbstractDAOFactory {

    public DAO getStyleSheetDAO (Database d) {
        return new StyleSheetDAO(d.getConnection());
    }
}

abstract class AbstractDAOFactory {
    public static final int DATABASE_DAO_FACTORY = 0;

    public abstract DAO getStyleSheetDAO (Database d);

    public static AbstractDAOFactory getFactory(int type){
        switch(type){
            case DATABASE_DAO_FACTORY:
                return new DAOFactory();
            default:
                return null;
        }
    }
}

class StyleSheet {
    private String path;

    public StyleSheet(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

abstract class DAO<T> {
    protected Connection connect = null;

    public DAO (Connection conn){
        this.connect = conn;
    }

    public abstract int rowCount();
    public abstract T findAll();
}

class StyleSheetDAO extends DAO<StyleSheet> {
    private String rowCountQuery = "select count(*) from styleSheet";
    private String findAllQuery = "select * from styleSheet";

    public StyleSheetDAO(Connection conn) {
        super(conn);
    }

    public int rowCount() {
        int count = -1;
        try {
            PreparedStatement prepare = connect.prepareStatement(rowCountQuery);
            ResultSet result = prepare.executeQuery();
            if (result.next())
                count = result.getInt(1);
            prepare.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public StyleSheet findAll() {
        StyleSheet s = null;
        try {
            PreparedStatement prepare = connect.prepareStatement(findAllQuery);
            ResultSet result = prepare.executeQuery();
            if (result.next())
                s = new StyleSheet(result.getString(1));
            prepare.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }
}

public class Database {
    private final static String url = "jdbc:hsqldb:file:DB_GhirtorTool";
    private final static String user = "sa";
    private final static String passwd = "";
    private Connection connect;
    public static String create_table = "/sql/create_table.sql";
    public static String delete_rows = "/sql/delete_rows.sql";
    public static String insert_row = "/sql/insert_row.sql";

    public Connection getConnection () {
        try {
            if (connect == null || connect.isClosed())
                login();
        } catch (SQLException e) {
            login();
        }
        return connect;
    }

    public ArrayList<String> getTables() {
        ArrayList<String> tables = new ArrayList<String>();
        DatabaseMetaData md;
        try {
            md = getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                if (rs.getString(3).contains("STYLESHEET"))
                    tables.add(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public void login () {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            connect = DriverManager.getConnection(url, user, passwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execScript(String s) {
        int lineCount = -1;
        if (!s.equals(create_table)) {
            DAO<StyleSheet> sDAO = Main.factory.getStyleSheetDAO(this);
            lineCount = sDAO.rowCount();
        }
        if ((getTables().size() == 0 && s.equals(create_table)) || (s.equals(delete_rows) && lineCount > 0) || (s.equals(insert_row) && lineCount == 0)) {
            getConnection();
            try (InputStream inputStream = getClass().getResourceAsStream(s)) {
                SqlFile sqlFile = new SqlFile(new InputStreamReader(inputStream), "init", System.out, "UTF-8", false, new File("."));
                sqlFile.setConnection(connect);
                sqlFile.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logout () {
        try {
            Statement statement = connect.createStatement();
            statement.executeQuery("SHUTDOWN");
            statement.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
