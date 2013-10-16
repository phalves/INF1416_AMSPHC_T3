package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.authentication.User;

public class DataBase {

	private static final String mdbFile = "C:\\Users\\Paulo\\workspace\\T1_Seg_20131.mdb";
	
	private Connection connection;
	
	private static DataBase instance = null;

	public static DataBase getDataBase(){
		if (instance == null)
			setInstance(new DataBase());
		return getInstance();
	}

	public void connectToDataBase(){
		Connection connection = null;
		
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			String connectionString = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + mdbFile + ";";
			connection = DriverManager.getConnection(connectionString, "", "");

			this.connection = connection;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't stablish connection to " + mdbFile);
		}
	}

	public void disconnectFromDataBase() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to disconnect from " + mdbFile);
		}
	}
	
	public static DataBase getInstance() {
		return instance;
	}

	public static void setInstance(DataBase instance) {
		DataBase.instance = instance;
	}

	public String selectName(String name) {
		String returningName = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningName = resultSet.getString("UserName");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningName;
	}
	
	public User getUser(String name) {
		User user = new User();
		
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				user.setLoginName(resultSet.getString("UserName"));
				user.setNomeProprio(resultSet.getString("Nome"));
				user.setRole(resultSet.getString("Grupos_Id"));
				user.setSALT(resultSet.getString("SALT"));
				user.setPasswd(resultSet.getString("Passwd"));
				
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return user;
	}

	public boolean isUserBlocked(String name) {
		Date blockedTime = null;
		Date blockedDate = null;
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				blockedDate = resultSet.getDate("BlockedDate");
				blockedTime = resultSet.getTime("BlockedTime");
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		if( blockedTime != null && blockedDate != null ) {

			Calendar dateBlocked = Calendar.getInstance();
			Calendar timeBlocked = Calendar.getInstance();
			timeBlocked.setTime( blockedTime );
			dateBlocked.setTime( blockedDate );

			dateBlocked.set(dateBlocked.get(Calendar.YEAR),
					dateBlocked.get(Calendar.MONTH),
					dateBlocked.get(Calendar.DAY_OF_MONTH),
					timeBlocked.get(Calendar.HOUR_OF_DAY),
					timeBlocked.get(Calendar.MINUTE),
					timeBlocked.get(Calendar.SECOND));

			Calendar dateTimeNow =  Calendar.getInstance();	
			dateBlocked.add( Calendar.MINUTE, 2 );

			if( dateBlocked.after( dateTimeNow ) ) {
				return true;
			}
		}
		return false;
	}
	
	public String selectSALT(String name) {
		String returningSALT = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningSALT = resultSet.getString("SALT");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningSALT;
	}
	
	public String selectPasswd(String name) {
		String returningPasswd = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningPasswd = resultSet.getString("Passwd");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningPasswd;
	}
	
	public void setNumberOfAttempts(int numberOfAttempts, String name, int state) {
		String table;
		if (state == 1) {
			table = "NumberOfAttempts";
		} else {
			table = "Attempts2";
		}
		String sql = "UPDATE Usuarios SET " + table + " = " + numberOfAttempts + " WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}
	
	public String selectNomeProprio(String name) {
		String returningName = "";
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				returningName = resultSet.getString("Nome");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return returningName;
	}

	public String getUserRole(String name) {
		String role = "";
		String sql = "SELECT * FROM Usuarios " +
				"INNER JOIN Grupos ON Usuarios.Grupos_Id = Grupos.Id " +
				"WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				role = resultSet.getString("Description");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return role;
	}
	
	public void blockUser(String name) {
		Date now = new Date();
		String date = new SimpleDateFormat("dd/MM/yyyy").format( now );
		String time = new SimpleDateFormat("HH:mm:ss").format( now );
		String sql = "UPDATE Usuarios SET BlockedDate = '" + date + "'," +
				" BlockedTime = '" + time + "' WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}
	}
	
	public int getNumberOfAccess(String name) {
		int n = 0;
		String sql = "SELECT * FROM Usuarios WHERE UserName = '" + name + "';";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				n = resultSet.getInt("Access");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return n;
	}
	
	public int totalUsers() {
		int n = 0;
		String sql = "SELECT COUNT(*) AS N FROM Usuarios;";
		try {
			Statement stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);

			while(resultSet.next()) {
				n = resultSet.getInt("N");
				break;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Unable to realize '" + sql + "' command");
		}

		return n;
	}
	
}

