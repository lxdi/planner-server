package com.sogoodlabs.planner.util;

import com.sogoodlabs.planner.model.entities.Target;
import org.junit.Test;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;

public class BeanUtilsTest {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException {

        var target = new Target();
        target.setDefinitionOfDone("test");

        var source = new Target();
        source.setTitle("title");

        BeanUtils.copyPropertiesNullIgnore(target, source);

        assertNotNull(target.getDefinitionOfDone());

    }
}
