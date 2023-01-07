package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class ChatViewController implements Initializable {
    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);
    private final MainApplication application;
    private final SocketClient socketClient;
    private final RestClient restClient;
    private final MessageManager messageManager;
    private final ChannelManager channelManager;
    @FXML
    public ListView<Message> messages;
    final ListChangeListener<Message> listChangeListener = new ListChangeListener<>() {
        @Override
        public void onChanged(Change c) {
            messages.scrollTo(messages.getItems().size() - 1);
        }
    };
    @FXML
    public Button sendButton;
    @FXML
    public TextField message;
    public ListView<Channel> channels;
    @FXML
    public Text userNameSurname;
    @FXML
    public StackPane userAvatar;
    @FXML
    public ListView<Channel> direct;
    private Channel currentChannel = new Channel();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ChatViewController(MessageManager messageManager, SocketClient socketClient, RestClient restClient, Application application, ChannelManager channelManager) {
        this.messageManager = messageManager;
        this.socketClient = socketClient;
        this.restClient = restClient;
        this.application = (MainApplication) application;
        this.channelManager = channelManager;
        logger.info("ChatViewController created");
        currentChannel.setId(1L);
        currentChannel.setName("Test test");
        currentChannel.setIsDirectMessage(false);
        currentChannel.setIsPrivate(false);
    }

    @FXML
    public void onSendMessage(){
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            socketClient.sendMessage("/app/channel/"+currentChannel.getId(), message.getText());
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

    private void setCellFactories() {
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
            protected void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {
                    Node channelIcon;
                    if (item.getIsPrivate()) {
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
            protected void updateItem(Channel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {

                    Insets padding = new Insets(0, 5, 0, 0);
                    StackPane avatar = AvatarFactory.createAvatar(item.getName(), 18.0, padding);
                    Button directMessageButton = ChangeChatButtonFactory.createChangeChatButton(avatar, item, param.getWidth());

                    setGraphic(directMessageButton);

                }
            }
        });
    }
    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        UserInfo currentUser = restClient.getCurrentUser();
        logger.info("Logged in user: {}", currentUser.toString());
        userNameSurname.setText(currentUser.toString());
        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 25.0, padding));
        setCellFactories();
        messages.setItems(messageManager.getMessageBuffer(currentChannel).getMessages());
        messageManager.getMessageBuffer(currentChannel).getMessages().addListener(listChangeListener);
        channels.setItems(channelManager.getStandardChannels());
        direct.setItems(channelManager.getDirectMessages());

        restClient.getLastFeed(currentChannel).forEach(messageManager::addMessage);
        restClient.listChannels().forEach(channelManager::addChannel);

        try{
            socketClient.connect();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
