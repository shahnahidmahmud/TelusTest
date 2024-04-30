package com.telus.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telus.todo.model.ApiError;
import com.telus.todo.model.CompletionStatus;
import com.telus.todo.model.Todo;
import com.telus.todo.service.TodoService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Use if you have a specific profile for test configurations
public class TodoControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Long createdTodoId;
    @SpyBean
    private TodoService todoService;

    @BeforeEach
    public void setup() throws Exception {
        // Create a todo
        MvcResult result = mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Task for deletion\", \"completionStatus\": \"PENDING\"}"))
                .andExpect(status().isOk()).andReturn();

        createdTodoId = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class).getId();
    }

    @AfterEach
    public void cleanup() throws Exception {
        // Delete the created todo
        mockMvc.perform(MockMvcRequestBuilders.delete("/todos/" + createdTodoId));
    }

    @Test
    public void testGetAllTodos() throws Exception {
        mockMvc.perform(get("/todos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }
    @Test
    public void testGlobalExceptionHandler() throws Exception {
        // Arrange
        String invalidBodyContent = "{invalid_json}"; // This will trigger an HttpMessageNotReadableException

        // Act and Assert
        MvcResult mvcResult = mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBodyContent))
                .andExpect(status().isInternalServerError())
                .andReturn();

        // Parse and check the returned ApiError
        ApiError error = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiError.class);
        assertEquals(error.getMessage(),"JSON parse error: Unexpected character ('i' (code 105)): was expecting double-quote to start field name");
    }

    @Test
    public void testGetTodoById_Found() throws Exception {
        // Assuming data.sql or a setup method has inserted this record
        mockMvc.perform(get("/todos/"+createdTodoId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(createdTodoId));
    }

    @Test
    public void testGetTodoById_NotFound() throws Exception {
        mockMvc.perform(get("/todos/999"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateTodo() throws Exception {
        Todo newTodo = new Todo(null, "Integration Test Todo", CompletionStatus.PENDING);
        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.description").value("Integration Test Todo"));
    }

    @Test
    public void testUpdateTodo_Found() throws Exception {
        Todo updatedTodo = new Todo(1L, "Updated Todo", CompletionStatus.COMPLETED);
        mockMvc.perform(patch("/todos/"+createdTodoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTodo)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.description").value("Updated Todo"));
    }

    @Test
    public void testUpdateTodo_NotFound() throws Exception {
        Todo updatedTodo = new Todo(999L, "Nonexistent Todo", CompletionStatus.COMPLETED);
        mockMvc.perform(patch("/todos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTodo)))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTodo_Found() throws Exception {
        // Ensure there is a Todo to delete
        mockMvc.perform(delete("/todos/"+createdTodoId))
               .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTodo_NotFound() throws Exception {
        mockMvc.perform(delete("/todos/999"))
               .andExpect(status().isNotFound());
    }
}
