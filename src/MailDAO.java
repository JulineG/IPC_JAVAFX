import java.sql.*;
import java.util.ArrayList;

public class MailDAO {

    public static ArrayList<Mail> getMails(int refUserDst){

        ArrayList<Mail> listMails = new ArrayList<>();

        Connection co = ConnexionBD.getInstance();

        try {
            PreparedStatement rechercheMails = co.prepareStatement("SELECT * FROM mail WHERE refUserDst= ?");
            rechercheMails.setInt(1,refUserDst);

            ResultSet rs = rechercheMails.executeQuery();


            boolean notEmpty = rs.next();

            while(notEmpty){
                Mail mail = new Mail();

                mail.setIdMail(rs.getInt(1));
                mail.setUserSrc(UserDAO.getUserById(rs.getInt(2)));
                mail.setUserDst(UserDAO.getUserById(rs.getInt(3)));
                mail.setObjet(rs.getString(4));
                mail.setCorps(rs.getString(5));

                Timestamp timestamp = rs.getTimestamp(6);
                mail.setDate(timestamp);
                mail.setNbOctets();

                listMails.add(mail);
                notEmpty = rs.next();
            }

            rs.close();
        } catch (Exception e){
            System.out.println("erreur getmails");
        }

        return listMails;
    }

    public static int countMails(int refUserDst){

        int count = -1;
        Connection co = ConnexionBD.getInstance();

        try {
            PreparedStatement rechercheMails = co.prepareStatement("SELECT COUNT(*) FROM mail WHERE refUserDst= ?");
            rechercheMails.setInt(1,refUserDst);

            ResultSet rs = rechercheMails.executeQuery();

            boolean notEmpty = rs.next();

            while(notEmpty){

                count = rs.getInt(1);
                notEmpty = rs.next();
            }

            rs.close();
        } catch (Exception e){
            System.out.println("erreur count mails");
        }

        return count;
    }

    public static Mail getMail(int refUserDst, int numMail){

        Mail mail = new Mail();
        Connection co = ConnexionBD.getInstance();

        try {
            PreparedStatement rechercheMails = co.prepareStatement("SELECT * FROM mail WHERE refUserDst= ? ORDER BY date LIMIT 1 OFFSET ?");
            rechercheMails.setInt(1,refUserDst);
            rechercheMails.setInt(2, numMail-1);

            ResultSet rs = rechercheMails.executeQuery();

            boolean notEmpty = rs.next();

            while(notEmpty){

                mail.setIdMail(rs.getInt(1));
                mail.setUserSrc(UserDAO.getUserById(rs.getInt(2)));
                mail.setUserDst(UserDAO.getUserById(rs.getInt(3)));
                mail.setObjet(rs.getString(4));
                mail.setCorps(rs.getString(5));
                Timestamp timestamp = rs.getTimestamp(6);
                mail.setDate(timestamp);

                mail.setNbOctets();

                notEmpty = rs.next();
            }

            rs.close();
        } catch (Exception e){
            System.out.println("erreur getMail");
        }

        return mail;

    }

    public static void prepareDeletingMail(int refUserDst, int numMail){
        Connection co = ConnexionBD.getInstance();

        try {
            Mail mail = getMail(refUserDst, numMail);
            PreparedStatement rechercheMails = co.prepareStatement("UPDATE mail SET isDeleting = 1 WHERE idMail = ?");
            rechercheMails.setInt(1, mail.getIdMail());

            rechercheMails.executeUpdate();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void cancelDeletion(){
        Connection co = ConnexionBD.getInstance();

        try {
            PreparedStatement rechercheMails = co.prepareStatement("UPDATE mail SET isDeleting = 0");

            rechercheMails.executeUpdate();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void deleteMails(){
        Connection co = ConnexionBD.getInstance();

        try {
            PreparedStatement rechercheMails = co.prepareStatement("DELETE from mail WHERE isDeleting = 1");

            rechercheMails.executeUpdate();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
