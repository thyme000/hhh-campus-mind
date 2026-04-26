package cn.hhh.campusmind.internal.service;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.internal.domain.dto.AiProfileResultDTO;
import jakarta.validation.Valid;

public interface IInternalProfileService {
    Result<Void> aiResult(String token, @Valid AiProfileResultDTO dto);

}
