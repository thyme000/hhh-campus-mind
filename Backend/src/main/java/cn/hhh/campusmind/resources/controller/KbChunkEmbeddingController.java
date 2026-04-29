package cn.hhh.campusmind.resources.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 知识库切分块向量表（pgvector） 前端控制器
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@RestController
@RequestMapping("/chunk/embedding")
@Tag(name = "知识库模块")
@RequiredArgsConstructor
public class KbChunkEmbeddingController {

}
