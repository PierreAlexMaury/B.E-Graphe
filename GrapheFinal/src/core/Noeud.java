package core;

import java.util.*;

public class Noeud {

	private float longitude;
	private float latitude;
	private int numero;
	private int nb_successeur;
	private ArrayList <Route> ListeDeRoutes;
	private ArrayList <Route> ListePred;

	public int getNumero() {
		return this.numero;
	}

	public ArrayList<Route> getListePred() {
		return ListePred;
	}

	public void setListePred(ArrayList<Route> listePred) {
		ListePred = listePred;
	}

	public float getLong(){
		return this.longitude; 
	}

	public float getLat(){
		return this.latitude;
	}

	public int getNbsucc(){
		return this.nb_successeur;
	}

	public Noeud(int numero,float longitude, float latitude, int nb_successeur){
		this.numero=numero;
		this.longitude=longitude;
		this.latitude=latitude;
		this.nb_successeur=nb_successeur;
		this.ListeDeRoutes= new ArrayList <Route> ();
		this.ListePred= new ArrayList <Route> ();
	}

	public void AddRoute (Route R) throws ExisteDeja {
		for (Route Rbis : ListeDeRoutes){
			if (Rbis==R){
				throw new ExisteDeja("La route est déjà présente dans la liste de routes");
			}
		}
		this.ListeDeRoutes.add(R);
	}

	public void AddPred (Route R) throws ExisteDeja {
		for (Route Rbis : ListePred){
			if (Rbis==R){
				throw new ExisteDeja("La route est déjà présente dans la liste de pred");
			}
		}
		this.ListePred.add(R);
	}	
	

	public ArrayList<Route> getListeDeRoutes() {
		return ListeDeRoutes;
	}

}
