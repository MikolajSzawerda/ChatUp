package com.chatup.chatup_client.component;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import org.kordamp.ikonli.javafx.FontIcon;

public class ChannelIconFactory {

    public static Node createChannelIcon(Boolean isPrivate, Integer newMessages){
        Text newMessagesText = new Text(newMessages.toString());
        newMessagesText.setBoundsType(TextBoundsType.VISUAL);
        newMessagesText.setFont(Font.font("Roboto Slab", FontPosture.REGULAR, 20));
        if(isPrivate){
            FontIcon icon = new FontIcon();
            icon.setFont(Font.font("Calibri", FontPosture.REGULAR, 40));
            icon.setIconLiteral("fa-lock");
            icon.setIconColor(Paint.valueOf("#6aba9c"));
            icon.setStroke(Paint.valueOf("#000000"));
            return icon;
        }
        StackPane channelIcon = new StackPane();
        Rectangle rect = new Rectangle();
        rect.setHeight(30);
        rect.setWidth(30);
        rect.setStyle("-fx-padding: 0 5 0 0");
        rect.setStroke(Paint.valueOf("#000000"));
        rect.setFill(Paint.valueOf("#6aba9c"));

        Insets padding = new Insets(0, 5, 0, 0);
        channelIcon.setPadding(padding);

        channelIcon.getChildren().addAll(rect, newMessagesText);
        return channelIcon;
    }

}
