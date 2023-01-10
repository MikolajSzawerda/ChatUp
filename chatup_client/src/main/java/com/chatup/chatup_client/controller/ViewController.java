package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UncheckedIOException;

abstract public class ViewController implements Initializable {

    protected final MainApplication application;

    protected final SocketClient socketClient;
    protected final RestClient restClient;


    public void openChannelDialog(){
    }
    public void closeChannelDialog(){
    }

    public void openDMDialog(){
    }
    public void closeDMDialog(){
    }

    abstract public Channel getCurrentChannel();

    public void goToMessage(Message message){
    }

    public void changeChannel(Channel channel){
    }

    public ViewController(SocketClient socketClient, RestClient restClient, Application application) {
        this.socketClient = socketClient;
        this.restClient = restClient;
        this.application = (MainApplication) application;
    }


}
