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
		service.completeTodo(buyMilk.getId());
		isCompleted = buyMilk.getCompleted();
		assertEquals(true, isCompleted);
	}
}
