package core ;

import java.awt.Color;
import java.io.* ;
import java.util.*;

import base.Readarg ;

public class Pcc extends Algo {


	// Numero des sommets origine et destination
	protected int zoneOrigine ;
	protected int origine ;

	protected int zoneDestination ;
	protected int destination ;

	//booleen indiquant si l'on est en astar ou pas
	private boolean star=false;

	//booleen indiquant si l'on fait la recherche en temps ou distance(false=temps)
	private boolean dist_temps=false;

	//entier lie au covoiturage : 0 si on est en pcc standard, 1 pour le pieton et 2 pour la voiture
	private int covoit=0;

	//getters et setters
	public void setcovoit(int cv) {this.covoit=cv;}
	public int getcovoit(){return this.covoit;}

	public void setstar(boolean star){this.star=star;}
	public boolean getstar(){return this.star;}

	public void setdist(boolean dt){this.dist_temps=dt;}
	public boolean getdist(){return this.dist_temps;}
	//tas et hashmap 
	private BinaryHeap<Label> tas=new BinaryHeap<Label>();

	private HashMap<Sommet,Label> maplabel=new HashMap<Sommet, Label>();

	//liste de tous les labels qu'on a marque lors de l'algorithme.
	//le plus court chemin vers un sommet est extrait en prenant le sommet dans la liste, puis son pere, puis le pere de son pere...jusqu'a l'origine
	public ArrayList<Label> chem=new ArrayList<Label>();
	//on a modifie le constructeur pour indiquer si l'on veut optimiser un covoiturage ou effectuer une simple recherche de PCC
	public Pcc(Graphe gr, PrintStream sortie, Readarg readarg, int covoit) {
		super(gr, sortie, readarg) ;
		//integer lie au covoiturage : 0 = pas de covoiturage, 1=pieton, 2=voiture
		this.setcovoit(covoit);

		if (covoit==0)
		{
			//ALGORITHME CLASSIQUE DE PCC
			this.zoneOrigine = gr.getZone () ;
			while ((this.origine = readarg.lireInt ("Numero du sommet d'origine ? "))>=this.graphe.sommets.size()) {//le sommet n'appartient pas au graphe ! 
				System.out.println("Sommet inexistant ! Retapez un numéro de sommet.");
			}

			// Demander la zone et le sommet destination.
			this.zoneDestination = gr.getZone () ;
			while ((this.destination = readarg.lireInt ("Numero du sommet destination ? "))>=this.graphe.sommets.size()) {//le sommet n'appartient pas au graphe ! 
				System.out.println("Sommet inexistant ! Retapez un numéro de sommet.");
			}
		}
		else 
		{
			//ALGORITHME DE COVOITURAGE
			//Seul le sommet d'origine nous interesse car on calcule les plus courts chemins vers tous les autres.
			//on instancie arbitrarement le sommet de destination sur celui d'origine, puisque dans tout les cas
			//on a ajoute une condition dans l'algorithme qui le force a calculer le PCC vers tous les autres sommets, sans s'arreter a la destination.
			this.zoneOrigine = gr.getZone () ;
			while ((this.origine=readarg.lireInt ("Numero du sommet? "))>=this.graphe.sommets.size()) 
			{//le sommet n'appartient pas au graphe
				System.out.println("Sommet inexistant ! Retapez un numéro de sommet.");
			}
			this.zoneDestination = gr.getZone () ;
			this.destination=this.origine;

		}

	}


