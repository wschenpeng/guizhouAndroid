package com.example.wzy.guizhouandroid.model;


public class Person {
	private Integer id;
	private String name;
	private String logtime;
	
	public Person(){
		
	};
	
	public Person(String name, String logtime) {
		this.name = name;
		this.logtime = logtime;
	};

	public Person(Integer id, String name, String logtime) {
		
		this.id = id;
		this.name = name;
		this.logtime = logtime;
	};
	
	public Integer getId() {
		return id;
	};
	
	public void setId(Integer id) {
		this.id = id;
	};
	
	public String getName() {
		return name;
	};
	
	public void setName(String name) {
		this.name = name;
	};
	public String getLogtime() {
		return logtime;
	}

	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", logtime=" + logtime
				+ "]";
	}

}
