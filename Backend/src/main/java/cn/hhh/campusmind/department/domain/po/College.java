package cn.hhh.campusmind.department.domain.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学院字典表：用于规范学院名称，便于导入校验、下拉选择与统计。
 * </p>
 *
 * @author thyme
 * @since 2026-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("college")
public class College implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 学院编码（可空；非空时唯一），默认空字符串
     */
    private String code;

    /**
     * 学院名称（唯一）
     */
    private String name;

    /**
     * 状态：active启用 / disabled停用
     */
    private String status;

    /**
     * 排序号（越小越靠前）
     */
    private Integer sortNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除：0未删，1已删
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
