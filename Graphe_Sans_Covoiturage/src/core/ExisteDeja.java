package core;

public class ExisteDeja extends RuntimeException{
	public ExisteDeja(String mess){
		System.out.println(mess);
	}
}
