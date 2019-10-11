package com.bastump.todoapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import spark.utils.IOUtils;

public class TodoControllerTests {

	@BeforeAll
	public static void init() throws InterruptedException {
		Main.main(null);
		Thread.sleep(1500); // wait for embedded server to start
	}

	@AfterEach
	public void cleanup() throws InterruptedException {
		request("DELETE", "/"); // clear all data
	}

	@Test
	public void addTodoTest() throws InterruptedException {
		JsonObject buymilk = new JsonObject();
		buymilk.addProperty("title", "Buy milk");
		buymilk.addProperty("completed", false);
		String jsonString = request("PUT", "/", buymilk);
		JsonObject responseJson = new Gson().fromJson(jsonString, JsonObject.class);
		assertTrue(responseJson.isJsonObject());
		assertEquals(3, responseJson.size());
	}

	@Test
	public void getTodoListTest() {
		JsonObject buymilk = new JsonObject();
		buymilk.addProperty("title", "Buy milk");
		buymilk.addProperty("completed", false);
		JsonObject buyapples = new JsonObject();
		buyapples.addProperty("title", "Buy apples");
		buyapples.addProperty("completed", false);
		request("PUT", "/", buymilk);
		request("PUT", "/", buyapples);
		String jsonString = request("GET", "/");
		JsonArray responseJson = new Gson().fromJson(jsonString, JsonArray.class);
		assertEquals(2, responseJson.size());
		for (JsonElement element : responseJson) {
			JsonObject json = element.getAsJsonObject();
			assertTrue(json.get("title").getAsString().equals("Buy milk")
					|| json.get("title").getAsString().equals("Buy apples"));
			assertEquals(json.get("completed").getAsBoolean(), false);
		}
	}

	@Test
	public void getCompletedListTest() throws InterruptedException {
		JsonObject buycookies = new JsonObject();
		buycookies.addProperty("title", "Buy cookies");
		buycookies.addProperty("completed", false);
		JsonObject buyeggs = new JsonObject();
		buyeggs.addProperty("title", "Buy eggs");
		buyeggs.addProperty("completed", true);
		request("PUT", "/", buycookies);
		request("PUT", "/", buyeggs);
		String jsonString = request("GET", "/?q=completed");
		JsonArray responseJson = new Gson().fromJson(jsonString, JsonArray.class);
		assertEquals(1, responseJson.size());
		for (JsonElement element : responseJson) {
			JsonObject json = element.getAsJsonObject();
			assertTrue(json.get("title").getAsString().equals("Buy eggs"));
			assertEquals(json.get("completed").getAsBoolean(), true);
		}
	}

	@Test
	public void getAndDeleteTodoTest() {
		boolean checked = false;
		JsonObject buylemons = new JsonObject();
		buylemons.addProperty("title", "Buy lemons");
		buylemons.addProperty("completed", false);
		request("PUT", "/", buylemons);
		String todoList = request("GET", "/");
		JsonArray responseList = new Gson().fromJson(todoList, JsonArray.class);
		assertEquals(1, responseList.size());
		for (JsonElement element : responseList) {
			JsonObject json = element.getAsJsonObject();
			if (json.get("title").getAsString().equals("Buy lemons")) {
				String lemonsId = json.get("id").getAsString();
				String jsonString = request("GET", "/todo/" + lemonsId);
				JsonObject responseJson = new Gson().fromJson(jsonString, JsonObject.class);
				assertTrue(responseJson.get("title").getAsString().equals("Buy lemons"));
				assertEquals(3, responseJson.size());
				checked = true;

				// also check delete method
				String deleteResponse = request("DELETE", "/todo/" + lemonsId);
				assertEquals("{\"id\":\"" + lemonsId + "\"}", deleteResponse);
			}
		}
		assertTrue(checked);
	}

	@Test
	public void getPatchTodoTest() {
		JsonObject buymilk = new JsonObject();
		buymilk.addProperty("title", "Buy milk");
		buymilk.addProperty("completed", false);
		request("PUT", "/", buymilk);
		String todoList = request("GET", "/");
		JsonArray responseList = new Gson().fromJson(todoList, JsonArray.class);
		assertEquals(1, responseList.size());
		for (JsonElement element : responseList) {
			JsonObject json = element.getAsJsonObject();
			String milkId = json.get("id").getAsString();
			String jsonString = request("GET", "/todo/" + milkId);
			JsonObject responseJson = new Gson().fromJson(jsonString, JsonObject.class);
			assertEquals(false, responseJson.get("completed").getAsBoolean());

			JsonObject inputJson = new JsonObject();
			inputJson.addProperty("id", milkId);
			inputJson.addProperty("completed", true);
			String patchResponse = request("PATCH", "/todo/" + milkId, inputJson);
			JsonObject patchJsonResponse = new Gson().fromJson(patchResponse, JsonObject.class);
			assertEquals(true, patchJsonResponse.get("completed").getAsBoolean());
			assertEquals("{\"id\":\"" + milkId + "\"," + "\"title\":\"Buy milk\",\"completed\":true}", patchResponse);
		}
	}

	private String request(String method, String path) {
		return request(method, path, null);
	}

	private String request(String method, String path, JsonObject input) {
		if (method == "GET") {
			return getRequest(method, path, input);
		} else if (method == "PUT") {
			return putRequest(method, path, input);
		} else if (method == "PATCH") {
			return patchRequest(method, path, input);
		} else if (method == "DELETE") {
			return deleteRequest(method, path, input);
		}
		fail("Error - invalid method request :" + method);
		return null;
	}

	private String getRequest(String method, String path, JsonObject input) {
		try {
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet("http://localhost:4567" + path);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return IOUtils.toString(response.getEntity().getContent());
			} else {
				fail("Error - " + response.getStatusLine().getReasonPhrase());
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
		}
		return null;
	}

	private String putRequest(String method, String path, JsonObject input) {
		try {
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpPut request = new HttpPut("http://localhost:4567" + path);
			request.addHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(input.toString()));
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return IOUtils.toString(response.getEntity().getContent());
			} else {
				fail("Error - " + response.getStatusLine().getReasonPhrase());
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
		}
		return null;
	}

	private String patchRequest(String method, String path, JsonObject input) {
		try {
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpPatch request = new HttpPatch("http://localhost:4567" + path);
			request.addHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(input.toString()));
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return IOUtils.toString(response.getEntity().getContent());
			} else {
				fail("Error - " + response.getStatusLine().getReasonPhrase());
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
		}
		return null;
	}

	private String deleteRequest(String method, String path, JsonObject input) {
		try {
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpDelete request = new HttpDelete("http://localhost:4567" + path);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return IOUtils.toString(response.getEntity().getContent());
			} else {
				fail("Error - " + response.getStatusLine().getReasonPhrase());
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
		}
		return null;
	}

}
