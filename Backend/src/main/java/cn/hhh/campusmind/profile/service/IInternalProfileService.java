package cn.hhh.campusmind.profile.service;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.profile.domain.dto.AiProfileResultDTO;
import jakarta.validation.Valid;

public interface IInternalProfileService {
    Result<Void> aiResult(String token, @Valid AiProfileResultDTO dto);

}
