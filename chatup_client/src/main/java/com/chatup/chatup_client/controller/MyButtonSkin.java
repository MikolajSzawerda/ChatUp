package com.chatup.chatup_client.controller;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
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


public class MyButtonSkin extends ButtonSkin {
    public MyButtonSkin(Button control){
        super(control);

        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(400));
                setInterpolator(Interpolator.EASE_OUT);
            }
            @Override
            protected void interpolate(double frac) {
                Color vColor = new Color(0.33, 0.42, 0.86, 1 - frac);
                control.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }


        };
        control.setBackground(new Background(new BackgroundFill(new Color(0, 0 ,0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        control.setOnAction(e->animation.play());
    };

}

