package com.chatup.chatup_client.component.skin;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;



public class MyButtonSkin2 extends ButtonSkin {
    public MyButtonSkin2(Button control){
        super(control);
        final FadeTransition fadeIn = new FadeTransition(Duration.millis(150));
        EventHandler<ActionEvent> current = control.getOnAction();
        fadeIn.setNode(control);
        fadeIn.setToValue(0.5);
        fadeIn.setCycleCount(2);
        fadeIn.setAutoReverse(true);
        control.setOnAction(e->fadeIn.play());
        if(current != null) fadeIn.setOnFinished(e->current.handle(e));

        control.setOpacity(1.0);
        };

}

