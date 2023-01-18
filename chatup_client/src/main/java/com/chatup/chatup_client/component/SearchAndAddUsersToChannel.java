package com.chatup.chatup_client.component;

import com.chatup.chatup_client.model.users.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.Collection;

public class SearchAndAddUsersToChannel {
    public static void searchUsers(TextField searchField, ListView<UserInfo> searchResult, RestClient restClient){
        UserInfo currentUser = restClient.getCurrentUser();
        searchResult.setVisible(true);
        Collection<UserInfo> searchResultsCollection = restClient.searchUsers(searchField.getText());
        searchResultsCollection.remove(currentUser);
        ObservableList<UserInfo> searchResultsList = FXCollections.observableArrayList(searchResultsCollection);
        searchResult.setItems(searchResultsList);
        searchResult.prefHeightProperty().bind(Bindings.size((searchResultsList)).multiply(33));
        CellFactories.userCellFactory(searchResult);
    }
}
