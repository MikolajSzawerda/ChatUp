package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.CellFactories;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.manager.exception.OutOfMessagesException;
import com.chatup.chatup_client.model.channels.Channel;
import com.chatup.chatup_client.model.messaging.Message;
import com.chatup.chatup_client.model.users.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
public class ChatViewController implements Initializable {
    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    public SimpleObjectProperty<Channel> currentChannel = new SimpleObjectProperty<>();

    private final SocketClient socketClient;
    private final RestClient restClient;
    private final MessageManager messageManager;

    private final ChannelManager channelManager;

    private final ArrayList<Listener> listeners = new ArrayList<>();
    
    @FXML
    private ListView<Message> messages;
    final ListChangeListener<Message> listChangeListener = new ListChangeListener<>() {
        @Override
        public void onChanged(Change c) {
            messages.scrollTo(messages.getItems().size() - 1);
        }
    };

    @FXML
    private Button sendButton;
    @FXML
    private TextField message;
    @FXML
    private Rectangle backdrop;
    @FXML
    private HeadbarController headbarController;
    private HeadbarController.Listener headbarListener;
    @FXML
    private SidebarController sidebarController;
    private SidebarController.Listener sidebarListener;
    @FXML
    private CreateDMDialogController createDMDialogController;
    private CreateDMDialogController.Listener createDMDialogListener;
    @FXML
    private CreateChannelDialogController createChannelDialogController;
    private CreateChannelDialogController.Listener createChannelDialogListener;

    private BooleanProperty isCreateDMDialogOpen = new SimpleBooleanProperty(false);
    private BooleanProperty isCreateChannelDialogOpen = new SimpleBooleanProperty(false);

    private final AtomicBoolean loadingMessagesAfterScroll = new AtomicBoolean(false);

    @Autowired
    public ChatViewController(MessageManager messageManager, SocketClient socketClient, RestClient restClient, ChannelManager channelManager) {
        this.socketClient = socketClient;
        this.restClient = restClient;
        this.messageManager = messageManager;
        this.channelManager = channelManager;
        logger.info("ChatViewController created");
        this.sidebarListener = new SidebarController.Listener() {
            @Override
            public void onAddChannel() {
                backdrop.setVisible(true);
                isCreateChannelDialogOpen.set(true);
            }

            @Override
            public void onAddDM() {
                backdrop.setVisible(true);
                isCreateDMDialogOpen.set(true);
            }

            @Override
            public void onChangeChannel(Channel channel) {
                changeChannel(channel);
            }
        };
        this.createChannelDialogListener = new CreateChannelDialogController.Listener() {
            @Override
            public void onCloseDialog() {
                backdrop.setVisible(false);
                isCreateChannelDialogOpen.set(false);
            }

            @Override
            public void onChannelCreate(Channel channel) {
                changeChannel(channel);
                this.onCloseDialog();
            }
        };

        this.createDMDialogListener = new CreateDMDialogController.Listener() {
            @Override
            public void onDialogClose() {
                backdrop.setVisible(false);
                isCreateDMDialogOpen.set(false);
            }

            @Override
            public void onDMCreate(UserInfo user) {
                createDM(user.getId());
                this.onDialogClose();
            }
        };

        this.headbarListener = new HeadbarController.Listener() {
            @Override
            public void onAvatarClicked() {
                for(Listener listener:listeners) listener.onGoToDashboard();
            }

            @Override
            public void onMessageSearched(Message selectedMessage) {
                scrollToMessage(selectedMessage);
            }
        };

    }

    public interface Listener{
        void onInitialize();
        void onChannelChange(Channel channel);
        void onGoToDashboard();
    }

    public void addToListener(Listener listener){
        listeners.add(listener);
    }
    void removeListener(HeadbarController.Listener listener) {listeners.remove(listener);}

