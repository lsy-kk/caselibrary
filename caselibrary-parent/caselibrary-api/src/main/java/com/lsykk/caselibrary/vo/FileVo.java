package com.lsykk.caselibrary.vo;

import lombok.Data;

@Data
public class FileVo {

    // 文件名称
    private String name;

    // 文件地址url
    private String url;
    
    // 文件大小，单位为B
    private Integer size;
}
