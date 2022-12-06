package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.MeObject;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.ConnectionHandler;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;


public class ChatViewController implements Initializable {
    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);
    private final String URL = "ws://localhost:8080/chat";
    private final LinkedList<String> topics = new LinkedList<>(){{
        add("/topic/channel/1");
    }};
    private final SocketClient socketClient;

    private final RestClient restClient;

    private Channel currentChannel = new Channel(1L, "Test", false, false);

    private final MessageManager messageManager;
    @FXML
    public ListView<Message> messages;
    @FXML
    public Button sendButton;
    @FXML
    public TextField message;
    public ListView<String> channels;

    @FXML
    public ListView<String> direct;

    final ListChangeListener<Message> listChangeListener = new ListChangeListener<>() {
        @Override
        public void onChanged(Change c) {
            messages.scrollTo(messages.getItems().size() - 1);
        }
    };
    public ChatViewController(String token) {
        this.messageManager = new MessageManager();
        this.socketClient = new SocketClient(
            URL,
            token,
            new ConnectionHandler(this.messageManager, topics)
        );
        this.restClient = new RestClient(token);
    }

    @FXML
    public void onSendMessage(){
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            socketClient.sendMessage("/app/channel/"+currentChannel.channelID(), message.getText());
            message.clear();
        }
    }

    public void changeChannel(Channel channel){
        messageManager.getMessageBuffer(currentChannel).getMessages().removeListener(listChangeListener);
        currentChannel = channel;
        messages.setItems(messageManager.getMessageBuffer(currentChannel).getMessages());
        messageManager.getMessageBuffer(currentChannel).getMessages().addListener(listChangeListener);
        restClient.getLastFeed(currentChannel).forEach(messageManager::addMessage);
    }

    @FXML
    public void setOnKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            onSendMessage();
        }

    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        MeObject me = restClient.getMe();
        messages.setCellFactory(param -> new ListCell<>() {
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
                    GridPane message = MessageFactory.createMessage(item.getContent(), item.getAuthorFirstName() + " " + item.getAuthorLastName(), param.getWidth());

                    setGraphic(message);
                }
            }
        });

        channels.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {
                    Node channelIcon;
                    if (item.equals("Kanał drugi")) {
                        channelIcon = ChannelIconFactory.createChannelIcon(true, 12);
                    } else {
                        channelIcon = ChannelIconFactory.createChannelIcon(false, 12);
                    }

                    Button channelButton = ChangeChatButtonFactory.createChangeChatButton(channelIcon, item, param.getWidth());
                    channelButton.getStyleClass().add("my-button");

                    setGraphic(channelButton);
                }
            }
        });

        direct.setCellFactory(param -> new ListCell<>() {
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

                    setGraphic(directMessageButton);

                }
            }
        });
        messages.setItems(messageManager.getMessageBuffer(currentChannel).getMessages());
        messageManager.getMessageBuffer(currentChannel).getMessages().addListener(listChangeListener);
        restClient.getLastFeed(currentChannel).forEach(messageManager::addMessage);


        ObservableList<String> channelList = FXCollections.observableArrayList();
        channelList.add("Kanał pierwszy");
        channelList.add("Kanał drugi");
        channels.setItems(channelList);

        ObservableList<String> directMessages = FXCollections.observableArrayList();
        directMessages.add("Dawid Kaszyński");
        directMessages.add("Jan Kowalczewski");
        directMessages.add("Mikołaj Szawerda");
        direct.setItems(directMessages);

        try{
            socketClient.connect();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
