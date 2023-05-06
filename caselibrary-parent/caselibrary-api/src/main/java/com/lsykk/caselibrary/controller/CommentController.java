package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.Comment;
import com.lsykk.caselibrary.service.CommentService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/getCommentList")
    public ApiResult getCommentList(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false) Long id,
                                 @RequestParam(required = false) Long authorId,
                                 @RequestParam(required = false) Long caseId,
                                 @RequestParam(required = false) Integer status) {
        PageParams pageParams = new PageParams(page, pageSize);
        return commentService.getCommentList(pageParams, id, authorId, caseId, status);
    }

    @PreAuthorize("@authorizeService.checkComment(#comment) or hasAuthority('admin')")
    @PostMapping("/insert")
    public ApiResult insertComment(@RequestBody Comment comment){
        return commentService.insertComment(comment);
    }

    @PreAuthorize("@authorizeService.checkComment(#comment) or hasAuthority('admin')")
    @PutMapping("/update")
    public ApiResult updateComment(@RequestBody Comment comment){
        return commentService.updateComment(comment);
    }
}
