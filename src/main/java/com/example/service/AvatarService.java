package com.example.service;

import com.example.util.StringUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Lexin Huang
 */
public interface AvatarService {

    /**
     * 获取头像图片资源 url 的前缀, 一般以 http:// 开头 (不包含头像图片名称)
     * 此方法用于在 {@link AvatarService#deleteAvatar(String portraitPath)}}中, 确定文件 url 路径前缀
     * @return 头像图片超链接前缀
     */
    String getAvatarUrlPrefix();

    /**
     * 获取头像图片在持久性存储上的存储前缀 (不包含头像图片名称)
     * 此方法用于在 {@link AvatarService#deleteAvatar(String portraitPath)}}中, 确定文件的路径前缀
     * @return 头像图片在持久性存储上的路径前缀
     */
    String getAvatarResourcePathPrefix();

    /**
     * 获取头像图片的默认名称
     * 此方法用于在 {@link AvatarService#deleteOriginalAvatarIfExists(String portraitPath)} 中判断是否为默认头像
     * @return 头像图片的默认名称
     */
    String getDefaultAvatarName();

    /**
     * 删除之前的头像 (如果头像存在且不是默认头像)
     * @param portraitPath 超链接形式的头像路径
     * @throws IOException 当原有头像存在而删除失败时
     */
    default void deleteOriginalAvatarIfExists(String portraitPath) throws IOException {
        if (!portraitPath.endsWith(getDefaultAvatarName())) {
            deleteAvatar(portraitPath);
        }
    }

    /**
     * 根据图片资源的超链接删除对应的图片
     * @param avatarUrl 图片资源 url
     * @throws IOException 当删除头像图片失败时
     */
    private void deleteAvatar(String avatarUrl) throws IOException {
        String fileName = avatarUrl.split(getAvatarUrlPrefix())[1];
        File fileToDelete = new File(getAvatarResourcePathPrefix() + fileName);
        boolean deleteSuccess = fileToDelete.delete();
        if (!deleteSuccess) {
            throw new IOException("删除原有头像失败!");
        }
    }

    /**
     * 根据上传的文件, 创建头像文件
     * @param image 上传的头像文件
     * @return 创建的头像文件的超链接
     * @throws IOException 当创建文件失败时 或 同名称的文件已存在时
     */
    default String createAvatarFile(MultipartFile image) throws IOException {
        String avatarName = generateUniqueImageName();
        File file = new File(getAvatarResourcePathPrefix() + avatarName);
        boolean createSuccess = file.createNewFile();
        if (!createSuccess) {
            throw new IOException("创建图片文件失败!");
        }
        if (file.exists()) {
            throw new IOException("原名称的图片文件仍然存在! 创建失败!");
        }
        image.transferTo(file);
        return getAvatarUrlPrefix() + avatarName;
    }

    /**
     * 生成图片名称 (确保名称唯一)
     * @return 图片名称
     */
    private String generateUniqueImageName() {
        StringBuilder builder = new StringBuilder();
        String uniqueName = StringUtil.getUniqueName();
        String pictureType = getAvatarSuffix();
        if (Objects.isNull(pictureType)) {
            pictureType = ".png";
        }
        builder.append(uniqueName).append(pictureType);
        return builder.toString();
    }

    /**
     * 获取头像图片的文件格式, 一般以 "." 字符开头, 如 ".jpg", ".png" 等
     * 此方法用于在 {@link AvatarService#generateUniqueImageName()} 中生成头像文件名称
     * @return 头像图片存储的路径前缀
     */
    default String getAvatarSuffix() {
        return null;
    }
}
