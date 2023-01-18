package com.chatup.chatup_server.service.messaging;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BrokerService {
    private final AmqpAdmin amqpAdmin;

    public BrokerService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public void addChannel(Channel channel){
        String root = channel.getId().toString();
        List<String> children = channel
                .getUsers()
                .stream()
                .map(AppUser::getUsername).collect(Collectors.toList());
        addExchanges(new FanoutForest(new HashMap<>(){{put(root, children);}}));
    }

    public void addChannels(List<Channel> channels){
        FanoutForest forest = new FanoutForest();
        for(var channel:channels){
            String root = channel.getId().toString();
            List<String> children = channel
                    .getUsers()
                    .stream()
                    .map(AppUser::getUsername).collect(Collectors.toList());
            forest.addTree(root, children);
        }
        addExchanges(forest);
    }

    public void addExchanges(FanoutForest fanoutForest){
        fanoutForest
                .getNodes().stream()
                .map(this::createExchange)
                .forEach(amqpAdmin::declareExchange);
        addExchangesConnections(fanoutForest);
    }

    public void addExchangesConnections(FanoutForest fanoutForest){
        fanoutForest
                .getFlatForest().stream()
                .map(this::createBinding)
                .forEach(amqpAdmin::declareBinding);

    }

    private FanoutExchange createExchange(String name){
        return ExchangeBuilder
                .fanoutExchange(name)
                .durable(true)
                .build();
    }
    private Binding createBinding(Map.Entry<String, String> edge){
        return createBinding(edge.getKey(), edge.getValue());
    }

    private Binding createBinding(String root, String child){
        return BindingBuilder
                .bind(createExchange(child))
                .to(createExchange(root));
    }

    public void removeChannel(Channel channel) {
        amqpAdmin.deleteExchange(channel.getId().toString());
    }
}
