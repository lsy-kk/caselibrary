package com.lsykk.caselibrary.handler;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.CaseService;
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

import static java.lang.Math.pow;

@Component
@Slf4j
@EnableScheduling
public class HotScoreHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CaseService caseService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0/5 * * * *") // 每5分钟触发一次
    //@Scheduled(cron = "0 0 3 * * *")
    // 定时任务，每天凌晨3点执行，计算新的案例热度，以供下一天排序
    @Async("taskExecutor") // 放到线程池中执行，定时任务
    public void updateCaseHotData(){
        log.info("=====>>>>> 同步案例热度数据开始执行  {}",new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        Set<Object>  keys = redisTemplate.opsForHash().keys("Viewtimes");
        for (Object key: keys){
            Long caseId = Long.parseLong(key.toString());
            CaseHeader caseHeader = caseService.getCaseHeaderById(caseId);
            // 更新viewtimes，并清空redis中数据
            Object value = redisTemplate.opsForHash().get("Viewtimes", key);
            int viewtimes = Integer.parseInt(value.toString());
            caseHeader.setViewtimes(caseHeader.getViewtimes() + viewtimes);
            redisTemplate.opsForHash().delete("Viewtimes", key);
            // 更新comment，并清空redis中数据
            value = redisTemplate.opsForHash().get("Comment", key);
            int comment = 0;
            if (value != null){
                comment = Integer.parseInt(value.toString());
                redisTemplate.opsForHash().delete("Comment", key);
            }
            caseHeader.setComment(caseHeader.getComment() + comment);
            // 更新favorites，并清空redis中数据
            value = redisTemplate.opsForHash().get("Favorites", key);
            int favorites = 0;
            if (value != null){
                favorites = Integer.parseInt(value.toString());
                redisTemplate.opsForHash().delete("Favorites", key);
            }
            caseHeader.setFavorites(caseHeader.getFavorites() + favorites);
            // 更新thumb，并清空redis中数据
            value = redisTemplate.opsForHash().get("Thumb", key);
            int thumb = 0;
            if (value != null){
                thumb = Integer.parseInt(value.toString());
                redisTemplate.opsForHash().delete("Thumb", key);
            }
            caseHeader.setThumb(caseHeader.getThumb() + thumb);
            // 更新热度
            long time = System.currentTimeMillis() - caseHeader.getCreateTime().getTime();
            long sum = viewtimes + 5L * thumb + 10L * favorites + Math.min(comment, viewtimes);
            long total = caseHeader.getViewtimes() + 5L * caseHeader.getThumb() +
                    10L * caseHeader.getFavorites() + Math.min(caseHeader.getViewtimes(), caseHeader.getComment());
            double hot = (double)total / pow((double)time, (double) 1/3) + (double)sum;
            caseHeader.setHot(hot);
            caseService.updateCaseHeader(caseHeader);
        }
        log.info("=====>>>>> 同步案例热度数据结束  {}",new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
    }

    @Scheduled(cron = "0 0/5 * * * *") // 每5分钟触发一次
    //@Scheduled(cron = "0 0 3 * * *")
    @Async("taskExecutor")
    public void updateTagCaseNumber() {
        log.info("=====>>>>> 同步标签下案例数量开始执行  {}",new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        Set<Object>  keys = redisTemplate.opsForHash().keys("Tag_CaseNumber");
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
