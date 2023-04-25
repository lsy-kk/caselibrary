package com.lsykk.caselibrary.dao.repository;

import com.lsykk.caselibrary.vo.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVoRepository extends ElasticsearchRepository<UserVo, Long> {

    // 根据名称和描述查找
    Page<UserVo> findByUsername(String username, Pageable pageable);
}
