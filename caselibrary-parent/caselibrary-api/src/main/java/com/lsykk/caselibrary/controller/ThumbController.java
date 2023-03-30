package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.Thumb;
import com.lsykk.caselibrary.service.ThumbService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.CaseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/thumb")
public class ThumbController {
    @Autowired
    private ThumbService thumbService;

    @PostMapping("")
    public ApiResult thumbById(@RequestBody Thumb thumb){
        return thumbService.insertThumb(thumb);
    }
}
