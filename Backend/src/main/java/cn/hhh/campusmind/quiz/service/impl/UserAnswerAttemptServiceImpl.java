package cn.hhh.campusmind.quiz.service.impl;

import cn.hhh.campusmind.quiz.domain.po.UserAnswerAttempt;
import cn.hhh.campusmind.quiz.mapper.UserAnswerAttemptMapper;
import cn.hhh.campusmind.quiz.service.IUserAnswerAttemptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户作答记录表（核心日志） 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-22
 */
@Service
public class UserAnswerAttemptServiceImpl extends ServiceImpl<UserAnswerAttemptMapper, UserAnswerAttempt> implements IUserAnswerAttemptService {

}
