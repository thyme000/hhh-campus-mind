package cn.hhh.campusmind.resources.controller;


import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.resources.domain.dto.DocumentPageQuery;
import cn.hhh.campusmind.resources.domain.dto.UploadDocumentDTO;
import cn.hhh.campusmind.resources.domain.vo.DocumentVO;
import cn.hhh.campusmind.resources.service.IKbDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 知识库文档主表（老师上传文件后落库） 前端控制器
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@RestController
@RequestMapping("/document")
@Tag(name = "知识库模块")
@RequiredArgsConstructor
public class KbDocumentController {

    private final IKbDocumentService kbDocumentService;

    @Operation(summary = "教师上传知识库文档")
    @PostMapping("/upload")
    public Result<Void> uploadDocument(@Valid @RequestBody UploadDocumentDTO uploadDocumentDTO) {
        return kbDocumentService.uploadDocument(uploadDocumentDTO);
    }

    /**
     * 删除知识库文档
     * @param documentId
     * @return
     */
    @Operation(summary = "删除知识库文档")
    @PostMapping("/delete/{documentId}")
    public Result<Void> deleteDocument(@PathVariable Long documentId) {
        return kbDocumentService.deleteDocument(documentId);
    }

    /**
     * 恢复知识库文档
     * @param documentId
     * @return
     */
    @Operation(summary = "恢复知识库文档")
    @PostMapping("/restore/{documentId}")
    public Result<Void> restoreDocument(@PathVariable Long documentId) {
        return kbDocumentService.restoreDocument(documentId);
    }

    /**
     * 获取知识库文档列表(条件分页查询)
     * @param pageQuery
     * @return
     */
    @Operation(summary = "获取知识库文档列表(条件分页查询)")
    @GetMapping("/list")
    public Result<PageResult<DocumentVO>> getDocumentList(@ModelAttribute DocumentPageQuery pageQuery) {
        return kbDocumentService.getDocumentList(pageQuery);
    }


}
