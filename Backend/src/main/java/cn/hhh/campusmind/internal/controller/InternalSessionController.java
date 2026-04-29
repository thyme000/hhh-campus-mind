package cn.hhh.campusmind.internal.controller;

import cn.hhh.campusmind.common.result.Result;
import cn.hhh.campusmind.internal.domain.dto.AiSessionTitleResultDTO;
import cn.hhh.campusmind.internal.service.IInternalSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/session")
@Tag(name = "内部模块")
@RequiredArgsConstructor
public class InternalSessionController {

    private final IInternalSessionService internalSessionService;

    /**
     * 回调ai生成的会话标题
     * @param titleDTO
     * @return
     */
    @Operation(summary = "回调ai生成的会话标题")
    @PostMapping("/ai-title")
    public Result<Void> aiTitle(@Valid @RequestBody AiSessionTitleResultDTO titleDTO) {
        return internalSessionService.aiTitle(titleDTO);
    }
}
