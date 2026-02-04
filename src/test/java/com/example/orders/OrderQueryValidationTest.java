package com.example.orders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderQueryValidationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void pageZero_shouldReturn400() throws Exception {
        mockMvc.perform(get("/orders").param("page", "0").param("limit", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void limitZero_shouldReturn400() throws Exception {
        mockMvc.perform(get("/orders").param("page", "1").param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void limitTooLarge_shouldReturn400() throws Exception {
        mockMvc.perform(get("/orders").param("page", "1").param("limit", "101"))
                .andExpect(status().isBadRequest());
    }

}
