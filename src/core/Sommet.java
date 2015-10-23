package core;
import java.util.*;

/**
 * Classe représentant un sommet
 *  Our Vertex
 * @author mdesousa
 *
 */
public class Sommet {
	
	// Num identifiant 
	private int num;
	
	//Coordonnées
	private float lon;
	private float lat;
	
	//Liste des routes liés au sommet
	ArrayList <Route> routes=new ArrayList<Route>();
	
	//listes pour l'inversion de graphe : routes entrantes et un buffer utile pour switcher entre entrantes et sortantes
	ArrayList <Route> routes_entrantes=new ArrayList<Route>();
	ArrayList <Route> buffer=new ArrayList<Route>();
	
	//Constructeur, j'ai renoncer a la classe coordonnée
	//De peur d'avoir trop d'acces mémoire supplémentaire du a la structure objet
	public Sommet(int num, float lon, float lat) {
		this.num = num;
		this.lon = lon;
		this.lat = lat;
	}
	
	public double dist_vol_oiseau(Sommet dest){
		// calcul de la distance a vol d'oiseau vers le sommet dest  (a star)
		double x=Graphe.distance(this.getlon(),this.getlat(),dest.getlon(),dest.getlat());
		return x;
	}
	
	public double cout_vol_oiseau(Sommet dest, double vit){
		//calcul du cout temporel minimal a vol d'oiseau entre ce sommet et le sommet destination (Astar)
		double x=this.dist_vol_oiseau(dest);
		double y=(60.0*x)/(vit*1000.0);
		return y;
	}
	//getters, setters
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public float getlon() {
		return lon;
	}
	public void setlon(float lon) {
		this.lon = lon;
	}
	public float getlat() {
		return lat;
	}
	public void setlat(float lat) {
		this.lat = lat;
	}
	
	
}
