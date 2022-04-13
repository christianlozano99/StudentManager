package com.hcl.Student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import com.hcl.Student.entity.Student;

public class StudentManager {

    public static void main( String[] args ) throws IOException, ParseException {
    
        header();
		
        //Sort during insertion
        Set<Student> studentSet = new TreeSet<>(new Comparator<Student>() {
        	@Override
        	public int compare(Student o1, Student o2) {
			// TODO Auto-generated method stub
				return o1.getName().compareTo(o2.getName());
			}
		});
        
        //Extracts the information from the text file and inputs it into the set
        File file = extractFileInformation(studentSet);
		
        Scanner myObj = new Scanner(System.in);
        String actionKey = "";
        
        showCommands();
        
        while(!(actionKey.equals("q")|| actionKey.equals("quit"))) {
            System.out.print("\nEnter the next command: ");

            actionKey = myObj.next();
            
            switch (actionKey) {
			case "insert":
			case "i":
				insertStudent(studentSet, myObj);
				break;
			case "delete":
			case "d":
				deleteStudent(studentSet, myObj);
				break;
			case "update":
			case "u":
				updateStudent(studentSet, myObj);
				break;
			case "find":
			case "search":
				searchStudent(studentSet, myObj);
				break;
			case "showall":
				printAllStudents(studentSet);
				break;
			case "q":
			case "quit":
				break;
			default:
				System.out.println( "Invalid Command, try again!" );
				break;
			}
        }
        
        // puts set into text file for next use
        inputDataBack(studentSet, file);
        
        
        footer();
    }

    private static void inputDataBack(Set<Student> studentSet, File file) throws IOException {
		FileWriter fw = new FileWriter(file);

		for(Student curr: studentSet) {
			String insertString = curr.getID() +"," + curr.getName() + "," +
					curr.getAge() +","+curr.getDate() +"\n";
			fw.write(insertString);
		}
		
		fw.close();
	}

    private static File extractFileInformation(Set<Student> studentSet) throws ParseException {
		File file = new File("StoredInfo.txt");
		try {
			File myObj = new File("StoredInfo.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data[] = myReader.nextLine().split(",");
				int id = Integer.parseInt(data[0]);
				String name = data[1];
				int age = Integer.parseInt(data[2]);
				String date = data[3];
				studentSet.add(new Student(id, name, age, date));
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return file;
	}

    // Searchs for studdent in given treeset
    private static void searchStudent(Set<Student> studentSet, Scanner myObj) {
		// TODO Auto-generated method stub
    	System.out.print( "What is the student ID: " );
    	int id = myObj.nextInt();
    	
    	for(Student curr: studentSet) {
    		if(curr.getID() == id) {
    			System.out.println("_______________________________________________________________\n" );
        		System.out.println("Student information: \nName: " + curr.getName()
        		+"\nAge: " +curr.getAge()+ "\nID :" + curr.getID() + "\nDate Added: " + curr.getDate());
        		System.out.println("_______________________________________________________________\n" );
    			return;
    		}
    	}
   		idNotInStore(id);
    	
	}

    // Message anytime an operation is tried with a ID that does not exist
	private static void idNotInStore(int id) {
		System.out.println("____________________________________________\n" );
		System.out.println("ID: " + id + " does not exist");
		System.out.println("____________________________________________\n" );
	}

	// Updates student data by ID
	private static void updateStudent(Set<Student> studentSet, Scanner myObj) {
		// TODO Auto-generated method stub
    	try {
	    	System.out.print( "Would you like to update age or name: " );
	    	String updateType = myObj.next();
	    	
	    	while(!(updateType.equals("age") ||updateType.equals("name")))
	    	{
	    		System.out.print( "Invalid update, choose 'age' or 'name'!: " );
	    		updateType = myObj.next();
	    	}
	    	
	    	System.out.print( "What is the student ID: " );
	    	int id = myObj.nextInt();
	    	
	    	for(Student curr: studentSet) {
	    		if(curr.getID() == id) {
	    			if(updateType.equals("age")) {
	    				System.out.print( "What is the new age: " );
	    				curr.setAge( myObj.nextInt());
	    			}
	    			else if(updateType.equals("name")) {
	    				System.out.print( "What is the new name: " );
	    				curr.setName( myObj.next());
	    			}
	    			
	    			success("updated", curr.getName());
	    			
	    			return;
	    		}
	    	}
	   		idNotInStore(id);
    	} catch (Exception e) {
    		System.out.println("Invalid number!");
    	}
	}

	// Deletes student data by ID
	private static void deleteStudent(Set<Student> studentSet, Scanner myObj) {
		// TODO Auto-generated method stub
    	try {
	    	System.out.println( "Input student's ID to delete:" );
	    	int id = myObj.nextInt();
	    	
	    	for(Student curr: studentSet) {
	    		if(curr.getID() == id) {
	    			studentSet.remove(curr);
	    			success("removed", curr.getName());
	        		
	    			return;
	    		}
	    	}
	   		idNotInStore(id);
    	} catch (Exception e) {
    		System.out.println("Invalid ID!");
    	}
	}

	//  Prints all students currently stored
	private static void printAllStudents(Set<Student> studentSet) {
		// TODO Auto-generated method stub
    	int counter = 1;
    	System.out.println("_________________________________________________________________");
    	for(Student curr: studentSet) {
    		System.out.println("");
    		System.out.printf(counter+ ". Name: "+ curr.getName()+"\tAge: "+
    		curr.getAge()+"\tID:"+ curr.getID()+ "\tDate Added: " + curr.getDate());
    		counter++;
    	}
		System.out.println("\n_________________________________________________________________\n\n" );
	}

	// Inserts new student into the treeSet
	private static void insertStudent(Set<Student> studentSet, Scanner myObj) {
    	try {
    		System.out.print( "Input new student's name: " );
        	String name = myObj.next();
        	
    		System.out.print( "Input "+ name+"'s id: " );
        	int id = myObj.nextInt();
        	
    		System.out.print( "Input "+ name +"'s age: " );
    		int age = myObj.nextInt();
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("M-dd-yyyy hh:mm:ss");
    		Date date = new Date();
    		studentSet.add(new Student(id, name, age, sdf.format(date)));

    		success("added", name);

    		
    	} catch (Exception e) {
    		System.out.println("Invalid inputs!");
    		return;
    	}
    }
    
	// Success Message 
	private static void success(String type, String name) {
		System.out.println("\n____________________________________________\n" );
		System.out.println("Successfully "+ type +": " + name);
		System.out.println("____________________________________________\n" );
	}
	
    // footer exit message
	private static void footer() {
		System.out.println( "______________________________________________" );
        System.out.println( "|                                             |" );
        System.out.println( "| Thank you for using the Student Set Manager!|" );
        System.out.println( "|_____________________________________________|\n" );
	}

	// shows commands
	private static void showCommands() {
		System.out.println("Available Commands:\n");
		System.out.println("'insert' or 'i'\nDescription: inserts new student\n");
		
		System.out.println("'delete' or 'd'\nDescription: deletes student on ID\n");
		
		System.out.println("'update' or 'u'\nDescription: updates existing student on ID\n");
		
		System.out.println("'search' or 'find'\nDescription: finds existing student on ID\n");
		
		System.out.println("'showall' \nDescription: Shows all existing students\n");
		
		System.out.println("'q' or 'quit' \nDescription: Saves and exits the application\n");

	}

	// header for first opening application
	private static void header() {
		System.out.println( "_____________________________________" );
        System.out.println( "|                                    |" );
        System.out.println( "| Welcome to the Student Set Manager!|" );
        System.out.println( "|____________________________________|\n" );
	}
}
