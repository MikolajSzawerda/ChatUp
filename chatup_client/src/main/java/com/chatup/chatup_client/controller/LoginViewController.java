package com.chatup.chatup_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {
    private static LoginViewController INSTANCE;
    @FXML
    public Text invalidCredentialsText;

    @FXML
    public Button loginButton;

    public LoginViewController() {
        INSTANCE = this;
    }

    public LoginViewController getInstance() {
        return INSTANCE;
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent e) throws IOException {
        //verification of credentials
        switchToChatView(e);
        //invalidCredentialsText.setText("Invalid Credentials");

    }
    void switchToChatView(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/chatup-view.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Roboto+Slab");
        stage.setScene(scene);
        stage.show();

    }
}
