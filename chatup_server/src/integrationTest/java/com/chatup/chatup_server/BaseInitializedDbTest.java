package com.chatup.chatup_server;

import org.springframework.test.context.jdbc.Sql;

@Sql({"file:src/integrationTest/resources/cleanUp.sql", "file:src/integrationTest/resources/init.sql"})
public abstract class BaseInitializedDbTest extends BaseIntegrationTest{
    protected static final String USER_1 = "test.test.1";
    protected static final String USER_2 = "test.test.2";
    protected static final String USER_3 = "test.test.3";
    protected static final String USER_4 = "test.test.4";
}
