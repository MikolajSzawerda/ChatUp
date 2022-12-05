package com.chatup.chatup_client.component;

import com.chatup.chatup_client.component.skin.MyButtonSkin;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

public class ChangeChatButtonFactory {
    public static Button createChangeChatButton(Node graphic, String channelName, Double width){
        Text channelNameText = new Text(channelName);
        HBox layout = new HBox();
        Button changeChatButton = new Button();

        channelNameText.setFont(Font.font("Calibri", FontPosture.REGULAR, 15));

        layout.getChildren().addAll(graphic, channelNameText);
        layout.setAlignment(Pos.CENTER_LEFT);
        changeChatButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        changeChatButton.setGraphic(layout);
        changeChatButton.setPrefWidth(width);
        changeChatButton.setSkin(new MyButtonSkin(changeChatButton));
        return(changeChatButton);
    }
}