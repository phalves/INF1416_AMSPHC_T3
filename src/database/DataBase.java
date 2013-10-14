package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}

