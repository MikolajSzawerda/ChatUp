package com.chatup.chatup_client.component.skin;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;


//public class MyButtonSkin extends ButtonSkin {
//    public MyButtonSkin(Button control){
//        super(control);
//
//        final FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
//        fadeIn.setNode(control);
//        fadeIn.setToValue(1);
//        control.setOnMouseEntered(e->fadeIn.playFromStart());
//
//        final FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
//        fadeOut.setNode(control);
//        fadeOut.setToValue(0.5);
//        control.setOnMouseExited(e->fadeOut.playFromStart());
//
//        control.setOpacity(0.5);
//    }
//
//}


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
        fadeIn.setOnFinished(e->current.handle(e));

        control.setOpacity(1.0);
        };

}

