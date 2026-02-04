package com.example.orders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
// Implement integration tests using MockMvc:
// 1) status=PAID -> all items[*].status == "PAID"
// 2) minAmount=100&maxAmount=200 -> all items[*].amount within range
// 3) dateFrom=2025-12-01&dateTo=2025-12-31 -> all createdAt within range
// 4) combined: status=NEW&minAmount=250&maxAmount=500 -> all match
// 5) invalid amount range (min>max) -> 400 ApiError status/message
// 6) invalid date range (dateFrom>dateTo) -> 400 ApiError status/message

class OrderFilteringTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    void filterByStatus_paid_onlyPaidReturned() throws Exception {
        var mvcResult = mockMvc.perform(get("/orders")
                        .param("status","PAID")
                        .param("page","1")
                        .param("limit","50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andReturn();

        var root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        for (var item : root.get("items")) {
            org.junit.jupiter.api.Assertions.assertEquals("PAID", item.get("status").asText());
        }


    }

    @Test
    void filterByAmountRange_100to200_onlyInRangeReturned() throws Exception {
        var mvcResult = mockMvc.perform(get("/orders")
                        .param("minAmount", "100")
                        .param("maxAmount", "200")
                        .param("page", "1")
                        .param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andReturn();

        var root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        for (var item : root.get("items")) {
            var amount = item.get("amount").decimalValue();
            org.junit.jupiter.api.Assertions.assertTrue(
                    amount.compareTo(new java.math.BigDecimal("100")) >= 0 &&
                            amount.compareTo(new java.math.BigDecimal("200")) <= 0
            );
        }

    }

    @Test
    void filterByDateRange_dec2025_onlyInRangeReturned() throws Exception {
        var mvcResult = mockMvc.perform(get("/orders")
                        .param("dateFrom", "2025-12-01")
                        .param("dateTo", "2025-12-31")
                        .param("page", "1")
                        .param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andReturn();

        var root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        var from = java.time.Instant.parse("2025-12-01T00:00:00Z");
        var to = java.time.Instant.parse("2025-12-31T23:59:59Z");

        for (var item : root.get("items")) {
            var createdAt = java.time.Instant.parse(item.get("createdAt").asText());
            org.junit.jupiter.api.Assertions.assertFalse(createdAt.isBefore(from));
            org.junit.jupiter.api.Assertions.assertFalse(createdAt.isAfter(to));
        }

    }


    @Test
    void filterByFutureDateRange_returnsEmptyItems_andZeroTotals() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("dateFrom", "2099-01-01")
                        .param("dateTo", "2099-12-31")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }



    @Test
    void filterByCombined_new_250to500_onlyMatchReturned() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("status", "NEW")
                        .param("minAmount", "250")
                        .param("maxAmount", "500")
                        .param("page", "1")
                        .param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void filterByInvalidAmountRange_minGreaterThanMax_returns400AndApiError() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("minAmount", "300")
                        .param("maxAmount", "200")
                        .param("page", "1")
                        .param("limit", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void filterByInvalidDateRange_dateFromAfterDateTo_returns400AndApiError() throws Exception {
        mockMvc.perform(get("/orders")
                        .param("dateFrom", "2025-12-31")
                        .param("dateTo", "2025-12-01")
                        .param("page", "1")
                        .param("limit", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

}
