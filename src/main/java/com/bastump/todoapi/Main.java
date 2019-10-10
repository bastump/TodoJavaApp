package com.bastump.todoapi;

import org.apache.log4j.BasicConfigurator;

public class Main {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		new TodoController(new TodoService());
	}

}
