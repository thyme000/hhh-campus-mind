package cn.hhh.campusmind.college.service.impl;

import cn.hhh.campusmind.college.domain.po.College;
import cn.hhh.campusmind.college.mapper.CollegeMapper;
import cn.hhh.campusmind.college.service.ICollegeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学院字典表：用于规范学院名称，便于导入校验、下拉选择与统计。 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements ICollegeService {

}
