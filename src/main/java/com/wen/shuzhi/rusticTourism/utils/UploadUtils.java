package com.wen.shuzhi.rusticTourism.utils;

/*
@author peng
@create 2022-09-20-16:11
@description 
*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 将上传的图片重新命名，保存到/static/picture目录下，返回新的文件名
 */
@Slf4j
public class UploadUtils {
    //返回文件名
    public String getNewFileName(MultipartFile multipartFile, String realPath) throws IOException {
        //MultipartFile是spring类型，代表了HTML中form data方式上传的文件，包含二进制数据+文件名称
        //1、获取文件名
        String originalFilename = multipartFile.getOriginalFilename();
        log.info("原文件名:"+originalFilename);

        //2、修改文件名称
        //2.1、将时间戳作为文件名前缀
        String fileNamePrefix = new SimpleDateFormat("yyyyMMddHHmmSS").format(new Date());
        //2.2、截取文件名后缀
        String fileNameSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //2.3、拼接形成新的文件名
        String newFileName = fileNamePrefix + fileNameSuffix;
        log.info("新的文件名:"+newFileName);
        //3、保存到目录中
        multipartFile.transferTo(new File(realPath,newFileName));
        //4、返回新的文件名
        return newFileName;
    }
}