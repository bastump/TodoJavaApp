package com.bastump.todoapi;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import spark.Spark;
import spark.utils.IOUtils;

public class TodoControllerTests {

	@BeforeAll
	public static void beforeClass() throws InterruptedException {
		Main.main(null);
		Thread.sleep(5000); // wait for embedded server to start
	}

	@AfterAll
	public static void afterClass() {
		Spark.stop(); // stop embedded server
	}

	@Test
	public void addTodoTest() {
		JsonObject buymilk = new JsonObject();
		buymilk.addProperty("title", "Buy milk");
		buymilk.addProperty("completed", false);
		JsonObject buyapples = new JsonObject();
		buyapples.addProperty("title", "Buy apples");
		buyapples.addProperty("completed", false);
		String jsonString = request("PUT", "/", buymilk);
		request("PUT", "/", buyapples);
		JsonObject responseJson = new Gson().fromJson(jsonString, JsonObject.class);
		assertTrue(responseJson.isJsonObject());
		assertEquals(responseJson.size(), 3);
	}

	@Test
	public void getTodoListTest() {
		String jsonString = request("GET", "/");
		System.out.println();
		System.out.println(jsonString.toString());
		JsonArray responseJson = new Gson().fromJson(jsonString, JsonArray.class);
		for (JsonElement element : responseJson) {
			JsonObject json = element.getAsJsonObject();
			assertTrue(json.get("title").getAsString().equals("Buy milk")
					|| json.get("title").getAsString().equals("Buy apples"));
			assertEquals(json.get("completed").getAsBoolean(), false);
			assertEquals(responseJson.size(), 2);
		}

	}

	private String request(String method, String path) {
		try {
			URL url = new URL("http://localhost:4567" + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			connection.connect();
			String jsonOutput = IOUtils.toString(connection.getInputStream());
			return jsonOutput;
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}
	}

	private String request(String method, String path, String paramName, String paramValue) {
		try {
			URL url = new URL("http://localhost:4567" + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(paramName, paramValue));
			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQuery(params));
			writer.flush();
			writer.close();
			os.close();

			connection.connect();
			String jsonOutput = IOUtils.toString(connection.getInputStream());
			return jsonOutput;
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}
	}

	private String request(String method, String path, JsonObject input) {
		try {
			URL url = new URL("http://localhost:4567" + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			os.write(input.toString().getBytes("UTF-8"));
			os.close();

			connection.connect();
			String jsonOutput = IOUtils.toString(connection.getInputStream());
			return jsonOutput;
		} catch (IOException e) {
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}
	}

	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

}
