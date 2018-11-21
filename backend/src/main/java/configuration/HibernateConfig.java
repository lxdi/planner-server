package configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Alexander on 10.03.2018.
 */

@Configuration
public class HibernateConfig {

    @Bean
    @Qualifier("hibernate_properties")
    public Properties hibernateProperties() throws NamingException {
        Properties properties = new Properties();
        Properties extProps = extProps();
        if(extProps.getProperty("use_database").equals("true")){
            properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.put("hibernate.show_sql", "false");
            properties.put("hibernate.hbm2ddl.auto", "update");
            return properties;
        }
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        return properties;
    }

    @Bean
    public DataSource getDataSource() throws NamingException {
        Properties extProps = extProps();
        if(extProps.getProperty("use_database").equals("true")) {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setUrl(extProps.getProperty("url"));
            ds.setUsername(extProps.getProperty("username"));
            ds.setPassword(extProps.getProperty("password"));
            return ds;
        }
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("test_embedded_db")
                .build();
    }

    @Bean
    @Qualifier("ext_props")
    public Properties extProps(){
        Properties properties = new Properties();
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            properties.put("use_database", envCtx.lookup("use_database"));
            properties.put("url", envCtx.lookup("url"));
            properties.put("username", envCtx.lookup("username"));
            properties.put("password", envCtx.lookup("password"));

            properties.put("generate_weeks", envCtx.lookup("generate_weeks"));
            properties.put("gen_weeks_from", envCtx.lookup("gen_weeks_from"));
            properties.put("gen_weeks_to", envCtx.lookup("gen_weeks_to"));

            properties.put("generate_quarters", envCtx.lookup("generate_quarters"));
            properties.put("gen_quarters_from", envCtx.lookup("gen_quarters_from"));
            properties.put("gen_quarters_to", envCtx.lookup("gen_quarters_to"));

        } catch (NamingException e) {
            e.printStackTrace();
            properties.put("use_database", "false");
            properties.put("generate_weeks", "false");
        }
        return properties;
    }

    /**
    Example for Tomcat - add in context.xml within <Context>
    <Environment name="use_database" type="java.lang.String" value="true" />
    <Environment name="url" type="java.lang.String" value="jdbc:postgresql://localhost:5432/planner" />
    <Environment name="username" type="java.lang.String" value="postgres" />
    <Environment name="password" type="java.lang.String" value="" />
    <Environment name="generate_weeks" type="java.lang.String" value="true" />
    <Environment name="gen_weeks_from" type="java.lang.String" value="2017" />
    <Environment name="gen_weeks_to" type="java.lang.String" value="2019" />
     <Environment name="generate_quarters" type="java.lang.String" value="true" />
     <Environment name="gen_quarters_from" type="java.lang.String" value="2017" />
     <Environment name="gen_quarters_to" type="java.lang.String" value="2019" />
     */

}
