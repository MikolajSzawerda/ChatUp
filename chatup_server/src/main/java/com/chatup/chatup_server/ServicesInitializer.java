package com.chatup.chatup_server;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.repository.ChannelRepository;
import com.chatup.chatup_server.service.messaging.BrokerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.Hibernate;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class ServicesInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ServicesInitializer.class);
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private final BrokerService brokerService;
    private final ChannelRepository channelRepository;

    public ServicesInitializer(BrokerService brokerService, ChannelRepository channelRepository) {
        this.brokerService = brokerService;
        this.channelRepository = channelRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Populating elastic search...");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        SearchSession searchSession = Search.session(entityManager);
        MassIndexer indexer = searchSession.massIndexer(Message.class, AppUser.class)
                .threadsToLoadObjects(7);
        try {
            indexer.startAndWait();
        } catch (InterruptedException e) {
            logger.info("Finished!");
        }
        logger.info("Populating rabbitmq graph...");
        List<Channel> channels = channelRepository
                .findAll().stream()
                .map(Channel::getId)
                .map(id->entityManager.find(Channel.class, id))
                .collect(Collectors.toList());
        channels.forEach(c-> Hibernate.initialize(c.getUsers()));
        brokerService.addChannels(channels);
        logger.info("READY TO GO!");
    }

}
