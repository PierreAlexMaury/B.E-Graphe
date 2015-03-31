
package core ;

import java.awt.Color;
import java.io.* ;
import java.util.Collections;

import base.Readarg ;

public class PccStar extends Algo {

	// Numero des sommets origine et destination
	protected int zoneOrigine ;
	protected int origine ;

	protected int zoneDestination ;
	protected int destination ;

	protected Label_Star[] tab_label; //tableau de labels
	protected BinaryHeap<Label_Star> tas;  //Le tas

	protected long duree; //dur������e d'ex������cution
	protected int maxTas;//Nombre maximum d'elemnt dans le tas
	protected int nb_elements_explo;//Nombre d'element explor������s
	protected String sortieAlgo=""; //contient le r������sultat ������ enregister dans un fichier

	protected Label_Star dest ; //label destinataire
	protected int choix;//en temps (choix=1),  en distance (choix=0)
	protected String Affichage;//Afficher ou non le d������roulement de l'algo




	public PccStar(Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr, sortie, readarg) ;

		// Demander la zone et le sommet origine
		this.zoneOrigine = graphe.getZone () ;
		this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;

		if( (origine<=0) || (origine>graphe.getReseau().length)){
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
			CharSequence c=sortieAlgo;  //voir: API PrintStream(������crire dans un fichier de sortie) et CharSequence (string->char)
			sortie.append(c);
			System.exit(-1);
		}

		//en temps ou en distance?
		this.choix=readarg.lireInt("Plus court en:\n0-Distance\n1-Temps");
		//Afficher ou non le d������roulement de l'algo?
		this.Affichage=readarg.lireString("Voulez vous afficher le deroulement de l'algo (y) or (no) :");
	}


	public Label_Star getLabel(Label_Star[] tab, int num_node, int num_dest){
		if (tab[num_node]==null){
			tab[num_node]= new Label_Star(this.graphe.getReseau()[num_node],this.graphe.getReseau()[num_dest]);
		}
		return tab[num_node];
	}

	public void initialisation(){
		Label_Star source = getLabel(tab_label,origine, destination);
		source.setCout(0);
		tas.insert(source);

		this.dest = getLabel(tab_label, destination,destination);
		
	}

	public void affichercout(){

		if(choix==0){ //distance
			System.out.println("Le coût est de "+(dest.getcout())/1000+ "km\n"+ //on divise ici par 1000 puisque si on le fait  ��chaque boucle trop petite valeur
					"Temps de Calcul: "+duree+ " ms\n"+
					"Nb max d'element: "+maxTas+"\nNb élements explorés: "+nb_elements_explo);
			sortieAlgo+="Le coût est de "+(dest.getcout())/1000+ "km\n"+ 
					"Temps de Calcul: "+duree+ " ms\n"+
					"Nb max d'element: "+maxTas+"\nNb éléments explorés: "+nb_elements_explo+"\n";
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
		Label_Star labelcourant= dest;

		while((labelcourant).getfather()!=-1){
			Noeud node=this.graphe.getReseau()[labelcourant.getfather()];
			//System.out.println(labelcourant.getfather());
			chemin.AddNoeud_ATrajet(node);
			labelcourant = tab_label[labelcourant.getfather()];
		}
		// on ajoute �� la fin donc reverse 
		Collections.reverse(chemin.getTrajet());
		//Cout et affichage du chemin
		chemin.cout_chemin_distance();
		chemin.tracerChemin(this.graphe.getDessin());	
	}

	public void run() {



		System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

		this.tab_label = new Label_Star[graphe.getReseau().length];
		this.tas=new BinaryHeap <Label_Star> ();
		this.maxTas=tas.size();
		this.nb_elements_explo=1;
		float nouv_cout=0;
		this.duree=System.currentTimeMillis(); //dur��e execution
		Label.temps=choix==1;
		initialisation();

		while (!tas.isEmpty() && !dest.getmarquage()) {
			Label_Star min=tas.deleteMin(); //on supprime le min actuel du tas et on retourne sa valeur pour pouvoir s'en servir

			if (min.getmarquage()){ // si deja "vrai" on saute toute la boucle et on repart du while
				continue;
			}
			min.setMarquage(true);

			for(Route r: this.graphe.getReseau()[min.getcurrent_node()].getListeDeRoutes()) {
				Noeud nsucc = r.getdest_node();			
				Label_Star labsucc = getLabel(tab_label,nsucc.getNumero(),destination);

				if(!(labsucc.getmarquage())){

					if (choix==0){ //mise �� jour du cout (en distance ou en temps)
						nouv_cout=(r.getlongueur_route())+min.getcout();
					}

					else{
						//getEstim:metre vit_max:km/h
						float temps_arc=(60.0f*(float)r.getlongueur_route())/((r.getdescr().vitesseMax())*1000);
						nouv_cout=temps_arc+min.getcout();
					}

					if (labsucc.getcout()>nouv_cout){ //set papa et le cout du succ
						labsucc.setCout(nouv_cout);
						labsucc.setFather(r.getsrc_node().getNumero());
						//Mise �� jour du tas

						if (tas.update(labsucc)==true){						
							nb_elements_explo++;

							if(Affichage.compareTo("y")==0){
								this.graphe.getDessin().setColor(Color.green);
								this.graphe.getDessin().drawPoint(nsucc.getLong(),nsucc.getLat(), 2);
							}
						}
					}

				}
			}
			if(maxTas<tas.size()){ // Mise �� jour du nombre maximal d'��l��ment dans le tas
				maxTas=tas.size();
			}
		}

		duree=(System.currentTimeMillis()-duree); //temps de calcul
		System.out.println("Duree= "+duree+" ms");	
		affichercout();	//Afficher le r��sultat du calcul
		CharSequence b=sortieAlgo;
		sortie.append(b); //Ecrire le r��sultat dans le fichier de sortie
		cheminDij(); //Tracer le chemin 
		this.graphe.getDessin().setColor(Color.black);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[origine].getLong(),this.graphe.getReseau()[origine].getLat(), 5);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[destination].getLong(),this.graphe.getReseau()[destination].getLat(), 5);
	}
}








