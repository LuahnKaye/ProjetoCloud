package br.edu.ibmec.todo.controller;


import br.edu.ibmec.todo.model.Todo;
import br.edu.ibmec.todo.service.TodoService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {
    
    @Autowired
    private TodoService service;

    @GetMapping
    public ResponseEntity<List<Todo>> getTodo() {
        return new ResponseEntity(service.getAllItems(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable("id") int id) {
        Todo response = service.getItem(id);

        if (response == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @PostMapping
    public ResponseEntity<Todo> saveTodo(@Valid @RequestBody Todo todo) throws Exception {
        Todo response = service.createTodo(todo);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable("id") int id, @Valid @RequestBody Todo novosDados) {

        Todo todoASerAtualizado = service.getItem(id);

        if (todoASerAtualizado == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        Todo response = service.updateTodo(id, todoASerAtualizado);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Todo> deleteTodo(@PathVariable("id") int id) {
        
        if (service.getItem(id) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        service.deleteTodo(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
