package com.hcl.Student;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class StudentManager {
	
    public static void main( String[] args ) throws IOException, ParseException {
    
        header();
        
        //Initialize JDBC connection
        Connection dbConnection = getConnection();
        		
        Scanner myObj = new Scanner(System.in);
        String actionKey = "";
        
        showCommands();
        
        while(!(actionKey.equals("q")|| actionKey.equals("quit"))) {
            System.out.print("\nEnter the next command: ");

            actionKey = myObj.next();
            
            switch (actionKey) {
			case "insert":
			case "i":
				insertStudent(myObj, dbConnection);
				break;
			case "delete":
			case "d":
				deleteStudent(myObj, dbConnection);
				break;
			case "update":
			case "u":
				updateStudent(myObj, dbConnection);
				break;
			case "find":
			case "search":
				searchStudent(myObj, dbConnection);
				break;
			case "averageAge":
			case "aa":
				getAverageAge(dbConnection);
				break;
			case "showall":
				printAllStudents(dbConnection);
				break;
			case "q":
			case "quit":
				break;
			default:
				System.out.println( "Invalid Command, try again!" );
				break;
			}
        }
        
        footer();
    }

    // Connects to the database
    public static Connection getConnection() {
    	Connection connection = null;
        try {
        	String jdbcURL = "jdbc:mysql://localhost:3306/jdbcdb";
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
    private static void getAverageAge( Connection sqlConnection) {
		// TODO Auto-generated method stub
    	try {
		
		PreparedStatement pst = sqlConnection.prepareStatement("SELECT AVG(age) as average FROM student");
		ResultSet rs = pst.executeQuery();
		
		double averageAge = 0;
		if(rs.next())
			averageAge =  rs.getDouble(1);
		
    	System.out.println("_______________________________________________");
    	System.out.println("\nThe average age of the students is: " + String.format("%.2f", averageAge));
    	System.out.println("_______________________________________________");
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
	}
        
    // Search for student in database
    private static void searchStudent( Scanner myObj, Connection sqlConnection) {
		// TODO Auto-generated method stub
    	System.out.print( "What is the student ID: " );
    	int id = myObj.nextInt();

		boolean found = false;
		try {			
			PreparedStatement pst = sqlConnection.prepareStatement("select * from jdbcdb.student where id = ?");
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			
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
	private static void updateStudent( Scanner myObj, Connection sqlConnection) {
		// TODO Auto-generated method stub
    	try {
	    	System.out.print( "Would you like to update age or name: " );
	    	String updateType = myObj.next();
	    	
	    	while(!(updateType.equals("age") ||updateType.equals("name"))){
	    		System.out.print( "Invalid update, choose 'age' or 'name'!: " );
	    		updateType = myObj.next();
	    	}
	    	
	    	System.out.print( "What is the student ID: " );
	    	int id = myObj.nextInt();
	    	
	    	// Gets student name to let user who they updated
    		PreparedStatement pst = sqlConnection.prepareStatement("select studentName from jdbcdb.student where id = ?");
    		pst.setInt(1, id);
    		ResultSet rs = pst.executeQuery();
    		String name = "";
    		
    		// Checks if any one has given id if not print message and return
    		if(rs.next()) {
    			name = rs.getString(1);
    		}
    		else {
    			idNotInStore(id);
    			return;
    		}
    		
    		// Calls procedure based on user input
			if(updateType.equals("age")) {
				System.out.print( "What is the new age: " );
				int newAge = myObj.nextInt();
				CallableStatement cst = sqlConnection.prepareCall("{call updateAge(?, ?)}");
				cst.setInt(1, id);
				cst.setInt(2, newAge);
				cst.execute();
			}
			else if(updateType.equals("name")) {
				System.out.print( "What is the new name: " );
				String newName = myObj.next();
				CallableStatement cst = sqlConnection.prepareCall("{call updateName(?, ?)}");
				cst.setInt(1, id);
				cst.setString(2, newName);
				cst.execute();
			}
    		
    		success("Updated", name);
    		
    	} catch (Exception e) {
    		System.out.println("Unexpected issue with inputs!");
    	}
	}

	// Deletes student data by ID
	private static void deleteStudent( Scanner myObj, Connection sqlConnection) {
		// TODO Auto-generated method stub
    	try {
	    	System.out.print( "Input student's ID to delete:" );
	    	int id = myObj.nextInt();
	    	
	    	//query to get Student name if found
    		PreparedStatement pst = sqlConnection.prepareStatement("select studentName from student where id = ?");
    		pst.setInt(1, id);
    		ResultSet rs = pst.executeQuery();
    		
    		String name = "";
    		
    		if(rs.next()) {
    			name = rs.getString(1);
    		}
    		else {
    			idNotInStore(id);
    			return;
    		}
    		
    		//calling SQL procedure to delete student given ID
			CallableStatement cst = sqlConnection.prepareCall("{call deleteStudent(?)}");
			cst.setInt(1, id);
			cst.execute();

    		if(!name.equals("")) {
    			success("removed", name);
    		}
    		
    	} catch (Exception e) {
    		System.out.println("Invalid ID!");
    	}
	}

	//  Prints all students currently stored
	private static void printAllStudents( Connection sqlConnection) {
		// TODO Auto-generated method stub
    	
    	System.out.println("_________________________________________________________________");
		
    	try {
	    	PreparedStatement pst = sqlConnection.prepareStatement( "select * from student order by studentName");
			ResultSet rs = pst.executeQuery();
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
    	catch(Exception e) {
    		System.out.println("Unexpected issue with database");
    	}
		System.out.println("\n_________________________________________________________________\n\n" );

    	
	}

	// Inserts new student into the treeSet
	private static void insertStudent( Scanner myObj, Connection sqlConnection) {
    	try {
    		System.out.print( "Input new student's name: " );
        	String name = myObj.next();
        	
    		System.out.print( "Input "+ name+"'s id: " );
        	int id = myObj.nextInt();
        	
    		System.out.print( "Input "+ name +"'s age: " );
    		int age = myObj.nextInt();
    		
    		//Calls procedure to insert students name id and age, procedure handles time
			CallableStatement cst = sqlConnection.prepareCall("{call insertStudent(?,?,?)}");
			cst.setInt(1, id);
			cst.setString(2, name);
			cst.setInt(3, age);
			cst.execute();

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
