package com.lsykk.caselibrary.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

@Document(indexName = "case_header_vo")
@Data
public class CaseHeaderVo {

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String summary;

    @Field(type = FieldType.Integer)
    private Integer thumb;

    @Field(type = FieldType.Integer)
    private Integer viewtimes;

    @Field(type = FieldType.Integer)
    private Integer comment;

    @Field(type = FieldType.Integer)
    private Integer favorites;

    @Field(type = FieldType.Double)
    private Double hot;

    @Field(type = FieldType.Integer)
    private Integer visible;

    @Field(type = FieldType.Integer)
    private Integer state;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(store = false)
    private String createTime;

    @Field(store = false)
    private String updateTime;

    // 额外的可选参数
    @Field(store = false)
    private CaseBodyVo caseBody;

    @Field(store = false)
    private List<CommentVo> comments;

    @Field(type=FieldType.Nested)
    private UserVo author;

    @Field(type=FieldType.Nested)
    private List<TagVo> tags;
}
