package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.web.RestClient;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.ResourceBundle;

@Component
public class SidebarController implements Initializable {

    private final ChannelManager channelManager;
    private final RestClient restClient;
    public SimpleObjectProperty<Channel> currentChannel = new SimpleObjectProperty<>();

    private final ArrayList<Listener> listeners = new ArrayList<>();

    @FXML
    private ListView<Channel> channels;

    @FXML
    public Button addChannel;

    @FXML
    public Button addDM;

    @FXML
    public ListView<Channel> direct;

    @Autowired
    public SidebarController(ChannelManager channelManager, RestClient restClient) {
        this.channelManager = channelManager;
        this.restClient = restClient;
    }

    public interface Listener {
        void onAddChannel();
        void onAddDM();
        void onChangeChannel(Channel channel);
    }

    public void addListener(Listener listener){
        listeners.add(listener);
    }
    void removeListener(Listener listener) {listeners.remove(listener);}


    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
            channels.setItems(channelManager.getStandardChannels());
            direct.setItems(channelManager.getDirectMessages());
            restClient.listChannels().forEach(channelManager::addChannel);
            currentChannel.addListener(((observable, oldValue, newValue) -> {
                direct.refresh();
                channels.refresh();
            }));
            direct.refresh();
            channels.refresh();
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
                        channelButton.setOnAction(event -> {
                            for (var listener : listeners) listener.onChangeChannel(item);
                        });
                        channelButton.setSkin(new MyButtonSkin(channelButton));
                        if (item.equals(currentChannel.getValue())) {
                            channelButton.setBackground(new Background(new BackgroundFill(new Color(0.33, 0.42, 0.86, 1), CornerRadii.EMPTY, Insets.EMPTY)));
                        }
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
                        directMessageButton.getStyleClass().add("my-button");
                        directMessageButton.setOnAction(event -> {
                            for (var listener : listeners) listener.onChangeChannel(item);
                        });
                        directMessageButton.setSkin(new MyButtonSkin(directMessageButton));
                        if (item.equals(currentChannel.getValue())) {
                            directMessageButton.setBackground(new Background(new BackgroundFill(new Color(0.33, 0.42, 0.86, 1), CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                        setGraphic(directMessageButton);

                    }
                }
            });


            addDM.setOnAction(e -> {
                for (var listener : listeners) listener.onAddDM();
            });
            addDM.setSkin(new MyButtonSkin2(addDM));
            addChannel.setOnAction(e -> {
                for (var listener : listeners) listener.onAddChannel();
            });
            addChannel.setSkin(new MyButtonSkin2(addChannel));

    }
}
