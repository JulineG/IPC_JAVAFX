import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class CommunicationPOP3 implements Runnable {
    private Socket sComm;
    private BufferedInputStream bis;
    private BufferedWriter bw;
    private boolean connected = false;
    private String timestamp;
    private User currentUser;
    private boolean isConnected = false;


    public CommunicationPOP3(Socket s){
        this.sComm =s;
        System.out.println("new client");
        System.out.println(s.getInetAddress());

        //envoie +OK POP3 Server Ready

    }

    public void response(){
        try{
            readBuffer();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }



/*
    public void sendResponseOK(String filename){
        try{
            String[] ct=contentType(filename);
            bw.write("HTTP/1.1 200 OK\r\n");
            bw.write("Content-Type: "+ct[0]+ct[1]+"\r\n");
            bw.write("\r\n");

            //lecture fichier texte
            if(ct[0].equals("image") || ct[0].equals("text")) {
                InputStream flux = new FileInputStream(Server.getFolder() + filename);
                InputStreamReader lecture = new InputStreamReader(flux);
                BufferedReader buff = new BufferedReader(lecture);
                String textline;
                while ((textline = buff.readLine()) != null) {
                    bw.write(textline + "\r\n");
                    System.out.println(textline);
                }
                buff.close();
            }

            /*else if(ct[0].equals("")){
                File img = new File(Server.getFolder()+filename);
                BufferedImage bufferedImage = ImageIO.read(img);
                WritableRaster raster = bufferedImage .getRaster();
                DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
                System.out.println(data.getData().length);
                for(int i=0;i<data.getData().length;i++){
                    bw.write(data.getData()[i]);
                    System.out.println(data.getData()[i]);
                }
            }

            bw.flush();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
*/
    public boolean readBuffer(){
        ArrayList<ArrayList<String>> tab=new ArrayList<>();
        ArrayList<String> contenu = new ArrayList<>();
        try{
            System.out.println("----------------");

            String response = "";
            int stream;
            byte[] b = new byte[4096];
            stream = bis.read(b);
            response = new String(b, 0, stream);

            System.out.println(response);

            String[] parametres = response.split(" ");

            switch (parametres[0]) {

                case "APOP" : {

                    if(!isConnected){

                        currentUser = UserDAO.getUser(parametres[1], parametres[2], timestamp);
                        if(currentUser.getLogin() == null){

                            bw.write("-ERR wrong login or password");
                            bw.flush();

                        } else {

                            int countMails = MailDAO.countMails(currentUser.getIdUser());
                            bw.write("+OK maildrop has " + countMails + " message(s)");
                            bw.flush();
                            isConnected = true;
                        }

                    }

                    break;
                }

                case "STAT" : {

                    if(isConnected){

                        ArrayList<Mail> mails = MailDAO.getMails(currentUser.getIdUser());
                        int nbBytes = 0;

                        for(Mail m : mails){
                            nbBytes += m.getNbOctets();
                        }
                        int countMails = MailDAO.countMails(currentUser.getIdUser());
                        bw.write("+OK " + countMails + " " + nbBytes);
                        bw.flush();
                    }
                    else {

                    }
                    break;
                }

                case "RETR" : {

                    if(isConnected){

                        Mail mail = MailDAO.getMail(currentUser.getIdUser(), Integer.parseInt(parametres[1]));
                        if(mail.getObjet() == null){
                            bw.write("-ERR no such message");
                            bw.flush();
                        } else {

                            bw.write("+OK "+ mail.getNbOctets() +" octets\r\n");
                            bw.write("----\r\n");
                            bw.write("From: " + mail.getUserSrc().getLogin() + "\r\n");
                            bw.write("To: " + mail.getUserDst().getLogin() + "\r\n");
                            bw.write("Subject: " + mail.getObjet() + "\r\n");
                            bw.write("Date: " + mail.getDate() + "\r\n");
                            bw.write("Message-ID: <" + mail.getIdMail() + "@" + InetAddress.getLocalHost().getHostAddress()+">\r\n");
                            bw.write("\r\n");

                            for(int i = 0 ; i< mail.getCorps().length(); i += 78)
                                bw.write(mail.getCorps().substring(i, Math.min(mail.getCorps().length(), i + 78)) + "\r\n");

                            bw.write("----");
                            bw.flush();
                        }
                    }

                    break;
                }

                case "DELE" :{
                    //TODO : Handle Deletion
                }

                case "QUIT" : {

                    bw.write("+OK dewey POP3 server signing off");
                    bw.flush();

                    try{
                        bw.close();
                        bis.close();
                        sComm.close();
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                    }

                    break;
                }

                default :
                    // hmm
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    public static String createTimestamp(){
        String timestamp="";
        try {
            timestamp="<"+System.currentTimeMillis()+"@"+ InetAddress.getLocalHost().getHostAddress()+">";
        } catch (Exception e) {
            System.out.println("ERROR : create timestamp");
        }
        return timestamp;
    }

    @Override
    public void run() {
        try {
            boolean firstMsg = true;
            while(true) {
                bis = new BufferedInputStream(sComm.getInputStream());
                bw = new BufferedWriter(new OutputStreamWriter(sComm.getOutputStream()));
                //TODO : a modif car si yo se co avec pls comptes depuis la meme socket -> bug, modif du curretn user, ajouter un connected
                if(firstMsg){
                    //System.out.println("test");
                    timestamp = createTimestamp();
                    String srvRdy = "+OK POP3 server ready " + timestamp;
                    bw.write(srvRdy);

                    bw.flush();
                    firstMsg = false;
                }

                response();
            }
        } catch (IOException e) {

            try {
                bis.close();
                bw.close();
                sComm.close();
            } catch (IOException d) {
                System.out.println(d.getMessage());
            }

            System.out.println(e.getMessage());
        }
    }
}
