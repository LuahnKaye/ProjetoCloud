package br.edu.ibmec.todo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.edu.ibmec.todo.model.Todo;
import nonapi.io.github.classgraph.utils.Assert;

@SpringBootTest
public class TodoServiceTest {

    @Autowired
    private TodoService service;

    @Test
    public void should_create_todo() throws Exception {

        // Arrange
        Todo item = new Todo();
        item.setName("Nova Tarefa");
        item.setOwner("Dummy User");
        item.setDescription("Tarefa de teste");
        item.setEmail("teste@teste.com");
        item.setId(1);
        item.setStatus("Doing");

        // Act
        Todo result = service.createTodo(item);

        // Assertion
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getId() == 1);
        Assertions.assertEquals(result.getOwner(), item.getOwner());

    }

    @Test
    public void should_get_item() throws Exception {
        // Arrange
        Todo item = new Todo();
        item.setName("Nova Tarefa");
        item.setOwner("Dummy User");
        item.setDescription("Tarefa de teste");
        item.setEmail("teste@teste.com");
        item.setId(5);
        item.setStatus("Doing");
        service.createTodo(item);

        // Act
        Todo response = service.getItem(5);

        // Assertion
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getId() == 5);
        Assertions.assertEquals(response.getOwner(), item.getOwner());

    }

    @Test
    public void should_create_item_if_owner_has_less_five_tasks() throws Exception {

        // Arrange
        // Criar tarefas 
        for (int i = 0; i < 4; i++) {
            
            Todo item = new Todo();
            item.setName("Nova Tarefa " + i);
            item.setOwner("Dummy User");
            item.setDescription("Tarefa de teste" + i);
            item.setEmail("teste@teste.com");
            item.setId(i);
            item.setStatus("Doing");
            service.createTodo(item);

        }

        Todo newTodo = new Todo();
        newTodo.setName("Nova Tarefa ");
        newTodo.setOwner("Dummy User");
        newTodo.setDescription("Tarefa de teste");
        newTodo.setEmail("teste@teste.com");
        newTodo.setId(10);
        newTodo.setStatus("Doing");

        //Act 
        Todo response = service.createTodo(newTodo);

        // Assertion
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getId() == 10);
        Assertions.assertEquals(response.getOwner(), "Dummy User");

    }

    @Test
    public void should_not_create_item_if_owner_has_more_five_task() throws Exception {
         // Arrange
        // Criar tarefas 
        for (int i = 0; i < 5; i++) {
            
            Todo item = new Todo();
            item.setName("Nova Tarefa " + i);
            item.setOwner("Dummy User");
            item.setDescription("Tarefa de teste" + i);
            item.setEmail("teste@teste.com");
            item.setId(i);
            item.setStatus("Doing");
            service.createTodo(item);

        }

        Todo newTodo = new Todo();
        newTodo.setName("Nova Tarefa ");
        newTodo.setOwner("Dummy User");
        newTodo.setDescription("Tarefa de teste");
        newTodo.setEmail("teste@teste.com");
        newTodo.setId(10);
        newTodo.setStatus("Doing");

        //Act 

        Assertions.assertThrowsExactly(Exception.class, () -> {
            Todo response = service.createTodo(newTodo);
        });

    }

}
