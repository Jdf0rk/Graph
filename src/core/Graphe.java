package core ;
import java.util.*;
import java.awt.*;
import java.io.* ;

import base.* ;

public class Graphe{

	// Nom de la carte utilisee pour construire ce graphe
	private final String nomCarte ;

	//vitesse max, instanciée lrosqu'on parcourt les descripteurs, et utile pour le pcc-star en temps.
	private double vit_max=0.0;

	public double getvitmax(){
		return this.vit_max;
	}
	// Fenetre graphique
	private final Dessin dessin ;
	//variables utilisées par le constructeur

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
	 * Vous devez modifier et ameliorer ce choix de conception simpliste.
	 */
	//LISTE DE SOMMETS	
	ArrayList <Sommet> sommets=new ArrayList<Sommet>();

	//LISTE DESCRIPTEURS
	ArrayList <Descripteur> desc=new ArrayList<Descripteur>();
	private int nb_som;

	// Deux malheureux getters.
	public Dessin getDessin() { return dessin ; }
	public int getZone() { return numzone ; }


	// Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
	public Graphe (String nomCarte, DataInputStream dis, Dessin dessin) {

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
			nb_som=nb_nodes;

			// Nombre de successeurs enregistrÃ©s dans le fichier.
			int[] nsuccesseurs_a_lire = new int[nb_nodes] ;

			// Adapter en fonction de nos choix
			float longit;
			float latit;

			// Lecture des noeuds
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture du noeud numero num_node

				longit = ((float)dis.readInt ()) / 1E6f ;
				latit = ((float)dis.readInt ()) / 1E6f ;
				Sommet S=new Sommet(num_node,longit,latit);
				sommets.add(S);
				nsuccesseurs_a_lire[num_node] = dis.readUnsignedByte() ;

			}
			// Lecture des descripteurs
			Utils.checkByte(255, dis) ;

			for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) {
				// Lecture du descripteur numero num_descr
				Descripteur D = new Descripteur(dis) ;
				desc.add(D);
				if ((double)D.vitesseMax()>this.vit_max) {this.vit_max=(double)D.vitesseMax();}
				//On affiche quelques descripteurs parmi tous.
				// if (0 == num_descr % (1 + nb_descripteurs / 400))
				//System.out.println("Descripteur " + num_descr + " = " + desc.get(num_descr)) ;
			}

			Utils.checkByte(254, dis) ;

			// Lecture des routes
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) 
			{
				// Lecture de tous les successeurs du noeud num_node
				for (int num_succ = 0 ; num_succ < nsuccesseurs_a_lire[num_node] ; num_succ++) 
				{
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

					//instanciation route
					Route R = new Route(sommets.get(dest_node),longueur,desc.get(descr_num));
					sommets.get(num_node).routes.add(R);

					//instanciation des routes du graphe inverse : on instancie les routes entrantes du sommet
					Route R_inverse = new Route(sommets.get(num_node),longueur,desc.get(descr_num));
					sommets.get(dest_node).routes_entrantes.add(R_inverse);

					if (!(desc.get(descr_num).isSensUnique())){//si la route est a double sens...
						//Instanciation des routes du graphe normal
						Route R1 = new Route(sommets.get(num_node),longueur,desc.get(descr_num));
						sommets.get(dest_node).routes.add(R1);

						//Instanciation des routes du graphe inverse
						Route R1_inverse = new Route(sommets.get(dest_node),longueur,desc.get(descr_num));
						sommets.get(num_node).routes_entrantes.add(R1_inverse);

					}

					edges++ ;

					Couleur.set(dessin, desc.get(descr_num).getType()) ;

					float current_long = sommets.get(num_node).getlon() ;
					float current_lat  = sommets.get(num_node).getlat() ;

					// Chaque segment se dessine
					for (int i = 0 ; i < nb_segm ; i++) 
					{
						float delta_lon = (dis.readShort()) / 2.0E5f ;
						float delta_lat = (dis.readShort()) / 2.0E5f ;
						dessin.drawLine(current_long, current_lat, (current_long + delta_lon), (current_lat + delta_lat)) ;
						current_long += delta_lon ;
						current_lat  += delta_lat ;
					}

					// Le dernier trait rejoint le sommet destination.
					// On le dessine si le noeud destination est dans la zone du graphe courant.
					if (succ_zone == numzone) 
					{
						dessin.drawLine(current_long, current_lat, sommets.get(dest_node).getlon(), sommets.get(dest_node).getlat()) ;
					}
				}
			}

			Utils.checkByte(253, dis) ;

			System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, " 
					+ nb_descripteurs + " descripteurs.") ;

		}
		catch (IOException e) 
		{
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

	// Rayon de la terre en metres
	private static final double rayon_terre = 6378137.0 ;

	/**
	 *  Calcule de la distance orthodromique - plus court chemin entre deux points à la surface d'une sphère
	 *  @param long1 longitude du premier point.
	 *  @param lat1 latitude du premier point.
	 *  @param long2 longitude du second point.
	 *  @param lat2 latitude du second point.
	 *  @return la distance entre les deux points en metres.
	 *  Methode ecrite par Thomas Thiebaud, mai 2013
	 */
	public static double distance(double long1, double lat1, double long2, double lat2) {
		double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
		double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
		double cosLong = Math.cos(Math.toRadians(long2-long1));
		return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
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

			for (int num_node = 0 ; num_node < nb_som ; num_node++) {
				float londiff = (sommets.get(num_node).getlon() - lon) ;
				float latdiff = (sommets.get(num_node).getlat() - lat) ;
				float dist = londiff*londiff + latdiff*latdiff ;
				if (dist < minDist) {
					noeud = num_node ;
					minDist = dist ;
				}
			}

			System.out.println("Noeud le plus proche : " + noeud) ;
			System.out.println() ;
			dessin.setColor(java.awt.Color.red) ;
			dessin.drawPoint(sommets.get(noeud).getlon(), sommets.get(noeud).getlat(), 5) ;
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
			int previous_node = 0 ;
			int previous_zone = 0 ;
			Chemin C = new Chemin(sommets.get(first_node),sommets.get(last_node));
			previous_zone = dis.readUnsignedByte();
			previous_node = Utils.read24bits(dis);
			System.out.println(" --> " + previous_zone + ":" + previous_node) ;
			// Tous les noeuds du chemin
			for (int i = 1 ; i < nb_noeuds ; i++) {
				current_zone = dis.readUnsignedByte() ;
				current_node = Utils.read24bits(dis) ;
				System.out.println(" --> " + current_zone + ":" + current_node) ;
				
				
				//Parametre necessaire pour trouver la plus courte route entre deux sommets
				int A=2000000000;
				int B=0;
				
				for (int j=0;j<(this.sommets.get(previous_node).routes.size());j++)
				{
					if (this.sommets.get(previous_node).routes.get(j).getArrivee().getNum()==current_node) {
						//on cherche la plus courte route entre les deux sommets, au cas ou il y en aie plusieurs
						if (A>this.sommets.get(previous_node).routes.get(j).getDist()){
							A=this.sommets.get(previous_node).routes.get(j).getDist();
							B=j;
						}
					}
				}
				C.routes.add(this.sommets.get(previous_node).routes.get(B));
				previous_node=current_node;
				previous_zone=current_zone;
			}

			if ((current_zone != last_zone) || (current_node != last_node)) {
				System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
				System.exit(1) ;
			}
			C.setCout();
			dessin.setColor(Color.green);
			C.drawChemin(dessin);
			System.out.println("Cout : "+C.getCout()+ " min, Distance totale : "+C.getDist_tot()+" km");

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

}
