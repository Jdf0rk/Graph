package core ;

import java.io.* ;
import base.Readarg ;

/**
 * L'algorythme A-Star permet un calcul du chemin le plus court avec orientation
 * On se permet de ne faire qu'une extention limité puisque Djikstra classique = Astar avec estimation = 0
 * @author mdesousa
 *
 */
public class PccStar extends Pcc 
{

	public PccStar(Graphe gr, PrintStream sortie, Readarg readarg)
	{
		super(gr, sortie, readarg,0) ;
		

		this.setstar(true);//ce booleen permet de declencher ou non l'algorithme a*

	}



}
