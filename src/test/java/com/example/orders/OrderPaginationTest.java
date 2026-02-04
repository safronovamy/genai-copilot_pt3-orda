package com.example.orders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@SpringBootTest
@AutoConfigureMockMvc

class OrderPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void page1_limit10_returns10Items_andTotals() throws Exception {
        mockMvc.perform(get("/orders").param("page","1").param("limit","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.totalItems").value(greaterThanOrEqualTo(50)))
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(10));

    }

    @Test
    void page2_limit10_returns10Items_andPage2() throws Exception {
        mockMvc.perform(get("/orders").param("page","2").param("limit","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(10));
    }

    @Test
    void page5_limit10_returns10Items_andPage5() throws Exception {
        mockMvc.perform(get("/orders").param("page", "5").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(5))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(10));
    }


    @Test
    void missingPageAndLimit_usesDefaults_returns10Items_andPage1() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.totalItems").value(greaterThanOrEqualTo(50)))
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(10));
    }
}
