package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.web.AuthClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginViewController implements Initializable {
    private final Logger logger = LoggerFactory.getLogger(LoginViewController.class);
    private final MainApplication application;
    @FXML
    public Text invalidCredentialsText;
    @FXML
    public Button loginButton;
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;

    private final AuthClient authClient;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public LoginViewController(AuthClient authClient, Application application) { // Application bean is added manually, so IDE may show an error here
        this.authClient = authClient;
        this.application = (MainApplication) application;
        logger.info("LoginViewController created");
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent e) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        boolean success = authClient.authenticate(username, password);

        if(success) {
            application.switchToChatView(e);
            return;
        }

        invalidCredentialsText.setText("Invalid credentials");
        usernameField.clear();
        passwordField.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
