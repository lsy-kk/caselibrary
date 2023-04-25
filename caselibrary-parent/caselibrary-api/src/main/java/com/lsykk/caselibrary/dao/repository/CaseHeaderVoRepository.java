package com.lsykk.caselibrary.dao.repository;

import com.lsykk.caselibrary.vo.CaseHeaderVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseHeaderVoRepository extends ElasticsearchRepository<CaseHeaderVo, Long> {

    Page<CaseHeaderVo> findByTitleAndSummary(String title, String summary, Pageable pageable);
}
