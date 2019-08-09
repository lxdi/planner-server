package services;

public class JsonParsingFixUtils {

    public static Long returnLong(Object input){
        if(input instanceof Long){
            return (Long) input;
        }
        if(input instanceof Integer){
            return Long.valueOf((Integer)input);
        }
        throw new NumberFormatException();
    }

}
