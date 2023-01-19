package com.chatup.chatup_client.component;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MessageFactory {
    public static GridPane createMessage(String text, String authorName, String authorUsername, Double width, EventHandler avatarClickHandler){
        GridPane message  = new GridPane();
        Text authorTextName = new Text(authorName);
        Text authorTextUser = new Text("\t" + "(" + authorUsername+")");
        Text message_content = new Text(text);

        authorTextName.setFont(Font.font("Calibri", FontWeight.EXTRA_BOLD, 15));
        authorTextUser.setFont(Font.font("Calibri", FontWeight.LIGHT, 11));
        message_content.setFont(Font.font(12));
        message_content.setWrappingWidth(width - 120); //to change in future
        message_content.setTextOrigin(VPos.TOP);


        Insets padding = new Insets(0, 30, 0, 0);
        StackPane avatar = AvatarFactory.createAvatar(authorName, 25.0, padding);
        message.add(avatar, 0, 0, 1, 3);
        message.add(authorTextName, 1, 0, 1, 1);
        message.add(authorTextUser, 2, 0, 1, 1);
        message.add(message_content, 1, 2, 2, 2);
        //to change in future
        if(avatarClickHandler != null) {
            avatar.setOnMouseClicked(avatarClickHandler);
        }
        if (message_content.getWrappingWidth() < message_content.getText().length() * 7) {
            message.setVgap(5);
        }
        return message;
    }
}
