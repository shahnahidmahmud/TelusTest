package com.telus.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.telus.todo.service.TodoService;

@SpringBootApplication
public class TodoServiceApplication   {
    private final TodoService todoService;

    public TodoServiceApplication(TodoService todoService) {
        this.todoService = todoService;
    }
    public static void main(String[] args) {

    SpringApplication.run(TodoServiceApplication.class, args);
    }


}
