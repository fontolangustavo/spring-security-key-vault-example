package com.fontolan.spring.securitykeyvault.example.adapters.web;

import com.fontolan.spring.securitykeyvault.example.application.service.ProductService;
import com.fontolan.spring.securitykeyvault.example.application.service.UserDeviceService;
import com.fontolan.spring.securitykeyvault.example.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    ProductService productService;
    @MockBean
    UserDeviceService userDeviceService;

    @Test
    @WithMockUser(roles = "USER")
    void getAllAuthorizedWhenTrusted() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(new Product()));
        when(userDeviceService.isTrustedDevice(any(), any())).thenReturn(true);
        when(productService.getAll(any(PageRequest.class))).thenReturn(page);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllDeniedWhenNotTrusted() throws Exception {
        when(userDeviceService.isTrustedDevice(any(), any())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
