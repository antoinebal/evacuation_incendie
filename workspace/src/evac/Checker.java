package evac;
//https://gist.github.com/nandor/9518116#file-avl-java-L109

import java.io.IOException;
import java.lang.Comparable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

public class Checker {

	Solution solution;
	
	Foret foret;
	
	ArrayList<Noeud> noeuds = new ArrayList<Noeud>();
	
    /*
	public class Event {
		int temps ; 
		int nbpers ;
		
		void translation(int duree) {
			temps = temps + duree;
		}
		
		 
	}
	*/
	
	public class Noeud {
	int id;
	TreeMap<Integer, Integer> avl;
	
	Noeud(int id) {
		this.id=id;
		avl = new TreeMap<Integer, Integer>();
	}
	
	void addEvent(int date, int flux) {
		avl.put(date, flux);
	}
	int getID() {return id;}
	TreeMap<Integer, Integer> getAVL() {return avl;}
	}
	
	
	public Checker (String fileName){
		    try {
		    	//on construit la forêt et la solution
		    	FileAgent fa = new FileAgent(fileName);
				solution = fa.processLineByLineSolution();
				FileAgent fa2 = new FileAgent("../InstancesInt/"+solution.nomInstance);
				foret=fa2.processLineByLineForest();
			} catch (IOException e) {
				e.printStackTrace();
			}
	 	  }
	
	public boolean checkSolution() {
		
		
		boolean ok = true ; 
		//I : le nombre de feuilles est il correct?
		if (solution.nbFeuilleEvac > foret.nbFeuille) { 
			ok = false ;
			System.out.println("Erreur : nombre de feuilles à évacuer plus grand que le nombre de feuilles de la forêt!"); 
		}
		else { 
			/* ici on voulait comparer pour chaque feuille de la soluce le taux d'évac
			 * à celui qui est vraiment dans la forêt
			 
			for (int i=0 ; i<solution.nbFeuAVL<Integer, Integer>();illeEvac ; i++) {
				
				//Sommet feuille = solution.feuilles.get(i) ; // rendre feuillesevac public !
				int txevacSol = solution.feuilles.get(i).tauxEvac ; 
				int maxRateFo = getMaxRate() (des feuilles de la foret);
				if (solution.feuilles.get(i)>foret (verif ok mais normalement deja bien fait) .
			}*/
			
			
			// Vérifier qu'on ne dépasse jamais la capacité des arc traversé
			
			// trouver les feuilles qui commencent à la date 0 et partir dessus
			
			// Pour chaque feuille, on a son taux evac
			// Commencer à évacuer par des paquets de cet taille la en suivant le chemin et ajustant le tuple 
			// (temps départ, nb pers sur cet arc à ce moment) 
			
			// TEMPS ??? T=0 ?? 
			
			//on parcourt toutes les feuilles de la solution et
			//on crée tous les évènements
			for (int i=0 ; i<solution.nbFeuilleEvac ; i++) { 
				
				int txEvacSol = solution.feuilles.get(i).tauxEvac ; 
				int debut = solution.feuilles.get(i).dateDebut ;  
				int idFeuille = solution.feuilles.get(i).id ; 	
								
				//on récup le chemin d'évac
				Chemin cheminEvac = foret.recupChemin(idFeuille);
				
				//on récup la feuille
				Sommet feuille = cheminEvac.feuille;
				
				//on récup l'arc entre la feuille et le premier sommet du chemin d'évac
				Sommet sommetSuivant = cheminEvac.getNoeudAt(0);
				Arc arc = foret.recupArc(idFeuille, sommetSuivant.getId());
				
				//on vérifie bien que le taux < capa arc
				if (txEvacSol > arc.getCapa()) {
					ok=false;
					System.out.println("mauvais taux d'évac de la feuille "+idFeuille+" dans la solution");
				}
				
				//on vérifie bien que le duedate > dateDebut
				else if (txEvacSol > arc.getDateExp()) {
					ok=false;
					System.out.println("mauvaise date de début de la feuille "+idFeuille+" dans la solution");
				}
				else { 
					//on va créer tous les events
					
					//compteur qui va être décrémenté à mesure que les events sont crées 
					int cptPopulation=feuille.getPop();
					
					//on crée le noeud de l'autre côté du chemin
					Noeud noeud = new Noeud(sommetSuivant.getId());
					noeuds.add(noeud);
					
					//variable à être incrémentée
					int temps = debut;
					while (cptPopulation > txEvacSol) {
						noeud.addEvent(temps, txEvacSol);
						cptPopulation = cptPopulation - txEvacSol;
						temps = temps + 1;
					}
					
					/*
					 * il reste peut être quelques personnes à passer, dont le nombre
					 * est inférieur au taux
					 */				
					if (cptPopulation != 0) {
						noeud.addEvent(temps, cptPopulation);
					}
					
					
				} 
			} 
			
			boolean over=false;
			//on vient de parcourir les feuilles
			while (ok&&!over) {
				for (int i=0 ; i < noeuds.size() ; i++) {
					
				}
			}
			
			
			
		
		}
		return ok;
		
	} 
		
}	
	 



