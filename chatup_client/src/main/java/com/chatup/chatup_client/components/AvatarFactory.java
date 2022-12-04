package com.chatup.chatup_client.components;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class AvatarFactory {
    public static StackPane createAvatar(String initials, Double radius, Insets padding){
        Circle circle = new Circle(radius);
        circle.setFill(Color.rgb(254, 213, 66));
        circle.setStroke(Color.rgb(0, 0, 0));
        Text initialsText  = new Text(initials);
        initialsText.setBoundsType(TextBoundsType.VISUAL);
        initialsText.setFont(Font.font("Roboto Slab", FontPosture.REGULAR, radius-5));
        StackPane avatar = new StackPane();
        avatar.setPadding(padding);
        avatar.getChildren().addAll(circle, initialsText);
        return avatar;
    }
}
