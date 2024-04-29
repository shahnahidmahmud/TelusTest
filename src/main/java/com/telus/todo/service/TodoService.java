package com.telus.todo.service;


import com.telus.todo.model.Todo;
import com.telus.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> findAllTodos() {
        return todoRepository.findAll();
    }

    public Optional<Todo> findTodoById(Long id) {
        return Optional.ofNullable(todoRepository.findById(id));
    }

    @Transactional
    public synchronized Todo saveTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    public Todo update(Todo todo) {
         todoRepository.save(todo.getId() ,todo);
         return todo;
    }
}
