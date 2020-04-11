import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ClientPOP3 client = new ClientPOP3("localhost", 8000);
        if(client.readResponse() == 1) {
            primaryStage.setTitle("Client POP3");

            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            Scene scene = new Scene(grid, 500, 400);
            primaryStage.setScene(scene);
            Text scenetitle = new Text("Bienvenue");
            scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(scenetitle, 0, 0, 2, 1);

            Label userName = new Label("Login:");
            grid.add(userName, 0, 1);

            TextField userTextField = new TextField();
            grid.add(userTextField, 1, 1);

            Label pw = new Label("Mot de passe:");
            grid.add(pw, 0, 2);

            PasswordField pwBox = new PasswordField();
            grid.add(pwBox, 1, 2);

            Button btn = new Button("Se connecter");
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(btn);
            grid.add(hbBtn, 1, 4);
            final Text actiontarget = new Text();
            grid.add(actiontarget, 1, 6);
            primaryStage.show();
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    String username = userTextField.getText();
                    String password = pwBox.getText();
                    int userValide = client.userConnexion(username, password);
                    if(userValide == -2) {
                        actiontarget.setFill(Color.FIREBRICK);
                        actiontarget.setText("Nom d'utilisateur ou mot de passe incorrect");
                    }
                    if (userValide == 1){
                        primaryStage.close();
                        Stage secondaryStage = new Stage();
                        secondaryStage.setTitle("Boite de réception");
                        final FlowPane container = new FlowPane();
                        ScrollPane scroll = new ScrollPane();
                        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                        scroll.setPadding(new Insets(25, 25, 25, 25));
                        Scene scene = new Scene(scroll, 600, 500);
                        secondaryStage.setScene(scene);

                        ArrayList<String> mails = client.getMails();
                        int nbMails = 0;
                        if(mails.get(0).contains("-ERR")){
                            Text error = new Text("Vous n'avez aucun message.");
                            container.getChildren().add(error);
                            scroll.setContent(container);
                        } else{
                            nbMails = mails.size();
                            Text messNbmails = new Text("Vous avez "+nbMails+" messages.");
                            container.getChildren().add(messNbmails);
                            for(int i=0; i<nbMails; i++){
                                String contenu = mails.get(i).split("----")[1];
                                Text mailContent = new Text(contenu);

                                Rectangle rect = new Rectangle();
                                rect.setFill(Color.TRANSPARENT);
                                rect.setStroke(Color.BLACK);
                                rect.setWidth(550);
                                rect.setHeight(250);

                                Group group = new Group();
                                group.getChildren().addAll(rect, mailContent);

                                container.getChildren().add(group);
                            }
                            scroll.setContent(container);
                        }
                        secondaryStage.show();
                    }
                }
            });

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    client.sendQuit();
                    Platform.exit();
                    System.exit(0);
                }
            });

        } else {
            System.exit(0);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
