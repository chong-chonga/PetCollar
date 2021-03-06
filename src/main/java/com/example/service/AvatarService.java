package com.example.service;

import com.example.util.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Lexin Huang
 */
public interface AvatarService {

    String getAvatarUrlPrefix();

    String getAvatarResourcePathPrefix();

    String getImageSuffix();

    String getDefaultImageName();

    default void deleteOriginalAvatarIfExists(String portraitPath) throws IOException {
        if (!portraitPath.endsWith(getDefaultImageName())) {
            deleteImage(portraitPath);
        }
    }

    private void deleteImage(String avatarUrl) throws IOException {
        String fileName = avatarUrl.split(getAvatarUrlPrefix())[1];
        File fileToDelete = new File(getAvatarResourcePathPrefix() + fileName);
        boolean deleteSuccess = fileToDelete.delete();
        if (!deleteSuccess) {
            throw new IOException("删除原有头像失败!");
        }
    }

    /**
     *
     * @param image 上传的头像文件
     * @return 创建的头像文件的超链接
     * @throws IOException 仅当创建文件失败时, 会抛出以产生错误消息
     */
    default String createAvatarFile(MultipartFile image) throws IOException {
        String avatarName = generateUniqueImageName();
        File file = new File(getAvatarResourcePathPrefix() + avatarName);
        boolean createSuccess = file.createNewFile();
        if (!createSuccess) {
            throw new IOException("创建文件失败!");
        }
        image.transferTo(file);
        return getAvatarUrlPrefix() + avatarName;
    }

    private String generateUniqueImageName() {
        StringBuilder builder = new StringBuilder();
        String uniqueName = StringUtil.getUniqueName();
        builder.append(uniqueName).append(getImageSuffix());
        return builder.toString();
    }
}
