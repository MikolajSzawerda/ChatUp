package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import com.sandec.mdfx.MarkdownView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.util.Duration;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Component
public class ChatViewController implements Initializable {
    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);
    private final MainApplication application;
    private final SocketClient socketClient;
    private final RestClient restClient;
    private final MessageManager messageManager;
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
    public ListView<String> channels;
    @FXML
    public Text userNameSurname;
    @FXML
    public StackPane userAvatar;

    @FXML
    public Button addChannel;

    @FXML
    public Rectangle backdrop;

    @FXML
    public Button closeChannelCreateButton;

    @FXML
    public Button createChannelButton;


    @FXML
    public ListView<String> searchUserResults;

    @FXML
    public TextField searchField;

    @FXML
    public Pane addDMDialog;

    @FXML
    public Pane addChannelDialog;

    @FXML
    public Button addDM;


    @FXML
    public ListView<String> direct;
    private Channel currentChannel = new Channel(1L, "Test", false, false);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ChatViewController(MessageManager messageManager, SocketClient socketClient, RestClient restClient, Application application) {
        this.messageManager = messageManager;
        this.socketClient = socketClient;
        this.restClient = restClient;
        this.application = (MainApplication) application;
        logger.info("ChatViewController created");
    }

    @FXML
    public void onSendMessage(){
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            socketClient.sendMessage("/app/channel/"+currentChannel.channelID(), message.getText());
            message.clear();
        }
    }

    @FXML
    public void onAddDM(){
        backdrop.setVisible(true);
        addDMDialog.setVisible(true);
        //searchUserResults.setVisible(false);
        FadeTransition ft = new FadeTransition(Duration.millis(200), addDMDialog);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    @FXML
    public void onAddChannel(){
        backdrop.setVisible(true);
        addChannelDialog.setVisible(true);
        searchUserResults.setVisible(false);
        FadeTransition ft = new FadeTransition(Duration.millis(200), addChannelDialog);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    @FXML
    public void onCloseButton(){
        FadeTransition ft = new FadeTransition(Duration.millis(200), addChannelDialog);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        addChannelDialog.setVisible(false);
        backdrop.setVisible(false);
    }

    @FXML
    public void onSearchUser(){
        if(searchField.getText().length() == 0) searchUserResults.setVisible(false);
        else searchUserResults.setVisible(true);
        // in future ask API about results;
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
        //closeChannelCreateButton.setSkin(new MyButtonSkin(closeChannelCreateButton));
        addChannelDialog.setVisible(false);
        addDMDialog.setVisible(false);
        backdrop.setVisible(false);
        UserInfo currentUser = restClient.getCurrentUser();
        logger.info("Logged in user: {}", currentUser.toString());
        userNameSurname.setText(currentUser.toString());
        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 25.0, padding));


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

        searchUserResults.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {

                    Insets padding = new Insets(0, 5, 0, 0);
                    StackPane avatar = AvatarFactory.createAvatar(item, 13.0, padding);
                    Button directMessageButton = ChangeChatButtonFactory.createChangeChatButton(avatar, item, param.getWidth());

                    setGraphic(directMessageButton);

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
                    StackPane avatar = AvatarFactory.createAvatar(item, 18.0, padding);
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

        ObservableList<String> searchResults = FXCollections.observableArrayList();
        searchResults.add("Dawid Kaszyński");
        searchResults.add("Jan Kowalczewski");
        searchResults.add("Mikołaj Szawerda");
        searchUserResults.setItems(searchResults);

        try{
            socketClient.connect();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
