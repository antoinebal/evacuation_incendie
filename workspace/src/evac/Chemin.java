package evac;
import java.io.IOException;
import java.nio.file.Path;
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
	
	//retourne le sommet qui suit le sommet en argument
	Sommet getNoeudSuivant(Sommet sommet) {
		int indexSommet = chemin.indexOf(sommet);
		if (indexSommet == chemin.size()-1) {
			//sommet est le noeud safe donc on retourne null
			return null;
		} else {
			return chemin.get(indexSommet+1);
		}
	}
	
	/*
	 * longueur du chemin en temps si jamais
	 * on la parcourait sans s'arrêter
	 */
	public int dureeChemin(Foret foret) {
		int duree=0;
		for (int i = 0 ; i < chemin.size()-1 ; i++) {
			duree=duree+foret.recupArc(chemin.get(i).getId(), chemin.get(i+1).getId()).getDuree();
		}
		//System.out.println(duree);
		return duree;
	}
	
	public void printChemin() {
		System.out.println("Feuille : "+feuille.id_+" ; max pack : "+feuille.maxPack_+" ; nb personnes : "+feuille.nbPers_);
		for(int no=0 ; no < chemin.size() ; no++) {
			System.out.println(chemin.get(no).id_);
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
