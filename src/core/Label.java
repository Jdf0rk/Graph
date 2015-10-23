package core;


public class Label implements Comparable<Label> // On utilise cette héritage pour beneficier de la méthode compareTo
{
	//utilitaires djikstra
	private boolean marquage;
	//cout principal, celui utilise pour le tas
	private double cout;
	//cout secondaire temps pour un algorithme en distance, et distance pour un algorithme en temps)
	private double cout_secondaire;
	//estimation, pour A*
	private double estimation=0.0;
	//sommets pere et actuel
	private Sommet pere;
	private Sommet courant;

	/**
	 * OVERRIDE
	 * C'est la méthode magique qui va nous permettre
	 * de comparer les différents labels pour les ordonner
	 */
	public int compareTo(Label lab)
	{
		if ((this.cout+this.estimation)<(lab.getCout()+lab.getEstim())) 
		{
			return -1;
		}
		else if ((this.cout+this.estimation)==(lab.getCout()+lab.getEstim())) 
		{
			if (this.estimation<lab.getEstim()) 
			{
				return 1;
			}
			else if (this.estimation==lab.getEstim()) 
			{
				return 0;
			}
			else 
			{
				return -1;
			}
		}
		else 
		{
			return 1;
		}
	}

	public Label(boolean marquage, double cout, Sommet pere, Sommet courant)
	{
		this.marquage = marquage;
		this.cout = cout;
		this.pere = pere;
		this.courant = courant;
	}
	//getters, setters, et tostring
	public String toString()
	{
		return "label "+courant.getNum()+" cout "+cout+" estim "+estimation+" somme "+(cout+estimation)+marquage;
	}

	public boolean isMarquage() 
	{
		return marquage;
	}
	public void setMarquage(boolean marquage) 
	{
		this.marquage = marquage;
	}
	public double getCout()
	{
		return cout;
	}
	public void setCout(double cout) 
	{
		this.cout = cout;
	}
	public double getCoutSec() 
	{
		return cout_secondaire;
	}
	public void setCoutSec(double cout) 
	{
		this.cout_secondaire = cout;
	}
	public double getEstim() 
	{
		return estimation;
	}
	public void setEstim(double cout) 
	{
		this.estimation = cout;
	}
	public Sommet getPere()
	{
		return pere;
	}
	public void setPere(Sommet pere) 
	{
		this.pere = pere;
	}
	public Sommet getCourant() 
	{
		return courant;
	}
	public void setCourant(Sommet courant)
	{
		this.courant = courant;
	}

}
