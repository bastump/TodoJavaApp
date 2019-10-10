package com.bastump.todoapi;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.patch;
import static spark.Spark.put;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TodoController {

	public TodoController(final TodoService todoService) {
		// List all todos
		get("/", (req, res) -> {
			res.type("application/json");
			return new Gson().toJsonTree(todoService.getAllTodos());
		});

		// List all completed todos
		get("/?=completed", (req, res) -> {
			res.type("application/json");
			return new Gson().toJsonTree(todoService.getCompletedTodos());
		});

		// Create a new todo
		put("/", (req, res) -> {
			res.type("application/json");
			JsonObject requestBody = new Gson().fromJson(req.body(), JsonObject.class);
			if (requestBody.isJsonObject()) {
				Todo todo = todoService.createTodo(requestBody);
				return new Gson().toJson(todo);
			} else {
				res.status(422);
				return new Gson().toJson("Request input not a valid Json: " + req.body());
			}
		});

		// Get todo by id
		get("/todos/:id", (req, res) -> {
			res.type("application/json");
			UUID id = UUID.fromString(req.params(":id"));
			Todo todo = todoService.getTodo(id);
			if (todo != null) {
				return new Gson().toJson(todo);
			} else {
				res.status(400);
				return new Gson().toJson("Todo with id " + id.toString() + " not found");
			}
		});

		// Update (Complete) todo
		patch("todos/:id", (req, res) -> {
			res.type("application/json");
			UUID id = UUID.fromString(req.params(":id"));
			Todo todo = todoService.getTodo(id);
			if (todo != null) {
				todo.setCompleted(true);
				return new Gson().toJson(todo);
			} else {
				res.status(400);
				return new Gson().toJson("Todo with id " + id.toString() + " not found");
			}
		});

		// Delete todo
		delete("/todos/:id", (req, res) -> {
			res.type("application/json");
			UUID id = UUID.fromString(req.params(":id"));
			Todo todo = todoService.getTodo(id);
			if (todo != null) {
				todoService.deleteTodo(id);
				res.status(200);
				return new Gson().toJson("{\"id\": id.toString()}");
			} else {
				res.status(400);
				return new Gson().toJson("Todo with id " + id.toString() + " not found");
			}
		});
	}
}
