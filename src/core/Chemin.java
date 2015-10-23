package core;
import java.util.*;
import base.*;

/**
 * Classe représentant le chemin entre un sommet et un autre
 * Peut être défini par plusieurs routes (Souvent le cas)
 * @author mdesousa
 *
 */
public class Chemin
{
	private Sommet first;
	private Sommet last;
	private float cout;
	private float dist_tot;
	ArrayList <Route> routes=new ArrayList<Route>();

	public Chemin (Sommet s1,Sommet s2) 
	{
		this.first=s1;
		this.last=s2;
	}

	public Sommet getFirst() 
	{
		return first;
	}

	public void setFirst(Sommet first)
	{
		this.first = first;
	}

	public Sommet getLast()
	{
		return last;
	}

	public void setLast(Sommet last) 
	{
		this.last = last;
	}

	public void setCout() 
	{
		int i;
		float res=0;
		float dist=0;
		for (i=0;i<(this.routes.size());i++) 
		{
			res=res+60*((float)(this.routes.get(i).getDist())/1000)/((float)(this.routes.get(i).getDesc().vitesseMax()));
			dist=dist+(((float)this.routes.get(i).getDist())/1000);
		}
		this.cout=res;
		this.dist_tot=dist;
	}

	public float getDist_tot() 
	{
		return dist_tot;
	}


	public float getCout ()
	{
		return this.cout;
	}

	public void drawChemin(Dessin d)
	{
		d.drawLine(this.first.getlon(),this.first.getlat(),this.routes.get(0).getArrivee().getlon(), this.routes.get(0).getArrivee().getlat());
		for (int i=1;i<this.routes.size();i++)
		{
			d.drawLine(this.routes.get(i-1).getArrivee().getlon(), this.routes.get(i-1).getArrivee().getlat(), this.routes.get(i).getArrivee().getlon(),this.routes.get(i).getArrivee().getlat());
		}
	}

}
