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
	private Sommet sommet;
	private TreeMap<Integer, Integer> avl;
	Chemin cheminEvac;
	
	Noeud(Sommet sommet, Chemin ce) {
		this.sommet=sommet;
		avl = new TreeMap<Integer, Integer>();
		cheminEvac=ce;
	}
	
	void addEvent(int date, int flux) {
		if (avl.containsKey(date)) {
			/*
			 * si un event déjà associé à cette date est dans l'avl, 
			 * on ajoute aux flux actuel qui y est associé le flux du nouvel
			 * event généré
			 */
			int fluxActuel = avl.get(date);
			avl.put(date, fluxActuel+flux);
		} else {
			/*
			 * si aucun event n'a été crée pour cette date on le crée avec
			 * le flux spécifié
			 */
			avl.put(date, flux);
		}
		
	}
	Sommet getSommet() {return sommet;}
	TreeMap<Integer, Integer> getAVL() {return avl;}
	Chemin getChemin() {return cheminEvac;}
	
	
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
					Noeud noeud = new Noeud(sommetSuivant, cheminEvac);
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
						
						//on met cptPop à 0 pour modéliser que tout le monde est sorti
						cptPopulation=0;
					}
					
					
				} 
			} 
			
			boolean over=false;
			//on vient de parcourir les feuilles
			while (ok&&!over) {
				int dateMin=Integer.MAX_VALUE;
				
				//on cherche la date minimale dans les events
				for (int i=0 ; i < noeuds.size() ; i++) {
					Noeud noeudCourant = noeuds.get(i);
					TreeMap<Integer, Integer> avlNC = noeudCourant.getAVL();
					if (!(avlNC.isEmpty())) {
						int date = avlNC.firstKey();
						if (date < dateMin) {
							dateMin=date;
						}
					}
				}
				
				for (int i=0 ; i < noeuds.size() ; i++) {
					
					Noeud noeudCourant = noeuds.get(i);
					TreeMap<Integer, Integer> avlNC = noeudCourant.getAVL();
					
					/*
					 * le nombre de personnes partant depuis ce noeud
					 * au temps courant
					 */
					if (avlNC.isEmpty()) {
						over=true;
					} else {
						over=false;
						int date = avlNC.firstKey();
						int flux = avlNC.remove(date);
						
						//ce noeud contient des events de la date minimale : on les traite
						if (date==dateMin) {
					
							//on récupère l'arc qui le lie au noeud suivant
							Sommet sommetSuivant = noeudCourant.getChemin().getNoeudSuivant(noeudCourant.getSommet());
							Arc arc = foret.recupArc(noeudCourant.getSommet().getId(), sommetSuivant.getId());
							
							//on vérifie si les events respectent bien les règles de l'arc
							if (arc.capacite < flux) {
								ok=false;
								System.out.print("Capa dépassée entre "+noeudCourant.getSommet().getId()+" et "+sommetSuivant.getId());
							}
							
							if (arc.dateExp < date) {
								ok=false;
								System.out.print("Arc expiré entre "+noeudCourant.getSommet().getId()+" et "+sommetSuivant.getId());
							}
							
							//on crée le noeud associé au noeud suivant s'il n'existe pas
							boolean found=false;
							int no = 0;
							while ((!found)&&(no < noeuds.size())) {
								if (noeuds.get(no).getSommet().getId()==sommetSuivant.getId()) {
									found=true;
									
								} else {
									no++;
								}
							}
							
							if (found) {
								//si le noeud est déjà existant on y ajoute l'event avec date courante + la durée de l'arc
								noeuds.get(no).addEvent(date+arc.duree, flux);
							} else {
								//sinon on le crée et on lui ajoute l'event avec date courant + la durée de l'arc
								Noeud noeud = new Noeud(sommetSuivant, noeudCourant.getChemin());
								noeuds.add(noeud);
								noeud.addEvent(date+arc.duree, flux);
							}
							
						
						
						}
					
					
					}
				}
			}
			
			
			
		
		}
		return ok;
		
	} 
	
	
	 public static void main(String[] args) {
		 TreeMap<Integer, Integer> avl = new TreeMap<Integer, Integer>();
		 avl.put(1, 3);
		 avl.put(1, 4);
		 avl.put(1, 5);
		 avl.put(1, 6);
		 
		 /*
		 int tempsMin = avl.firstKey();
			while (avl.firstKey()==tempsMin) {
				System.out.println("tm : "+avl.remove(avl.firstKey()));
			}
			
			*/
		 
		 int fk = avl.firstKey();
		 System.out.println("fk : "+fk);
		 System.out.println("value : "+avl.remove(fk));
		 
		 fk = avl.firstKey();
		 System.out.println("fk : "+fk);
		 System.out.println("value : "+avl.remove(fk));
		 
		}
		
}	
	 



