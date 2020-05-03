package com.sogoodlabs.planner.test_configs;

import com.sogoodlabs.planner.model.entities.DaysOfWeek;
import com.sogoodlabs.planner.services.StringUtils;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class WeeksMappingChecker {

    public static void checkWeek(Map<DaysOfWeek, List<String>> mappings, List<Map<String, Object>> weeks, int weekNum){
        for(DaysOfWeek daysOfWeek : DaysOfWeek.values()){
            if(mappings.get(daysOfWeek)!=null){
                assertTrue((int) StringUtils.getValue(weeks,
                        "get("+weekNum+").get('days').get('"+daysOfWeek.name()+"').size()") == mappings.get(daysOfWeek).size());
                for(int i=0;i<mappings.get(daysOfWeek).size(); i++){
                    assertTrue(StringUtils.getValue(weeks,
                            "get("+weekNum+").get('days').get('"+daysOfWeek.name()+"').get("+i+").get('title')")
                            .equals(mappings.get(daysOfWeek).get(i)));
                }
            } else {
                assertTrue(StringUtils.getValue(weeks, "get("+weekNum+").get('days').get('"+daysOfWeek.name()+"')") == null);
            }
        }
    }

}
