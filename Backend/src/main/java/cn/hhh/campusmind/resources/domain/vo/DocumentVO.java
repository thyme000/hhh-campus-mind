package cn.hhh.campusmind.resources.domain.vo;

import cn.hhh.campusmind.resources.domain.enums.DocumentTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class DocumentVO {

    /** 文档id */
    private Long documentId;

    /** 文档标题 */
    private String documentName;

    /** 文档地址 */
    private String documentUrl;

    /** 文档类型 */
    private DocumentTypeEnum documentType;

    /** 文档上传者id */
    private Long uploaderId;

    /** 文档上传者姓名 */
    private String uploaderName;

    /** 文档上传时间 */
    private LocalDateTime uploadTime;

    /** 文档所属课程id */
    private Long courseId;

    /** 文档所属课程名称 */
    private String courseName;

    /** 文档所属课程所属学院id */
    private Long collegeId;

    /** 文档所属课程所属学院名称 */
    private String collegeName;
}
