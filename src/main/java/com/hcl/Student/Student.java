package com.hcl.Student;

/**
 * Hello world!
 *
 */
public class Student {
	private int ID;
	private String name;
	private int age;

	public Student(int id, String name, int age) {
		this.ID = id;
		this.name = name;
		this.age = age;
	}

	public int getID() {
		return ID;
	}

	public void setID(int rollno) {
		this.ID = rollno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.ID+ this.name.hashCode()+ this.age;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Student s = (Student) obj;
		return s.age == age && s.ID == ID && s.name.equals(name);
	}

}