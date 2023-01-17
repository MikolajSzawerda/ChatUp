package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.Animations;
import com.chatup.chatup_client.component.CellFactories;
import com.chatup.chatup_client.component.SearchAndAddUsersToChannel;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Component
public class CreateDMDialogController implements Initializable {

    private final ArrayList<Listener> listeners= new ArrayList<>();
    private final RestClient restClient;
    @FXML
    private Button closeDMDialogButton;
    @FXML
    private ListView<UserInfo> searchUserResultsDM;
    @FXML
    private TextField searchFieldDM;
    @FXML
    public Pane addDMDialog;


    @Autowired
    public CreateDMDialogController(RestClient restClient) {
        this.restClient = restClient;
    }

    public interface Listener {
        void onDialogClose();
        void onDMCreate(UserInfo user);
    }

    public void addListener(Listener listener){listeners.add(listener);}
    public void removeListener(Listener listener) {listeners.remove(listener);}

    @FXML
    private void createDM(){
        UserInfo selectedUser = searchUserResultsDM.getSelectionModel().getSelectedItem();
        for(var listener:listeners) listener.onDMCreate(selectedUser);
    }

    @FXML
    private void onCloseDMDialogButton(){
        FadeTransition ft = Animations.createFadeOutTransition(addDMDialog);
        ft.play();
        for(var listener:listeners) listener.onDialogClose();
    }

    @FXML
    public void onSearchUserDM(){
        if(searchFieldDM.getText().length() == 0) searchUserResultsDM.setVisible(false);
        else SearchAndAddUsersToChannel.searchUsers(searchFieldDM, searchUserResultsDM, restClient);
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        closeDMDialogButton.setSkin(new MyButtonSkin2(closeDMDialogButton));
        CellFactories.userCellFactory(searchUserResultsDM);

        addDMDialog.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                searchFieldDM.setText("");
                searchUserResultsDM.setVisible(false);
                FadeTransition ft = Animations.createFadeInTransition(addDMDialog);
                ft.play();
            }
        });
    }
}
