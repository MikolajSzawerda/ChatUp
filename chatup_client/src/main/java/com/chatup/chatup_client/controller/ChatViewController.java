package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;


@Component
public class ChatViewController implements Initializable {
    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    private ChatViewController headController;
    private final RestClient restClient;
    private final MainApplication application;
    private final SocketClient socketClient;
    

    @FXML
    public Rectangle backdrop;
    @FXML
    private HeadbarController headbarController;

    @FXML
    private SidebarController sidebarController;

    @FXML
    private CreateDMDialogController createDMDialogController;

    @FXML
    private CreateChannelDialogController createChannelDialogController;

    @FXML
    private MessagingController messagingController;

    @FXML
    private DashboardController dashboardController;

    private Channel currentChannel;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ChatViewController(SocketClient socketClient, RestClient restClient, Application application) {
        this.socketClient = socketClient;
        this.restClient = restClient;
        this.application = (MainApplication) application;
        logger.info("ChatViewController created");
    }



    public void switchToMessaging(){
        dashboardController.dashboard.setVisible(false);
        messagingController.messaging.setVisible(true);
    }

    public void switchToDashboard(){
        messagingController.messaging.setVisible(false);
        dashboardController.dashboard.setVisible(true);
    }

    public void switchToLoginView() throws IOException {
        application.switchToLoginView( (Stage) backdrop.getScene().getWindow());
    }

    public RestClient getRestClient(){
        return restClient;
    }

    public SocketClient getSocketClient(){
        return socketClient;
    }

    public Application getApplication(){
        return application;
    }


    public void enableBackdrop(){
        backdrop.setVisible(true);
    }

    public void disableBackdrop(){
        backdrop.setVisible(false);
    }

    public Channel getCurrentChannel(){
        //temporary lines
        if(currentChannel == null){
            Collection<Channel> channels = restClient.listChannels();

            // temporary lines for testing
            assert channels.size() > 0;
            channels.forEach((ch) -> {currentChannel = ch;});
        }
        return currentChannel;
    }

    public void openChannelDialog(){
        enableBackdrop();
        createChannelDialogController.show();
    }

    public void closeChannelDialog(){
        disableBackdrop();
        createChannelDialogController.close();
    }

    public void openDMDialog(){
        enableBackdrop();
        createDMDialogController.show();
    }

    public void closeDMDialog(){
        disableBackdrop();
        createDMDialogController.close();
    }

    public void changeChannel(Channel channel){
        switchToMessaging();
        if(channel.equals(currentChannel)) {
            sidebarController.channels.refresh();
            sidebarController.direct.refresh();
            return;
        }
        logger.info("Changing channel to: " + channel.getName());
        Channel prevChannel = currentChannel;
        currentChannel = channel;
        sidebarController.channels.refresh();
        sidebarController.direct.refresh();
        messagingController.changeChannel(prevChannel);
    }


    public void goToMessage(Message message) {
        //TODO change channel (if necessary) and scroll to found message
    }

    public void setTextWrappingOnResize(){
        Stage stage = (Stage) backdrop.getScene().getWindow();
        stage.widthProperty().addListener(((observable, oldValue, newValue) -> messagingController.messages.refresh()));
    }


    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {

        sidebarController.addChannel.setVisible(true);
        sidebarController.addDM.setVisible(true);




        backdrop.setVisible(false);
        sidebarController.direct.refresh();
        sidebarController.channels.refresh();
        closeDMDialog();
        closeChannelDialog();


        try{
            socketClient.connect();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(this::setTextWrappingOnResize);

    }

}
