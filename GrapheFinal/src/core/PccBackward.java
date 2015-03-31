package core ;

import java.awt.Color;
import java.io.* ;
import java.util.Collections;

import base.Readarg ;

public class PccBackward extends Algo { //Pcc sur les listes de prédecesseurs 

	// Numero des sommets origine et destination
	protected int zoneOrigine ;
	protected int origine ;

	protected int zoneDestination ;
	protected int destination ;

	protected Label_Dij[] tab_label; //tableau de labels
	protected BinaryHeap<Label_Dij> tas;  //Le tas

	//protected HashMap< Noeud,Label_Dij> mapLabel; //fait correspondre un noeud à un label
	protected long duree; //durée d'exécution
	protected int maxTas;//Nombre maximum d'elemnt dans le tas
	protected int nb_elements_explo;//Nombre d'element explorés
	protected String sortieAlgo=""; //contient le résultat à enregister dans un fichier

	protected Label_Dij dest ; //label destinataire
	protected int choix;//en temps (choix=1),  en distance (choix=0)
	protected String Affichage;//Afficher ou non le déroulement de l'algo



	public PccBackward(Graphe gr, PrintStream sortie, int depart, Readarg readarg) {
		super(gr, sortie, readarg) ;

		// Demander la zone et le sommet origine
		this.zoneOrigine = graphe.getZone () ;
		this.origine = depart;
		if((origine<=0)||(origine>graphe.getReseau().length)){
			System.out.println(" Le numero de sommet d'origine backward saisi n'appartient pas au graphe");
			sortieAlgo="Carte: "+graphe.getNomCarte()+"\nLe sommet origine "+origine+" n'est pas dans le graphe\n\n";
			CharSequence c=sortieAlgo;
			sortie.append(c);
			System.exit(-1);
		}

		// Demander la zone et le sommet destination.
		this.zoneDestination = graphe.getZone () ;
		this.destination = -1;

		//en temps
		this.choix=1;
		//Afficher déroulement algo
		this.Affichage="y";
	}

	public PccBackward(Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr, sortie, readarg) ;

		// Demander la zone et le sommet origine
		this.zoneOrigine = graphe.getZone () ;
		this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;
		if((origine<=0)||(origine>graphe.getReseau().length)){
			System.out.println(" Le numero de sommet saisi n'appartient pas au graphe");
			sortieAlgo="Carte: "+graphe.getNomCarte()+"\nLe sommet origine "+origine+" n'est pas dans le graphe\n\n";
			CharSequence c=sortieAlgo;
			sortie.append(c);
			System.exit(-1);
		}

		// Demander la zone et le sommet destination.
		this.zoneDestination = graphe.getZone () ;
		this.destination = readarg.lireInt ("Numero du sommet destination ? ");
		if((destination<=0)||(destination>graphe.getReseau().length)){
			System.out.println(" Le numero de sommet saisi n'appartient pas au graphe");
			sortieAlgo="Carte: "+graphe.getNomCarte()+"\nLe sommet destination "+origine+" n'est pas dans le graphe\n\n";
			CharSequence c=sortieAlgo;  //voir: API PrintStream(écrire dans un fichier de sortie) et CharSequence (string->char)
			sortie.append(c);
			System.exit(-1);
		}

