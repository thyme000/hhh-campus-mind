package cn.hhh.campusmind.resources.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadDocumentDTO {

    /** 文件 ObjectKey*/
    @NotBlank(message = "文件ObjectKey不能为空")
    private String objectKey;
    /** 文件名称*/
    @NotBlank(message = "文件名称不能为空")
    private String fileName;
    /** 文件大小*/
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;
    /** 文件类型*/
    @NotBlank(message = "文件类型不能为空")
    private String fileType;
    /** 课程id */
    @NotNull(message = "课程id不能为空")
    private Long courseId;
    @NotBlank(message = "文件SHA-256不能为空")
    private String fileSha256;
    /** 全链路追踪 ID，与日志/监控对齐 */
    private String traceId;
}
