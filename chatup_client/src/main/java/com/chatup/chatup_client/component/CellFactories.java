package com.chatup.chatup_client.component;

import com.chatup.chatup_client.model.messaging.Message;
import com.chatup.chatup_client.model.users.UserInfo;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public class CellFactories {

    public static void messageCellFactory(ListView<Message> listView, Consumer<Long> avatarClickCallback){
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (item != null) {
                setMaxWidth(param.getWidth());
                setMinWidth(param.getWidth() - 100);
                setPrefWidth(param.getWidth() - 100);

                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                GridPane message = MessageFactory.createMessage(item.getContent(),
                        item.getAuthorFirstName() + " " + item.getAuthorLastName(), item.getAuthorUsername(), param.getWidth(), e -> {
                            avatarClickCallback.accept(item.getAuthorID());
                        });
                setGraphic(message);
            }
            }
        });
    }

    public static void userCellFactory(ListView<UserInfo> listView){
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UserInfo item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (item != null) {

                Insets padding = new Insets(0, 5, 0, 0);
                StackPane avatar = AvatarFactory.createAvatar(item.getFirstName()+" "+item.getLastName(), 13.0, padding);
                setText(item.getFirstName()+" "+item.getLastName());
                setGraphic(avatar);
            }
            }
        });
    }

}
