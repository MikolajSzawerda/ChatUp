package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.MessageBuffer;
import com.chatup.chatup_client.web.ConnectionHandler;
import com.chatup.chatup_client.web.SocketClient;
import javafx.beans.InvalidationListener;
import javafx.collections.ArrayChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextBoundsType;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class MainController implements Initializable {
    Logger logger = LoggerFactory.getLogger(MainController.class);
    private final String URL = "ws://localhost:8080/chat";
    private final SocketClient socketClient;
    private final LinkedList<String> topics = new LinkedList<>(){{
       add("/topic/channel/all");
       add("/app/hello");
    }};
    private final MessageBuffer messageBuffer;
    @FXML
    public ListView<Message> messages;
    @FXML
    public Button sendButton;
    @FXML
    public TextField message;
    @FXML
    public ListView<String> channels;

    @FXML
    public ListView<String> direct;

    public MainController(){
        this.messageBuffer = new MessageBuffer();
        this.socketClient = new SocketClient(URL, new ConnectionHandler(this.messageBuffer, topics));
    }



    @FXML
    public void onSendMessage(){
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){;
            socketClient.sendMessage("/app/channel/all", message.getText());
            message.clear();
        }
    }

    @FXML
    public void setOnKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            onSendMessage();
        }
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        messages.setCellFactory(param -> new ListCell<Message>() {
                    @Override
                    protected void updateItem(Message item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else if (item != null) {
                            setMaxWidth(param.getWidth());
                            setMinWidth(param.getWidth() - 100);
                            setPrefWidth(param.getWidth() - 100);

                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            Circle circle = new Circle(25);
                            circle.setFill(Color.rgb(254, 213, 66));
                            circle.setStroke(Color.rgb(0, 0, 0));
                            Text initials = new Text("KR");
                            initials.setBoundsType(TextBoundsType.VISUAL);
                            initials.setFont(Font.font(20));
                            initials.setStyle("-fx-font-family: 'Roboto Slab'");


                            GridPane grid = new GridPane();
                            StackPane stack = new StackPane();
                            Insets padding = new Insets(0, 30, 0, 0);
                            stack.setPadding(padding);
                            Text author = new Text("Kajetan Rozej");

                            author.setFont(Font.font("Calibri", FontWeight.EXTRA_BOLD, 15));
                            Text message_content = new Text(item.toString());
                            message_content.setFont(Font.font(12));
                            message_content.setWrappingWidth(param.getWidth() - 120); //to change in future
                            message_content.setTextOrigin(VPos.TOP);
                            stack.getChildren().addAll(circle, initials);
                            grid.add(stack, 0, 0, 1, 3);
                            grid.add(author, 1, 0, 1, 1);
                            grid.add(message_content, 1, 2, 1, 2);
                            //to change in future
                            if (message_content.getWrappingWidth() < message_content.getText().length()*3)
                                grid.setVgap(5);
                            setGraphic(grid);
                        }
                    }
                });

        messages.setItems(messageBuffer.getMessages());

        try{
            socketClient.connect();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}