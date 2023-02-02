package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("user")
@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String email;

    private String image;

    private String username;

    private String password;

    private Integer authority;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
