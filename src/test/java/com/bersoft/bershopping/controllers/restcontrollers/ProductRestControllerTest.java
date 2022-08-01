package com.bersoft.bershopping.controllers.restcontrollers;

import com.bersoft.bershopping.persistence.entities.product.Product;
import com.bersoft.bershopping.services.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IProductService productService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Tests on '/api/v1/bershopping/products' endpoint METHOD:GET")
    class TestGetProducts {

        @Test
        @DisplayName("Test 'getProducts', expected -> 200 response status & non empty list of products ")
        void testGetProducts() throws Exception {
            // Given
            List<Product> userList = Arrays.asList(new Product(), new Product());
            when(productService.findAll()).thenReturn(userList);

            //when
            mvc.perform(get("/api/v1/bershopping/products").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(content().json(objectMapper.writeValueAsString(userList)))
                    .andDo(print());

            verify(productService).findAll();
        }

        @Test
        @DisplayName("Test 'getProducts' when products does not exist, expected -> 200 response status empty list of products ")
        void testGetProductWhenProductsDoestExist() throws Exception {
            // Given
            when(productService.findAll()).thenReturn(new ArrayList<>());

            //when
            mvc.perform(get("/api/v1/bershopping/products").contentType(MediaType.APPLICATION_JSON))

                    //then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)))
                    .andDo(print());

            verify(productService).findAll();
        }

    }


}