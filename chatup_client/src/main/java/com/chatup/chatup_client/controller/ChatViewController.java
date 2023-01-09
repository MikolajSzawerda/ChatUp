package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.application.Application;
import javafx.collections.ListChangeListener;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;

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
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javafx.util.Duration;

import java.io.IOException;
import java.io.UncheckedIOException;

import java.util.Collection;
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

    private final ObservableList<UserInfo> usersInChannel;
    
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
    public TextField channelName;
    @FXML
    public Text userNameSurname;
    @FXML
    public StackPane userAvatar;

    @FXML
    public Button goToDashboard;

    @FXML
    public Button addChannel;

    @FXML
    public Rectangle backdrop;

    @FXML
    public Button closeChannelDialogButton;

    @FXML
    public Button createChannelButton;

    @FXML
    public Button closeDMDialogButton;

    @FXML
    public Button createDMButton;

    @FXML
    public ListView<UserInfo> searchUserResultsView;

    @FXML
    public  ListView<UserInfo> usersAddedToChannelList;

    @FXML
    public ListView<String> searchUserResultsDM;

    @FXML
    public TextField searchField;

    @FXML
    public TextField searchFieldDM;

    @FXML
    public Pane addDMDialog;

    @FXML
    public Pane addChannelDialog;

    @FXML
    public Button addDM;

    @FXML
    public CheckBox isPrivate;

    @FXML
    public ListView<Channel> direct;
    private Channel currentChannel;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ChatViewController(MessageManager messageManager, SocketClient socketClient, RestClient restClient, Application application, ChannelManager channelManager) {
        this.messageManager = messageManager;
        this.socketClient = socketClient;
        this.restClient = restClient;
        this.application = (MainApplication) application;
        this.channelManager = channelManager;
        this.usersInChannel = FXCollections.observableArrayList();
        logger.info("ChatViewController created");
    }

    @FXML
    public void onSendMessage(){
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            socketClient.sendMessage("/app/channel."+currentChannel.getId(), message.getText());
            message.clear();
        }
    }

    @FXML
    public void addUserToChannel(){
        UserInfo selectedUser = searchUserResultsView.getSelectionModel().getSelectedItem();
        if(!usersInChannel.contains(selectedUser)){
            usersInChannel.add(selectedUser);
            searchField.setText("");
            searchUserResultsView.setVisible(false);
        }
    }

    public void createChannel(){
        HashSet<Long> userIds = new HashSet<>();
        usersInChannel.forEach(userInfo -> userIds.add(userInfo.getId()));
        if(!channelName.getText().isEmpty())
        restClient.createChannel(channelName.getText(), isPrivate.isSelected(),  false, userIds);
        addChannelDialog.setVisible(false);
        backdrop.setVisible(false);
    }

    @FXML
    public void checkIsPrivate(){
        if(isPrivate.isSelected()) {
            addChannelDialog.setMaxHeight(130);

            Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            KeyValue kv = new KeyValue(addChannelDialog.maxHeightProperty(), 550.0);
            KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
            timeline.getKeyFrames().add(kf);

            Timeline timelineCreateButton= new Timeline();
            timelineCreateButton.setCycleCount(1);
            KeyValue kv1 = new KeyValue(createChannelButton.translateYProperty(), 400.0);
            KeyFrame kf1 = new KeyFrame(Duration.millis(500), kv1);
            timelineCreateButton.getKeyFrames().add(kf1);

            Timeline timelineCancelButton = new Timeline();
            timelineCancelButton.setCycleCount(1);
            KeyValue kv2 = new KeyValue(closeChannelDialogButton.translateYProperty(), 400.0);
            KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);
            timelineCancelButton.getKeyFrames().add(kf2);

            timeline.play();
            timelineCancelButton.play();
            timelineCreateButton.play();

            timeline.setOnFinished(e-> {
                searchField.setText("");
                usersAddedToChannelList.setVisible(true);
                searchField.setVisible(true);
            });
        }
        else{
            addChannelDialog.setMaxHeight(550);

            searchField.setText("");
            searchUserResultsView.setVisible(false);
            usersAddedToChannelList.setVisible(false);
            searchField.setVisible(false);

            Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            KeyValue kv = new KeyValue(addChannelDialog.maxHeightProperty(), 130.0);
            KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
            timeline.getKeyFrames().add(kf);

            Timeline timelineCreateButton= new Timeline();
            timelineCreateButton.setCycleCount(1);
            KeyValue kv1 = new KeyValue(createChannelButton.translateYProperty(), 0.0);
            KeyFrame kf1 = new KeyFrame(Duration.millis(500), kv1);
            timelineCreateButton.getKeyFrames().add(kf1);

            Timeline timelineCancelButton = new Timeline();
            timelineCancelButton.setCycleCount(1);
            KeyValue kv2 = new KeyValue(closeChannelDialogButton.translateYProperty(), 0.0);
            KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);
            timelineCancelButton.getKeyFrames().add(kf2);

            timeline.play();
            timelineCancelButton.play();
            timelineCreateButton.play();

        }
    }

    @FXML
    public void onGoToDashboard(ActionEvent e) throws IOException {
        application.switchToDashboardView(e, (Stage) goToDashboard.getScene().getWindow());
    }

    public void onAddDM(){
        backdrop.setVisible(true);
        addDMDialog.setVisible(true);
        searchFieldDM.setText("");
        searchUserResultsDM.setVisible(false);
        FadeTransition ft = new FadeTransition(Duration.millis(200), addDMDialog);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    public void onAddChannel(){
        backdrop.setVisible(true);
        addChannelDialog.setVisible(true);
        searchField.setText("");
        searchUserResultsView.setVisible(false);
        usersInChannel.clear();

        UserInfo currentUser = restClient.getCurrentUser();
        usersInChannel.add(currentUser);

        FadeTransition ft = new FadeTransition(Duration.millis(200), addChannelDialog);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    @FXML
    public void onCloseChannelDialogButton(){
        FadeTransition ft = new FadeTransition(Duration.millis(200), addChannelDialog);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        addChannelDialog.setVisible(false);
        backdrop.setVisible(false);
    }

    @FXML
    public void onCloseDMDialogButton(){
        FadeTransition ft = new FadeTransition(Duration.millis(200), addDMDialog);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        addDMDialog.setVisible(false);
        backdrop.setVisible(false);
    }

    @FXML
    public void onSearchUser(){
        if(searchField.getText().length() == 0) searchUserResultsView.setVisible(false);
        else searchUserResultsView.setVisible(true);
        Collection <UserInfo>  searchResultsCollection =  restClient.searchUsers(searchField.getText());
        ObservableList<UserInfo>  searchResultsList= FXCollections.observableArrayList(searchResultsCollection);
        searchUserResultsView.setItems(searchResultsList);
        searchUserResultsView.prefHeightProperty().bind(Bindings.size((searchResultsList)).multiply(33));

    }

    @FXML
    public void onSearchUserDM(){
        if(searchField.getText().length() == 0) searchUserResultsDM.setVisible(false);
        else searchUserResultsDM.setVisible(true);
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
                    GridPane message = MessageFactory.createMessage(item.getContent(),
                            item.getAuthorFirstName() + " " + item.getAuthorLastName(), item.getAuthorUsername(), param.getWidth());

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

         searchUserResultsView.setCellFactory(param -> new ListCell<>() {
             @Override
             protected void updateItem(UserInfo item, boolean empty) {
                 super.updateItem(item, empty);
                 if (empty) {
                     setText(null);
                     setGraphic(null);
                 } else if (item != null) {

                     Insets padding = new Insets(0, 5, 0, 0);
                     StackPane avatar = AvatarFactory.createAvatar(item.getFirstName()+" "+item.getLastName(), 13.0, padding);
//                     Button directMessageButton = ChangeChatButtonFactory.createChangeChatButton(avatar, item, param.getWidth());
//
//                     setGraphic(directMessageButton);
                     setText(item.getFirstName()+" "+item.getLastName());
                     setGraphic(avatar);


                 }
             }
         });

        usersAddedToChannelList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UserInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {

                    Insets padding = new Insets(0, 5, 0, 0);
                    StackPane avatar = AvatarFactory.createAvatar(item.getFirstName()+" "+item.getLastName(), 13.0, padding);
//                     Button directMessageButton = ChangeChatButtonFactory.createChangeChatButton(avatar, item, param.getWidth());
//
//                     setGraphic(directMessageButton);
                    setText(item.getFirstName()+" "+item.getLastName());
                    setGraphic(avatar);


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
        usersAddedToChannelList.setItems(usersInChannel);
        UserInfo currentUser = restClient.getCurrentUser();
        logger.info("Logged in user: {}", currentUser.toString());
        userNameSurname.setText(currentUser.toString());
        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 25.0, padding));
        channels.setItems(channelManager.getStandardChannels());
        direct.setItems(channelManager.getDirectMessages());
        Collection<Channel> channels = restClient.listChannels();

        // temporary lines for testing
        assert channels.size() > 0;
        channels.forEach((ch) -> {currentChannel = ch;});

        restClient.listChannels().forEach(channelManager::addChannel);
        setCellFactories();
        messages.setItems(messageManager.getMessageBuffer(currentChannel).getMessages());
        messageManager.getMessageBuffer(currentChannel).getMessages().addListener(listChangeListener);
        restClient.getLastFeed(currentChannel).forEach(messageManager::addMessage);



        addChannelDialog.setMaxHeight(130);
        addChannelDialog.setVisible(false);
        addDMDialog.setVisible(false);
        backdrop.setVisible(false);

        addDM.setOnAction(e->{
            onAddDM();
        });
        addDM.setSkin(new MyButtonSkin2(addDM));
        addChannel.setOnAction(e->{
            onAddChannel();
        });
        addChannel.setSkin(new MyButtonSkin2(addChannel));

        createChannelButton.setOnAction(e->{
            createChannel();
        });
        createChannelButton.setSkin(new MyButtonSkin2(createChannelButton));
        closeChannelDialogButton.setOnAction(e->{
            onCloseChannelDialogButton();
        });
        closeChannelDialogButton.setSkin(new MyButtonSkin2(closeChannelDialogButton));

        createDMButton.setOnAction(e->{

        });
        createDMButton.setSkin(new MyButtonSkin2(createDMButton));
        closeDMDialogButton.setOnAction(e->{
            onCloseDMDialogButton();
        });
        closeDMDialogButton.setSkin(new MyButtonSkin2(closeDMDialogButton));

        try{
            socketClient.connect();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
