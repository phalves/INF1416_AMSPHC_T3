package controller;

import java.util.ArrayList;

public class PasswordTree {
	ArrayList<ArrayList> passwordTree ;
	
	public PasswordTree(){
		passwordTree = new ArrayList<ArrayList>();
	}
	
	public void dump()
	{
		System.out.println("size= "+passwordTree.size());
		for(int i = 0 ; i < passwordTree.size(); i++){
			@SuppressWarnings("unchecked")
			ArrayList<String> array = passwordTree.get(i);
			
			for(String s : array)
				System.out.print(s+" ");
			
			System.out.println();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buildPasswordTree(int step, ArrayList<Integer> userOptions )
	{
		int size = userOptions.size();
		if(step == 1)
		{
			ArrayList<String> password = new ArrayList<String>();
			ArrayList<String> secoundPassword = new ArrayList<String>();

			password.add(userOptions.get(size-2).toString());	
			passwordTree.add(password);
			secoundPassword.add(userOptions.get(size-1).toString());
			passwordTree.add(secoundPassword);
			
		}
		else
		{
			int i,j;
			double duplicate = Math.pow(2,step) / 2;
			for(i = 0 ; i < duplicate ; i++)
			{
				ArrayList<String> clonedArrayList = new ArrayList<String>();
				clonedArrayList = (ArrayList<String>) passwordTree.get(i).clone();
				passwordTree.add(clonedArrayList);
			}
			for(i = 0 ; i < (passwordTree.size()/2); i=i+2){
				passwordTree.get(i).add(userOptions.get(size-2).toString());
				passwordTree.get(i+1).add(userOptions.get(size-1).toString());
				
			}
			for(j = (int) duplicate; j < duplicate*2 ; j=j+2){
				passwordTree.get(j).add(userOptions.get(size-1).toString());
				passwordTree.get(j+1).add(userOptions.get(size-2).toString());
			}
		}
	}
}
