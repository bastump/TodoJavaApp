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
			if (req.queryParams("q") != null) {
				String param = req.queryParams("q");
				if (param.equals("completed")) {
					return new Gson().toJsonTree(todoService.getCompletedTodos());
				} else {
					throw new Exception("Error - unknown query parameter '" + param + "'");
				}
			} else {
				return new Gson().toJsonTree(todoService.getAllTodos());
			}
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
				return new Gson().toJson("Error - request input not a valid json: " + req.body());
			}
		});

		// Get todo by id
		get("/todo/:id", (req, res) -> {
			res.type("application/json");
			UUID id = UUID.fromString(req.params("id"));
			Todo todo = todoService.getTodo(id);
			if (todo != null) {
				return new Gson().toJson(todo);
			} else {
				res.status(400);
				return new Gson().toJson("Todo with id " + id.toString() + " not found");
			}
		});

		// Update (Complete) todo
		patch("todo/:id", (req, res) -> {
			res.type("application/json");
			JsonObject requestBody = new Gson().fromJson(req.body(), JsonObject.class);
			boolean updateStatus = requestBody.get("completed").getAsBoolean();
			UUID id = UUID.fromString(req.params(":id"));
			Todo todo = todoService.getTodo(id);
			if (todo != null) {
				todo.setCompleted(updateStatus);
				return new Gson().toJson(todo);
			} else {
				res.status(400);
				return new Gson().toJson("Todo with id " + id.toString() + " not found");
			}
		});

		// Delete todo
		delete("/todo/:id", (req, res) -> {
			res.type("application/json");
			UUID id = UUID.fromString(req.params(":id"));
			Todo todo = todoService.getTodo(id);
			if (todo != null) {
				todoService.deleteTodo(id);
				res.status(200);
				JsonObject result = new JsonObject();
				result.addProperty("id", id.toString());
				return result.toString();
			} else {
				res.status(400);
				return new Gson().toJson("Todo with id " + id.toString() + " not found");
			}
		});

		// Delete all todos for cleanup purposes
		delete("/", (req, res) -> {
			res.type("application/json");
			int count = todoService.getAllTodos().size();
			todoService.deleteAll();
			res.status(200);
			return new Gson().toJson("Deleted all " + String.valueOf(count) + " items.");
		});
	}
}
