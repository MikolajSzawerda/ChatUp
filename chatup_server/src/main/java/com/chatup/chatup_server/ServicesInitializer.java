package com.chatup.chatup_server;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Message;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class ServicesInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ServicesInitializer.class);
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Populating elastic search...");
        SearchSession searchSession = Search.session( entityManagerFactory.createEntityManager() );
        MassIndexer indexer = searchSession.massIndexer( Message.class, AppUser.class )
                .threadsToLoadObjects( 7 );
        try {
            indexer.startAndWait();
        } catch (InterruptedException e) {
            logger.info("Finished!");
        }
        logger.info("READY TO GO!");
    }
}
