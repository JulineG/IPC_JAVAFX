import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientPOP3 {

    private BufferedWriter bw;
    private BufferedInputStream br;
    private Socket sClient;
    private String timestamp;



    public ClientPOP3(String nomServ, int port){
        try{

            sClient = new Socket(InetAddress.getByName(nomServ), port);
            System.out.println("Connexion réussie sur le serveur : " + nomServ);
            bw = new BufferedWriter(new OutputStreamWriter(sClient.getOutputStream(), StandardCharsets.UTF_8));
            br = new BufferedInputStream(sClient.getInputStream());
        }catch (Exception e){
            System.out.println("ERROR : connection failed");
        }
    }

    public String getTimeStamp(){
        return this.timestamp;
    }

    public void setTimestamp(String str){
        String[] bloc=str.split(" ");
        this.timestamp=bloc[4];
    }

    public boolean sendApop(String username, String password){
        try{
            byte[] bytesOfMessage = (this.timestamp+password).getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytesOfMessage);
            String res =new String(digest,"UTF-8");
            System.out.println(res);
            System.out.println("C : APOP "+username+" "+res);
            bw.write("APOP "+username+" "+res);
            bw.flush();
        }catch (Exception e) {
            System.out.println("ERROR : send apop");
            return false;
        }
        return true;
    }

    public boolean sendStat(){
        try{
            System.out.println("C : STAT");
            bw.write("STAT");
            bw.flush();
        }catch (Exception e) {
            System.out.println("ERROR : send stat");
            return false;
        }
        return true;
    }

    public int readResponse(){
        //return 1 si ok
        //return -1 si pb connexion
        //return -2 si serveur envoi -ERR
        try {
            String response = "";
            int stream;
            byte[] b = new byte[4096];
            stream = br.read(b);
            response = new String(b, 0, stream);
            System.out.println("S : "+response);

            //cas erreur
            if(response.contains("-ERR")) {
                return -2;
            }
            //premier retour connexion
            if(response.contains("+OK POP3 server ready")) {
                setTimestamp(response);
                return 1;
            }
            //apop sucessful
            if(response.contains("+OK maildrop")) {
                return 1;
            }
            //connection sucessful
            if(response.contains("+OK dewey POP3 server signing off")) {
                return 1;
            }
            return 1;
        } catch (Exception e){
            return -1;
        }
    }

    public int readResponseStat(){
        try {
            String response = "";
            int stream;
            byte[] b = new byte[4096];
            stream = br.read(b);
            response = new String(b, 0, stream);
            System.out.println("S : "+response);

            //ok
            if(response.contains("+OK")) {
                String[] bloc=response.split(" ");
                return Integer.parseInt(bloc[1]);
            }


            return 0;
        } catch (Exception e){
            System.out.println("ERROR : read response stat");
            return 0;
        }
    }

    public String doRetr(int n){
        //send
        try{
            System.out.println("C : RETR "+n);
            bw.write("RETR "+n);
            bw.flush();
        }catch (Exception e) {
            System.out.println("ERROR : send retr "+n);
        }
        //read
        try {
            String response = "";
            int stream;
            byte[] b = new byte[4096];
            stream = br.read(b);
            response = new String(b, 0, stream);
            return response;

            /*//ok
            if(response.contains("+OK")) {

            }
            //erreur
            if(response.contains("-ERR")) {

            }*/

        } catch (Exception e){
            return  "ERROR : read retr "+n;
        }

    }

    public void sendQuit(){
        try{
            System.out.println("C : QUIT");
            bw.write("QUIT");
            bw.flush();
        }catch (Exception e) {
            System.out.println("ERROR : send quit");
        }
    }

    public int userConnexion(String username, String password) {
        //connexion APOP
        sendApop(username, password);
        //si APOP ok démarrer l'échange
        int response = readResponse();
        return response;
    }

    public ArrayList<String> getMails(){
        //get timestamp
        System.out.println("--CONNEXION MAILS POP3--");
        sendStat();
        int nbMail = readResponseStat();
        ArrayList<String> mails = new ArrayList<>();
        if(nbMail == 0){
            mails.add("-ERR");
        }
        for (int i = 1; nbMail > 0 && i <= nbMail; i++) {
            mails.add(doRetr(i));
        }
        sendQuit();
        readResponse();
        try {
            br.close();
            bw.close();
            sClient.close();
        } catch (IOException e) {
            mails.add("ERROR : close socket client");
            return mails;
        }
        return mails;
    }

}
