package cn.hhh.campusmind.resources.service.impl;

import cn.hhh.campusmind.resources.domain.po.KbChunkEmbedding;
import cn.hhh.campusmind.resources.mapper.KbChunkEmbeddingMapper;
import cn.hhh.campusmind.resources.service.IKbChunkEmbeddingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 知识库切分块向量表（pgvector） 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Service
public class KbChunkEmbeddingServiceImpl extends ServiceImpl<KbChunkEmbeddingMapper, KbChunkEmbedding> implements IKbChunkEmbeddingService {

}
