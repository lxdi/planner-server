package model.dto;


import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BasicDtoValidator {

    public boolean checkIfIdAbsent(Map<String, Object> dto){
        if(dto.get("id")!=null && Long.parseLong(""+dto.get("id"))>0){
            return false;
        }
        return true;
    }

    public boolean checkForRealm(Map<String, Object> dto){
        if(dto.get("realmid")==null || Long.parseLong(""+dto.get("realmid"))<1){
            return false;
        }
        return true;
    }

}
