package com.lsykk.caselibrary.handler;

import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@EnableScheduling
public class CaseNumberHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0/5 * * * *") // 每5分钟触发一次
    //@Scheduled(cron = "0 0 3 * * *")
    @Async("taskExecutor")
    public void updateTagCaseNumber() {
        log.info("=====>>>>> 同步标签下案例数量开始执行  {}",new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        Set<Object> keys = redisTemplate.opsForHash().keys("Tag_CaseNumber");
        for (Object key: keys) {
            Long tagId = Long.parseLong(key.toString());
            Tag tag = tagService.findTagById(tagId);
            Object value = redisTemplate.opsForHash().get("Tag_CaseNumber", key);
            int caseNumber = Integer.parseInt(value.toString());
            tag.setCaseNumber(tag.getCaseNumber() + caseNumber);
            tagService.updateTag(tag);
            redisTemplate.opsForHash().delete("Tag_CaseNumber", key);
        }
        log.info("=====>>>>> 同步标签下案例数量结束  {}",new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
    }

    @Scheduled(cron = "0 0/5 * * * *") // 每5分钟触发一次
    //@Scheduled(cron = "0 0 3 * * *")
    @Async("taskExecutor")
    public void updateUserCaseNumber() {
        log.info("=====>>>>> 同步用户下案例数量开始执行  {}",new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        Set<Object>  keys = redisTemplate.opsForHash().keys("User_CaseNumber");
        for (Object key: keys) {
            Long userId = Long.parseLong(key.toString());
            User user = userService.findUserById(userId);
            Object value = redisTemplate.opsForHash().get("User_CaseNumber", key);
            int caseNumber = Integer.parseInt(value.toString());
            user.setCaseNumber(user.getCaseNumber() + caseNumber);
            userService.updateUser(user);
            redisTemplate.opsForHash().delete("User_CaseNumber", key);
        }
        log.info("=====>>>>> 同步用户下案例数量结束  {}",new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
    }
}
