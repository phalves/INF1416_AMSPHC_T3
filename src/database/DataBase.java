package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

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
	
}

