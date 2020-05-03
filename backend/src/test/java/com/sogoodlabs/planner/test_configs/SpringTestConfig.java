package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.configuration.main.SpringConfig;
import org.junit.runner.RunWith;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.naming.NamingException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class,
        classes = {SpringConfig.class, TestCreators.class, TestCreatorsAnotherSession.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
abstract public class SpringTestConfig {

    static {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("java:comp/env/use_database", "false");
        try {
            builder.activate();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

}
