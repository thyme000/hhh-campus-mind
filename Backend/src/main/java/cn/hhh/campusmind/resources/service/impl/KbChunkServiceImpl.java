package cn.hhh.campusmind.resources.service.impl;

import cn.hhh.campusmind.resources.domain.po.KbChunk;
import cn.hhh.campusmind.resources.mapper.KbChunkMapper;
import cn.hhh.campusmind.resources.service.IKbChunkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 知识库切分块表（RAG 检索主数据） 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Service
public class KbChunkServiceImpl extends ServiceImpl<KbChunkMapper, KbChunk> implements IKbChunkService {

}
