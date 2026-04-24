package cn.hhh.campusmind.profile.service;

import cn.hhh.campusmind.profile.domain.po.ProfileSnapshot;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学生画像更新快照表：记录每次画像更新的完整结果、变更delta、证据与置信度，便于回溯与可解释展示。 服务类
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
public interface IProfileSnapshotService extends IService<ProfileSnapshot> {

}
