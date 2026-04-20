package cn.hhh.campusmind.college.mapper;


import cn.hhh.campusmind.college.domain.po.College;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 学院字典表：用于规范学院名称，便于导入校验、下拉选择与统计。 Mapper 接口
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
public interface CollegeMapper extends BaseMapper<College> {

}
