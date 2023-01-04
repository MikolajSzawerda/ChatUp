package com.chatup.chatup_server;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.service.JwtTokenService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql({"file:src/integrationTest/resources/cleanUp.sql", "file:src/integrationTest/resources/init.sql"})
public abstract class BaseInitializedDbTest extends BaseIntegrationTest{
    protected static final String USER_1 = "test.test.1";
    protected static final String USER_2 = "test.test.2";
    protected static final String USER_3 = "test.test.3";
    protected static final String USER_4 = "test.test.4";

    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private AppUserRepository appUserRepository;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void initElastic(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        SearchSession searchSession = Search.session( entityManager );
        MassIndexer indexer = searchSession.massIndexer( Message.class )
                .threadsToLoadObjects( 7 );
        try {
            indexer.startAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected String createUserToken(String username){
        AppUser appUser = appUserRepository.findAppUserByUsername(username);
        return jwtTokenService.generateToken(appUser);
    }
}
