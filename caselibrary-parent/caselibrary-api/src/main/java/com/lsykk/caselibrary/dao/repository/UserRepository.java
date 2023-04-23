package com.lsykk.caselibrary.dao.repository;

import com.lsykk.caselibrary.dao.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<User, Long> {

    // 根据名称和描述查找
    Page<User> findByName(String name, Pageable pageable);
}
