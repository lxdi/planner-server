package com.sogoodlabs.planner.util;

public class IdUtils {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

    public static boolean isUUID(String id){
        return id!=null && id.matches(UUID_PATTERN);
    }

}
