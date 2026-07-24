package com.venus.meetspace.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`users`")
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
    @TableField(value = "first_name")
    private String firstname;
    @TableField(value = "last_name")
    private String lastname;
}
