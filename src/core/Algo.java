package core ;

import java.io.* ;
import base.* ;

/**
 * Classe abstraite representant un algorithme (connexite, plus court chemin, etc.)
 * Utilisation de la polymorphie dans la classe Launch.java Algo = new classefille();
 */
public abstract class Algo {

	
    protected PrintStream sortie ;
    protected Graphe graphe ;
    
    /**
     * Méthode appelée si l'algo dans la classe Launch est attribué.
     * @param gr
     * @param fichierSortie
     * @param readarg
     */
    protected Algo(Graphe gr, PrintStream fichierSortie, Readarg readarg) {  
	this.graphe = gr ;
	this.sortie = fichierSortie ;	
    }
    
    public abstract void run() ; //Méthode à implementer pour chaque algo

}
