package com.bastump.todoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

class TodoServiceTest {

	@Test
	public void createTodoTest() throws Exception {
		JsonObject milkInputJson = new JsonObject();
		milkInputJson.addProperty("title", "Buy milk");
		milkInputJson.addProperty("completed", false);
		TodoService service = new TodoService();

		Todo responseTodo = service.createTodo(milkInputJson);
		Todo buyMilk = service.getTodo(responseTodo.getId());
		assertTrue(buyMilk.getTitle().equals("Buy milk"));
	}

	@Test
	public void completeTodoTest() throws Exception {
		JsonObject milkInputJson = new JsonObject();
		milkInputJson.addProperty("title", "Buy milk");
		milkInputJson.addProperty("completed", false);
		TodoService service = new TodoService();

		Todo buyMilk = service.createTodo(milkInputJson);
		boolean isCompleted = buyMilk.getCompleted();
		assertEquals(false, isCompleted);
		service.updateTodo(buyMilk.getId(), true);
		isCompleted = buyMilk.getCompleted();
		assertEquals(true, isCompleted);
	}

	@Test
	public void getCompletedTodoTest() throws Exception {
		JsonObject milkInputJson = new JsonObject();
		milkInputJson.addProperty("title", "Buy milk");
		milkInputJson.addProperty("completed", false);
		JsonObject eggsInputJson = new JsonObject();
		eggsInputJson.addProperty("title", "Buy eggs");
		eggsInputJson.addProperty("completed", true);

		TodoService service = new TodoService();
		Todo buyMilk = service.createTodo(milkInputJson);
		Todo buyEggs = service.createTodo(eggsInputJson);

		boolean milkCompleted = buyMilk.getCompleted();
		boolean eggsCompleted = buyEggs.getCompleted();
		assertEquals(false, milkCompleted);
		assertEquals(true, eggsCompleted);
		assertEquals(2, service.getAllTodos().size());
		assertEquals(1, service.getCompletedTodos().size());
	}
}
