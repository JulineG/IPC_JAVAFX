import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;

public class ServeurPOP3 {

    public final int PORT = 8000;
    public SSLServerSocket ss;
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
                SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                ss = (SSLServerSocket) factory.createServerSocket(port);
                String[] suites = ss.getSupportedCipherSuites();
                ArrayList<String> anon_suites = new ArrayList<>();
                for (int i = 0; i < suites.length; i++) {
                    if (suites[i].contains("anon")) { anon_suites.add(suites[i]);}
                }
                ss.setEnabledCipherSuites(anon_suites.toArray(new String[anon_suites.size()]));
                ss.setEnabledProtocols(ss.getSupportedProtocols());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void start(){
            try {
                while(true) {
                    System.out.println("waiting for clients...");
                    CommunicationPOP3 c = new CommunicationPOP3((SSLSocket)ss.accept());
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

