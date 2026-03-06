package com.giosmaju.todolistapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public String home() {
        return "Microservicio corriendo en Docker";
    }

    @GetMapping("/saludo")
    public String saludo() {
        return "Hola desde Spring Boot en Docker";
    }

    @GetMapping("/todos")
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    @PostMapping("/todos")
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }
}
