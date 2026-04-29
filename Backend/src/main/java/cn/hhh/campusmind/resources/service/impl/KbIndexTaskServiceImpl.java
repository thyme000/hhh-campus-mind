package cn.hhh.campusmind.resources.service.impl;

import cn.hhh.campusmind.resources.domain.po.KbIndexTask;
import cn.hhh.campusmind.resources.mapper.KbIndexTaskMapper;
import cn.hhh.campusmind.resources.service.IKbIndexTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 知识库文档索引任务表（解析/切分/向量化异步任务） 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Service
public class KbIndexTaskServiceImpl extends ServiceImpl<KbIndexTaskMapper, KbIndexTask> implements IKbIndexTaskService {

}
