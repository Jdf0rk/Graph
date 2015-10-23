package core;



/**
 * Classe représentant les routes Entre le sommet au quel il est lié et le sommet d'arrivé
 * @author mdesousa
 *
 */
public class Route 
{
	private Sommet arrivee;
	private int dist;
	private Descripteur desc;

	public Route(Sommet arrivee, int dist, Descripteur des) 
	{
		this.arrivee = arrivee;
		this.dist = dist;
		this.desc=des;
	}
	public Sommet getArrivee() 
	{
		return arrivee;
	}
	public void setArrivee(Sommet arrivee) 
	{
		this.arrivee = arrivee;
	}
	public int getDist()
	{
		return dist;
	}
	public void setDist(int dist) 
	{
		this.dist = dist;
	}
	public Descripteur getDesc() 
	{
		return desc;
	}
	public void setDesc(Descripteur desc) 
	{
		this.desc = desc;
	}


}
