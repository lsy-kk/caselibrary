package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.service.NoticeService;
import com.lsykk.caselibrary.service.NoticeWebSocket;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.NoticeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PreAuthorize("@authorizeService.checkUserId(#userId)")
    @GetMapping("/getListByUserIdAndTypeAndIsRead")
    public ApiResult getListByUserIdAndTypeAndIsRead(@RequestParam Long userId,
                                                     @RequestParam Integer type,
                                                     @RequestParam Integer isRead){
        return ApiResult.success(noticeService.findNoticeVoByUserIdAndTypeAndIsRead(userId, type, isRead));
    }

    @PreAuthorize("@authorizeService.checkUserId(#userId)")
    @GetMapping("/updateUnRead")
    public ApiResult updateUnRead(@RequestParam Long userId,
                                  @RequestParam Integer type){
        return noticeService.updateUnRead(userId, type);
    }

}
