package com.telus.todoservice.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.telus.todo.model.CompletionStatus;
import com.telus.todo.model.Todo;
import com.telus.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class TodoRepositoryTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TodoRepository todoRepository;

    @BeforeEach
    void setup() {
        // Initialize TodoRepository with mocked JdbcTemplate
    }

    @Test
    void testSave() {
        // Arrange
        Todo todo = new Todo(null, "Test Todo", CompletionStatus.PENDING);
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Map.of("id", 1));  // Simulate the generation of an ID
            return 1;  // Simulate one row updated
        });

        // Act
        Todo savedTodo = todoRepository.save(todo);

        // Assert
        verify(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
        assertNotNull(savedTodo.getId(), "ID should be set on the saved Todo");
        assertEquals(1L, savedTodo.getId().longValue(), "The saved Todo ID should be 1");
    }
    @Test
    void testFindAll() {
        when(jdbcTemplate.query(
                eq("SELECT id, description, completion_status FROM todos"),
                any(RowMapper.class))
        ).thenReturn(Arrays.asList(new Todo(1L, "Existing Todo", CompletionStatus.PENDING)));

        List<Todo> todos = todoRepository.findAll();

        assertFalse(todos.isEmpty());
        assertEquals(1, todos.size());
        assertEquals("Existing Todo", todos.get(0).getDescription());
    }

    @Test
    void testFindById() {
        when(jdbcTemplate.query(
                eq("SELECT id, description, completion_status FROM todos WHERE id = ?"),
                any(Object[].class),
                any(RowMapper.class))
        ).thenReturn(Arrays.asList(new Todo(1L, "Existing Todo", CompletionStatus.PENDING)));

        Todo foundTodo = todoRepository.findById(1L);

        assertNotNull(foundTodo);
        assertEquals("Existing Todo", foundTodo.getDescription());
    }

    @Test
    void testSaveExistingTodo() {
        Todo todo = new Todo(1L, "Updated Todo", CompletionStatus.COMPLETED);
        when(jdbcTemplate.update(
                anyString(),
                any(),
                any(),
                anyLong())
        ).thenReturn(1);

        int updateCount = todoRepository.save(1L, todo);

        assertEquals(1, updateCount);
        verify(jdbcTemplate).update(
                eq("UPDATE todos SET description = ?, completion_status = ? WHERE id = ?"),
                eq("Updated Todo"), eq("COMPLETED"), eq(1L)
        );
    }

    @Test
    void testDeleteById() {
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);

        int deleteCount = todoRepository.deleteById(1L);

        assertEquals(1, deleteCount);
        verify(jdbcTemplate).update(
                eq("DELETE FROM todos WHERE id = ?"),
                eq(1L)
        );
    }
}
