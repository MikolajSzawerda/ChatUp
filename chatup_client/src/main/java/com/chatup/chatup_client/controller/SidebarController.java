package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;
import java.util.Stack;

@Component
public class SidebarController implements Initializable {

    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);
    private final ChannelManager channelManager;
    private final RestClient restClient;
    private final MainApplication application;

    private  ChatViewController headController;

    public ListView<Channel> channels;

    @FXML
    public Button addChannel;

    @FXML
    public Button addDM;

    @FXML
    public ListView<Channel> direct;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public SidebarController(ChannelManager channelManager, RestClient restClient, Application application) {
        this.channelManager = channelManager;
        this.restClient = restClient;
        this.application = (MainApplication) application;
        logger.info("ChatViewController created");
    }


    public void setHeadController(ChatViewController headController){
        this.headController=headController;
    }
    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
        channels.setItems(channelManager.getStandardChannels());
        direct.setItems(channelManager.getDirectMessages());
        restClient.listChannels().forEach(channelManager::addChannel);
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
                    channelButton.addEventHandler(ActionEvent.ACTION, event -> {
                        headController.changeChannel(item);
                    });

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
                    directMessageButton.addEventHandler(ActionEvent.ACTION, event -> {
                        headController.changeChannel(item);
                    });

                    setGraphic(directMessageButton);

                }
            }
        });


        addDM.setOnAction(e->{
            headController.openDMDialog();
        });
        addDM.setSkin(new MyButtonSkin2(addDM));
        addChannel.setOnAction(e->{
            headController.openChannelDialog();
        });
        addChannel.setSkin(new MyButtonSkin2(addChannel));

    }
}
