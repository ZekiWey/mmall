package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2017/12/26.
 */
public interface IFileService {
    public String uploadFile(MultipartFile file, String path);
}
