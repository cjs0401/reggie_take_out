package com.itheima.reggie.controller;

import com.itheima.reggie.entity.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author jsc
 * @version 1.0
 */

@Slf4j
@RequestMapping("/common")
@RestController
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String extendedFileType = filename.substring(filename.lastIndexOf("."));
        filename = UUID.randomUUID().toString() + extendedFileType;
        File originalFile = new File(basePath);
        if (!originalFile.exists()) {
            originalFile.mkdirs();
        }
        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return  R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse httpServletResponse) {
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        if (!StringUtils.hasLength(name)){
            return;
        }
        String fileName = basePath + name;
        try {
            fileInputStream = new FileInputStream(new File(fileName));
            outputStream = httpServletResponse.getOutputStream();
            httpServletResponse.setContentType("image/jpg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len =fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null || fileInputStream != null) {
                    outputStream.close();
                    fileInputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
