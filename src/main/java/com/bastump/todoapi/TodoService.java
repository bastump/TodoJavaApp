package com.bastump.todoapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TodoService {

	private Map<UUID, Todo> todos = new HashMap<>();

	public Collection<Todo> getAllTodos() {
		return todos.values();
	}

	public Collection<Todo> getCompletedTodos() {
		Collection<Todo> todoList = this.getAllTodos();
		Collection<Todo> completedTodos = new ArrayList<Todo>();
		for (Todo todo : todoList) {
			if (todo.getCompleted()) {
				completedTodos.add(todo);
			}
		}
		return completedTodos;
	}

	public Todo getTodo(UUID id) {
		return todos.get(id);
	}

	public Todo createTodo(String title) {
		Todo todo = new Todo(title, false); // instantiates random id
		todos.put(todo.getId(), todo); // places random id
		return todo;
	}

	public Todo completeTodo(UUID id) {
		Todo todo = todos.get(id);
		todo.setCompleted(true);
		return todo;
	}

	public void deleteTodo(UUID id) {
		todos.remove(id);
	}
}
