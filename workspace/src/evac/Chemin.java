package evac;
import java.util.ArrayList;

public class Chemin {

	//noeud à évcuer
	Sommet feuille ;
	int taille ; 
	// Le noeud feuille n'est pas contenu dans le chemin, de taille taille 
	ArrayList<Sommet> chemin;
	
	
	Chemin(Sommet feuille, int taille, ArrayList<Sommet> chemin) {
		this.feuille=feuille;
		this.taille=taille;
		this.chemin=chemin;
	}
	
	void setTaille(int taille) {this.taille = taille;}
	
	Sommet getFeuille() {return feuille;}
	
	Sommet getNoeudAt(int index) {return chemin.get(index);}
	
	public void printChemin() {
		System.out.println("Feuille : "+feuille.id_+" ; max pack : "+feuille.maxPack_+" ; nb personnes : "+feuille.nbPers_);
		for(int no=0 ; no < chemin.size() ; no++) {
			System.out.println(chemin.get(no).id_);
		}
	}
	
}