    @FXML
    public void onSendMessage(){
        if(currentChannel == null) return;
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            socketClient.sendMessage("/app/" + currentChannel.getValue().getId(), message.getText());
            message.clear();
        }
    }

    @FXML
    public void setOnKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            onSendMessage();
        }
    }


    public void subscribeAll(){
        headbarController.setListener(headbarListener);
        sidebarController.addListener(sidebarListener);
        createChannelDialogController.addListener(createChannelDialogListener);
        createDMDialogController.addListener(createDMDialogListener);
    }

    public void unsubscribeAll(){
        sidebarController.removeListener(sidebarListener);
        createChannelDialogController.removeListener(createChannelDialogListener);
        createDMDialogController.removeListener(createDMDialogListener);
    }

    public void changeChannel(Channel channel){
        Text placeholder = new Text("This channel is empty. Write your first message!");
        placeholder.setFont(Font.font("Roboto Slab", FontPosture.REGULAR, 20));
        messages.setPlaceholder(placeholder);
        message.setDisable(false);
        for(Listener listener:listeners) listener.onChannelChange(channel);
    }

    public void createDM(Long userId){
        UserInfo currentUser = restClient.getCurrentUser();
        if(userId.equals(currentUser.getId())) return;
        HashSet<Long> userIds = new HashSet<>();
        userIds.add(userId);
        userIds.add(currentUser.getId());
        Channel newChannel = restClient.createChannel("", true,  true, userIds);
        this.changeChannel(newChannel);

    }

    public void scrollToMessage(Message message) {
        Channel channel = channelManager.getChannelForMessage(message);
        if(channel == null) {
            throw new RuntimeException();
        }
        changeChannel(channel);
        try {
            while (!messageManager.getMessageBuffer(channel.getId()).getMessages().contains(message)) {
                messageManager.getMessageBuffer(channel.getId()).loadNextMessages();
            }
        }
        catch (OutOfMessagesException e) {
            throw new RuntimeException();
        }
        Platform.runLater(() -> messages.scrollTo(message));
    }

    private void setMessagesScrollHandler() {
        messages.setOnScroll(e -> {
            if(currentChannel != null &&
                    e.getDeltaY() > 0 &&
                    loadingMessagesAfterScroll.compareAndSet(false, true)
            ) {

                int prevSize = messages.getItems().size();
                try {
                    messageManager.getMessageBuffer(currentChannel.getValue().getId()).loadNextMessages();
                } catch (OutOfMessagesException ex) {}
                int currentSize = messages.getItems().size();
                if(currentSize > 0)
                    messages.scrollTo(Math.max(currentSize - prevSize - 1, 0));

                loadingMessagesAfterScroll.set(false);
            }
        });
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
            for (Listener listener : listeners) listener.onInitialize();
            sidebarController.currentChannel.bind(currentChannel);
            CellFactories.messageCellFactory(messages);

            sidebarController.addDM.setVisible(true);
            sidebarController.addChannel.setVisible(true);

            setMessagesScrollHandler();
            if (currentChannel.get() != null) {
                messages.setItems(messageManager.getMessageBuffer(currentChannel.getValue().getId()).getMessages());
                try {
                    messageManager.getMessageBuffer(currentChannel.getValue().getId()).loadNextMessages();
                } catch (OutOfMessagesException ignored) {}

                int messagesSize = messageManager.getMessageBuffer(currentChannel.get().getId()).getMessages().size();
                messages.scrollTo(messagesSize - 1);
            }

            Text placeholder = new Text("Hello! Choose your channel");
            placeholder.setFont(Font.font("Roboto Slab", FontPosture.REGULAR, 20));
            messages.setPlaceholder(placeholder);
            message.setDisable(true);

            createDMDialogController.addDMDialog.visibleProperty().bind(isCreateDMDialogOpen);
            createChannelDialogController.addChannelDialog.visibleProperty().bind(isCreateChannelDialogOpen);


            sidebarController.addListener(sidebarListener);
            createChannelDialogController.addListener(createChannelDialogListener);
            createDMDialogController.addListener(createDMDialogListener);
            headbarController.setListener(headbarListener);

            currentChannel.addListener(((observable, oldValue, newValue) -> {
                if (oldValue != null)
                    messageManager.getMessageBuffer(oldValue.getId()).getMessages().removeListener(listChangeListener);
                if (newValue != null) {
                    messageManager.getMessageBuffer(newValue.getId()).getMessages().addListener(listChangeListener);
                    messages.setItems(messageManager.getMessageBuffer(currentChannel.getValue().getId()).getMessages());
                    try {
                        messageManager.getMessageBuffer(currentChannel.getValue().getId()).loadNextMessages();
                    } catch (OutOfMessagesException ignored) {}
                    int messagesSize = messageManager.getMessageBuffer(newValue.getId()).getMessages().size();
                    messages.scrollTo(messagesSize - 1);
                }
            }));
    }
}
