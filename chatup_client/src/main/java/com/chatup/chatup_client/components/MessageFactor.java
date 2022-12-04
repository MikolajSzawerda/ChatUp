package com.chatup.chatup_client.components;

import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MessageFactor {
    public static GridPane createMessage(String text, String author, Double width){
        GridPane message  = new GridPane();
        Text authorText = new Text(author);
        Text message_content = new Text(text);
        String initials = new String();

        authorText.setFont(Font.font("Calibri", FontWeight.EXTRA_BOLD, 15));
        message_content.setFont(Font.font(12));
        message_content.setWrappingWidth(width - 120); //to change in future
        message_content.setTextOrigin(VPos.TOP);

        String[] parts = author.split(" ");
        for (String part:parts){
            initials += part.charAt(0);
        }
        Insets padding = new Insets(0, 30, 0, 0);
        StackPane avatar = AvatarFactory.createAvatar(initials, 25.0, padding);
        message.add(avatar, 0, 0, 1, 3);
        message.add(authorText, 1, 0, 1, 1);
        message.add(message_content, 1, 2, 1, 2);
        //to change in future

        if (message_content.getWrappingWidth() < message_content.getText().length() * 7) {
            message.setVgap(5);
        }
        return message;
    }
}
