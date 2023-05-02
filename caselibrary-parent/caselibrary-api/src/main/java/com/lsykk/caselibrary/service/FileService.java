package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {


    /**
     * 计算MultipartFile的md5
     * @param file
     * @return
     */
    String getFileMD5(MultipartFile file);

    /**
     * 根据分号分割的url字符串（appendixList），获取其中包含的文件信息
     * @param appendixList
     * @return
     */
    List<FileVo> getFileVoByString(String appendixList);

    /**
     * 上传文件到又拍云
     * @param file
     * @return
     */
    ApiResult uploadFile(MultipartFile file);

    /**
     * 根据云存储路径，获取附件大小
     * @param filePath
     * @return
     */
    Integer getFileSize(String filePath);

    /**
     * 根据地址，删除又拍云中文件
     * @param filePath
     * @return
     */
    ApiResult deleteFile(String filePath);

}
