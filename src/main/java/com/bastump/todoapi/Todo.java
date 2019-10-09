package com.bastump.todoapi;

import java.util.UUID;

public class Todo {

	private UUID id;
	private String title;
	private boolean completed;

	public Todo(String title, boolean completed) {
		this.id = UUID.randomUUID();
		this.title = title;
		this.completed = completed;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean getCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
