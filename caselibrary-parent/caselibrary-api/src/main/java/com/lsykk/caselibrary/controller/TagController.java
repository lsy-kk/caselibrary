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

    @GetMapping("/getTagVoList")
    public ApiResult getTagVoList(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false)  Long id,
                                  @RequestParam(required = false)  String name){
        PageParams pageParams = new PageParams(page, pageSize);
        return tagService.getTagVoList(pageParams, id, name);
    }

    @GetMapping("/getTagListByPrefix")
    public ApiResult getTagListByPrefix(@RequestParam(required = false) String prefix){
        return tagService.getTagListByPrefix(prefix);
    }

    @GetMapping("/getSearchList")
    public ApiResult getSearchList(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") String keyword){
        PageParams pageParams = new PageParams(page, pageSize);
        return tagService.getSearchList(pageParams, keyword);
    }

    @GetMapping("/getTagVoById")
    public ApiResult getTagVoById(@RequestParam Long id){
        return tagService.findTagVoById(id);
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
