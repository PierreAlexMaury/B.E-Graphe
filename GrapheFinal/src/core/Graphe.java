package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.io.* ;
//import java.util.*;

import base.* ;

public class Graphe {

	public static float vitesse_max;

	// Nom de la carte utilisee pour construire ce graphe
	private final String nomCarte ;

	// Fenetre graphique
	private final Dessin dessin ;

	// Version du format MAP utilise'.
	private static final int version_map = 4 ;
	private static final int magic_number_map = 0xbacaff ;

	// Version du format PATH.
	private static final int version_path = 1 ;
	private static final int magic_number_path = 0xdecafe ;

	// Identifiant de la carte
	private int idcarte ;

	// Numero de zone de la carte
	private int numzone ;

	/*
	 * Ces attributs constituent une structure ad-hoc pour stocker les informations du graphe.
	 * Vous devez modifier et ameliorer ce choix de conception simpliste.*/

	private Descripteur[] descripteurs ;

	//Tableau de Noeuds du graphe
	private Noeud[] Reseau;

	//Un chemin
	private Chemin chemin;

	// Deux malheureux getters.
	public Dessin getDessin() { return dessin ; }
	public int getZone() { return numzone ; }
	public Noeud[] getReseau() {return Reseau;}
	public String getNomCarte() {return nomCarte;}

	// Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
	public Graphe (String nomCarte, DataInputStream dis, Dessin dessin){

		this.nomCarte = nomCarte ;
		this.dessin = dessin ;
		Utils.calibrer(nomCarte, dessin) ;

		// Lecture du fichier MAP. 
		// Voir le fichier "FORMAT" pour le detail du format binaire.
		try {

			// Nombre d'aretes
			int edges = 0 ;

			// Verification du magic number et de la version du format du fichier .map
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_map, version, version_map, nomCarte, ".map") ;

			// Lecture de l'identifiant de carte et du numero de zone, 
			this.idcarte = dis.readInt () ;
			this.numzone = dis.readInt () ;

			// Lecture du nombre de descripteurs, nombre de noeuds.
			int nb_descripteurs = dis.readInt () ;
			int nb_nodes = dis.readInt () ;

			// Nombre de successeurs enregistr������������������������������������s dans le fichier.
			int nbsuccesseurs ;

			//flaot long, float lag
			float longitude;
			float latitude;

			// En fonction de vos choix de conception, vous devrez certainement adapter la suite.
			this.Reseau= new Noeud[nb_nodes];
			this.descripteurs = new Descripteur[nb_descripteurs] ;

			// Lecture des noeuds
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture du noeud numero num_node
				longitude= ((float)dis.readInt ()) / 1E6f ;
				latitude= ((float)dis.readInt ()) / 1E6f ;
				nbsuccesseurs= dis.readUnsignedByte() ;
				Reseau[num_node]= new Noeud(num_node,longitude, latitude, nbsuccesseurs);
			}

			Utils.checkByte(255, dis) ;

			Graphe.vitesse_max=Float.MIN_VALUE;
			
