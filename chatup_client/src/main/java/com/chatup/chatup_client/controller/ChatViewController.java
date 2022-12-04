package com.chatup.chatup_client.controller;

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
                    Button chanellButton = new Button();
                    Text chanelName = new Text(item);
                    FontIcon icon = new FontIcon();
                    Rectangle rect = new Rectangle();
                    HBox layout = new HBox();
                    StackPane stack = new StackPane();
                    Insets padding = new Insets(0, 5, 0, 0);
                    Text new_messages = new Text("34");
                    chanelName.setFont(Font.font("Calibri", FontPosture.REGULAR, 15));
                    if(item.equals("Kanał drugi")) {
                        icon.setFont(Font.font("Calibri", FontPosture.REGULAR, 40));
                        icon.setIconLiteral("fa-lock");
                        icon.setIconColor(Paint.valueOf("#6aba9c"));
                        icon.setStroke(Paint.valueOf("#000000"));
                        layout.getChildren().addAll(icon, chanelName);
                    }
                    else {
                        rect.setHeight(30);
                        rect.setWidth(30);
                        rect.setStyle("-fx-padding: 0 5 0 0");
                        rect.setStroke(Paint.valueOf("#000000"));
                        rect.setFill(Paint.valueOf("#6aba9c"));


                        stack.setPadding(padding);
                        new_messages.setBoundsType(TextBoundsType.VISUAL);
                        new_messages.setFont(Font.font("Roboto Slab", FontPosture.REGULAR, 20));
                        stack.getChildren().addAll(rect, new_messages);
                        layout.getChildren().addAll(stack, chanelName);
                    }




                    layout.setAlignment(Pos.CENTER_LEFT);
                    chanellButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    chanellButton.setGraphic(layout);
                    chanellButton.setPrefWidth(param.getWidth());
                    chanellButton.getStyleClass().add("my-button");

                    chanellButton.setSkin(new MyButtonSkin(chanellButton));

                    setGraphic(chanellButton);
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
                    Button directMessageButton = new Button();

                    Circle circle = new Circle(18);
                    circle.setFill(Color.rgb(233, 100, 232));
                    circle.setStroke(Color.rgb(0, 0, 0));
                    Text initials = new Text("DK");
                    initials.setBoundsType(TextBoundsType.VISUAL);
                    initials.setFont(Font.font("Roboto Slab", FontPosture.REGULAR, 12));

                    StackPane stack = new StackPane();
                    Insets padding = new Insets(0, 5, 0, 0);
                    stack.setPadding(padding);
                    stack.getChildren().addAll(circle, initials);

                    Text username = new Text(item);
                    username.setFont(Font.font("Calibri", FontPosture.REGULAR, 15));

                    directMessageButton.setPrefWidth(param.getWidth());
                    directMessageButton.getStyleClass().add("my-button");

                    HBox layout = new HBox();
                    layout.getChildren().addAll(stack, username);
                    layout.setAlignment(Pos.CENTER_LEFT);
                    directMessageButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    directMessageButton.setGraphic(layout);

                    directMessageButton.setOnAction((ActionEvent e) -> {

                    });


                    directMessageButton.setSkin(new MyButtonSkin(directMessageButton));
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
