package evac;
//https://gist.github.com/nandor/9518116#file-avl-java-L109

import java.io.IOException;
import java.lang.Comparable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class Checker {

	Solution solution;
	
	Foret foret;
	
	ArrayList<Noeud> noeuds = new ArrayList<Noeud>();
	
	
    /*
     * les évènements représentent un flux de personne sur
     * un arc à un instant donné.
     * Ils sont voués à être propagés de noeuds en noeuds,
     * où ils sont rangés dans des AVL en fonction de leur date.
     */
	public class Event { 
		//le flux de personne
		int nbPers ;
		
		/*
		 * liste des ids des feuilles ayant contribué
		 * au flux de personnes de cet event.
		 * Utile pour corriger des erreurs invalides
		 */
		ArrayList<Integer> idFeuillesSource ; 
		
		Event (int nbPers, int idFeuilleSource) {
			this.nbPers = nbPers;
			idFeuillesSource = new ArrayList<Integer>();
			idFeuillesSource.add(idFeuilleSource);
		}
		
		//rassembler deux évènement en un
		void merge(Event e) {
			this.nbPers+=e.nbPers;
			//on concatène les listes de feuilles source
			this.idFeuillesSource.addAll(e.idFeuillesSource);
		}
		
		int getNbPers() {
			return nbPers;
		}
		ArrayList<Integer> getSources() {
			return idFeuillesSource;
		}
	}
	
	
	/*
	 * classe expréssément créée pour le checker.
	 * Il s'en sert pour associer chaque noeud intermédiaire
	 * à son AVL d'évènements
	 */
	public class Noeud {
	private Sommet sommet;
	private TreeMap<Integer, Event> avl;
	Chemin cheminEvac;
	
	Noeud(Sommet sommet, Chemin ce) {
		this.sommet=sommet;
		avl = new TreeMap<Integer, Event>();
		cheminEvac=ce;
	}
	
	void addEvent(int date, Event e) {
		if (avl.containsKey(date)) {
			/*
			 * si un event dÃ©jÃ  associÃ© Ã  cette date est dans l'avl, 
			 * on ajoute aux flux actuel qui y est associÃ© le flux du nouvel
			 * event gÃ©nÃ©rÃ©
			 */
			avl.get(date).merge(e);
			avl.put(date, avl.get(date));
		} else {
			/*
			 * si aucun event n'a Ã©tÃ© crÃ©e pour cette date on le crÃ©e avec
			 * le flux spÃ©cifiÃ©
			 */
			avl.put(date, e);
		}
		
	}
	Sommet getSommet() {return sommet;}
	TreeMap<Integer, Event> getAVL() {return avl;}
	Chemin getChemin() {return cheminEvac;}
	}
	
	
	/*
	 * renvoie son numéro dans la liste noeuds
	 * si le noeud correspondant à sommet y est présent,
	 * retourne -1 sinon
	 */
	public int noeudExiste(Sommet sommet) {
		boolean found=false;
		int no = 0;
		while ((!found)&&(no < noeuds.size())) {
			if (noeuds.get(no).getSommet().getId()==sommet.getId()) {
				found=true;
			} else {
				no++;
			}
		}
		if (found) {
			return no;
		} else {
			return -1;
		}
	}
	
	//le checker construit la solution à partir du path
	public Checker (String fileName){
		    try {
		    	//on construit la forÃªt et la solution
		    	FileAgent fa = new FileAgent(fileName);
				solution = fa.processLineByLineSolution();
				FileAgent fa2 = new FileAgent("InstancesInt/"+solution.nomInstance+".full");
				foret=fa2.processLineByLineForest();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public Checker(Solution solution) {
		this.solution=solution;
		FileAgent fa2 = new FileAgent("InstancesInt/"+solution.nomInstance+".full");
		try {
			foret=fa2.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//change la solution et met à jour la forêt
	public void setSolutionEtForet(Solution solution) {
		this.solution=solution;
		FileAgent fa2 = new FileAgent("InstancesInt/"+solution.nomInstance+".full");
		try {
			foret=fa2.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		noeuds.clear();
	}
	
	//change la solution uniquement
	public void setSolution(Solution solution) {
		this.solution=solution;
		noeuds.clear();
	}
	
	/*
	 * vérifie la validité de la solution en attribut du checker.
	 * ce checker vérifie : 
	 * > si le nombre de feuilles de la solution est correct
	 * > si la solution n'induit pas de dépassements de capacités d'arcs
	 */
	public boolean checkSolution() throws SolutionIncorrecteException {
		boolean ok = true ; 
		
		//> le nombre de feuilles est il correct?
		int date=0;
		/*
		 *  on vérifie simplement que le nombre de feuilles de la solution
		 *  est bien inférieur ou égal au nombre de feuilles dans la forêt
		 */
		if (solution.nbFeuilleEvac > foret.nbFeuille) { 
			ok = false ;
			System.out.println("Erreur : nombre de feuilles Ã  Ã©vacuer plus grand que le nombre de feuilles de la forÃªt!");
			throw new SolutionIncorrecteException();
		}
		//> une capacité est elle dépassée?
		else { 

			/*
			 * Etape 1 : on parcourt toutes le feuilles de la solution
			 * et on crée tous les évènements
			 */
			try {
			for (int i=0 ; i<solution.nbFeuilleEvac ; i++) { 
				
				int txEvacSol = solution.feuilles.get(i).tauxEvac ; 
				int debut = solution.feuilles.get(i).dateDebut ;  
				int idFeuille = solution.feuilles.get(i).id ; 	
								
				//on rÃ©cup le chemin d'Ã©vac
				Chemin cheminEvac = foret.recupChemin(idFeuille);
				
				//on rÃ©cup la feuille
				Sommet feuille = cheminEvac.feuille;
				
				//on rÃ©cup l'arc entre la feuille et le premier sommet du chemin d'Ã©vac
				Sommet sommetSuivant = cheminEvac.getNoeudAt(1);				
				Arc arc=null;
				try {
					arc = foret.recupArc(idFeuille, sommetSuivant.getId());
				} catch (NoSuchElementException e) {
					e.printStackTrace();
					System.out.println("PB en cherchant l'arc entre "+idFeuille+" et "+sommetSuivant.getId());
				}
				
				//on vÃ©rifie bien que le taux de la feuille < capa arc
				if (txEvacSol > arc.getCapa()) {
					ok=false;
	
					ArrayList<Integer> idFeuillesErreur = new ArrayList<Integer>();
					idFeuillesErreur.add(idFeuille);				
					throw new SolutionIncorrecteException(idFeuille, sommetSuivant.getId(), idFeuillesErreur,txEvacSol-arc.getCapa(), txEvacSol);
				}
				
				
				/*else if (debut > arc.getDateExp()) {
					ok=false;
					System.out.println("mauvaise date de dÃ©but de la feuille "+idFeuille+" dans la solution");*/
				//}
				else { 				
					//on va crÃ©er tous les events					
					//compteur qui va Ãªtre dÃ©crÃ©mentÃ© Ã  mesure que les events sont crÃ©es 
					int cptPopulation=feuille.getPop();
					
					//on crÃ©e le noeud de l'autre cÃ´tÃ© de l'arc
					Noeud noeud = new Noeud(sommetSuivant, cheminEvac);
					noeuds.add(noeud);
					
					//variable Ã  Ãªtre incrÃ©mentÃ©e
					int temps = debut+arc.duree;
					while (cptPopulation > txEvacSol) {
						noeud.addEvent(temps, new Event(txEvacSol, feuille.getId()));
						cptPopulation = cptPopulation - txEvacSol;
						temps = temps + 1;
					}
					
					/*
					 * il reste peut Ãªtre quelques personnes Ã  passer, dont le nombre
					 * est infÃ©rieur au taux
					 */				
					if (cptPopulation != 0) {
						noeud.addEvent(temps, new Event(txEvacSol, feuille.getId()));
						//on met cptPop Ã  0 pour modÃ©liser que tout le monde est sorti
						cptPopulation=0;
					}			
				} 
			} 
			
			/*
			 * Etape 2 : maintenant que nous venons de créer tous les events,
			 * les opérations suivantes vont être répétées jusqu'à ce que tous les AVL
			 * soient vides (i.e. qu'il n'y ait plus aucun event à propager) ou qu'il
			 * soit avéré que la solution est invalide.
			 * 1. on cherche la date minimale parmi tous les events
			 * 2. on parcourt la liste des noeuds intermédiaires : 
			 * 		si leur AVL contient un event associé à la date
			 * 		minimale, on vérifie que son flux ne dépasse pas
			 * 		la capacité de l'arc liant son noeud au suivant.
			 * 			si c'est le cas, on lève une SolutionIncorrecteException
			 * 			sinon, on le propage
			 */
			boolean avlPleins=true;
			while (ok&&avlPleins) {
				
				avlPleins=false;
				int dateMin=Integer.MAX_VALUE;
				//1. on cherche la date minimale parmi tous les events
				for (int i=0 ; i < noeuds.size() ; i++) {
					Noeud noeudCourant = noeuds.get(i);
					TreeMap<Integer, Event> avlNC = noeudCourant.getAVL();
					if (!(avlNC.isEmpty())) {
						avlPleins=avlPleins||true;
						date = avlNC.firstKey();
						if (date < dateMin) {
							dateMin=date;
						}
					}
				}
				
				/*
				 * 2. on parcourt la liste des noeuds intermédiaires : 
				 * 		si leur AVL contient un event associé à la date
				 * 		minimale, on vérifie que son flux ne dépasse pas
				 * 		la capacité de l'arc liant son noeud au suivant.
				 * 			si c'est le cas, on lève une SolutionIncorrecteException
				 * 			sinon, on le propage
				 */
				for (int i=0 ; i < noeuds.size() ; i++) {
					Noeud noeudCourant = noeuds.get(i);
					TreeMap<Integer, Event> avlNC = noeudCourant.getAVL();

					if (!avlNC.isEmpty()) {
						date = avlNC.firstKey();
			
						//ce noeud contient des events de la date minimale : on les traite
						if (date==dateMin) {
							Event event = avlNC.remove(date);
							int flux = event.getNbPers();
							
							//on rÃ©cupÃ¨re l'arc qui le lie au noeud suivant
							Sommet sommetSuivant = noeudCourant.getChemin().getNoeudSuivant(noeudCourant.getSommet());
							if (sommetSuivant != null) {
								Arc arc=null;
								try {
									arc = foret.recupArc(noeudCourant.getSommet().getId(), sommetSuivant.getId());
								} catch (NoSuchElementException e) {
									e.printStackTrace();
									System.out.println("PB en cherchant les arcs des inter");
								}
								
								//on vÃ©rifie si les events respectent bien les rÃ¨gles de l'arc
								if (arc.capacite < flux) {
									ok=false;
									
									/*
									 * on Ã©crit dans la variable idFeuillesErreur, spÃ©cifiant
									 * toutes les feuilles dont sont issues les populations Ã  
									 * l'origine de ce dÃ©passement
									 */
									ArrayList<Integer> idFeuillesErreur = event.getSources();
									throw new SolutionIncorrecteException(noeudCourant.getSommet().getId(), sommetSuivant.getId(), idFeuillesErreur,flux -arc.capacite, flux);
								} 
								
								/*if (arc.dateExp < date) {
									ok=false;
									System.out.print("Arc expirÃ© entre "+noeudCourant.getSommet().getId()+" et "+sommetSuivant.getId());
								}*/
								
								//on crÃ©e le noeud associÃ© au noeud suivant s'il n'existe pas dans notre liste								
								int index=noeudExiste(sommetSuivant);
								if (index!=-1) {
									//si le noeud est dÃ©jÃ  existant on y ajoute l'event avec date courante + la durÃ©e de l'arc
									noeuds.get(index).addEvent(date+arc.duree, event);
								} else {
									//sinon on le crÃ©e et on lui ajoute l'event avec date courant + la durÃ©e de l'arc
									Noeud noeud = new Noeud(sommetSuivant, noeudCourant.getChemin());
									noeuds.add(noeud);
									noeud.addEvent(date+arc.duree, event);
								}
							}
						}				
					}
				}
			}
			
			if (ok) {
				date++;
				solution.fctObjectif = date;
				solution.valid="valid";
				System.out.println(date);
				return ok;
			}
				
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
			
		}
		return ok;
		
	} 
	
	
	 public static void main(String[] args) {
		 Checker checker = new Checker("Solution/graphe-TD-sans-DL-sol.txt");
		 
		 try {
			System.out.println(checker.checkSolution());
		} catch (SolutionIncorrecteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* Path currentRelativePath = Paths.get("");
		 String s = currentRelativePath.toAbsolutePath().toString();
		 System.out.println("Current relative path is: " + s);*/
		 
		}
		
}	
	 



