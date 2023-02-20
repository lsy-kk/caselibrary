package com.lsykk.caselibrary.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsykk.caselibrary.dao.pojo.User;
import org.apache.ibatis.annotations.Mapper;

// MybatisPlus关联User表
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