	public void run() {

		if (!star) {System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;}
		if (star) {System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;}
		
		//decision temps ou distance. Si on est en mode covoiturage, ce n'est pas nécessaire car on est forcément en temps
		if (covoit==0) {
			Scanner sc = new Scanner(System.in);
			System.out.println("En temps(0) ou distance(1)?");
			if (sc.nextInt()==1) {this.setdist(true);}
		}
		//chronometrage
		long start_time = System.currentTimeMillis();

		Label l=new Label(false,0.0,null,this.graphe.sommets.get(origine));
		chem.add(l);
		tas.insert(l);
		maplabel.put(this.graphe.sommets.get(origine), l);

		//affichage de l'origine et de la destination
		this.graphe.getDessin().setColor(Color.BLUE);
		this.graphe.getDessin().drawPoint(this.graphe.sommets.get(origine).getlon(), this.graphe.sommets.get(origine).getlat(), 10);
		this.graphe.getDessin().drawPoint(this.graphe.sommets.get(destination).getlon(), this.graphe.sommets.get(destination).getlat(), 10);

		//compteur d'elements explores
		int nb_explor=0;
		int tas_max=0;

		while (!(tas.isEmpty())) {
			//extraction du minimum
			Label pere=tas.findMin();
			tas.deleteMin();

			//on arrete le programme une fois la destination trouvee, sauf si l'on ne souhaite pas arreter ---> covoiturage
			if (pere.getCourant().getNum()==destination&&(covoit==0)){
				System.out.println("Algorithme termine!");
				break;
			}

			pere.setMarquage(true);
			chem.add(pere);

			for (Route route : pere.getCourant().routes) {
				Sommet suiv=route.getArrivee();
				//affichage du parcours en temps reel. On ne le fait pas dans l'optimisation de covoiturage
				//car cela surcharge trop le dessin.
				if (this.getcovoit()==0){
					this.graphe.getDessin().setColor(Color.gray);
					this.graphe.getDessin().drawLine(suiv.getlon(), suiv.getlat(), pere.getCourant().getlon(), pere.getCourant().getlat());
				}
				//declarations des variables de cout, cout secondaire et estimation
				double new_cout=0.0;
				
				double estim = 0.0;     //ESTIMATION VERY IMPORTANT
				
				double cout_sec=0.0;

				if (this.dist_temps)//Cout n distance
				{
					double vitesse=((double)(route.getDesc().vitesseMax()));
					new_cout=pere.getCout()+((double)(route.getDist()));

					//cout secondaire, ici en temps
					cout_sec=pere.getCoutSec()+(60.0*((double)(route.getDist()))/(1000.0*vitesse));
					if (this.star) {estim=suiv.dist_vol_oiseau(this.graphe.sommets.get(destination));}
				}
				else 
				{
					//Cout en temps
					double vitesse=((double)(route.getDesc().vitesseMax()));
					//cas du pieton
					if (this.getcovoit()==1&&(vitesse==130.0||vitesse==110.0)){

						//ici, le pieton ne peut pas acceder a cette route,
						//elle est reservee aux voitures, le cout est donc le plus grand possible
						new_cout=Double.MAX_VALUE;
					}
					else {
						if (this.getcovoit()==1) {vitesse=4.0;}
						new_cout=pere.getCout()+(60.0*((double)(route.getDist()))/(1000.0*vitesse));

						//cout secondaire, ici en distance
						cout_sec=pere.getCoutSec()+((double)(route.getDist()));
					}
					
					// Ici on modifie le calcul de l'estimation dans le cas d'un calcul Astar.  VERY IMPORTANT
					if (this.star) {estim=suiv.cout_vol_oiseau(this.graphe.sommets.get(destination),this.graphe.getvitmax());}  
				}

				//on regarde si le sommet a deja un label associe avec un cout different de l'infini)
				//pour ce faire, on verifie juste qu'il est integre a la hashmap ou pas
				if (maplabel.containsKey(suiv)){
					maplabel.get(suiv).setEstim(estim);

					//maj du label, et de la hashmap
					//on ne verifie pas si le sommet est marque : en effet, s'il l'est, son cout
					//est deja minimal et la condition suivante sera toujours fausse.
					if (maplabel.get(suiv).getCout()>new_cout) {
						maplabel.get(suiv).setCout(new_cout);
						maplabel.get(suiv).setPere(pere.getCourant());
						maplabel.get(suiv).setCoutSec(cout_sec);
						tas.update(maplabel.get(suiv));
					}
				}
				else {
					//le sommet a un cout infini : on cree un label, et on l'integre a la hashmap
					Label lab=new Label(false,new_cout,pere.getCourant(),suiv);
					lab.setEstim(estim);
					lab.setCoutSec(cout_sec);
					tas.insert(lab);
					maplabel.put(suiv, lab);

					//compteurs de performance
					nb_explor++;
					if (tas.size()>tas_max) {tas_max=tas.size();}
				}
			}



		}
		// on affiche le chemin 
		this.print_chemin(this.graphe.sommets.get(destination));

		//fin du chronometre
		long end_time = System.currentTimeMillis();
		long difference = end_time-start_time;

		//on affiche les performances
		System.out.println("Temps écoulé en millisecondes : "+difference);
		System.out.println("Elements explorés : "+nb_explor);
		System.out.println("Taille max du tas : "+tas_max);

	}	
	//methodes covoiturage
	//renvoie le cout vers un chemin en particulier a partir de la hashmap
	public double cout_vers(Sommet S) 
	{
		if (maplabel.containsKey(S)){return maplabel.get(S).getCout();}
		else{return Double.MAX_VALUE;}
	}
	//dessin et affichage d'un chemin depuis l'origine à partir de l'origine vers un sommet en particulier
	public void print_chemin(Sommet dest) 
	{
		if (maplabel.containsKey(dest))
		{
			boolean var=true;
			Label lab=maplabel.get(dest);
			while  (var) 
			{

				//dessin du chemin. on n'affiche pas le chemin noeud par noeud car ca prend trop de place dans la console
				if (lab.getCourant()==this.graphe.sommets.get(origine)){break;}
				this.graphe.getDessin().setColor(Color.GREEN);
				this.graphe.getDessin().setWidth(3);
				this.graphe.getDessin().drawLine(lab.getCourant().getlon(), lab.getCourant().getlat(), lab.getPere().getlon(), lab.getPere().getlat());
				this.graphe.getDessin().setWidth(1);
				if (lab.getPere()==this.graphe.sommets.get(origine)){break;}
				lab=maplabel.get(lab.getPere());
			}
			//affichage du cout final
			if (this.getdist()&&(this.getcovoit()==0)) {System.out.println(maplabel.get(dest).getCout()/1000.0+" kilomètres et "+maplabel.get(dest).getCoutSec()+" min");}
			else if(this.getcovoit()==0){System.out.println(maplabel.get(dest).getCout()+" min et "+maplabel.get(dest).getCoutSec()/1000.0+" kilomètres");}
		}
		else {//le sommet destination n'est pas dans la hashmap->cout infini
			System.out.println("Cout infini");}
	}
}
