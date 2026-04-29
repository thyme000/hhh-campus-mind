package cn.hhh.campusmind.resources.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 文件后缀枚举
 * 文件后缀（pdf/docx/ppt/pptx/md/markdown）
 */
@Getter
public enum FileExtEnum {

    /** PDF文件 */
    PDF("pdf", "PDF文件"),

    /** Word文档 */
    DOCX("docx", "Word文档"),

    /** PowerPoint演示文稿 */
    PPT("ppt", "PowerPoint演示文稿"),

    /** PowerPoint演示文稿（新版） */
    PPTX("pptx", "PowerPoint演示文稿（新版）"),

    /** Markdown文件 */
    MD("md", "Markdown文件"),

    /** txt文件 */
    TXT("txt", "txt文件"),

    /** Markdown文件（完整名称） */
    MARKDOWN("markdown", "Markdown文件（完整名称）");


    @EnumValue
    private final String code;
    private final String name;

    FileExtEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}