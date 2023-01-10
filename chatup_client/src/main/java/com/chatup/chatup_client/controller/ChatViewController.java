package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;


@Component
public class ChatViewController extends ViewController {
    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);
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

    private Channel currentChannel;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public ChatViewController(MessageManager messageManager, SocketClient socketClient, RestClient restClient, Application application) {
        super(socketClient, restClient, application);
        this.messageManager = messageManager;
        logger.info("ChatViewController created");
    }

    @FXML
    public void onSendMessage(){
        if(currentChannel == null) return;
        logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            socketClient.sendMessage("/app/channel."+currentChannel.getId(), message.getText());
            message.clear();
        }
    }


    public void enableBackdrop(){
        backdrop.setVisible(true);
    }

    public void disableBackdrop(){
        backdrop.setVisible(false);
    }

    @Override
    public void openChannelDialog(){
        enableBackdrop();
        createChannelDialogController.show();
    }

    @Override
    public void closeChannelDialog(){
        disableBackdrop();
        createChannelDialogController.close();
    }

    @Override
    public void openDMDialog(){
        enableBackdrop();
        createDMDialogController.show();
    }

    @Override
    public void closeDMDialog(){
        disableBackdrop();
        createDMDialogController.close();
    }

    @Override
    public void changeChannel(Channel channel){
        if(channel.equals(currentChannel)) {
            sidebarController.channels.refresh();
            sidebarController.direct.refresh();
            return;
        }
        logger.info("Changing channel to: " + channel.getName());
        if(currentChannel != null) {
            messageManager.getMessageBuffer(currentChannel).getMessages().removeListener(listChangeListener);
        }
        currentChannel = channel;
        sidebarController.channels.refresh();
        sidebarController.direct.refresh();
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
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        headbarController.setHeadController(this);
        sidebarController.setHeadController(this);
        createDMDialogController.setHeadController(this);
        createChannelDialogController.setHeadController(this);
        Collection<Channel> channels = restClient.listChannels();

        // temporary lines for testing
        assert channels.size() > 0;
        channels.forEach((ch) -> {currentChannel = ch;});

        setCellFactories();
        messages.setItems(messageManager.getMessageBuffer(currentChannel).getMessages());
        messageManager.getMessageBuffer(currentChannel).getMessages().addListener(listChangeListener);
        restClient.getLastFeed(currentChannel).forEach(messageManager::addMessage);

        backdrop.setVisible(false);
        closeDMDialog();
        closeChannelDialog();

        try{
            socketClient.connect();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
