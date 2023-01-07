package com.chatup.chatup_server;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.service.JwtTokenService;
import com.chatup.chatup_server.service.channels.ChannelCreateRequest;
import com.chatup.chatup_server.service.channels.ChannelInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Sql({"file:src/integrationTest/resources/cleanUp.sql", "file:src/integrationTest/resources/init.sql"})
public abstract class BaseInitializedDbTest extends BaseIntegrationTest{
    protected static final String USER_1 = "test.test.1";
    protected static final String USER_2 = "test.test.2";
    protected static final String USER_3 = "test.test.3";
    protected static final String USER_4 = "test.test.4";
    private final String CREATE_CHANNEL_ENDPOINT = "/channel/create";


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

    protected Long addNewChannel(String token, String... usernames){
        Set<Long> userIds = Arrays.stream(usernames)
                .map(appUserRepository::findAppUserByUsername)
                .map(AppUser::getId)
                .collect(Collectors.toSet());

        ChannelCreateRequest request = new ChannelCreateRequest(
                null, false, false, userIds
        );

        return getCreateChannelRequest(token, request).getBody().id();
    }

    protected ResponseEntity<ChannelInfo> getCreateChannelRequest(String token, ChannelCreateRequest body){
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(CREATE_CHANNEL_ENDPOINT)
                .build().toUri();

        HttpEntity<ChannelCreateRequest> httpBody = new HttpEntity<>(body, createAuthHeaders(token));
        return restTemplate.exchange(uri, HttpMethod.POST, httpBody, ChannelInfo.class);
    }
}
