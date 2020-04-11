import java.io.UnsupportedEncodingException;
import java.util.Date;

public class Mail {

    private int idMail;
    private User UserSrc;
    private User UserDst;
    private String objet;
    private String corps;
    private String date;
    private int nbOctets;


    public Mail(){

    }

    public int getIdMail() {
        return idMail;
    }

    public void setIdMail(int idMail) {
        this.idMail = idMail;
    }

    public User getUserSrc() {
        return UserSrc;
    }

    public void setUserSrc(User userSrc) {
        this.UserSrc = userSrc;
    }

    public User getUserDst() {
        return UserDst;
    }

    public void setUserDst(User userDst) {
        this.UserDst = userDst;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getCorps() {
        return corps;
    }

    public void setCorps(String corps) {
        this.corps = corps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.date = sdf.format(date);
    }

    public int getNbOctets() {
        return nbOctets;
    }

    public void setNbOctets() {
        try {
            nbOctets = this.getCorps().getBytes("UTF-8").length + this.getDate().toString().getBytes("UTF-8").length
                    + this.getObjet().getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
