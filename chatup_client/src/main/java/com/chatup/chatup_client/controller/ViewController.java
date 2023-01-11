package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.application.Application;
import javafx.fxml.Initializable;

abstract public class ViewController implements Initializable {

    protected final MainApplication application;

    protected final SocketClient socketClient;
    protected final RestClient restClient;

    protected Channel currentChannel;

    public void openChannelDialog(){
    }
    public void closeChannelDialog(){
    }

    public void openDMDialog(){
    }
    public void closeDMDialog(){
    }

    public void createDM(Long userID){}

    public Channel getCurrentChannel(){return this.currentChannel;}

    public void changeChannel(Channel channel){
    }

    public void scrollToMessage(Message message) {}

    public ViewController(SocketClient socketClient, RestClient restClient, Application application) {
        this.socketClient = socketClient;
        this.restClient = restClient;
        this.application = (MainApplication) application;
    }


}
