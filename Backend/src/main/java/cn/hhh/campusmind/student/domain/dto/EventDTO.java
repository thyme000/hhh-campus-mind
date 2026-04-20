package cn.hhh.campusmind.student.domain.dto;

import lombok.Data;

@Data
public class EventDTO {

    /** 事件类型 */
    private String eventType;

    /** 关联业务id */
    private Long refId;

    /** 课程id */
    private Long courseId;
}
