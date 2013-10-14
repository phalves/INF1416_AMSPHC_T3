package view;
import java.util.Scanner;

import database.DataBase;;

public class Main {

	public static void main(String[] args) {
		loginMenu();
	}
	
	public static void loginMenu()
	{
		int keepChoosing = 3;
		do{
			Scanner reader = new Scanner(System.in);  
	        
			System.out.println ("Entre com o seu USER NAME:");
	        String userLogin = reader.next();
			boolean isAuthenticated = authenticateUserLogin(userLogin);
			
			if(isAuthenticated == true)
			{	System.out.println("Usuario LOGADO!");
				break;
			}
			
			keepChoosing--;
		}while(keepChoosing >= 0);
	}
	
	private static boolean authenticateUserLogin(String userLogin)
	{
		DataBase db = DataBase.getDataBase();
		db.connectToDataBase();
		
		String foundName = db.selectName(userLogin);
		if (!foundName.equals("")) 
			return true;
		
		db.disconnectFromDataBase();
		return false;
	}
}