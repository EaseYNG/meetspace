package com.venus.meetspace.service.impl;

import com.venus.meetspace.DTO.Profile;
import com.venus.meetspace.entity.User;
import com.venus.meetspace.service.HomeService;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

    private final UserServiceImpl usi;

    public HomeServiceImpl(UserServiceImpl usi) {
        this.usi = usi;
    }


}
