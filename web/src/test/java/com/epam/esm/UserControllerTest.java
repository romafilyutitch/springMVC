package com.epam.esm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersistanceConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql","classpath:data.sql"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showUsers_shouldReturnUsersOnFistPage() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.userList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.userList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.userList[0].name", is("user")))
                .andExpect(jsonPath("$._embedded.userList[0].surname", is("test")))
                .andExpect(jsonPath("$._embedded.userList[0]._links.user.href", is("http://localhost/users/1")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/users?offset=0&limit=10")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/users?offset=10&limit=10")))
                .andExpect(jsonPath("$._links.previous.href", is("http://localhost/users?offset=-10&limit=10")));
    }

    @Test
    public void showUser_shouldReturnUserThatHasPassedId() throws Exception {
        mockMvc.perform(get("/users/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user")))
                .andExpect(jsonPath("$.surname", is("test")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/users/1")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/users/1/orders?offset=0&limit=10")));
    }

    @Test
    public void showUser_shouldThrowExceptionIfThereIsNoUserWithId() throws Exception {
        mockMvc.perform(get("/users/{id}", 100))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.errorCode", is("40401")))
                .andExpect(jsonPath("$.message", is("User with id 100 not found")));
    }

    @Test
    public void showUserOrders_shouldReturnUserOrder() throws Exception {
        mockMvc.perform(get("/users/{userId}/orders", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.orderList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.orderList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.orderList[0].cost", is(200.5)))
                .andExpect(jsonPath("$._embedded.orderList[0]._links.order.href", is("http://localhost/users/1/orders/1")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/users/1/orders?offset=0&limit=10")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/users/1/orders?offset=10&limit=10")))
                .andExpect(jsonPath("$._links.previous.href", is("http://localhost/users/1/orders?offset=-10&limit=10")));
    }

    @Test
    public void showSuersOrder_shouldReturnUserOrder() throws Exception {
        mockMvc.perform(get("/users/{userId}/orders/{id}", 1, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cost", is(200.50)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/users/1/orders/1")))
                .andExpect(jsonPath("$._links.user.href", is("http://localhost/users/1")))
                .andExpect(jsonPath("$._links.certificate.href", is("http://localhost/certificates/1")));
    }
}