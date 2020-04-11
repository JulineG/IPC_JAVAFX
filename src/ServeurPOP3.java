import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;

public class ServeurPOP3 {

    public final int PORT = 8000;
    public ServerSocket ss;
    public BufferedReader in;
    public PrintStream out;
    public Socket comm;

    /* Fonction Test
    public Socket initServeurSocket(){

        try{
            ss = new ServerSocket(PORT);
            comm = ss.accept();

            in = new BufferedReader(new InputStreamReader(comm.getInputStream()));
            out = new PrintStream(comm.getOutputStream());

            System.out.println("Connexion établie avec " + comm.getInetAddress());

        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }

        return comm;
    }
    */

        public ServeurPOP3(int port){
            try {
                ss=new ServerSocket(port);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void start(){
            try {
                while(true) {
                    System.out.println("waiting for clients...");
                    CommunicationPOP3 c = new CommunicationPOP3(ss.accept());
                    //wait for opensuccess
                    new Thread(c).start();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public static void main(String args[]){

            ServeurPOP3 s=new ServeurPOP3(8000);
            s.start();
        }
    }

