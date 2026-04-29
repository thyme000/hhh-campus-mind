package cn.hhh.campusmind.auth.service;

import cn.hhh.campusmind.auth.domain.dto.LoginDTO;
import cn.hhh.campusmind.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    Result<String> login(LoginDTO loginDTO, HttpServletRequest request);
}
