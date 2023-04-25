package com.lsykk.caselibrary.vo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "tag_vo")
@Data
public class TagVo {

    @Id
    private Long id;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String name;

    private String image;

    private String description;

    private Integer status;

    private Integer caseNumber;

    private String createTime;
}
