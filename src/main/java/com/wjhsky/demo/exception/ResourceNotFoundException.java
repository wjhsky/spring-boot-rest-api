package com.wjhsky.demo.exception;

/**
 * 资源未找到异常
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 根据资源名称和 ID 构造异常
     *
     * @param resourceName 资源名称
     * @param id           资源 ID
     */
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s not found with id: %s", resourceName, id));
    }
}
