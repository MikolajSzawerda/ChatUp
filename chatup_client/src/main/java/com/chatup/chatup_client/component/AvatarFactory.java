package com.chatup.chatup_client.component;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class AvatarFactory {
    public static StackPane createAvatar(String name, Double radius, Insets padding){

        StringBuilder initialsBuilder = new StringBuilder();
        String[] parts = name.split(" ");
        for (String part:parts){
            initialsBuilder.append(part.charAt(0));
        }
        String initials = initialsBuilder.toString();

        int hash =  (initials.codePointAt(0) + initials.codePointAt(1)) % 8;
        Circle circle = new Circle(radius);
        Color color = switch (hash) {
            case 0 -> Color.rgb(254, 213, 66);
            case 1 -> Color.rgb(255, 123, 105);
            case 2 -> Color.rgb(254, 144, 66);
            case 3 -> Color.rgb(173, 224, 100);
            case 4 -> Color.rgb(116, 208, 162);
            case 5 -> Color.rgb(108, 151, 173);
            case 6 -> Color.rgb(188, 142, 227);
            case 7 -> Color.rgb(245, 159, 204);
            default -> Color.rgb(254, 213, 66);
        };
        circle.setFill(color);
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
