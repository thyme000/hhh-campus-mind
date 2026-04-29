package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * MIME 类型枚举
 */
@Getter
public enum MimeTypeEnum {

    /** 文本类 */
    TXT("text/plain"),
    MARKDOWN("text/markdown"),

    /** 文档类 */
    PDF("application/pdf"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("application/vnd.ms-powerpoint"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    /** 图片类 */
    PNG("image/png"),
    JPG("image/jpeg"),
    JPEG("image/jpeg"),

    /** 音频类 */
    MP3("audio/mpeg"),

    /** 视频类 */
    MP4("video/mp4"),

    /** JSON类 */
    JSON("application/json"),
    /** 二进制流类 */
    OCTET_STREAM("application/octet-stream");

    @EnumValue
    private final String value;

    MimeTypeEnum(String value) {
        this.value = value;
    }
}