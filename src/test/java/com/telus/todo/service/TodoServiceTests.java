package com.telus.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.telus.todo.model.CompletionStatus;
import com.telus.todo.model.Todo;
import com.telus.todo.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTests {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        // Set up is handled by Mockito's annotations
    }

    @Test
    void testFindAllTodos() {
        Todo todo = new Todo(1L, "Task", CompletionStatus.PENDING);
        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo));

        List<Todo> todos = todoService.findAllTodos();

        assertFalse(todos.isEmpty(), "The returned list should not be empty");
        assertEquals(1, todos.size(), "The returned list should contain one todo");
        assertEquals(todo, todos.get(0), "The returned todo should match the mock");
    }

    @Test
    void testFindTodoById() {
        Long id = 1L;
        Todo todo = new Todo(id, "Task", CompletionStatus.PENDING);
        when(todoRepository.findById(id)).thenReturn(todo);

        Optional<Todo> foundTodo = todoService.findTodoById(id);

        assertTrue(foundTodo.isPresent(), "A Todo should be found with the given ID");
        assertEquals(todo, foundTodo.get(), "The found Todo should match the mock");
    }

    @Test
    void testSaveTodo() {
        Todo todo = new Todo(null, "New Task", CompletionStatus.PENDING);
        when(todoRepository.save(todo)).thenReturn(new Todo(1L, "New Task", CompletionStatus.PENDING));

        Todo savedTodo = todoService.saveTodo(todo);

        assertNotNull(savedTodo.getId(), "The saved Todo should have a non-null ID");
        assertEquals("New Task", savedTodo.getDescription(), "The description of the saved Todo should match");
    }

    @Test
    void testUpdateTodo() {
        Todo originalTodo = new Todo(1L, "Original Description", CompletionStatus.PENDING);
        Todo updatedTodo = new Todo(1L, "Updated Description", CompletionStatus.COMPLETED);
        when(todoRepository.save(originalTodo.getId(), updatedTodo)).thenReturn(1);

        Todo result = todoService.update(updatedTodo);

        assertEquals("Updated Description", result.getDescription(), "The description should be updated");
        assertEquals(CompletionStatus.COMPLETED, result.getCompletionStatus(), "The status should be updated");
    }

    @Test
    void testDeleteTodo() {
        Long id = 1L;
        when(todoRepository.deleteById(any())).thenReturn(0);

        todoService.deleteTodo(id);

        verify(todoRepository).deleteById(id);
    }
}
