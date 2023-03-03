package com.lsykk.caselibrary.dao.repository;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseHeaderRepository extends ElasticsearchRepository<CaseHeader, Long> {

    // 根据标题和简介查找
    Page<CaseHeader> findByTitleAndSummary(String title, String summary, Pageable pageable);
}
