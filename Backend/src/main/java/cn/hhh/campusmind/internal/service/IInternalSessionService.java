package cn.hhh.campusmind.internal.service;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.internal.domain.dto.AiSessionTitleResultDTO;
import jakarta.validation.Valid;

public interface IInternalSessionService {
    Result<Void> aiTitle(@Valid AiSessionTitleResultDTO titleDTO);
}
