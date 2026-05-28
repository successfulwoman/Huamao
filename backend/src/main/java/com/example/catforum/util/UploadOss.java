package com.example.catforum.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class UploadOss {
    //阿里域名
    public static final String AIL_DOMAIN = "https://huamaoss.oss-cn-qingdao.aliyuncs.com/";

    // 默认上传路径
    public static final String DEFAULT_PATH = "";
    // 帖子图片上传路径
    public static final String POST_IMAGE_PATH = "tagfileimage/";
    // 用户头像上传路径
    public static final String AVATAR_PATH = "avatar/";

    // 文件大小限制
    public static final long MAX_POST_IMAGE_SIZE = 10 * 1024 * 1024; // 帖子图片最大 10MB
    public static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024;     // 头像最大 2MB

    // 支持的图片格式
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"};

    /**
     * 上传文件到OSS（默认路径）
     * @param file 要上传的文件
     * @return 文件URL
     */
    public static String upload(MultipartFile file) throws IOException {
        return upload(file, DEFAULT_PATH);
    }

    /**
     * 上传文件到OSS（指定路径）
     * @param file 要上传的文件
     * @param path 文件存储路径（如 "tagfileimage/"）
     * @return 文件URL
     */
    public static String upload(MultipartFile file, String path) throws IOException {
        // 验证文件名
        String originalName = file.getOriginalFilename();//原来的文件名
        if (originalName == null || !originalName.contains(".")) {
            throw new IOException("无效的文件名");
        }

        // 验证文件大小
        long maxSize = AVATAR_PATH.equals(path) ? MAX_AVATAR_SIZE : MAX_POST_IMAGE_SIZE;
        if (file.getSize() > maxSize) {
            throw new IOException("文件大小超过限制（最大 " + (maxSize / 1024 / 1024) + "MB）");
        }

        // 验证文件格式
        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();//文件后缀如.png
        boolean formatValid = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(ext)) {
                formatValid = true;
                break;
            }
        }
        if (!formatValid) {
            throw new IOException("不支持的图片格式，仅支持 jpg、png、gif、webp、bmp");
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String newFileName = uuid + ext;
        
        // 完整的OSS存储路径（路径 + 文件名）
        String ossFilePath = (path != null ? path : "") + newFileName;
        
        //地域节点
        String endpoint = "http://oss-cn-qingdao.aliyuncs.com";
        String accessKeyId = "REDACTED_ACCESS_KEY_ID";
        String accessKeySecret = "REDACTED_ACCESS_KEY_SECRET";

        //自动匹配Content-Type
        String contentType = "image/jpeg";
        if (originalName.toLowerCase().endsWith(".png")) {
            contentType = "image/png";
        } else if (originalName.toLowerCase().endsWith(".jpg") || originalName.toLowerCase().endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (originalName.toLowerCase().endsWith(".gif")) {
            contentType = "image/gif";
        } else if (originalName.toLowerCase().endsWith(".webp")) {
            contentType = "image/webp";
        }

        //OSS客户端对象
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 上传文件（带元数据）
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            // 设置Content-Disposition为inline，使图片可以直接预览而不是下载
            metadata.setContentDisposition("inline");
            ossClient.putObject("huamaoss", ossFilePath, file.getInputStream(), metadata);
            // 返回可直接预览的URL
            return AIL_DOMAIN + ossFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ossClient.shutdown();
        }
    }
}
