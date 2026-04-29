package cn.hhh.campusmind.course.service.impl;

import cn.hhh.campusmind.course.domain.po.Course;
import cn.hhh.campusmind.course.mapper.CourseMapper;
import cn.hhh.campusmind.course.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程信息：课程编码、名称、学分学时及开课属性 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-29
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

}
