package com.lsykk.caselibrary.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsykk.caselibrary.dao.mapper.CaseHeaderMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
// 不属于Controller、Service、Repository的就标注Component
public class ThreadService {

    @Resource
    private CaseHeaderMapper caseHeaderMapper;

    @PostConstruct
    public void init(){
        // 初始化浏览量
//        List<CaseHeader> caseHeaderList = caseHeaderMapper.selectList(new LambdaQueryWrapper<>());
//        for (CaseHeader caseHeader : caseHeaderList) {
//            String viewCountStr = (String) redisTemplate.opsForHash().get("Viewtimes", caseHeader.getId());
//            if (viewCountStr == null){
//                //初始化
//                redisTemplate.opsForHash().put("Viewtimes", String.valueOf(caseHeader.getId()),
//                        String.valueOf(caseHeader.getViewtimes()));
//            }
//            String CommentStr = (String) redisTemplate.opsForHash().get("Comment", String.valueOf(caseHeader.getId()));
//            if (CommentStr == null){
//                //初始化
//                redisTemplate.opsForHash().put("Comment", String.valueOf(caseHeader.getId()),
//                        String.valueOf(caseHeader.getComment()));
//            }
//            String FavoritesStr = (String) redisTemplate.opsForHash().get("Favorites", String.valueOf(caseHeader.getId()));
//            if (FavoritesStr == null){
//                //初始化
//                redisTemplate.opsForHash().put("Favorites", String.valueOf(caseHeader.getId()),
//                        String.valueOf(caseHeader.getFavorites()));
//            }
//            String ThumbStr = (String) redisTemplate.opsForHash().get("Thumb", String.valueOf(caseHeader.getId()));
//            if (ThumbStr == null){
//                //初始化
//                redisTemplate.opsForHash().put("Thumb", String.valueOf(caseHeader.getId()),
//                        String.valueOf(caseHeader.getThumb()));
//            }
//        }
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    //期望此操作在线程池 执行 不会影响原有的主线程
    @Async("taskExecutor")
    public void updateCaseViewtimes(Long id) {
        //采用redis进行浏览量的增加
        //hash结构 key 浏览量标识 field 文章id  后面1 表示自增加1
        if (redisTemplate.opsForHash().hasKey("Viewtimes", String.valueOf(id))){
            redisTemplate.opsForHash().increment("Viewtimes", String.valueOf(id), 1);
        }
        else {
            redisTemplate.opsForHash().put("Viewtimes", String.valueOf(id), 1);
        }
        //定时任务在ViewCountHandler中

        //还有一种方式是，redis自增之后，直接发送消息到消息队列中，由消息队列进行消费 来同步数据库，比定时任务要好一些
    }

    @Async("taskExecutor")
    public void updateCaseComment(Long id) {
        if (redisTemplate.opsForHash().hasKey("Comment", String.valueOf(id))){
            redisTemplate.opsForHash().increment("Comment", String.valueOf(id),1);
        }
        redisTemplate.opsForHash().put("Comment", String.valueOf(id), 1);
    }

    @Async("taskExecutor")
    public void updateCaseFavorites(Long id) {
        if (redisTemplate.opsForHash().hasKey("Favorites", String.valueOf(id))){
            redisTemplate.opsForHash().increment("Favorites", String.valueOf(id),1);
        }
        redisTemplate.opsForHash().put("Favorites", String.valueOf(id), 1);
    }

    @Async("taskExecutor")
    public void updateCaseThumb(Long id) {
        if (redisTemplate.opsForHash().hasKey("Thumb", String.valueOf(id))){
            redisTemplate.opsForHash().increment("Thumb", String.valueOf(id),1);
        }
        redisTemplate.opsForHash().put("Thumb", String.valueOf(id), 1);
    }
}
