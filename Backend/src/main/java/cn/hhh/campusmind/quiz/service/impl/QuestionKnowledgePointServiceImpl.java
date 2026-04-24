package cn.hhh.campusmind.quiz.service.impl;

import cn.hhh.campusmind.quiz.domain.po.QuestionKnowledgePoint;
import cn.hhh.campusmind.quiz.mapper.QuestionKnowledgePointMapper;
import cn.hhh.campusmind.quiz.service.IQuestionKnowledgePointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 题目-知识点关联表（多对多） 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Service
public class QuestionKnowledgePointServiceImpl extends ServiceImpl<QuestionKnowledgePointMapper, QuestionKnowledgePoint> implements IQuestionKnowledgePointService {

}
