package com.venus.meetspace.convert;

import com.venus.meetspace.model.cmd.ProfileUpdateCmd;
import com.venus.meetspace.model.entity.User;
import com.venus.meetspace.model.vo.UserProfileVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserConvert {

    UserProfileVO toProfileVO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfile(@MappingTarget User user, ProfileUpdateCmd cmd);
}
