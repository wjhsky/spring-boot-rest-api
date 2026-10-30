package com.wjhsky.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一错误响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** 时间戳 */
    private LocalDateTime timestamp;

    /** HTTP 状态码 */
    private int status;

    /** 错误描述 */
    private String error;

    /** 错误信息 */
    private String message;

    /** 请求路径 */
    private String path;
}
