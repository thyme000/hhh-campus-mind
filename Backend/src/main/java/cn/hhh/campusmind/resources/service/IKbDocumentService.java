package cn.hhh.campusmind.resources.service;

import cn.hhh.campusmind.common.page.PageResult;
import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.resources.domain.dto.DocumentPageQuery;
import cn.hhh.campusmind.resources.domain.dto.UploadDocumentDTO;
import cn.hhh.campusmind.resources.domain.po.KbDocument;
import cn.hhh.campusmind.resources.domain.vo.DocumentVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 知识库文档主表（老师上传文件后落库） 服务类
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
public interface IKbDocumentService extends IService<KbDocument> {

    Result<Void> uploadDocument(UploadDocumentDTO uploadDocumentDTO);

    Result<Void> deleteDocument(Long documentId);

    Result<Void> restoreDocument(Long documentId);

    Result<PageResult<DocumentVO>> getDocumentList(DocumentPageQuery pageQuery);
}
