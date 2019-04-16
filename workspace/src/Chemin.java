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
	
	
	
}
