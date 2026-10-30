package com.wjhsky.demo.service;

import com.wjhsky.demo.dto.ProductDTO;
import com.wjhsky.demo.dto.PageResponse;
import com.wjhsky.demo.exception.ResourceNotFoundException;
import com.wjhsky.demo.model.Product;
import com.wjhsky.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品业务逻辑层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String RESOURCE_NAME = "Product";

    private final ProductRepository productRepository;

    /**
     * 分页查询商品列表
     *
     * @param page 页码 (从0开始)
     * @param size 每页大小
     * @return 分页响应
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> getProducts(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("页码不能为负数");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("每页大小必须大于0");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ProductDTO> pageResult = productRepository.findAll(pageable).map(ProductDTO::fromEntity);
        return PageResponse.of(pageResult);
    }

    /**
     * 根据 ID 查询商品详情
     *
     * @param id 商品 ID
     * @return 商品 DTO
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = findProductById(id);
        return ProductDTO.fromEntity(product);
    }

    /**
     * 创建商品
     *
     * @param dto 商品 DTO
     * @return 创建后的商品 DTO
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = dto.toEntity();
        Product saved = productRepository.save(product);
        log.info("创建商品成功, id={}", saved.getId());
        return ProductDTO.fromEntity(saved);
    }

    /**
     * 更新商品
     *
     * @param id  商品 ID
     * @param dto 商品 DTO
     * @return 更新后的商品 DTO
     */
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product existing = findProductById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        Product updated = productRepository.save(existing);
        log.info("更新商品成功, id={}", updated.getId());
        return ProductDTO.fromEntity(updated);
    }

    /**
     * 删除商品
     *
     * @param id 商品 ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        findProductById(id);
        productRepository.deleteById(id);
        log.info("删除商品成功, id={}", id);
    }

    /**
     * 根据 ID 查询实体，不存在则抛出异常
     */
    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}
