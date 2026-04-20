package cn.hhh.campusmind.admin.service;

import cn.hhh.campusmind.admin.domain.dto.UserDTO;
import cn.hhh.campusmind.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

public interface IAdminService {
    Result<String> importStudents(MultipartFile file);

    Result<String> importTeachers(MultipartFile file);

    Result<String> importStudent(UserDTO userDTO);

    Result<String> importTeacher(UserDTO userDTO);
}
