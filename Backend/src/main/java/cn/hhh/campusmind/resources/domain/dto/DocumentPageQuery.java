package cn.hhh.campusmind.resources.domain.dto;


import cn.hhh.campusmind.common.page.PageQuery;
import cn.hhh.campusmind.resources.domain.enums.DocumentTypeEnum;
import lombok.Data;

@Data
public class DocumentPageQuery extends PageQuery {

    /** 课程id */
    private Long courseId;

    /** 搜索关键词 */
    private String keyword;

    /** 教师id */
    private Long teacherId;

    /** 文档类型 */
    private DocumentTypeEnum documentType;

    /** 文档语言 */
    private String language;
}
