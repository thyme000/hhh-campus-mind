package cn.hhh.campusmind.resources.domain.po;

import cn.hhh.campusmind.common.handler.JsonbTypeHandler;
import cn.hhh.campusmind.resources.domain.enums.TaskStatusEnum;
import cn.hhh.campusmind.resources.domain.enums.TaskStepEnum;
import cn.hhh.campusmind.resources.domain.enums.TaskTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

/**
 * <p>
 * 知识库文档索引任务表（解析/切分/向量化异步任务）
 * </p>
 *
 * @author thyme
 * @since 2026-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("kb_index_task")
public class KbIndexTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 业务任务编号（唯一）
     */
    private String taskNo;

    /**
     * 关联文档ID（kb_document.id）
     */
    private Long docId;

    /**
     * 任务发起教师ID
     */
    private Long teacherId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 任务类型，默认 DOCUMENT_INDEX
     */
    private TaskTypeEnum taskType;

    /**
     * 任务状态：CREATED/QUEUED/RUNNING/SUCCESS/FAILED/CANCELLED
     */
    private TaskStatusEnum status;

    /**
     * 当前步骤：INIT/PARSE/CHUNK/EMBED/UPSERT/DONE
     */
    private TaskStepEnum currentStep;

    /**
     * 已重试次数
     */
    private Integer retryCount;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 任务开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 任务结束时间
     */
    private LocalDateTime finishedAt;

    /**
     * 任务总耗时毫秒
     */
    private Long costMs;

    /**
     * 任务结果JSON，如 chunkCount/vectorCount
     */
    @TableField(typeHandler = JsonbTypeHandler.class, jdbcType = JdbcType.OTHER)
    private String resultJson;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误详情
     */
    private String errorMessage;

    /**
     * 逻辑删除标记：0否 1是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
