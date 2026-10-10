package com.example.catforum.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 阿里云 OSS 上传工具
 *
 * 安全说明：AccessKey、Endpoint、Bucket、域名等敏感配置统一从
 * application.yml 的 aliyun.oss.* 读取（支持环境变量覆盖），
 * 严禁把真实密钥硬编码在本类中。
 */
@Component
public class UploadOss {

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

    /** OSS 地域节点，如 http://oss-cn-qingdao.aliyuncs.com */
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    /** 访问密钥 ID（从环境变量 OSS_ACCESS_KEY_ID 注入） */
    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    /** 访问密钥 Secret（从环境变量 OSS_ACCESS_KEY_SECRET 注入） */
    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    /** Bucket 名称 */
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    /** 访问域名（用于拼接返回的图片 URL） */
    @Value("${aliyun.oss.domain}")
    private String domain;

    /**
     * 上传文件到OSS（默认路径）
     * @param file 要上传的文件
     * @return 文件URL
     */
    public String upload(MultipartFile file) throws IOException {
        return upload(file, DEFAULT_PATH);
    }

    /**
     * 上传文件到OSS（指定路径）
     * @param file 要上传的文件
     * @param path 文件存储路径（如 "tagfileimage/"）
     * @return 文件URL
     */
    public String upload(MultipartFile file, String path) throws IOException {
        // 验证密钥是否已配置
        if (accessKeyId == null || accessKeyId.isEmpty() || accessKeySecret == null || accessKeySecret.isEmpty()) {
            throw new IOException("OSS 密钥未配置，请设置环境变量 OSS_ACCESS_KEY_ID 和 OSS_ACCESS_KEY_SECRET");
        }

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
            ossClient.putObject(bucketName, ossFilePath, file.getInputStream(), metadata);
            // 返回可直接预览的URL
            return domain + ossFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            ossClient.shutdown();
        }
    }
}
