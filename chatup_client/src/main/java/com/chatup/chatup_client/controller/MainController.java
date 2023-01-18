package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.model.channels.Channel;
import com.chatup.chatup_client.model.messaging.Message;
import com.chatup.chatup_client.web.SocketClient;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutionException;

@Component
public class MainController{
    public SimpleObjectProperty<Channel> currentChannel = new SimpleObjectProperty<>();

    final Logger logger = LoggerFactory.getLogger(MainController.class);

    MainApplication application;

    SocketClient socketClient;

    DashboardViewController dashboardViewController;

    ChatViewController chatViewController;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MainController(SocketClient socketClient, ChatViewController chatViewController, DashboardViewController dashboardViewController, Application application){
        this.socketClient = socketClient;
        this.chatViewController = chatViewController;
        this.dashboardViewController = dashboardViewController;
        this.application = (MainApplication) application;

        chatViewController.addToListener(new ChatViewController.Listener() {
            @Override
            public void onInitialize() {
                chatViewController.currentChannel.bind(currentChannel);
                try{ socketClient.connect();}
                catch (ExecutionException | InterruptedException e)
                { throw new RuntimeException(e);}
            }


            @Override
            public void onChannelChange(Channel channel) {
                logger.info("Changing channel to: " + channel.getName());
                currentChannel.set(channel);
            }

            @Override
            public void onGoToDashboard() {
                try {
                    currentChannel.setValue(null);
                    chatViewController.unsubscribeAll();
                    ((MainApplication) application).switchToDashboardView();
                    dashboardViewController.subscribeAll();
                }
                catch (IOException e){
                    throw new UncheckedIOException(e);
                }
            }
        });

        dashboardViewController.addListener(new DashboardViewController.Listener() {
            @Override
            public void onLogOut(Stage stage) {
                try {
                    dashboardViewController.unsubscribeAll();
                    ((MainApplication) application).switchToLoginView();
                }
                catch (IOException e){
                    throw new UncheckedIOException(e);
                }

            }

            @Override
            public void onGoBack() {
                try {
                    dashboardViewController.unsubscribeAll();
                    ((MainApplication) application).switchToChatView();
                    chatViewController.subscribeAll();
                }
                catch (IOException e){
                    throw new UncheckedIOException(e);
                }

            }

            @Override
            public void onChangeChannel(Channel channel) {
                currentChannel.set(channel);
                this.onGoBack();
            }

            @Override
            public void onMessageSearched(Message message) {
                try {
                    dashboardViewController.unsubscribeAll();
                    ((MainApplication) application).switchToChatView();
                    chatViewController.scrollToMessage(message);
                    chatViewController.subscribeAll();
                }
                catch (IOException e){
                    throw new UncheckedIOException(e);
                }
            }
        });
    }
}

