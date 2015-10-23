package core;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Scanner;

import base.*;

/**
 * Classe qui va gérer l'option covoiturage dans son ensemble.
 * A modifier pour redispatcher dans les classe deja creer 
 * @author mdesousa
 *
 */
public class Covoiturage extends Algo{
	
	//trois algortihmes de recherche différents, un pour chaque origine et un pour la destination
	private Pcc _recherche1;
	private Pcc _recherche2;
	private Pcc _recherchedest;
	
	//nombre maximum de minutes que marche le piéton
	private double _max_minutes;
	//ses getters et setters.
	public void setMax(double m) {
		this._max_minutes=m;
	}
	
	public double getMax() {
		return _max_minutes;
	}
	//constructeur de l'algo de covoiturage
	protected Readarg _read;
	
	public Covoiturage(Graphe g,PrintStream sortie, Readarg readarg){
		super(g,sortie,readarg);
		this._read=readarg;
	}
	
    //INVERSION DE GRAPHES : on modifie notre graphe. si l'on souhaite retrouver le graphe originel, on re-inverse le graphe inverse
	public void inverser(){
		//pour inverser le graphe, on interchange la liste des routes entrantes et celle des routes sortantes.
		for (int i=0;i<this.graphe.sommets.size();i++) {
			this.graphe.sommets.get(i).buffer=this.graphe.sommets.get(i).routes;
			this.graphe.sommets.get(i).routes=this.graphe.sommets.get(i).routes_entrantes;
		}
		
	}
	
	public void annuler_inversion() {
		//fonction inverse de la précédente.
		for (int i=0;i<this.graphe.sommets.size();i++) {
			this.graphe.sommets.get(i).routes_entrantes=this.graphe.sommets.get(i).routes;
			this.graphe.sommets.get(i).routes=this.graphe.sommets.get(i).buffer;
		}
		
	}
			
	
	public void run() {
		//distance max du piéton
		System.out.println("Entrer le temps maximal de chemin acceptable par le piéton, en minutes : ");
		Scanner sc = new Scanner(System.in);
		this.setMax(sc.nextDouble());
		
		//on effectue trois recherches PCC d'affil�e, et on extrait les r�sultats des trois hashmaps ainsi cr�ees
		System.out.println("Veuillez entrer le sommet piéton");
		_recherche1=new Pcc(graphe,sortie,_read,1);
		_recherche1.run();
		System.out.println("Veuillez entrer le sommet voiture");
		_recherche2=new Pcc(graphe,sortie,_read,2);
		_recherche2.run();
		System.out.println("Veuillez entrer le sommet destination");
		this.inverser();
		_recherchedest=new Pcc(graphe,sortie,_read,2);
		_recherchedest.run();
		this.annuler_inversion();
		
		//recherche de minimum sur tous les sommets du graphe
		double cout_min=Double.MAX_VALUE;
		int opti=Integer.MAX_VALUE;
		for(int i=0;i<this.graphe.sommets.size();i++){
			
			//on minimise le cout, en extrayant les couts de chacuns des trois algos de djikstra pour les sommets donn�s
			//on ne considère ce sommet QUE SI le piéton marche moins de X minutes
			double cout1=_recherche1.cout_vers(this.graphe.sommets.get(i));
			if(cout1<=this.getMax()){
				double cout2=_recherche2.cout_vers(this.graphe.sommets.get(i));
				double cout3=_recherchedest.cout_vers(this.graphe.sommets.get(i));
				if ((Math.max(cout1, cout2)+cout3)<cout_min) {
					cout_min=(Math.max(cout1, cout2)+cout3);
					opti=i;
				}
			}
		}
		//on affiche le sommet optimal de rendez vous, puis les trois chemins optimaux 
		this.graphe.getDessin().setColor(Color.gray);
    	this.graphe.getDessin().drawPoint(this.graphe.sommets.get(opti).getlon(), this.graphe.sommets.get(opti).getlat(), 10);
    	
    	//affichage des chemins
    	_recherche1.print_chemin(this.graphe.sommets.get(opti));
    	_recherche2.print_chemin(this.graphe.sommets.get(opti));
    	this.inverser();
    	_recherchedest.print_chemin(this.graphe.sommets.get(opti));
    	this.annuler_inversion();
    	
    	//affichage des couts
    	System.out.println("Cout optimal en temps : "+cout_min+" min");
    	System.out.println("Re ndez vous au sommet "+opti);
	}
	
	
}
