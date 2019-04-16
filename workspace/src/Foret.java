import java.util.ArrayList;

public class Foret {
	

	// Nb de feuilles 
	int nbFeuille ;
	
	//nb sommets
	int nbSommet ; 

	//nb arcs 
	int nbArc ; 
	
	// Racine 
	Sommet safe ; 
	
	
	//tous les chemins d'évacuation
	//le transformer en MAP{sommet à évacuer, chemin vers le noeud safe}
	ArrayList<Chemin> cheminsEvac;
	
	// Tous les arcs 
	ArrayList<Arc> arcs ;
	
	
	Foret() {}

	Foret (int nbFeuille, int nbSommet, int nbArc, Sommet safe,	ArrayList<Arc> arcs,ArrayList<Chemin> cheminsEvac) { 
		
		this.nbFeuille = nbFeuille ; 
		this.nbSommet = nbSommet ;
		this.nbArc = nbArc ; 
		this.safe = safe ; 
		this.cheminsEvac = cheminsEvac ; 
		this.arcs = arcs ; 
		
	}
	
	void setNbFeuille(int nbFeuille) {
		this.nbFeuille=nbFeuille;
	}
	
	void setNbSommet(int nbSommet) {
		this.nbSommet=nbSommet;
	}
	
	void setNbArc(int nbArc) {
		this.nbArc=nbArc;
	}
	
	
	void setSafe(Sommet safe) {
		this.safe=safe;
	}
	
	void ajoutArc(Arc arc) {
		arcs.add(arc);
	}
	
	void ajoutChemin(Chemin chemin) {
		cheminsEvac.add(chemin);
	}
	
	
}
