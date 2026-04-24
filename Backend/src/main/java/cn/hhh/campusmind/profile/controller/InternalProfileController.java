package cn.hhh.campusmind.profile.controller;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.profile.domain.dto.AiProfileResultDTO;
import cn.hhh.campusmind.profile.service.IInternalProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/profile")
@Tag(name = "内部接口-画像")
@RequiredArgsConstructor
public class InternalProfileController {

    private final IInternalProfileService internalProfileService;

    /**
     * AI结果回调（画像更新）
     * @param token
     * @param dto
     * @return
     */
    @Operation(summary = "AI结果回调（画像更新）")
    @PostMapping("/ai-result")
    public Result<Void> aiResult(
            @RequestHeader(value = "Internal-Token", required = false) String token,
            @Valid @RequestBody AiProfileResultDTO dto) {
        return internalProfileService.aiResult(token, dto);
    }

}

