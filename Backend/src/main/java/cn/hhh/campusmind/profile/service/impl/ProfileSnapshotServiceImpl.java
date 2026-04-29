package cn.hhh.campusmind.profile.service.impl;

import cn.hhh.campusmind.profile.domain.po.ProfileSnapshot;
import cn.hhh.campusmind.profile.mapper.ProfileSnapshotMapper;
import cn.hhh.campusmind.profile.service.IProfileSnapshotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学生画像更新快照表：记录每次画像更新的完整结果、变更delta、证据与置信度，便于回溯与可解释展示。 服务实现类
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
@Service
public class ProfileSnapshotServiceImpl extends ServiceImpl<ProfileSnapshotMapper, ProfileSnapshot> implements IProfileSnapshotService {

}
