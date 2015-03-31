package core;


public class Label_Dij extends Label implements Comparable<Label_Dij>{

	public Label_Dij(){
		super();
	}

	public Label_Dij (boolean marq, float cout, int fils){
		super(marq,cout,fils);
	}

	public Label_Dij (int courant){
		super(courant);
	}

	public int compareTo(Label_Dij aux) {
		float diff = this.getcout() - aux.getcout();
		return (diff < 0)  ? (-1) : (diff == 0) ? (0) : (1);			
	}
}

