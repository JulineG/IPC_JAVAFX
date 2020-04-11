import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import com.mysql.jdbc.Driver;

public class ConnexionBD {

    //URL de connexion
    private String url = "jdbc:mysql://localhost/mailipc";
    //Nom du user
    private String user = "userepul";
    //Mot de passe de l'utilisateur
    private String passwd = "epul";
    //Objet Connection
    private static Connection connect;

    private ConnexionBD(){

        try {
            connect = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getInstance(){

        if(connect == null){
            new ConnexionBD();
        }
        return connect;
    }

    public static void CloseConnection(){
        try {
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
