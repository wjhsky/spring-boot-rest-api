package com.wjhsky.demo.service;

import com.wjhsky.demo.dto.PageResponse;
import com.wjhsky.demo.dto.ProductDTO;
import com.wjhsky.demo.exception.ResourceNotFoundException;
import com.wjhsky.demo.model.Product;
import com.wjhsky.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProductService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(new BigDecimal("7999.00"))
                .stock(100)
                .build();
    }

    private ProductDTO sampleDTO() {
        return ProductDTO.builder()
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(new BigDecimal("7999.00"))
                .stock(100)
                .build();
    }

    @Nested
    @DisplayName("分页查询 getProducts")
    class GetProducts {

        @Test
        @DisplayName("正常分页查询应返回分页响应")
        void shouldReturnPageResponse() {
            List<Product> products = Arrays.asList(sampleProduct,
                    Product.builder().id(2L).name("MacBook").price(BigDecimal.TEN).stock(5).build());
            Page<Product> page = new PageImpl<>(products, PageRequest.of(0, 10), 2);
            when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);

            PageResponse<ProductDTO> response = productService.getProducts(0, 10);

            assertThat(response).isNotNull();
            assertThat(response.getPage()).isZero();
            assertThat(response.getSize()).isEqualTo(10);
            assertThat(response.getTotalElements()).isEqualTo(2);
            assertThat(response.getContent()).hasSize(2);
            assertThat(response.getContent().get(0).getName()).isEqualTo("iPhone 15");
            verify(productRepository, times(1)).findAll(any(PageRequest.class));
        }

        @Test
        @DisplayName("空结果集应返回空分页响应")
        void shouldReturnEmptyPage() {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
            when(productRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

            PageResponse<ProductDTO> response = productService.getProducts(0, 10);

            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("负页码应抛出非法参数异常")
        void shouldThrowWhenPageNegative() {
            assertThatThrownBy(() -> productService.getProducts(-1, 10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("页码");
        }

        @Test
        @DisplayName("非正每页大小应抛出非法参数异常")
        void shouldThrowWhenSizeNotPositive() {
            assertThatThrownBy(() -> productService.getProducts(0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("每页大小");
        }
    }

    @Nested
    @DisplayName("查询详情 getProductById")
    class GetProductById {

        @Test
        @DisplayName("存在时应返回商品")
        void shouldReturnProductWhenExists() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            ProductDTO result = productService.getProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("iPhone 15");
        }

        @Test
        @DisplayName("不存在时应抛出 ResourceNotFoundException")
        void shouldThrowWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("创建商品 createProduct")
    class CreateProduct {

        @Test
        @DisplayName("应保存并返回商品")
        void shouldSaveAndReturnProduct() {
            ProductDTO dto = sampleDTO();
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

            ProductDTO result = productService.createProduct(dto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("iPhone 15");
            verify(productRepository, times(1)).save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("更新商品 updateProduct")
    class UpdateProduct {

        @Test
        @DisplayName("存在时应更新并返回")
        void shouldUpdateWhenExists() {
            ProductDTO dto = ProductDTO.builder()
                    .name("iPhone 16")
                    .description("new")
                    .price(new BigDecimal("8999"))
                    .stock(50)
                    .build();
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductDTO result = productService.updateProduct(1L, dto);

            assertThat(result.getName()).isEqualTo("iPhone 16");
            assertThat(result.getPrice()).isEqualByComparingTo("8999");
            assertThat(result.getStock()).isEqualTo(50);
        }

        @Test
        @DisplayName("不存在时应抛出异常")
        void shouldThrowWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(99L, sampleDTO()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("删除商品 deleteProduct")
    class DeleteProduct {

        @Test
        @DisplayName("存在时应删除")
        void shouldDeleteWhenExists() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

            productService.deleteProduct(1L);

            verify(productRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("不存在时应抛出异常且不执行删除")
        void shouldThrowWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deleteProduct(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(productRepository, never()).deleteById(any());
        }
    }
}
