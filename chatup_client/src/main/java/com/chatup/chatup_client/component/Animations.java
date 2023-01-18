package com.chatup.chatup_client.component;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.scene.Node;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class Animations {
    public static PathTransition generateAPIKeyAnimation(Node node){
        Path path = new Path();
        MoveTo start = new MoveTo(60.0, 12.0);
        path.getElements().add(start);
        path.getElements().add(new HLineTo(600));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(200));
        pathTransition.setPath(path);
        pathTransition.setNode(node);
        pathTransition.setCycleCount((int) 1f);
        pathTransition.setAutoReverse(false);
        return pathTransition;
    }

    public static FadeTransition createFadeInTransition(Node node){
        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        return ft;
    }

    public static FadeTransition createFadeOutTransition(Node node){
        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        return ft;
    }
}
