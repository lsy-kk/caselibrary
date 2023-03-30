package com.lsykk.caselibrary.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CommentMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.Comment;
import com.lsykk.caselibrary.service.CommentService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CommentVo;
import com.lsykk.caselibrary.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public List<CommentVo> getCommentVoListByCaseId(Long caseId){
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getCaseId, caseId);
        queryWrapper.eq(Comment::getParentId,0);
        queryWrapper.orderByDesc(Comment::getCaseId);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList = copyList(comments);
        return commentVoList;
    }

    @Override
    public ApiResult insertComment(Comment comment){
        commentMapper.insertAndGetId(comment);
        return ApiResult.success(getCommentVoById(comment.getId()));
    }

    @Override
    public ApiResult updateComment(Comment comment){
        return ApiResult.success();
    }

    @Override
    public Comment getCommentById(Long id){
        return commentMapper.selectById(id);
    }

    @Override
    public CommentVo getCommentVoById(Long id){
        String commentJson = redisTemplate.opsForValue().get("CommentVo_" + id);
        if (StringUtils.isNotBlank(commentJson)){
            return JSON.parseObject(commentJson, CommentVo.class);
        }
        CommentVo commentVo = copy(getCommentById(id));
        redisTemplate.opsForValue().set("CommentVo_" + id, JSON.toJSONString(commentVo), 1, TimeUnit.HOURS);
        return commentVo;
    }

    private List<CommentVo> getCommentVoListByParentId(Long id){
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, id);
        queryWrapper.orderByDesc(Comment::getCaseId);
        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        return copyList(commentList);
    }

    private List<CommentVo> copyList(List<Comment> commentList){
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment){
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        UserVo userVo = userService.findUserVoById(comment.getAuthorId());
        commentVo.setAuthor(userVo);
        if (comment.getParentId() == 0){
            commentVo.setChildren(getCommentVoListByParentId(comment.getId()));
        }
        if (comment.getToUserId() != null){
            commentVo.setToUser(userService.findUserVoById(comment.getToUserId()));
        }
        commentVo.setCreateTime(DateUtils.getTime(comment.getCreateTime()));
        commentVo.setUpdateTime(DateUtils.getTime(comment.getUpdateTime()));
        return commentVo;
    }
}