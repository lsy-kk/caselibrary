package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("favorites_instance")
@Data
public class FavoritesInstance {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private Long favoritesId;

    private Long user_id;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}