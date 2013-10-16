package view;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.text.html.HTMLEditorKit.Parser;

import model.authentication.User;
import controller.Conversor;
import controller.PasswordTree;
import database.DataBase;

public class Main {

	private static User user;
	
	public static void main(String[] args) {
		int choose;
		
		//generateSalt();
		firstStep();
		secoundStep();

		cabecalho();
		if(user.getRole().equals("1"))
		{
			adminCorpo1();
			choose = adminCorpo2();
			
			switch (choose) {
			case 1:
				//tela de cadastro
				break;
			case 2:
				//tela de consulta
				break;
			case 4:
				//sair do sistema
				break;

			default:
				break;
			}
		}
		else{
			/*Visao de Usuario comum*/
		}
		
	}

	private static int adminCorpo2() {
		Scanner reader = new Scanner(System.in);
		
		System.out.println("\n>>> CORPO 2 <<<");
		System.out.println("Menu Principal:");
		System.out.println("1- Cadastrar um novo usuario");
		System.out.println("2- Consultar pasta de arquivos secretos");
		System.out.println("4- Sair do sistema");
		
		String choose = reader.next();
		
		return Integer.parseInt(choose);
	}

	private static void adminCorpo1() {
		System.out.println("\n>>> CORPO 1 <<<");
		int totalOfAccess = getNumberOfAccess(user.getLoginName());
		System.out.println("Total de acessos- "+totalOfAccess);
	}

	private static void cabecalho() {
		System.out.println("\n>>> CABECALHO <<<");
		System.out.println("Login: "+user.getLoginName());
		System.out.println("Grupo: "+user.getRole());
		System.out.println("Descricao do usuario "+user.getNomeProprio());
		
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
					user = new User();
					user = getUser(userLogin);
					
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
		boolean status;
		ArrayList<ArrayList> possiblePasswords = new ArrayList<ArrayList>();
		ArrayList<Integer> userOptions = new ArrayList<Integer>();
		int keepChoosing = 1;
		Scanner reader = new Scanner(System.in); 
		int chances = 3;
		PasswordTree password = new PasswordTree();
		while(chances > 0)
		{
			keepChoosing=1;
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
				possiblePasswords = password.buildPasswordTree(keepChoosing, userOptions);
				
				keepChoosing++;
				
			}while(keepChoosing < 7);
			
			status = comparePasswords(possiblePasswords,chances);
			if(status == true)
				break;
			else
				chances--;
		}
		
		if(chances == 0)
		{
			blockUser(user.getLoginName());
		}
	}
	
	private static boolean comparePasswords(
			ArrayList<ArrayList> possiblePasswords, int chances) {
		String salt = user.getSALT();
		String passwd = user.getPasswd();
		
		ArrayList <String> possiblePasswordsList = new ArrayList <String>();
		
		for(int i=0; i<possiblePasswords.size(); i++)
		{
			String pass = "";
			for(int j=0; j<possiblePasswords.get(i).size(); j++){
				pass = pass+possiblePasswords.get(i).get(j);
			}
			possiblePasswordsList.add(pass);
		}
		for (String s : possiblePasswordsList) {

			String utf8_plainText = s + salt;
			
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.update(utf8_plainText.getBytes());
				
				byte[] digest = messageDigest.digest();
				
			    if (Conversor.byteArrayToHexString(digest).equals(passwd)) {
			    	System.out.println("Senhas COMFEREM!");
			    	setNumberOfAttempts(chances, user.getLoginName(), 1);
			    	return true;
			    }
				
			} catch (NoSuchAlgorithmException exception) {
				exception.printStackTrace();
			}
		}
		
		System.out.println("Senhas NAO CONFEREM!");
		return false;
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
	
	public static String selectUserSALT(String userLogin) {
		DataBase db = DataBase.getDataBase();
		db.connectToDataBase();
		
		String SALT = db.selectSALT(userLogin);
		
		db.disconnectFromDataBase();
		
		return SALT;
	}
	
	public static String selectUserPasswd(String userLogin) {
		DataBase db = DataBase.getDataBase();
		db.connectToDataBase();
		
		String passwd = db.selectPasswd(userLogin);
		
		db.disconnectFromDataBase();
		
		return passwd;
	}
	
	public static void setNumberOfAttempts(int numberOfAttempts, String name, int state) {
		DataBase db = DataBase.getDataBase();
		db.connectToDataBase();
		
		db.setNumberOfAttempts(numberOfAttempts, name, state);
		
		db.disconnectFromDataBase();
	}
	
	public static String selectUserNomeProprio(String name) {
		DataBase db = DataBase.getDataBase();
		String userName = "";
		
		db.connectToDataBase();
		
		userName.equals(db.selectNomeProprio(name));
		
		db.disconnectFromDataBase();
		return userName;
	}
	
	public static String getUserRole(String name) {
		DataBase db = DataBase.getDataBase();
		String role = "";
		
		db.connectToDataBase();
		
		role.equals(db.getUserRole(name));
		
		db.disconnectFromDataBase();
		return role;
	}
	
	public static User getUser(String name) {
		DataBase db = DataBase.getDataBase();
		User user = new User();
		
		db.connectToDataBase();
		
		user = db.getUser(name);
		
		db.disconnectFromDataBase();
		return user;
	}
	
	public static void blockUser(String name) {
		DataBase db = DataBase.getDataBase();
				
		db.connectToDataBase();
		
		db.blockUser(name);
		
		db.disconnectFromDataBase();
	}
	
	public static int getNumberOfAccess(String name) {
		DataBase db = DataBase.getDataBase();
		int totalOfAccess;
		
		db.connectToDataBase();
		
		totalOfAccess = db.getNumberOfAccess(name);
		
		db.disconnectFromDataBase();
		
		return totalOfAccess;
	}
	
	public static int totalUsers() {
		DataBase db = DataBase.getDataBase();
		int totalUsers;
		
		db.connectToDataBase();
		
		totalUsers = db.totalUsers();
		
		db.disconnectFromDataBase();
		
		return totalUsers;
	}
	
	
	/***
	 * Metodos auxiliares
	 */
	private static void generateSalt() {
		String passwdToStore = null;
		String senha = "123456";
		String salt = String.valueOf((int)( 999999999*Math.random() ));
		
		System.out.println("Salt - "+salt);
		
		String utf8_plainText = senha + salt;
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(utf8_plainText.getBytes());

			byte[] digest = messageDigest.digest();

			passwdToStore = Conversor.byteArrayToHexString(digest);
			System.out.println("PasswdToStore - "+passwdToStore);
		} catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
		}
	}

}