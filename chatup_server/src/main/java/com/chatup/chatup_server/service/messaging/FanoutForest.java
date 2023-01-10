package com.chatup.chatup_server.service.messaging;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FanoutForest {

    Map<String, List<String>> graph;

    public FanoutForest() {
        graph = new HashMap<>();
    }

    public FanoutForest(Map<String, List<String>> graph) {
        this.graph = graph;
    }

    public void addTree(String rootExchange, List<String> childExchange) {
        graph.put(rootExchange, childExchange);
    }

    public Set<AbstractMap.Entry<String, String>> getFlatForest() {
        return graph
                .entrySet().stream()
                .flatMap(e -> e.getValue()
                        .stream()
                        .map(c -> new AbstractMap.SimpleImmutableEntry<String, String>(e.getKey(), c)))
                .collect(Collectors.toSet());
    }

    public Set<String> getNodes() {
        return Stream.concat(graph
                                .values()
                                .stream()
                                .flatMap(List::stream),
                        graph.keySet().stream())
                .collect(Collectors.toSet());
    }


}
