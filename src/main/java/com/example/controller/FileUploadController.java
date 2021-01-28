package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author 悠一木碧
 */
@Slf4j
@Controller
public class FileUploadController {
    @PostMapping("/user/upload")
    public String upload(@RequestParam("newPassword") String newPassword,
                         @RequestPart("headPortrait") MultipartFile headPortrait,
                         @RequestPart("photos") MultipartFile[] photos) {
        try{
            log.info("newPassword={}, headPortraitName={}, photos.count={}", newPassword,
                    headPortrait.getOriginalFilename(), photos.length);
            String prefixPath = "D:\\StatusCode\\IdeaProjects\\springboot-03-web\\src\\main\\resources" +
                    "\\static\\images\\headPortrait\\";
            String suffix = ".jpg";
            if(!headPortrait.isEmpty()){
                UUID headPortraitName = UUID.randomUUID();
                headPortrait.transferTo(new File(prefixPath + headPortraitName + suffix));
            }
            prefixPath = "D:\\StatusCode\\IdeaProjects\\springboot-03-web\\src\\main\\resources" +
                    "\\static\\images\\photos\\";
            for (MultipartFile photo : photos) {
                UUID photoName = UUID.randomUUID();
                photo.transferTo(new File(prefixPath + photoName + suffix));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "5xx";
        }

        return "redirect:/form_layouts";
    }

}
