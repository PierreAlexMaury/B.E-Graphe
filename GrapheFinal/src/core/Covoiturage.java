package core;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Collections;


import base.Readarg;

public class Covoiturage extends Algo {

	public static boolean nature=true; //true=pieton, false=automobiliste
	private int Pieton;
	private int Auto;
	private int Dest;
	Pcc PCC = null ;
	Pcc PCCAuto = null ;
	private Label[] tab_pieton;
	private Label[] tab_auto;

	PccBackward PCCdest = null;
	private Label[] tab_dest;
	
	public void cheminPCC(int org, int dest, Pcc algo){
		Chemin chemin=new Chemin(org, dest);
		chemin.AddNoeud_ATrajet(this.graphe.getReseau()[dest]);
		Label labelcourant= algo.tab_label[dest];

		while((labelcourant).getfather()!=-1){
			Noeud node=this.graphe.getReseau()[labelcourant.getfather()];
			//System.out.println(labelcourant.getfather());
			chemin.AddNoeud_ATrajet(node);
			labelcourant = algo.tab_label[labelcourant.getfather()];
		}
		// on ajoute ������ la fin donc reverse 
		Collections.reverse(chemin.getTrajet());
		//Cout et affichage du chemin
		chemin.cout_chemin_distance();
		chemin.tracerChemin(this.graphe.getDessin());	
	}
	
	public void cheminPCCBack(int org, int dest, PccBackward algo){
		Chemin chemin=new Chemin(org, dest);
		chemin.AddNoeud_ATrajet(this.graphe.getReseau()[dest]);
		Label labelcourant= algo.tab_label[dest];

		while((labelcourant).getfather()!=-1){
			Noeud node=this.graphe.getReseau()[labelcourant.getfather()];
			//System.out.println(labelcourant.getfather());
			chemin.AddNoeud_ATrajet(node);
			labelcourant = algo.tab_label[labelcourant.getfather()];
		}
		// on ajoute ������ la fin donc reverse 
		Collections.reverse(chemin.getTrajet());
		//Cout et affichage du chemin
		chemin.cout_chemin_distance_back();
		chemin.tracerChemin_back(this.graphe.getDessin());	
	}

	public Covoiturage (Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr,sortie,readarg);

		this.Pieton = readarg.lireInt ("Numéro du sommet d'origine pieton? ") ;
		if((Pieton<=0)||(Pieton>graphe.getReseau().length)){
			System.out.println(" Le numero de sommet d'origine pieton dans covoit saisi n'appartient pas au graphe");
		}
		this.Auto = readarg.lireInt("Numéro du sommet d'origine auto? ");
		if((Auto<=0)||(Auto>graphe.getReseau().length)){
			System.out.println(" Le numero de sommet d'origine auto dans covoit saisi n'appartient pas au graphe");
		}
		this.Dest = readarg.lireInt("Numéro du somment d'origine destination? ");
		if((Dest<=0)||(Dest>graphe.getReseau().length)){
			System.out.println(" Le numero de sommet d'origine destination saisi n'appartient pas au graphe");
		}
	}


	public void run(){

		Readarg readarg=new Readarg(new String[0]);
		PCC = new Pcc (graphe, sortie, Pieton, readarg);
		PCC.choix=1;
		PCC.Dijkstra();
		tab_pieton=PCC.tab_label;
		

		//System.out.println("fin du 1er Dijkstra");
		nature=false;//
		
		PCCAuto = new Pcc(graphe, sortie, Auto, readarg);
		PCCAuto.choix=1;
		//System.out.println("debut du 2nd Dijkstra");
		PCCAuto.Dijkstra();		
		tab_auto=PCCAuto.tab_label;
		

		PCCdest = new PccBackward(graphe, sortie, Dest, readarg);
		PCCdest.choix=1;
		PCCdest.Dijkstra();
		tab_dest=PCCdest.tab_label;		

		float mincout = Float.POSITIVE_INFINITY;
		float currentcout=0;
		int imin=0;

		System.out.println(this.graphe.getReseau().length);
		for (int i=0; i<this.graphe.getReseau().length; i++) {

			try {
				currentcout=tab_pieton[i].getcout()+tab_auto[i].getcout()+tab_dest[i].getcout();
				if (currentcout<mincout){
					mincout=currentcout;
					imin=i;
				}
			}
			catch (NullPointerException e) {

			}

		}
		//On trace les différents chemins
		cheminPCC(Pieton, imin, PCC);
        System.out.println("\n");
		cheminPCC(Auto, imin, PCCAuto);
        System.out.println("\n");
		cheminPCCBack(Dest, imin, PCCdest);
        System.out.println("\n");
		
		System.out.println("noeud min "+ imin );
		System.out.println("cout min trouvé " + mincout);
		
		//test insa-> pieton=32 auto=38 dest=67 INSA
		//test midip-> pieton=144007 auto=110442 dest=69916
		
		//affichage des différents points du covoiturage.
		this.graphe.getDessin().setColor(Color.green);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[Pieton].getLong(),this.graphe.getReseau()[Pieton].getLat(), 8);
		this.graphe.getDessin().setColor(Color.black);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[Auto].getLong(),this.graphe.getReseau()[Auto].getLat(), 8);
		this.graphe.getDessin().setColor(Color.blue);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[Dest].getLong(),this.graphe.getReseau()[Dest].getLat(), 8);
		this.graphe.getDessin().setColor(Color.cyan);
		this.graphe.getDessin().drawPoint(this.graphe.getReseau()[imin].getLong(),this.graphe.getReseau()[imin].getLat(), 8);
	}
}

