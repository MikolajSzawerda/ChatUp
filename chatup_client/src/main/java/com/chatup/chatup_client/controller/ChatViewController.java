package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.components.AvatarFactory;
import com.chatup.chatup_client.components.ChangeChatButtonFactory;
import com.chatup.chatup_client.components.ChannelIconFactory;
import com.chatup.chatup_client.components.MessageFactor;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.manager.MessageBuffer;
import com.chatup.chatup_client.web.ConnectionHandler;
import com.chatup.chatup_client.web.SocketClient;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {
    Logger logger = LoggerFactory.getLogger(ChatViewController.class);
    private final String URL = "ws://localhost:8080/chat";
    private final LinkedList<String> topics = new LinkedList<>(){{
        add("/topic/channel/all");
        add("/app/hello");
    }};
    private final SocketClient socketClient;

    private final MessageBuffer messageBuffer;

    private static ChatViewController INSTANCE;
    @FXML
    public ListView<Message> messages;
    @FXML
    public Button sendButton;
    @FXML
    public TextField message;
    public ListView<String> channels;

    @FXML
    public ListView<String> direct;


    public ChatViewController() {
        INSTANCE = this;
        this.messageBuffer = new MessageBuffer();
        this.socketClient = new SocketClient(URL, new ConnectionHandler(this.messageBuffer, topics));
    }

    public ChatViewController getInstance() {
        return INSTANCE;
    }

    @FXML
    public void onSendMessage(){
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
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
                    GridPane message = MessageFactor.createMessage(item.toString(), "Kajetan Rożej", param.getWidth());

                    setGraphic(message);
                }
            }
        });

        channels.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {
                    Node channelIcon = new Button();
                    if(item.equals("Kanał drugi")) {
                        channelIcon = ChannelIconFactory.createChannelIcon(true, 12);
                    }
                    else{
                        channelIcon = ChannelIconFactory.createChannelIcon(false, 12);
                    }

                    Button channelButton = ChangeChatButtonFactory.createChangeChatButton(channelIcon, item, param.getWidth());
                    channelButton.getStyleClass().add("my-button");

                    setGraphic(channelButton);
                }
            }
        });

        direct.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {

                    Insets padding = new Insets(0, 5, 0, 0);
                    StackPane avatar = AvatarFactory.createAvatar("DK", 18.0, padding);
                    Button directMessageButton = ChangeChatButtonFactory.createChangeChatButton(avatar, item, param.getWidth());

//                    directMessageButton.setOnAction((ActionEvent e) -> {
//
//                    });

                    setGraphic(directMessageButton);

                }
            }
        });
        messages.setItems(messageBuffer.getMessages());
        messageBuffer.getMessages().addListener(new ListChangeListener<Message>() {
            @Override
            public void onChanged(Change<? extends Message> c) {
                messages.scrollTo(messages.getItems().size()-3);
            }
        });

        ObservableList<String> channelList = FXCollections.observableArrayList();
        channelList.add("Kanał pierwszy");
        channelList.add("Kanał drugi");
        channels.setItems(channelList);

        ObservableList<String> directMessages = FXCollections.observableArrayList();
        directMessages.add("Dawid Kaszyński");
        directMessages.add("Jan Kowalczewski");
        directMessages.add("Mikołaj Szawerda");
        direct.setItems(directMessages);




//        try{
//            socketClient.connect();
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}
