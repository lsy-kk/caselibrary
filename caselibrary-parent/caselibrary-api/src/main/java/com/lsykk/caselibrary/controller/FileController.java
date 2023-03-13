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

    @GetMapping("/downloadFile")
    public void downloadFile(String filePath, HttpServletResponse response) throws Exception {
        FileInputStream in = new FileInputStream(filePath);
        byte[] bytes = IOUtils.toByteArray(in);
        response.setContentType("application/force-download");
        response.setContentLength(bytes.length);
        response.setHeader("Content-Disposition", "attachment;filename=result");
        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.close();
        in.close();
    }

    @PostMapping("/exportMarkdownFile")
    public void exportMarkdownFile(@RequestBody CaseBodyVo caseBodyVo, HttpServletResponse response) throws Exception {
        String filePath = fileService.exportMarkdownFile(caseBodyVo.getContent());
        FileInputStream in = new FileInputStream(filePath);
        byte[] bytes = IOUtils.toByteArray(in);
        response.setContentType("application/force-download");
        response.setContentLength(bytes.length);
        response.setHeader("Content-Disposition", "attachment;filename=result");
        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.close();
        in.close();
    }
}
