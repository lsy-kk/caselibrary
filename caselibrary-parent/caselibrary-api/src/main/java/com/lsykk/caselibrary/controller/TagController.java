package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/getList")
    public ApiResult getTagList(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false)  Long id,
                                @RequestParam(required = false)  String name){
        PageParams pageParams = new PageParams(page, pageSize);
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        return tagService.getTagList(pageParams, tag);
    }

    @GetMapping("/getTagListByPrefix")
    public ApiResult getTagListByPrefix(@RequestParam(required = false) String prefix){
        return tagService.getTagListByPrefix(prefix);
    }

    @GetMapping("/findTagVoByCaseId")
    public ApiResult findTagVoByCaseId(@RequestParam Long caseId){
        return ApiResult.success(tagService.findTagVoByCaseId(caseId));
    }

    @PostMapping("/updateTagVoByCaseId")
    public ApiResult updateTagVoByCaseId(List<Long> tagIds, Long caseId){
        return ApiResult.success();
    }

    @PostMapping("/insert")
    public ApiResult insert(@RequestBody Tag tag){
        return tagService.insertTag(tag);
    }

    @PutMapping("/update")
    public ApiResult update(@RequestBody Tag tag){
        return tagService.updateTag(tag);
    }

}
