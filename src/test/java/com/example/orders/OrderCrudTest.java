package com.example.orders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderCrudTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void getById_shouldReturn200_whenExists() throws Exception {
        long id = createOrderAndReturnId("CRUD GetById", new BigDecimal("111.11"), "NEW");

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.customerName").value("CRUD GetById"))
                .andExpect(jsonPath("$.amount").value(111.11))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void getById_shouldReturn404_whenMissing() throws Exception {
        mockMvc.perform(get("/orders/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Order not found")))
                .andExpect(jsonPath("$.path").value("/orders/999999"));
    }

    @Test
    void update_shouldUpdateStatusOnly_whenOnlyStatusProvided() throws Exception {
        long id = createOrderAndReturnId("CRUD UpdateStatusOnly", new BigDecimal("222.22"), "NEW");

        Map<String, Object> updateBody = Map.of("status", "PAID");

        mockMvc.perform(put("/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.customerName").value("CRUD UpdateStatusOnly"))
                .andExpect(jsonPath("$.amount").value(222.22))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void update_shouldUpdateAllFields_whenAllProvided() throws Exception {
        long id = createOrderAndReturnId("CRUD UpdateAll", new BigDecimal("333.33"), "NEW");

        Map<String, Object> updateBody = Map.of(
                "customerName", "CRUD UpdateAll Changed",
                "amount", 999.99,
                "status", "CANCELLED"
        );

        mockMvc.perform(put("/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.customerName").value("CRUD UpdateAll Changed"))
                .andExpect(jsonPath("$.amount").value(999.99))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void update_shouldReturn404_whenMissing() throws Exception {
        Map<String, Object> updateBody = Map.of("status", "PAID");

        mockMvc.perform(put("/orders/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/orders/999999"));
    }

    @Test
    void delete_shouldReturn204_andThen404_onGet() throws Exception {
        long id = createOrderAndReturnId("CRUD Delete", new BigDecimal("444.44"), "NEW");

        mockMvc.perform(delete("/orders/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn404_whenMissing() throws Exception {
        mockMvc.perform(delete("/orders/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/orders/999999"));
    }

    private long createOrderAndReturnId(String customerName, BigDecimal amount, String status) throws Exception {
        Map<String, Object> body = Map.of(
                "customerName", customerName,
                "amount", amount,
                "status", status
        );

        String responseJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(responseJson);
        return root.get("id").asLong();
    }
}
