package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;

@Component
public class CreateChannelDialogController implements Initializable {

    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    private final ObservableList<UserInfo> usersInChannel;
    private final RestClient restClient;
    private final MainApplication application;
    private ViewController headController;
    @FXML
    public TextField channelName;

    @FXML
    public Pane addChannelDialog;

    @FXML
    public Button closeChannelDialogButton;

    @FXML
    public Button createChannelButton;

    @FXML
    public ListView<UserInfo> searchUserResultsView;

    @FXML
    public  ListView<UserInfo> usersAddedToChannelList;

    @FXML
    public TextField searchField;

    @FXML
    public CheckBox isPrivate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public CreateChannelDialogController(RestClient restClient, Application application) {
        this.restClient = restClient;
        this.application = (MainApplication) application;
        this.usersInChannel = FXCollections.observableArrayList();
        logger.info("ChatViewController created");
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

    public void show(){
        addChannelDialog.setMaxHeight(130);
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

    public void close(){
        addChannelDialog.setVisible(false);

        FadeTransition ft = new FadeTransition(Duration.millis(200), addChannelDialog);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    public void createChannel(){
        HashSet<Long> userIds = new HashSet<>();
        if(isPrivate.isSelected())
            usersInChannel.forEach(userInfo -> userIds.add(userInfo.getId()));

        if(!channelName.getText().isEmpty()) {
            restClient.createChannel(channelName.getText(), isPrivate.isSelected(), false, userIds);
            headController.closeChannelDialog();
        }
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
    public void onCloseChannelDialogButton(){
        FadeTransition ft = new FadeTransition(Duration.millis(200), addChannelDialog);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        headController.closeChannelDialog();
    }

    @FXML
    public void onSearchUserChannel(){
        if(searchField.getText().length() == 0) searchUserResultsView.setVisible(false);
        else {
            UserInfo currentUser = restClient.getCurrentUser();
            searchUserResultsView.setVisible(true);
            Collection<UserInfo> searchResultsCollection = restClient.searchUsers(searchField.getText());
            searchResultsCollection.remove(currentUser);
            ObservableList<UserInfo> searchResultsList = FXCollections.observableArrayList(searchResultsCollection);
            searchUserResultsView.setItems(searchResultsList);
            searchUserResultsView.prefHeightProperty().bind(Bindings.size((searchResultsList)).multiply(33));
        }
    }
    public void setHeadController(ViewController headController){
        this.headController=headController;
    }
    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
        addChannelDialog.setVisible(false);
        searchField.setVisible(false);
        searchUserResultsView.setVisible(false);
        usersAddedToChannelList.setVisible(false);
        usersAddedToChannelList.setItems(usersInChannel);
        closeChannelDialogButton.setOnAction(e->{
            onCloseChannelDialogButton();
        });
        closeChannelDialogButton.setSkin(new MyButtonSkin2(closeChannelDialogButton));

        createChannelButton.setOnAction(e->{
            createChannel();
        });
       createChannelButton.setSkin(new MyButtonSkin2(createChannelButton));
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

    }
}
