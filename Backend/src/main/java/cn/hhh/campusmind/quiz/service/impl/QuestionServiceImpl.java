package cn.hhh.campusmind.quiz.service.impl;

import cn.hhh.campusmind.quiz.domain.po.Question;
import cn.hhh.campusmind.quiz.mapper.QuestionMapper;
import cn.hhh.campusmind.quiz.service.IQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 题目表 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

}
