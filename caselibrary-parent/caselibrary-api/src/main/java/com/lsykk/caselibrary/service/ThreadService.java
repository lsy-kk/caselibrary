package com.lsykk.caselibrary.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsykk.caselibrary.dao.mapper.CaseHeaderMapper;;
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
        // 初始化，载入所有case
        List<CaseHeader> caseHeaderList = caseHeaderMapper.selectList(new LambdaQueryWrapper<>());
        for (CaseHeader caseHeader: caseHeaderList){
            String id = String.valueOf(caseHeader.getId());
            if (!redisTemplate.opsForHash().hasKey("Viewtimes", id)){
                redisTemplate.opsForHash().put("Viewtimes", id, String.valueOf(0));
            }
            if (!redisTemplate.opsForHash().hasKey("Comment", id)){
                redisTemplate.opsForHash().put("Comment", id, String.valueOf(0));
            }
            if (!redisTemplate.opsForHash().hasKey("Favorites", id)){
                redisTemplate.opsForHash().put("Favorites", id, String.valueOf(0));
            }
            if (!redisTemplate.opsForHash().hasKey("Thumb", id)){
                redisTemplate.opsForHash().put("Thumb", id, String.valueOf(0));
            }
        }
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    //期望此操作在线程池 执行 不会影响原有的主线程
    @Async("taskExecutor")
    public void updateCaseViewtimes(Long id, Integer increase) {
        //采用redis进行浏览量的增加
        //hash结构 key 浏览量标识 field 文章id
        if (!redisTemplate.opsForHash().hasKey("Viewtimes", String.valueOf(id))){
            redisTemplate.opsForHash().put("Viewtimes", String.valueOf(id), String.valueOf(0));
        }
        redisTemplate.opsForHash().increment("Viewtimes", String.valueOf(id), increase);
        //定时任务在ViewCountHandler中
        //还有一种方式是，redis自增之后，直接发送消息到消息队列中，由消息队列进行消费 来同步数据库，比定时任务要好一些
    }

    @Async("taskExecutor")
    public void updateCaseComment(Long id, Integer increase) {
        if (!redisTemplate.opsForHash().hasKey("Comment", String.valueOf(id))){
            redisTemplate.opsForHash().put("Comment", String.valueOf(id), String.valueOf(0));
        }
        redisTemplate.opsForHash().increment("Comment", String.valueOf(id), increase);
    }

    @Async("taskExecutor")
    public void updateCaseFavorites(Long id, Integer increase) {
        if (!redisTemplate.opsForHash().hasKey("Favorites", String.valueOf(id))){
            redisTemplate.opsForHash().put("Favorites", String.valueOf(id), String.valueOf(0));
        }
        redisTemplate.opsForHash().increment("Favorites", String.valueOf(id), increase);
    }

    @Async("taskExecutor")
    public void updateCaseThumb(Long id, Integer increase) {
        if (!redisTemplate.opsForHash().hasKey("Thumb", String.valueOf(id))){
            redisTemplate.opsForHash().put("Thumb", String.valueOf(id), String.valueOf(0));
        }
        redisTemplate.opsForHash().increment("Thumb", String.valueOf(id), increase);
    }

    @Async("taskExecutor")
    public void updateTagCaseNumber(Long tagId, Integer increase) {
        if (!redisTemplate.opsForHash().hasKey("Tag_CaseNumber", String.valueOf(tagId))){
            redisTemplate.opsForHash().put("Tag_CaseNumber", String.valueOf(tagId), String.valueOf(0));
        }
        redisTemplate.opsForHash().increment("Tag_CaseNumber", String.valueOf(tagId), increase);
    }

    @Async("taskExecutor")
    public void updateUserCaseNumber(Long userId, Integer increase) {
        if (!redisTemplate.opsForHash().hasKey("User_CaseNumber", String.valueOf(userId))){
            redisTemplate.opsForHash().put("User_CaseNumber", String.valueOf(userId), String.valueOf(0));
        }
        redisTemplate.opsForHash().increment("User_CaseNumber", String.valueOf(userId), increase);
    }
}
