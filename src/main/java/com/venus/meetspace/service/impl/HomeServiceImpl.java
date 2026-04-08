package com.venus.meetspace.service.impl;

import com.venus.meetspace.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HomeServiceImpl implements HomeService {

    private final UserServiceImpl usi;

    public HomeServiceImpl(UserServiceImpl usi) {
        this.usi = usi;
    }


}
