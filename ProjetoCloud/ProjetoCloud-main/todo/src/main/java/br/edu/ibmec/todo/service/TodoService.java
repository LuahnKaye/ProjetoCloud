package br.edu.ibmec.todo.service;

import org.springframework.stereotype.Service;
import br.edu.ibmec.todo.model.Todo;
import jakarta.transaction.SystemException;

import java.util.ArrayList;

import java.util.List;


@Service
public class TodoService {
    private static List<Todo> Todos = new ArrayList<>();

    public List<Todo> getAllItems() {
        return TodoService.Todos;
    }

    public Todo getItem(int id) {
        return findTodo(id);
    }

    public Todo createTodo(Todo item) throws Exception {

        String owner = item.getOwner();
        int numTask = 0;
        for (Todo todo : Todos) {
            if (todo.getOwner() == owner) {
                numTask ++;
            }
        }

        if (numTask >= 5) {
            throw new Exception("Numero de tarefas excedidas para o responsavel");
        }

        TodoService.Todos.add(item);
        return item;
    }

    public Todo updateTodo(int id, Todo newTodo) {
        Todo todoASerAtualizado = findTodo(id);

        if (todoASerAtualizado == null)
            return null;


        //Atualiza o item
        Todos.remove(todoASerAtualizado);
    
        todoASerAtualizado.setName(newTodo.getName());
        todoASerAtualizado.setOwner(newTodo.getOwner());
        todoASerAtualizado.setStatus(newTodo.getStatus());
        todoASerAtualizado.setDescription(newTodo.getDescription());
        
        Todos.add(todoASerAtualizado);

        return todoASerAtualizado;

    }

    public void deleteTodo(int id) {
        Todo todoASerExcluido = findTodo(id);

        if (todoASerExcluido == null)
            return;

        //Remove da lista
        Todos.remove(todoASerExcluido);
    }

    private Todo findTodo(int id) {
        Todo response = null;

        for (Todo todo : Todos) {
            if (todo.getId() == id) {
                response = todo;
                break;
            }
        }
        return response;
    }
    
}
