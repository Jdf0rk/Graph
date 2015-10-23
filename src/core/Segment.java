package core;


/**
 * Classe représentant un segment de route, (route in real life)
 * Utile pour dessiner l'ensemble des segements composant nos routes
 * @author mdesousa
 *
 */
public class Segment
{
	private int deb_x;
	private int deb_y;
	private int fin_x;
	private int fin_y;

	public Segment(int i, int j, int m, int n) 
	{
		this.deb_x=i;
		this.deb_y=j;
		this.fin_x=m;
		this.fin_y=n;
	}

	public int getDeb_x()
	{
		return deb_x;
	}
	public void setDeb_x(int deb_x) 
	{
		this.deb_x = deb_x;
	}
	public int getDeb_y() 
	{
		return deb_y;
	}
	public void setDeb_y(int deb_y) 
	{
		this.deb_y = deb_y;
	}
	public int getFin_x() 
	{
		return fin_x;
	}
	public void setFin_x(int fin_x) 
	{
		this.fin_x = fin_x;
	}
	public int getFin_y() 
	{
		return fin_y;
	}
	public void setFin_y(int fin_y) 
	{
		this.fin_y = fin_y;
	}

}