		//en temps ou en distance?
		this.choix=readarg.lireInt("Plus court en:\n0-Distance\n1-Temps");
		//Afficher ou non le déroulement de l'algo?
		this.Affichage=readarg.lireString("Voulez vous afficher le deroulement de l'algo (y) or (n) :");
	}


	public Label_Dij getLabel(Label_Dij[] tab, int num_node){
		if (tab[num_node]==null){
			tab[num_node]= new Label_Dij(num_node);
		}
		return tab[num_node];
	}

	public void initialisation(){
		Label_Dij o = getLabel(tab_label, origine);
		o.setCout(0);
		tas.insert(o);
		if (destination != -1)
			this.dest = getLabel(tab_label, destination);
	}

	public void affichercout(){

		if(choix==0){ //distnode].getListePred()ance
			System.out.println("Le coût est de "+(dest.getcout())/1000+ "km\n"+ //on divise ici par 1000 puisque si on le fait  ������chaque boucle trop petite valeur
					"Temps de Calcul: "+duree+ " ms\n"+
					"Nb max d'element: "+maxTas+"\nNb élements explorés: "+nb_elements_explo);
			sortieAlgo+="Le coût est de "+(dest.getcout())/1000+ "km\n"+ 
					"Temps de Calcul: "+duree+ " ms\n"+
					"Nb max d'element: "+maxTas+"\nNb élements explorés: "+nb_elements_explo+"\n";
		}    

		else{
			System.out.println("Le coût est de "+ dest.getcout()+ " mn\n"+
					"Temps de Calcul: "+duree+ " ms\n"+
					"Nb max d'élément dans le tas : "+maxTas+"\nNombre d'éléments explorés: "+nb_elements_explo);
			sortieAlgo+="Le coût est de "+ dest.getcout()+ " mn\n"+
					"Temps de Calcul: "+duree+ " ms\n"+
					"Nb max d'élément dans le tas : "+maxTas+"\nNombre d'éléments explorés: "+nb_elements_explo+"\n";
		}
	}

	// Memorise le resultat dans un chemin
	public void cheminDij(){
		Chemin chemin=new Chemin(origine, destination);
		chemin.AddNoeud_ATrajet(this.graphe.getReseau()[this.destination]);
		Label labelcourant= dest;

		while((labelcourant).getfather()!=-1){
			Noeud node=this.graphe.getReseau()[labelcourant.getfather()];
			//System.out.println(labelcourant.getfather());
			chemin.AddNoeud_ATrajet(node);
			labelcourant = tab_label[labelcourant.getfather()];
		}
		// on ajoute à la fin donc reverse 
		Collections.reverse(chemin.getTrajet());
		//Cout et affichage du chemin
		chemin.cout_chemin_distance();
		chemin.tracerChemin(this.graphe.getDessin());	
	}

	public void Dijkstra () {

		this.tab_label = new Label_Dij[graphe.getReseau().length];
		this.tas=new BinaryHeap <Label_Dij> ();
		this.maxTas=tas.size();
		this.nb_elements_explo=1;
		float nouv_cout=0;
		this.duree=System.currentTimeMillis(); //durée execution

		initialisation();

		boolean bool=false;
		if (destination<0||(destination>graphe.getReseau().length))
			bool=true;
		else
			bool=!dest.getmarquage();

		while (!tas.isEmpty() && bool ) {
			Label_Dij min=tas.deleteMin(); //on supprime le min actuel du tas et on retourne sa valeur pour pouvoir s'en servir	

			if (min.getmarquage()){ // si deja "vrai" on saute toute la boucle et on repart du while
				continue;
			}
			min.setMarquage(true);

			for(Route r: this.graphe.getReseau()[min.getcurrent_node()].getListePred()) {
				Noeud nsucc = r.getdest_node();
				Label_Dij labsucc = getLabel(tab_label, nsucc.getNumero());

				if(!(labsucc.getmarquage())){

					if (choix==0){ //en distance
						nouv_cout=(r.getlongueur_route())+min.getcout();
					}
					if (choix==1) { //choix==1 temps

						if (destination==-1 && Covoiturage.nature){ //pieton
							//################### A 4km/h l'auto va toujours chercher le pieton donc on test �� 15km/h (v��lo)
							nouv_cout=60*((float)r.getlongueur_route())/(1000*30)+min.getcout();

							if (r.getdescr().vitesseMax()==110 || r.getdescr().vitesseMax()==130)
								nouv_cout=Float.POSITIVE_INFINITY;

						}
						else{ //automobiliste					
							nouv_cout=60*((float)r.getlongueur_route())/(1000*(r.getdescr().vitesseMax()))+min.getcout(); //cout en min
						}
					}

					if (labsucc.getcout()>nouv_cout){ //set papa et le cout du succ
						labsucc.setCout(nouv_cout);
						labsucc.setFather(r.getsrc_node().getNumero());

						//Mise à jour du tas
						if (tas.update(labsucc)==true){						
							nb_elements_explo++;

							if(Affichage.compareTo("y")==0){
								this.graphe.getDessin().setColor(Color.magenta);
								this.graphe.getDessin().drawPoint(nsucc.getLong(),nsucc.getLat(), 2);
							}
						}
					}

				}
				if(maxTas<tas.size()){ // Mise à jour du nombre maximal d'élément dans le tas
					maxTas=tas.size();
				}

			}
		}

		duree=(System.currentTimeMillis()-duree); //temps de calcul
		System.out.println("Duree= "+duree+" ms");

	}

	public void run() {

		System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

		Dijkstra();


		affichercout();	//Afficher le résultat du calcul
		CharSequence b=sortieAlgo;
		sortie.append(b); //Ecrire le résultat dans le fichier de sortie
		cheminDij(); //Tracer le chemin 
		this.graphe.getDessin().setColor(Color.black);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[origine].getLong(),this.graphe.getReseau()[origine].getLat(), 5);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[destination].getLong(),this.graphe.getReseau()[destination].getLat(), 5);
	}

}
