package cn.hhh.campusmind.profile.mapper;

import cn.hhh.campusmind.profile.domain.po.ProfileSnapshot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 学生画像更新快照表：记录每次画像更新的完整结果、变更delta、证据与置信度，便于回溯与可解释展示。 Mapper 接口
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
public interface ProfileSnapshotMapper extends BaseMapper<ProfileSnapshot> {

}
