package core;

public class Label {

	private boolean marquage;
	private float cout;
	private int father;
	private int current_node;
	public static boolean temps=false;

	public boolean getmarquage () {
		return this.marquage;
	}

	public float getcout () {
		return this.cout;
	}

	public int getfather () {
		return this.father;
	}

	public int getcurrent_node () {
		return this.current_node;
	}

	public void setMarquage(boolean marquage) {
		this.marquage = marquage;
	}

	public void setCout(float cout) {
		this.cout = cout;
	}

	public void setFather(int father) {
		this.father = father;
	}

	public void setCurrent_node(int current_node) {
		this.current_node = current_node;
	}

	public Label (boolean marq, float cout, int fils){
		this.marquage=marq;
		this.cout=cout;
		this.father=-1;
		this.current_node=fils;
	}//si -1 c'est que pas de pere

	public Label () {
	
	}

	public Label (int courant){
		this.marquage=false;
		this.cout= Float.POSITIVE_INFINITY;
		this.father=-1;
		this.current_node=courant;
	}//si -1 c'est que pas de pere


}
