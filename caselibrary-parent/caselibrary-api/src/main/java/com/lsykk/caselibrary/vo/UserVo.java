package com.lsykk.caselibrary.vo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "user_vo")
@Data
public class UserVo {

    @Id
    private Long id;

    private String email;

    private String image;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String username;

    private Integer authority;

    private Integer status;

    private Integer caseNumber;

    private String createTime;
}
