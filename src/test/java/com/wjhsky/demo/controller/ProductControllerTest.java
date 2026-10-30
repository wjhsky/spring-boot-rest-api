package com.wjhsky.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjhsky.demo.dto.ProductDTO;
import com.wjhsky.demo.model.Product;
import com.wjhsky.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ProductController 集成测试 (MockMvc + SpringBootTest)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        Product product = Product.builder()
                .name("iPhone 15")
                .description("Apple smartphone")
                .price(new BigDecimal("7999.00"))
                .stock(100)
                .build();
        existingId = productRepository.save(product).getId();
    }

    private ProductDTO validDTO() {
        return ProductDTO.builder()
                .name("MacBook Pro")
                .description("Apple laptop")
                .price(new BigDecimal("14999.00"))
                .stock(20)
                .build();
    }

    @Nested
    @DisplayName("GET /api/products")
    class GetProducts {

        @Test
        @DisplayName("默认分页应返回200和列表")
        void shouldReturnList() throws Exception {
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name", is("iPhone 15")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }

        @Test
        @DisplayName("自定义分页参数应生效")
        void shouldRespectPagingParams() throws Exception {
            mockMvc.perform(get("/api/products").param("page", "0").param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size", is(5)));
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProductById {

        @Test
        @DisplayName("存在的ID应返回商品")
        void shouldReturnProduct() throws Exception {
            mockMvc.perform(get("/api/products/{id}", existingId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(existingId.intValue())))
                    .andExpect(jsonPath("$.name", is("iPhone 15")));
        }

        @Test
        @DisplayName("不存在的ID应返回404")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/products/{id}", 99999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("POST /api/products")
    class CreateProduct {

        @Test
        @DisplayName("合法请求应创建并返回201")
        void shouldCreateProduct() throws Exception {
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDTO())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name", is("MacBook Pro")));
        }

        @Test
        @DisplayName("name为空应返回400")
        void shouldReturn400WhenNameBlank() throws Exception {
            ProductDTO dto = validDTO();
            dto.setName("");
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)));
        }

        @Test
        @DisplayName("price为空应返回400")
        void shouldReturn400WhenPriceNull() throws Exception {
            ProductDTO dto = validDTO();
            dto.setPrice(null);
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("stock为负数应返回400")
        void shouldReturn400WhenStockNegative() throws Exception {
            ProductDTO dto = validDTO();
            dto.setStock(-1);
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("name超长应返回400")
        void shouldReturn400WhenNameTooLong() throws Exception {
            ProductDTO dto = validDTO();
            dto.setName("a".repeat(101));
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class UpdateProduct {

        @Test
        @DisplayName("存在的ID应更新并返回200")
        void shouldUpdateProduct() throws Exception {
            ProductDTO dto = validDTO();
            mockMvc.perform(put("/api/products/{id}", existingId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("MacBook Pro")));
        }

        @Test
        @DisplayName("不存在的ID应返回404")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(put("/api/products/{id}", 99999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDTO())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class DeleteProduct {

        @Test
        @DisplayName("存在的ID应删除并返回204")
        void shouldDeleteProduct() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", existingId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("不存在的ID应返回404")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", 99999))
                    .andExpect(status().isNotFound());
        }
    }
}
