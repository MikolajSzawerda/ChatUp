package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.Animations;
import com.chatup.chatup_client.component.CellFactories;
import com.chatup.chatup_client.component.SearchAndAddUsersToChannel;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

@Component
public class CreateChannelDialogController implements Initializable {


    private final ObservableList<UserInfo> usersInChannel;
    private final RestClient restClient;
    private final ArrayList<Listener> listeners= new ArrayList<>();
    @FXML
    private TextField channelName;

    @FXML
    public Pane addChannelDialog;

    @FXML
    private Button closeChannelDialogButton;

    @FXML
    public Button createChannelButton;

    @FXML
    private ListView<UserInfo> searchUserResultsView;

    @FXML
    private  ListView<UserInfo> usersAddedToChannelList;

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox isPrivate;

    @Autowired
    public CreateChannelDialogController(RestClient restClient) {
        this.restClient = restClient;
        this.usersInChannel = FXCollections.observableArrayList();
    }

    public interface Listener{
        void onCloseDialog();
        void onChannelCreate(Channel channel);
    }

    void addListener(Listener listener){
        listeners.add(listener);
    }
    void removeListener(Listener listener) {listeners.remove(listener);}

    @FXML
    public void addUserToChannel(){
        UserInfo selectedUser = searchUserResultsView.getSelectionModel().getSelectedItem();
        if(!usersInChannel.contains(selectedUser)){
            usersInChannel.add(selectedUser);
            searchField.setText("");
            searchUserResultsView.setVisible(false);
        }
    }
    @FXML
    public void checkIsPrivate(){
        if(isPrivate.isSelected()) {
            addChannelDialog.setMaxHeight(130);
            dialogRollDownAnimation(550.0);
            buttonMoveAnimation(createChannelButton, 400.0);
            buttonMoveAnimation(closeChannelDialogButton, 400.0);
        }
        else{
            addChannelDialog.setMaxHeight(550);
            dialogRollUpAnimation(135.0);
            buttonMoveAnimation(createChannelButton, 0.0);
            buttonMoveAnimation(closeChannelDialogButton, 0.0);
        }
    }

    @FXML
    public void onCloseChannelDialogButton(){
        FadeTransition ft = Animations.createFadeOutTransition(addChannelDialog);
        ft.play();
        for(Listener listener:listeners){
            listener.onCloseDialog();
        }
    }

    @FXML
    public void onSearchUserChannel(){
        if(searchField.getText().length() == 0) searchUserResultsView.setVisible(false);
        else SearchAndAddUsersToChannel.searchUsers(searchField, searchUserResultsView, restClient);
    }

    public void createChannel(){
        HashSet<Long> userIds = new HashSet<>();
        if(isPrivate.isSelected())
            usersInChannel.forEach(userInfo -> userIds.add(userInfo.getId()));

        if(!channelName.getText().isEmpty()) {
            Channel newChannel =  restClient.createChannel(channelName.getText(), isPrivate.isSelected(), false, userIds);
            for(var listener:listeners) listener.onChannelCreate(newChannel);
        }
    }

    private void buttonMoveAnimation(Button button, Double endValue){
        Timeline timeline= new Timeline();
        timeline.setCycleCount(1);
        KeyValue kv1 = new KeyValue(button.translateYProperty(), endValue);
        KeyFrame kf1 = new KeyFrame(Duration.millis(500), kv1);
        timeline.getKeyFrames().add(kf1);
        timeline.play();
    }

    private void dialogRollUpAnimation(Double endValue){
        searchField.setText("");
        searchUserResultsView.setVisible(false);
        usersAddedToChannelList.setVisible(false);
        searchField.setVisible(false);
        Timeline timeline = dialogRollAnimation(endValue);
        timeline.play();
    }

    private void dialogRollDownAnimation(Double endValue){
        Timeline timeline = dialogRollAnimation(endValue);
        timeline.setOnFinished(e-> {
            searchField.setText("");
            usersAddedToChannelList.setVisible(true);
            searchField.setVisible(true);
        });
        timeline.play();
    }

    private Timeline dialogRollAnimation(Double endValue){
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        KeyValue kv = new KeyValue(addChannelDialog.maxHeightProperty(), endValue);
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timeline.getKeyFrames().add(kf);
        return timeline;
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
            usersInChannel.clear();
            usersInChannel.add(restClient.getCurrentUser());

            searchField.setVisible(false);
            searchUserResultsView.setVisible(false);

            usersAddedToChannelList.setVisible(false);
            usersAddedToChannelList.setItems(usersInChannel);

            addChannelDialog.setVisible(false);
            addChannelDialog.setMaxHeight(135.0);

            closeChannelDialogButton.setSkin(new MyButtonSkin2(closeChannelDialogButton));
            createChannelButton.setOnAction(e -> createChannel());
            createChannelButton.setSkin(new MyButtonSkin2(createChannelButton));

            CellFactories.userCellFactory(searchUserResultsView);
            CellFactories.userCellFactory(usersAddedToChannelList);

            addChannelDialog.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (true) {
                    channelName.setText("");
                    searchField.setText("");
                    searchUserResultsView.setVisible(false);
                    FadeTransition ft = Animations.createFadeInTransition(addChannelDialog);
                    ft.play();
                }
            });

    }
}
