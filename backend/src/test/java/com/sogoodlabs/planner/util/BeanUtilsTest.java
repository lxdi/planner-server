package com.sogoodlabs.planner.util;

import com.sogoodlabs.planner.model.entities.Mean;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;

public class BeanUtilsTest {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException {

        var mean = new Mean();
        mean.setCriteria("test");

        var source = new Mean();
        source.setTitle("title");

        BeanUtils.copyPropertiesNullIgnore(mean, source);

        assertNotNull(mean.getCriteria());

    }
}
