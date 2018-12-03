package model.dto.hquarter;

import model.dto.IMapper;
import model.entities.HQuarter;

public class HquarterMapper implements IMapper<HquarterDtoLazy, HQuarter> {

    @Override
    public HquarterDtoLazy mapToDto(HQuarter entity) {
        HquarterDtoLazy dto = new HquarterDtoLazy();
        dto.setId(entity.getId());
        dto.setYear(entity.getYear());
        dto.setStartmonth(entity.getStartMonth());
        dto.setStartmonth(entity.getStartMonth());
        return dto;
    }

    @Override
    public HQuarter mapToEntity(HquarterDtoLazy dto) {
        return null;
    }
}
