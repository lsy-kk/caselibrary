package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.Comment;
import com.lsykk.caselibrary.service.CommentService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.CaseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/insert")
    public ApiResult insertComment(@RequestBody Comment comment){
        return commentService.insertComment(comment);
    }
}
