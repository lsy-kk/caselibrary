package com.lsykk.caselibrary.service.impl;

import com.lsykk.caselibrary.service.FileService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.FileVo;
import com.upyun.RestManager;
import com.upyun.UpException;
import com.upyun.UpYunUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public String getFileMD5(MultipartFile file) {
        try {
            byte[] uploadBytes = file.getBytes();
            //file->byte[],生成md5
            return DigestUtils.md5Hex(uploadBytes);
        } catch (Exception e) {
            log.error("MultipartFile compute md5 error，message = " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<FileVo> getFileVoByString(String appendixList){
        if (StringUtils.isBlank(appendixList)){
            return null;
        }
        List<FileVo> fileVoList = new ArrayList<>();
        String[] list = appendixList.split(";");
        for (String appendix: list){
            FileVo fileVo = new FileVo();
            fileVo.setUrl(appendix);
            fileVo.setName(appendix.substring(appendix.lastIndexOf('/')+1));
            fileVo.setSize(getFileSize(appendix));
            fileVoList.add(fileVo);
        }
        return fileVoList;
    }

    @Override
    public ApiResult uploadFile(MultipartFile file){
        if (file == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        String originName = file.getOriginalFilename();
        if (StringUtils.isBlank(originName)){
            // 上传的文件类型错误
            return ApiResult.fail(ErrorCode.File_Upload_Illegal);
        }
        // set保存可接受的文件类型（后缀）
        Set<String> set = new HashSet<>();
        set.add(".pdf");
        set.add(".doc");
        set.add(".docx");
        set.add(".ppt");
        set.add(".pptx");
        set.add(".rar");
        set.add(".zip");
        set.add(".gif");
        set.add(".jpg");
        set.add(".png");
        // 获取文件后缀("."以及后面的内容)
        String fileType = originName.substring(originName.lastIndexOf("."));
        if (!set.contains(fileType)){
            // 上传的文件类型错误
            return ApiResult.fail(ErrorCode.File_Upload_Illegal);
        }
        // 又拍云实例构造
        RestManager manager = new RestManager("case-lib","kkysl", "Ms69o6Q8cI6spo8zscbu35ukL1z5nGI5");
        String savePath;
        if (fileType.equals(".jpg") || fileType.equals(".png") || fileType.equals(".gif")){
            // 图片类型：使用唯一的UUID作为图片名称
            savePath = "images/" + UUID.randomUUID() + fileType;
        }
        else {
            // 文件类型：若不是图片，使用原本的名称，保存在UUID收藏夹中
            savePath = "files/" + UUID.randomUUID() + "/" + originName;
        }
        try {
            // 拼接请求参数
            Map<String, String> params = new HashMap<>();
            // 设置文件的md5值，若又拍云发现md5不一致将回报 406 NotAcceptable 错误
            params.put(RestManager.PARAMS.CONTENT_MD5.getValue(), UpYunUtils.md5(file.getBytes()));
            // 设置待上传文件的"访问密钥"，访问时需要带上密钥，例如http://空间域名 /folder/test.jpg!bac
            // params.put(RestManager.PARAMS.CONTENT_SECRET.getValue(), "bac");
            Response res = manager.writeFile(savePath, file.getBytes(), params);
            // 成功传输文件，拼接地址
            if (res.isSuccessful()){
                String url = "http://case-lib.test.upcdn.net/" + savePath;
                return ApiResult.success(url);
            }
        } catch (Exception e) {
            log.error("upYun upload file error，message = " + e.getMessage());
            ApiResult.fail(ErrorCode.File_Upload_Error);
        }
        return ApiResult.fail(ErrorCode.File_Upload_Error);
    }

    @Override
    public Integer getFileSize(String filePath){
        // 又拍云实例构造
        RestManager manager = new RestManager("case-lib","kkysl", "Ms69o6Q8cI6spo8zscbu35ukL1z5nGI5");
        try {
            // 去掉测试域名，只留下文件地址
            if (filePath.startsWith("http://case-lib.test.upcdn.net")) {
                filePath = filePath.replaceFirst("http://case-lib.test.upcdn.net", "");
            }
            // 获取文件信息
            Response res = manager.getFileInfo(filePath);
            if (res.isSuccessful()){
                int siz = 0;
                try {
                    String content_length = res.headers().get("x-upyun-file-size");
                    if (StringUtils.isNotBlank(content_length)){
                        siz = Integer.parseInt(content_length);
                    }
                }
                catch (NumberFormatException e) {
                    log.error("NumberFormatException while parse file size, message = " + e.getMessage());
                }
                return siz;
            }
        } catch (IOException e) {
            log.error("IOException upYun cannot get the file " + filePath + ", message = " + e.getMessage());
        } catch (UpException e) {
            log.error("UpException cannot get the file " + filePath + ", message = " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ApiResult deleteFile(String filePath){
        RestManager manager = new RestManager("case-lib","kkysl", "Ms69o6Q8cI6spo8zscbu35ukL1z5nGI5");
        try {
            Response result = manager.deleteFile(filePath, null);
            if (result.isSuccessful()){
                return ApiResult.success();
            }
        } catch (IOException e) {
            log.error("IOException upYun cannot delete the file " + filePath + ", message = " + e.getMessage());
        } catch (UpException e) {
            log.error("UpException upYun cannot delete the file " + filePath + ", message = " + e.getMessage());
        }
        return ApiResult.fail(ErrorCode.CANNOT_DELETE_FILE);
    }
}
