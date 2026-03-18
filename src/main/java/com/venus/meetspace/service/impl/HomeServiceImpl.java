package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.Profile;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.security.UserContext;
import com.venus.meetspace.service.HomeService;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

    private final UserServiceImpl usi;

    public HomeServiceImpl(UserServiceImpl usi) {
        this.usi = usi;
    }


    @Override
    public void setProfile(Profile profile) {
        if(UserContext.get() == null) throw new RuntimeException("未登录");

        long id = UserContext.get();
        User current = usi.findById(id);
        current.setAge(profile.getAge());
        current.setGender(profile.getGender());
        current.setEmail(profile.getEmail());
        current.setFirstname(profile.getFirstname());
        current.setLastname(profile.getLastname());
        
        usi.save(current);
    }
}
