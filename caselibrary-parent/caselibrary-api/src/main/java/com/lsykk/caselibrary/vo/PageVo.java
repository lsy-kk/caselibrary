package com.lsykk.caselibrary.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {

    List<T> recordList;

    Long total;
}
