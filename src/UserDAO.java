import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public static User getUser(String login, String password, String timestamp){

        User user = new User();

        Connection co = ConnexionBD.getInstance();

        try {

            PreparedStatement rechercheUser = co.prepareStatement("SELECT * FROM user WHERE login = ?");
            rechercheUser.setString(1, login);

            ResultSet rs = rechercheUser.executeQuery();

            boolean notEmpty = rs.next();

            while(notEmpty){

                String res = "";
                try{

                    byte[] bytesOfMessage = (timestamp+rs.getString(3)).getBytes("UTF-8");
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] digest = md.digest(bytesOfMessage);
                    res =new String(digest,"UTF-8");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                if(res.equals(password)) {
                    user.setIdUser(rs.getInt(1));
                    user.setLogin(rs.getString(2));
                    user.setPassword(rs.getString(3));
                    notEmpty = rs.next();
                } else {
                    user.setIdUser(-1);
                    user.setLogin(null);
                }

            }

            rs.close();
        } catch (Exception e){
            user.setIdUser(-1);
            System.out.println(e.getMessage());
        }

        return user;
    }

    //ne récupère pas le mdp
    public static User getUserById(int idUser){
        User user = new User();

        Connection co = ConnexionBD.getInstance();

        try {

            PreparedStatement rechercheUser = co.prepareStatement("SELECT login FROM user WHERE idUser = ?");
            rechercheUser.setInt(1, idUser);

            ResultSet rs = rechercheUser.executeQuery();

            boolean notEmpty = rs.next();

            while(notEmpty){
                user.setIdUser(idUser);
                user.setLogin(rs.getString(1));
                notEmpty = rs.next();
            }

            rs.close();
        } catch (Exception e){
            user.setIdUser(-1);
            System.out.println(e.getMessage());
        }

        return user;
    }


}
