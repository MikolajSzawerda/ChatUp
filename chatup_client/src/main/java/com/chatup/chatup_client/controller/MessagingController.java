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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;


@Component
public class MessagingController implements Initializable {

    @FXML
    public Button sendButton;
    @FXML
    public TextField message;

    @FXML
    public AnchorPane messaging;

    @FXML
    public ListView<Message> messages;
    final ListChangeListener<Message> listChangeListener = new ListChangeListener<>() {
        @Override
        public void onChanged(Change c) {
            messages.scrollTo(messages.getItems().size() - 1);
        }
    };




    private ChatViewController headController;

    private final MessageManager messageManager;

    @FXML
    public void onSendMessage(){
        if(headController.getCurrentChannel() == null) return;
        headController.logger.info("Text: {}", message.getText());
        if(!message.getText().equals("")){
            headController.getSocketClient().sendMessage("/app/channel."+headController.getCurrentChannel().getId(), message.getText());
            message.clear();
        }
    }

    @FXML
    public void setOnKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            onSendMessage();
        }
    }

    public void changeChannel(Channel prevChannel){

        if(prevChannel != null) {
            messageManager.getMessageBuffer(prevChannel).getMessages().removeListener(listChangeListener);
        }
        messages.setItems(messageManager.getMessageBuffer(headController.getCurrentChannel()).getMessages());
        messageManager.getMessageBuffer(headController.getCurrentChannel()).getMessages().addListener(listChangeListener);
        headController.getRestClient().getLastFeed(headController.getCurrentChannel()).forEach(messageManager::addMessage);

    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public MessagingController(RestClient restClient, Application application, MessageManager messageManager, ChatViewController chatViewController) {
        this.messageManager = messageManager;
        this.headController = chatViewController;
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
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
        messages.setItems(messageManager.getMessageBuffer(headController.getCurrentChannel()).getMessages());
        messageManager.getMessageBuffer(headController.getCurrentChannel()).getMessages().addListener(listChangeListener);
        headController.getRestClient().getLastFeed(headController.getCurrentChannel()).forEach(messageManager::addMessage);

        messages.setOnScroll(e->{
            ListViewSkin<?> ts = (ListViewSkin<?>) messages.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            if(vf.getFirstVisibleCell().getIndex() == 0) {
                // TODO Add fetching of more messages
            }
        });

    }
}
