package com.lsykk.caselibrary.dao.repository;

import com.lsykk.caselibrary.vo.TagVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagVoRepository extends ElasticsearchRepository<TagVo, Long> {

    // 根据名称和描述查找
    Page<TagVo> findByNameAndDescription(String name, String description, Pageable pageable);
}
