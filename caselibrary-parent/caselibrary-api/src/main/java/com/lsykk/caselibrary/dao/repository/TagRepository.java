package com.lsykk.caselibrary.dao.repository;

import com.lsykk.caselibrary.dao.pojo.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TagRepository extends ElasticsearchRepository<Tag, Long> {

    // 根据名称和描述查找
    Page<Tag> findByNameAndDescription(String name, String description, Pageable pageable);
}
