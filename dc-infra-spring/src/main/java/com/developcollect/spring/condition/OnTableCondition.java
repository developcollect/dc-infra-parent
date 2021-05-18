package com.developcollect.spring.condition;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class OnTableCondition implements Condition {

    private static final String DATASOURCE_URL_PROPERTY = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME_PROPERTY = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD_PROPERTY = "spring.datasource.password";
    private static final String DATASOURCE_DRIVER_CLASS_NAME_PROPERTY = "spring.datasource.driver-class-name";




    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        MultiValueMap<String, Object> map = annotatedTypeMetadata.getAllAnnotationAttributes(ConditionalOnTable.class.getName());
        String tableName = map.getFirst("value").toString();
        Environment environment = context.getEnvironment();
        String url = environment.getProperty(DATASOURCE_URL_PROPERTY);
        String username = environment.getProperty(DATASOURCE_USERNAME_PROPERTY);
        String password = environment.getProperty(DATASOURCE_PASSWORD_PROPERTY);
        String driverClassName = environment.getProperty(DATASOURCE_DRIVER_CLASS_NAME_PROPERTY);
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        DataSource dataSource = dataSourceBuilder.build();
        return hasTable(dataSource, tableName);
    }

    private boolean hasTable(DataSource dataSource, String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT table_name FROM information_schema.TABLES WHERE table_name ='" + tableName + "'");
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }
}
