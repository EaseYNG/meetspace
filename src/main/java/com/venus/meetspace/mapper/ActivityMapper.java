package com.venus.meetspace.mapper;

import com.venus.meetspace.dto.request.ActivityCreateRequest;
import com.venus.meetspace.dto.request.ActivityUpdateRequest;
import com.venus.meetspace.dto.response.ActivityResponse;
import com.venus.meetspace.entity.Activity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper extends GlobalMapper {
    Activity toEntity(ActivityCreateRequest acr);
    Activity toEntity(ActivityUpdateRequest aur);
    ActivityCreateRequest toCreateRequest(Activity activity);
    ActivityUpdateRequest toUpdateRequest(Activity activity);
    ActivityResponse toResponse(Activity activity);
    List<ActivityResponse> toResponseList(List<Activity> activities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ActivityUpdateRequest aur, @MappingTarget Activity activity);
}
