package com.bastump.todoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TodoServiceTest {

	@Test
	void createTodoTest() {
		TodoService service = new TodoService();
		Todo buyMilk = service.createTodo("Buy Milk");
		assertEquals("Buy Milk", buyMilk.getTitle());
	}

	@Test
	void completeTodoTest() {
		TodoService service = new TodoService();
		Todo buyMilk = service.createTodo("Buy Milk");
		boolean isCompleted = buyMilk.getCompleted();
		assertEquals(false, isCompleted);
		service.completeTodo(buyMilk.getId());
		isCompleted = buyMilk.getCompleted();
		assertEquals(true, isCompleted);
	}
}
