package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.MessageBuffer;
import com.chatup.chatup_client.web.ConnectionHandler;
import com.chatup.chatup_client.web.SocketClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public MainController(){
        this.messageBuffer = new MessageBuffer();
        this.socketClient = new SocketClient(URL, new ConnectionHandler(this.messageBuffer, topics));
    }



    @FXML
    public void onSendMessage(){
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            socketClient.sendMessage("/app/channel/all", message.getText());
        }
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
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