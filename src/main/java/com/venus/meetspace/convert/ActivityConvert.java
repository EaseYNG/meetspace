package com.venus.meetspace.convert;

import com.venus.meetspace.model.cmd.ActivityCreateCmd;
import com.venus.meetspace.model.cmd.ActivityUpdateCmd;
import com.venus.meetspace.model.entity.Activity;
import com.venus.meetspace.model.vo.ActivityVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityConvert {

    Activity toEntity(ActivityCreateCmd cmd);

    ActivityVO toVO(Activity activity);

    List<ActivityVO> toVOList(List<Activity> activities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Activity activity, ActivityUpdateCmd cmd);
}
