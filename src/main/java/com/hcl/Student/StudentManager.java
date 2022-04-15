package com.hcl.Student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hcl.Student.entity.Student;

public class StudentManager {
	
    public static void main( String[] args ) throws IOException, ParseException {
    
        header();
        
        //Initialize JDBC connection
        Connection dbConnection = getConnection();
        
        
        //Sort during insertion
        Set<Student> studentSet = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
        
        //Extracts the information from the text file and inputs it into the set
        //File file = extractFileInformation(studentSet);
		
        Scanner myObj = new Scanner(System.in);
        String actionKey = "";
        
        showCommands();
        
        while(!(actionKey.equals("q")|| actionKey.equals("quit"))) {
            System.out.print("\nEnter the next command: ");

            actionKey = myObj.next();
            
            switch (actionKey) {
			case "insert":
			case "i":
				insertStudent(studentSet, myObj, dbConnection);
				break;
			case "delete":
			case "d":
				deleteStudent(studentSet, myObj, dbConnection);
				break;
			case "update":
			case "u":
				updateStudent(studentSet, myObj, dbConnection);
				break;
			case "find":
			case "search":
				searchStudent(studentSet, myObj, dbConnection);
				break;
			case "averageAge":
			case "aa":
				getAverageAge(studentSet, dbConnection);
				break;
			case "showall":
				printAllStudents(studentSet, dbConnection);
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
        //inputDataBack(studentSet, file);
        
        footer();
    }

    public static Connection getConnection() {
        // Registering drivers
    	//DriverManager.registerDriver();

    	Connection connection = null;
        try {
        	String jdbcURL = "jdbc:mysql://127.0.0.1:3306/?user=root";
        	String jdbcUsername = "root";
        	String jdbcPassword = "";
        	connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        return connection;
    }

    // Gets Average age from DB ages
    private static void getAverageAge(Set<Student> studentSet, Connection sqlConnection) {
		// TODO Auto-generated method stub
    	//double averageAge = studentSet.stream().mapToDouble(d -> d.getAge()).average().orElse(0.0);
    	try {
    	String averageSql = "SELECT AVG(age) as average FROM jdbcdb.student";
		
		Statement st = sqlConnection.createStatement();
		ResultSet rs = st.executeQuery(averageSql);
		
		double averageAge = 0;
		if(rs.next())
			averageAge =  rs.getDouble(1);
		
    	System.out.println("_______________________________________________");
    	System.out.println("\nThe average age of the students is: " + String.format("%.2f", averageAge));
    	System.out.println("_______________________________________________");
    	}
    	catch(Exception e)
    	{
    		System.out.println(e);
    	}
	}
        
    // Searchs for studdent in given treeset
    private static void searchStudent(Set<Student> studentSet, Scanner myObj, Connection sqlConnection) {
		// TODO Auto-generated method stub
    	System.out.print( "What is the student ID: " );
    	int id = myObj.nextInt();
    	/*final AtomicBoolean found = new AtomicBoolean(false);

		studentSet.stream().forEach(curr -> {
			if (curr.getID() == id) {
				System.out.println("_______________________________________________________________\n");
				System.out.println("Student information: \nName: " + curr.getName() + "\nAge: " + curr.getAge()
						+ "\nID: " + curr.getID() + "\nDate Added: " + curr.getDate());
				System.out.println("_______________________________________________________________\n");
				found.set(true);
			}
			
		});*/
		
		boolean found = false;
		try {
			String selectAllSql = "select * from jdbcdb.student where id =" + id;
			
			Statement st = sqlConnection.createStatement();
			ResultSet rs = st.executeQuery(selectAllSql);
			
			if(rs.next())
			{
				found = true;
				 String foundID= rs.getString(1);
		    	 String name = rs.getString(2);
		    	 String age = rs.getString(3);
		    	 String date = rs.getString(4);
				System.out.println("_______________________________________________________________\n");
				System.out.println("Student information: \nName: " + name + "\nAge: " + age
						+ "\nID: " + foundID + "\nDate Added: " + date);
				System.out.println("_______________________________________________________________\n");
			}
			
		} catch (Exception e) {
			System.out.println("Unexpected issue while searching in the database");
		}
		
		if(!found) {
			idNotInStore(id);
		}
    }

	// Updates student data by ID
	private static void updateStudent(Set<Student> studentSet, Scanner myObj, Connection sqlConnection) {
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
	    	
	    	/*for(Student curr: studentSet) {
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
	    	}*/
    		String updateSql = "select studentName from jdbcdb.student where id = "+ id;
    		Statement st = sqlConnection.createStatement();
    		ResultSet rs = st.executeQuery(updateSql);
    		String name = "";
    		if(rs.next())
    		{
    			name = rs.getString(1);
    		}
    		
			if(updateType.equals("age")) {
				System.out.print( "What is the new age: " );
				String newAge = myObj.next();
				updateSql = "UPDATE jdbcdb.student SET age = " + newAge + " WHERE id = " + id;
			}
			else if(updateType.equals("name")) {
				System.out.print( "What is the new name: " );
				String newName = myObj.next();
				updateSql = "UPDATE jdbcdb.student SET studentName = '" + newName + "' WHERE id = " + id;
			}
    		
    		int updated = st.executeUpdate(updateSql);
    		
    		if(updated == 1)
    		{
    			success("Updated", name);
    		}
	    	
	    	
	   		//idNotInStore(id);
    	} catch (Exception e) {
    		System.out.println("Unexpected issue with inputs!");
    	}
	}

	// Deletes student data by ID
	private static void deleteStudent(Set<Student> studentSet, Scanner myObj, Connection sqlConnection) {
		// TODO Auto-generated method stub
    	try {
	    	System.out.print( "Input student's ID to delete:" );
	    	int id = myObj.nextInt();
	    	/*
	    	for(Student curr: studentSet) {
	    		if(curr.getID() == id) {
	    			studentSet.remove(curr);
	    			success("removed", curr.getName());
	        		
	    			//return;
	    		}

	    	}*/
    		//SQL PART
	    	
    		String delSql = "select studentName from jdbcdb.student where id = "+ id;
    		Statement st = sqlConnection.createStatement();
    		ResultSet rs = st.executeQuery(delSql);
    		String name = "";
    		if(rs.next())
    		{
    			name = rs.getString(1);
    		}
    		
    		delSql = "delete from jdbcdb.student where  id = " + id;
    		int deleted = st.executeUpdate(delSql);
    		
    		if(deleted == 1)
    		{
    			success("removed", name);
    		}
	   		 
    	} catch (Exception e) {
    		System.out.println("Invalid ID!");
    	}
	}

	//  Prints all students currently stored
	private static void printAllStudents(Set<Student> studentSet, Connection sqlConnection) {
		// TODO Auto-generated method stub
    	//final AtomicInteger counter = new AtomicInteger();
    	
    	System.out.println("_________________________________________________________________");
    	/*  studentSet.stream().forEach(curr -> {
    		System.out.println("");
    		counter.getAndIncrement();
    		System.out.printf(counter+ ". Name: "+ curr.getName()+"\tAge: "+
    		curr.getAge()+"\tID:"+ curr.getID()+ "\tDate Added: " + curr.getDate());
    	});    	
 		*/  	
		

    	try {
	    	String selectAllSql = "select * from jdbcdb.student order by studentName";
			
			Statement st = sqlConnection.createStatement();
			ResultSet rs = st.executeQuery(selectAllSql);
			int counterSQL = 1;
	    	while (rs.next()) {
	    	    String id   = rs.getString(1);
	    	    String name = rs.getString(2);
	    	    String age = rs.getString(3);
	    	    String date = rs.getString(4);
	    	    System.out.printf("\n"+counterSQL+ ". Name: "+ name+"\tAge: "+
	    	    		age+"\tID:"+ id+ "\tDate Added: " + date);
	    	    counterSQL++;
	    	}
	    	
    	}
    	catch(Exception e)
    	{
    		System.out.println("Unexpected issue with database");
    	}
		System.out.println("\n_________________________________________________________________\n\n" );

    	
	}

	// Inserts new student into the treeSet
	private static void insertStudent(Set<Student> studentSet, Scanner myObj, Connection sqlConnection) {
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

    		
    		//SQL PART
    		String insertSql = "insert into jdbcdb.student "
    				+ "values(" + id + ",'" + name + "'," + age + ", now() )";
    		
    		Statement st = sqlConnection.createStatement();
    		st.executeUpdate(insertSql);
    		
    		
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	

    	
    }
    
	/*****************************************************
	 * Below this banner there are only message functions*
	 *****************************************************/
	
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
		
		System.out.println("'averageAge' or 'aa'\nDescription: shows the average age of the students\n");
		
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
    
	// Message anytime an operation is tried with a ID that does not exist
	private static void idNotInStore(int id) {
		System.out.println("____________________________________________\n" );
		System.out.println("ID: " + id + " does not exist");
		System.out.println("____________________________________________\n" );
	}

}

/*************************************************************************************************
 * Disregarding this part of code for now since there's no need to due to database replacing map *
 *************************************************************************************************
// Saves the data into txt file for next session
private static void inputDataBack(Set<Student> studentSet, File file) throws IOException {
	FileWriter fw = new FileWriter(file);
	studentSet.stream().forEach(curr -> {

		String insertString = curr.getID() +"," + curr.getName() + "," +
				curr.getAge() +","+curr.getDate() +"\n";
		try {
			fw.write(insertString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("An error saving data occurred.");
		}
	});
	
	fw.close();
}

// pulls last sessions data into program
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
		System.out.println("An error fetching data occurred.");
		e.printStackTrace();
	}
	return file;
}
 */
