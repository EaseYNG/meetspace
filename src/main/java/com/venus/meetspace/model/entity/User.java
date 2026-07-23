package com.venus.meetspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String nickname;
    private String username;
    private String password;

    private Integer age;
    private String gender;
    private String email;
    private String phone;
    private String firstname;
    private String lastname;
}
