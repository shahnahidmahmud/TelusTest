package com.telus.todo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telus.todo.controller.TodoController;
import com.telus.todo.exception.GlobalExceptionHandler;
import com.telus.todo.model.CompletionStatus;
import com.telus.todo.model.Todo;
import com.telus.todo.service.TodoService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
public class TodoControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @MockBean
    private TodoService todoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(new TodoController(todoService)).build();
    }

    @Test
    public void getAllTodosTest() throws Exception {
        when(todoService.findAllTodos()).thenReturn(Arrays.asList(new Todo(1L, "Task", CompletionStatus.PENDING)));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Task"));
    }

    @Test
    public void getTodoByIdTest_Found() throws Exception {
        Long id = 1L;
        when(todoService.findTodoById(id)).thenReturn(Optional.of(new Todo(id, "Task", CompletionStatus.PENDING)));

        mockMvc.perform(get("/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Task"));
    }



    @Test
    public void createTodoTest() throws Exception {
        Todo newTodo = new Todo(null, "New Task", CompletionStatus.PENDING);
        Todo savedTodo = new Todo(1L, "New Task", CompletionStatus.PENDING);
        when(todoService.saveTodo(any(Todo.class))).thenReturn(savedTodo);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("New Task"));
    }



    @Test
    public void updateTodoTest_Found() throws Exception {
        Long id = 1L;
        Todo existingTodo = new Todo(id, "Existing Task", CompletionStatus.PENDING);
        Todo updatedTodo = new Todo(id, "Updated Task", CompletionStatus.COMPLETED);
        when(todoService.findTodoById(id)).thenReturn(Optional.of(existingTodo));
        when(todoService.update(any(Todo.class))).thenReturn(updatedTodo);

        mockMvc.perform(patch("/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Task"));
    }


    @Test
    public void deleteTodoTest_Found() throws Exception {
        Long id = 1L;
        when(todoService.findTodoById(id)).thenReturn(Optional.of(new Todo(id, "Task to delete", CompletionStatus.PENDING)));

        mockMvc.perform(delete("/todos/{id}", id))
                .andExpect(status().isOk());
    }
}
