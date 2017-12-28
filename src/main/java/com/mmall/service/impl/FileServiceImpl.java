package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * Created by Administrator on 2017/12/26.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(IFileService.class);
    public String uploadFile(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
        String upLoadFileName = UUID.randomUUID().toString() + "." +fileExtName;

        logger.info("开始上传文件:文件名{}，路径{}，上传文件名{}",fileName,path,upLoadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,upLoadFileName);

        try {
            file.transferTo(targetFile);

            FTPUtil.upLoadFile(Lists.newArrayList(targetFile));

            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
        }
        return targetFile.getName();
    }
}
