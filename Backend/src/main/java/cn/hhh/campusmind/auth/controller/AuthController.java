package cn.hhh.campusmind.auth.controller;

import cn.hhh.campusmind.auth.domain.dto.LoginDTO;
import cn.hhh.campusmind.auth.service.IAuthService;
import cn.hhh.campusmind.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "认证模块")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        return authService.login(loginDTO, request);
    }
}
