package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.common.aop.LogAnnotation;
import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/getList")
    @LogAnnotation(module="标签",operator="管理员获取标签列表")
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
    @LogAnnotation(module="标签",operator="条件获取标签列表")
    public ApiResult getTagVoList(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false)  Long id,
                                  @RequestParam(required = false)  String name){
        PageParams pageParams = new PageParams(page, pageSize);
        return tagService.getTagVoList(pageParams, id, name);
    }

    @GetMapping("/getTagListByPrefix")
    @LogAnnotation(module="标签",operator="根据前缀获取标签列表")
    public ApiResult getTagListByPrefix(@RequestParam(required = false) String prefix){
        return tagService.getTagListByPrefix(prefix);
    }

    @GetMapping("/getSearchList")
    @LogAnnotation(module="标签",operator="获取搜索标签列表")
    public ApiResult getSearchList(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") String keyword){
        PageParams pageParams = new PageParams(page, pageSize);
        return tagService.getSearchList(pageParams, keyword);
    }

    @GetMapping("/getTagVoById")
    @LogAnnotation(module="标签",operator="根据ID获取标签")
    public ApiResult getTagVoById(@RequestParam Long id){
        return tagService.findTagVoById(id);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/insert")
    @LogAnnotation(module="标签",operator="新增标签")
    public ApiResult insert(@RequestBody Tag tag){
        return tagService.insertTag(tag);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/update")
    @LogAnnotation(module="标签",operator="修改标签信息")
    public ApiResult update(@RequestBody Tag tag){
        return tagService.updateTag(tag);
    }

}
