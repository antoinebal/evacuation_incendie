package evac;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

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
	
	//pour récupérer le chemin d'évac d'une feuille avec son id
	Chemin recupChemin(Integer idFeuille) {
		for (int i=0 ; i < cheminsEvac.size() ; i++) {
			if (cheminsEvac.get(i).getFeuille().getId()==idFeuille) {
				cheminsEvac.get(i).printChemin();
				return cheminsEvac.get(i);
			} 
		}
		System.out.println("FEUILLE INEXISTANTE");
		return null;
	}
	
	Arc recupArc(int id1, int id2) throws NoSuchElementException{
		for(int no=0 ; no < arcs.size() ; no++) {
			if ((arcs.get(no).getS1().getId()==id1)&&(arcs.get(no).getS2().getId()==id2)) {
				return arcs.get(no);
			}
		}
		throw new NoSuchElementException();
		
		
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


public static void main(String[] args) {
	try {
    	//on construit la forêt et la solution
    	FileAgent fa = new FileAgent("../InstancesInt/graphe-TD-sans-DL-data.txt");
		Foret foret=fa.processLineByLineForest();
		
		System.out.println(foret.recupArc(0, 1));
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	
}
