package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.service.FileService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseBodyVo;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/uploadFile")
    public ApiResult uploadFile(MultipartFile file){
        return fileService.uploadFile(file);
    }

    @GetMapping("/deleteFile")
    public ApiResult deleteFile(@RequestParam String path){
        return fileService.deleteFile(path);
    }

}
