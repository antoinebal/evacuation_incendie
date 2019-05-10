package evac;
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
	
	
	Foret() {
		arcs = new ArrayList<Arc>();
		cheminsEvac = new ArrayList<Chemin>();
	}

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
	
	
public void printForet() {
	System.out.println("***FORET***");
	System.out.println("Nombre feuilles : "+nbFeuille);
	System.out.println("Nombre sommets : "+nbSommet);
	System.out.println("Nombre arcs : "+nbArc);
	System.out.println("Safe sommet : "+safe.id_);
	System.out.println();
	System.out.println("CHEMINS d'EVACUATION");
	
	for(int no=0 ; no < cheminsEvac.size() ; no++) {
		cheminsEvac.get(no).printChemin();
	}
	System.out.println() ; 
	System.out.println("ARCS");
	System.out.println() ; 
	
	for(int no=0 ; no < arcs.size() ; no++) {
		arcs.get(no).printArc();
	}
	
}
	
}