			// Lecture des descripteurs
			for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) {
				// Lecture du descripteur numero num_descr
				descripteurs[num_descr] = new Descripteur(dis) ;
				if (Graphe.vitesse_max<descripteurs[num_descr].vitesseMax())
					Graphe.vitesse_max=descripteurs[num_descr].vitesseMax();			
			}


			Utils.checkByte(254, dis) ;


			// Lecture des successeurs
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {

				// Lecture de tous les successeurs du noeud num_node
				for (int num_succ = 0 ; num_succ < Reseau[num_node].getNbsucc() ; num_succ++) {
					// zone du successeur
					int succ_zone = dis.readUnsignedByte() ;

					// numero de noeud du successeur
					int dest_node = Utils.read24bits(dis) ;

					// descripteur de l'arete
					int descr_num = Utils.read24bits(dis) ;

					// longueur de l'arete en metres
					int longueur  = dis.readUnsignedShort() ;

					// Nombre de segments constituant l'arete
					int nb_segm   = dis.readUnsignedShort() ;

					//cr������ation des routes sortantes pour un noeud (num_node)			
					Route R = new Route(Reseau[num_node],Reseau[dest_node], longueur, descripteurs[descr_num]);
					this.Reseau[num_node].AddRoute(R);

					//si le sens n'est pas unique on doit ajouter une ar������te dans l'autre sens (noeud destinataire ->noeud actuel)
					if(!descripteurs[descr_num].isSensUnique()&&(succ_zone==numzone)){
						Route Rdest=new Route(Reseau[dest_node],Reseau[num_node], longueur,descripteurs[descr_num]);
						this.Reseau[dest_node].AddRoute(Rdest);
					}

					edges++ ;

					Couleur.set(dessin, descripteurs[descr_num].getType()) ;

					float current_long = Reseau[num_node].getLong();
					float current_lat  = Reseau[num_node].getLat();

					// Chaque segment est dessine'

					for (int i = 0 ; i < nb_segm ; i++) {
						float delta_lon = (dis.readShort()) / 2.0E5f ;
						float delta_lat = (dis.readShort()) / 2.0E5f ;
						Reseau[num_node].getListeDeRoutes().get(num_succ).addSegment(new Segment (delta_lon, delta_lat));
						dessin.drawLine(current_long, current_lat, (current_long + delta_lon), (current_lat + delta_lat)) ;
						current_long += delta_lon ;
						current_lat  += delta_lat ;
					}

					// Le dernier trait rejoint le sommet destination.
					// On le dessine si le noeud destination est dans la zone du graphe courant.
					if (succ_zone == numzone) {
						dessin.drawLine(current_long, current_lat, Reseau[dest_node].getLong(), Reseau[dest_node].getLat()) ;
					}
				}
			}
			//Liste de routes pred
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				
				for (Route R : Reseau[num_node].getListeDeRoutes()) {
					Route Raux= new Route(Reseau[R.getdest_node().getNumero()],Reseau[num_node], R.getlongueur_route(),R.getdescr());
					Reseau[R.getdest_node().getNumero()].AddPred(Raux);
				}
				
			}

			Utils.checkByte(253, dis) ;

			System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, " 
					+ nb_descripteurs + " descripteurs.") ;
			System.out.println("la vitesse max trouvée est de : "+Graphe.vitesse_max);

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}
	

	/**
	 *  Attend un clic sur la carte et affiche le numero de sommet le plus proche du clic.
	 *  A n'utiliser que pour faire du debug ou des tests ponctuels.
	 *  Ne pas utiliser automatiquement a chaque invocation des algorithmes.
	 */
	public void situerClick() {

		System.out.println("Allez-y, cliquez donc.") ;

		if (dessin.waitClick()) {
			float lon = dessin.getClickLon() ;
			float lat = dessin.getClickLat() ;

			System.out.println("Clic aux coordonnees lon = " + lon + "  lat = " + lat) ;

			// On cherche le noeud le plus proche. O(n)
			float minDist = Float.MAX_VALUE ;
			int   noeud   = 0 ;

			for (int num_node = 0 ; num_node < Reseau.length ; num_node++) {
				float londiff = (Reseau[num_node].getLong() - lon) ;
				float latdiff = (Reseau[num_node].getLat() - lat) ;
				float dist = londiff*londiff + latdiff*latdiff ;
				if (dist < minDist) {
					noeud = num_node ;
					minDist = dist ;
				}
			}

			System.out.println("Noeud le plus proche : " + noeud) ;
			System.out.println() ;
			dessin.setColor(java.awt.Color.red) ;
			dessin.drawPoint(Reseau[noeud].getLong(), Reseau[noeud].getLat(), 5) ;
		}
	}

	/**
	 *  Charge un chemin depuis un fichier .path (voir le fichier FORMAT_PATH qui decrit le format)
	 *  Verifie que le chemin est empruntable et calcule le temps de trajet.
	 */
	public void verifierChemin(DataInputStream dis, String nom_chemin) {

		try {

			// Verification du magic number et de la version du format du fichier .path
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

			// Lecture de l'identifiant de carte
			int path_carte = dis.readInt () ;

			if (path_carte != this.idcarte) {
				System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
				System.exit(1) ;
			}

			int nb_noeuds = dis.readInt () ;

			// Origine du chemin
			int first_zone = dis.readUnsignedByte() ;
			int first_node = Utils.read24bits(dis) ;

			// Destination du chemin
			int last_zone  = dis.readUnsignedByte() ;
			int last_node = Utils.read24bits(dis) ;

			System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

			int current_zone = 0 ;
			int current_node = 0 ;

			//cr������ation du chemin

			this.chemin = new Chemin(path_carte, first_node, last_node);

			// Tous les noeuds du chemin
			for (int i = 0 ; i < nb_noeuds ; i++) {
				current_zone = dis.readUnsignedByte() ;
				current_node = Utils.read24bits(dis) ;
				this.chemin.AddNoeud_ATrajet(this.Reseau[current_node]);
			}

			chemin.cout_chemin_temps();
			chemin.tracerChemin(dessin);

			if ((current_zone != last_zone) || (current_node != last_node)) {
				System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
				System.exit(1) ;
			}

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}
	}

}
