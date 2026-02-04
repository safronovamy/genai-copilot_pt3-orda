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
// Implement 5 more integration tests using MockMvc:
// - page=2&limit=10 -> items length 10 and page=2
// - page=5&limit=10 -> items length 10 and page=5
// - page=0 -> 400 and ApiError has status/message
// - limit=101 -> 400 and ApiError has status/message
// - missing page/limit -> should use defaults (page=1, limit=10) and return 10 items

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
    void page0_invalid_returns400_andApiError() throws Exception {
        mockMvc.perform(get("/orders").param("page", "0").param("limit", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void limit101_invalid_returns400_andApiError() throws Exception {
        mockMvc.perform(get("/orders").param("page", "1").param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
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


    @Test
    void limit1_minBoundary_page1_returns1Item_andMetadata() throws Exception {
        mockMvc.perform(get("/orders").param("page", "1").param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(1))
                .andExpect(jsonPath("$.totalItems").value(greaterThanOrEqualTo(50)))
                // with limit=1 and 50 seeded items, totalPages should be >= 50
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(50)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));
    }


    @Test
    void limit100_maxBoundary_page1_returnsNotMoreThan100Items() throws Exception {
        mockMvc.perform(get("/orders").param("page", "1").param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(100))
                .andExpect(jsonPath("$.totalItems").value(greaterThanOrEqualTo(50)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(100)));
    }

}
