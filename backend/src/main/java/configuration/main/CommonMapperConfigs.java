package configuration.main;

import com.sogoodlabs.common_mapper.CommonMapper;
import com.sogoodlabs.common_mapper.IEntityById;
import com.sogoodlabs.common_mapper.util.GetterSetterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.DateUtils;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.Map;

@Configuration
public class CommonMapperConfigs {

    @Autowired
    IEntityById entityById;

    @Bean
    public CommonMapper commonMapper(){
        return new CommonMapper(entityById){
            @Override
            public boolean customMapping(Object entity, Map<String, Object> result, Method method, Object fromGetter){
                if(fromGetter instanceof Date){ //Dates mapping
                    result.put(GetterSetterUtils.transformGetterToFieldName(method.getName()), DateUtils.fromDate((Date) fromGetter));
                    return true;
                }
                return false;
            }
        };
    }
}
