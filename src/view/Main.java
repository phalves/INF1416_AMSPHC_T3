package view;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import database.DataBase;;

public class Main {

	public static void main(String[] args) {
		firstStep();
		secoundStep();
	}
	
	public static void firstStep()
	{
		boolean keepChoosing = true;
		Scanner reader = new Scanner(System.in); 
		do{
	
			System.out.println ("Entre com o seu USER NAME:");
	        String userLogin = reader.next();
			boolean isAuthenticated = authenticateUserLogin(userLogin);
			
			if(isAuthenticated == true)
			{
				boolean isUserBlocked = verifyIfUserIsBlocked(userLogin);
				if(isUserBlocked == true)
				{
					System.out.println("Usuário com STATUS BLOQUEADO.");
				}
				else{
					System.out.println("Usuário com STATUS DESBLOQUEADO.");
					keepChoosing = false;
				}
			}
			else{
				System.out.println("ATENCAO - Usuário inválido.");
			}

			System.out.println("********************************\n");

		}while(keepChoosing);
		return;
	}
	
	public static void secoundStep()
	{
		ArrayList<Integer> userOptions = new ArrayList<Integer>();
		int keepChoosing = 6;
		Scanner reader = new Scanner(System.in); 
		int chances = 3;
		while(chances >= 0)
		{
			do{
				ArrayList<Integer> userPsswd = getAssortUserPassWord();
				System.out.println("Escolha a SENHA PESSOAL");
				System.out.println(String.format("1- %s,%s", userPsswd.get(0),userPsswd.get(1)));
				System.out.println(String.format("2- %s,%s", userPsswd.get(2),userPsswd.get(3)));
				System.out.println(String.format("3- %s,%s", userPsswd.get(4),userPsswd.get(5)));
				System.out.println(String.format("4- %s,%s", userPsswd.get(6),userPsswd.get(7)));
				System.out.println(String.format("5- %s,%s", userPsswd.get(8),userPsswd.get(9)));
				
				String choose = reader.next();
				userOptions = addNumberSelect(userOptions, choose, userPsswd);
				keepChoosing--;
				
			}while(keepChoosing > 0);
			chances = -1;
		}
		
		for(int i = 0; i < userOptions.size(); i++)
			System.out.println(userOptions.get(i));

		reader.close();
	}
	
	private static ArrayList<Integer> addNumberSelect(ArrayList<Integer> userOption,
			String choose, ArrayList<Integer> userPsswd)
	{
		switch(Integer.parseInt(choose))
		{
			case 1:
				userOption.add(userPsswd.get(0));
				userOption.add(userPsswd.get(1));
				break;
			case 2:
				userOption.add(userPsswd.get(2));
				userOption.add(userPsswd.get(3));
				break;
			case 3:
				userOption.add(userPsswd.get(4));
				userOption.add(userPsswd.get(5));
				break;
			case 4:
				userOption.add(userPsswd.get(6));
				userOption.add(userPsswd.get(7));
				break;
			case 5:
				userOption.add(userPsswd.get(8));
				userOption.add(userPsswd.get(9));
				break;
			default:
				break;
		}
		
		return userOption;
	}
	
	private static ArrayList<Integer> getAssortUserPassWord()
	{
		ArrayList<Integer> numbers = getNumbers();
		ArrayList<Integer> assortedNumbers = new ArrayList<Integer>();
		
		Collections.shuffle(numbers);
		for(int i=0;i<10;i++)
		{	
			Integer number = numbers.indexOf(i);
			assortedNumbers.add(number);
		}
		return assortedNumbers;
	}
	
	private static ArrayList<Integer> getNumbers()
	{
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i<10; i++)
			numbers.add(i);
		return numbers;
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

	private static boolean verifyIfUserIsBlocked(String userLogin)
	{
		DataBase db = DataBase.getDataBase();
		db.connectToDataBase();

		if(db.isUserBlocked(userLogin))
			return true;

		db.disconnectFromDataBase();
		return false;
	}

}