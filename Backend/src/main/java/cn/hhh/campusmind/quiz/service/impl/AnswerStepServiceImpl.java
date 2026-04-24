package cn.hhh.campusmind.quiz.service.impl;

import cn.hhh.campusmind.quiz.domain.po.AnswerStep;
import cn.hhh.campusmind.quiz.mapper.AnswerStepMapper;
import cn.hhh.campusmind.quiz.service.IAnswerStepService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 作答步骤记录（用于更精确区分错在第几步） 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Service
public class AnswerStepServiceImpl extends ServiceImpl<AnswerStepMapper, AnswerStep> implements IAnswerStepService {

}
