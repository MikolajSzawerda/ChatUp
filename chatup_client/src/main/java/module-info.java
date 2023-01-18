module com.chatup.chatup_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires org.apache.tomcat.embed.websocket;
    requires spring.websocket;
    requires spring.messaging;
    requires org.slf4j;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires spring.webflux;
    requires spring.web;
    requires reactor.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.module.paramnames;
    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires spring.context;
    requires spring.beans;
    requires static com.sandec.mdfx;
    opens com.chatup.chatup_client to javafx.fxml, spring.core, spring.beans;
    exports com.chatup.chatup_client;
    exports com.chatup.chatup_client.controller;
    exports com.chatup.chatup_client.model;
    opens com.chatup.chatup_client.controller to javafx.fxml, spring.core;
    exports com.chatup.chatup_client.manager;
    exports com.chatup.chatup_client.component.skin;
    opens com.chatup.chatup_client.component.skin to javafx.fxml;
    opens com.chatup.chatup_client.config to spring.core;
    exports com.chatup.chatup_client.config;
    exports com.chatup.chatup_client.web;
    exports com.chatup.chatup_client.model.messaging;
    exports com.chatup.chatup_client.model.channels;
    exports com.chatup.chatup_client.model.users;
}
