package core;

import java.util.ArrayList;

import base.*;

public class Chemin {

	private int idcarte;
	private int n_node_start;
	private int n_node_end;
	private ArrayList <Noeud> trajet;

	public int getIDcarte () {
		return this.idcarte;
	}

	public int getN_node_start () {
		return this.n_node_start;
	}

	public int getN_node_end () {
		return this.n_node_end;
	}

	public ArrayList <Noeud> getTrajet () {
		return this.trajet;
	}

	public void AddNoeud_ATrajet (Noeud N) throws ExisteDeja {
		for (Noeud Nbis : trajet){
			if (Nbis==N){
				throw new ExisteDeja("Le Noeud est déjà présente dans trajet");
			}
		}
		this.trajet.add(N);
	}


	public Chemin (int id, int debut, int fin) {
		this.idcarte=id;
		this.n_node_start=debut;
		this.n_node_end=fin;
		this.trajet= new ArrayList <Noeud> ();
	}

	public Chemin (int debut, int fin) {
		this.n_node_start=debut;
		this.n_node_end=fin;
		this.trajet= new ArrayList <Noeud> ();
	}

	/**
	 * Entre 2 sommets du graphe il peut y avoir plusieurs aretes ayant des poids differents.
	 * Cette fonction permet de choisir l'arete ayant le plus faible cout en distance.
	 * @param src_node sommet 1
	 * @param dest_node sommet 2
	 * @return un successeur ayant le plus faible cout en distance
	 */
	public Route Plus_faible_distance(Noeud src_node ,Noeud dest_node){
		Route R=null;
		int num_dest=dest_node.getNumero();
		int long_arete=0;
		int nb_passage=0;
		//pour chaque voisin du noeud noeud_dep
		for(Route Rbis: src_node.getListeDeRoutes()){
			//On examine les arêtes correspondant joignant les 2 noeuds
			//et on ne retient que celle qui a la plus petite largeur
			if(Rbis.getdest_node().getNumero()==num_dest){
				nb_passage++;
				if(nb_passage==1){
					long_arete=Rbis.getlongueur_route();
				};
				if(long_arete>=Rbis.getlongueur_route()){
					long_arete=Rbis.getlongueur_route();
					R=Rbis;
				}
			}
		}

		return R;
	}

	/**
	 * Entre 2 sommets du graphe il peut y avoir plusieurs aretes ayant des poids differents.
	 * Cette fonction permet de choisir l'arete ayant le plus faible cout en temps.
	 * @param noeud_dep sommet 1
	 * @param noeud_arr sommet 2
	 * @return un successeur ayant le plus faible cout en temps
	 */    
	public Route Plus_faible_temps(Noeud src_node ,Noeud dest_node){
		Route R=null;
		int num_dest=dest_node.getNumero();
		float temps_arete=0;
		int nb_passage=0;
		for(Route Rbis: src_node.getListeDeRoutes()){
			//On examine les arêtes correspondant joignant les 2 noeuds
			//et on ne retient que celle qui a le plus petit temps de trajet
			if(Rbis.getdest_node().getNumero()==num_dest){
				nb_passage++;
				if(nb_passage==1){
					temps_arete=((float)Rbis.getlongueur_route())/(Rbis.getdescr().vitesseMax());
				};
				if(temps_arete>=((float)Rbis.getlongueur_route())/(Rbis.getdescr().vitesseMax())){
					temps_arete=((float)Rbis.getlongueur_route())/(Rbis.getdescr().vitesseMax());
					R=Rbis;
				}
			}
		}
		return R;
	} 

	public void tracerChemin(Dessin dessin){
		float current_long, current_lat;
		System.out.println("Nb noeud du chemin ="+this.trajet.size());
		//Pour chaque noeud du chemin
		for(int node=0;node<this.getTrajet().size()-1;node++){
			current_long = this.getTrajet().get(node).getLong() ;
			current_lat  = this.getTrajet().get(node).getLat();
			//Pour chaque successeur du noeud numero node
			//on règle la largeur des traits et la couleur de l'affichage
			dessin.setWidth(2);
			dessin.setColor(java.awt.Color.blue);
			//On joint les sommets deux à deux
			dessin.drawLine(current_long, current_lat, this.getTrajet().get(node+1).getLong(),
					this.getTrajet().get(node+1).getLat()) ;		
		}
	}

	public void cout_chemin_distance(){

		float longueur=0;
		float temps=0;
		Route R;
		R=null;
		//Pour chaque noeud du chemin
		for(int node=0;node<this.getTrajet().size()-1;node++){
			R=Plus_faible_distance(this.getTrajet().get(node), this.getTrajet().get(node+1));
			longueur+=((float)R.getlongueur_route()/1000);
			//System.out.println(R.getdescr());
			temps+=60*((float)R.getlongueur_route())/(1000*(R.getdescr().vitesseMax()));

		}
		System.out.println("Longueur finale= "+longueur+" km");
		System.out.println("Temps finale= "+temps +" mn");
	}

	public void cout_chemin_temps(){
		float longueur=0;
		float temps=0;
		Route R;
		R=null;
		System.out.println("Nb noeud du chemin ="+this.getTrajet().size());
		//Pour chaque noeud du chemin
		for(int node=0;node<this.getTrajet().size()-1;node++){

			R=Plus_faible_temps(this.getTrajet().get(node), this.getTrajet().get(node+1));
			longueur+=((float)R.getlongueur_route()/1000);
			temps+=60*((float)R.getlongueur_route())/(1000*(R.getdescr().vitesseMax()));	
		}
		System.out.println("Longueur finale= "+longueur+" km");
		System.out.println("Temps finale= "+temps +" mn");
	}


}
