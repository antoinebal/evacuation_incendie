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
     * les �v�nements repr�sentent un flux de personne sur
     * un arc � un instant donn�.
     * Ils sont vou�s � �tre propag�s de noeuds en noeuds,
     * o� ils sont rang�s dans des AVL en fonction de leur date.
     */
	public class Event { 
		//le flux de personne
		int nbPers ;
		
		/*
		 * liste des ids des feuilles ayant contribu�
		 * au flux de personnes de cet event.
		 * Utile pour corriger des erreurs invalides
		 */
		ArrayList<Integer> idFeuillesSource ; 
		
		Event (int nbPers, int idFeuilleSource) {
			this.nbPers = nbPers;
			idFeuillesSource = new ArrayList<Integer>();
			idFeuillesSource.add(idFeuilleSource);
		}
		
		//rassembler deux �v�nement en un
		void merge(Event e) {
			this.nbPers+=e.nbPers;
			//on concat�ne les listes de feuilles source
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
	 * classe expr�ss�ment cr��e pour le checker.
	 * Il s'en sert pour associer chaque noeud interm�diaire
	 * � son AVL d'�v�nements
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
			 * si un event déjà associé à cette date est dans l'avl, 
			 * on ajoute aux flux actuel qui y est associé le flux du nouvel
			 * event généré
			 */
			avl.get(date).merge(e);
			avl.put(date, avl.get(date));
		} else {
			/*
			 * si aucun event n'a été crée pour cette date on le crée avec
			 * le flux spécifié
			 */
			avl.put(date, e);
		}
		
	}
	Sommet getSommet() {return sommet;}
	TreeMap<Integer, Event> getAVL() {return avl;}
	Chemin getChemin() {return cheminEvac;}
	}
	
	
	/*
	 * renvoie son num�ro dans la liste noeuds
	 * si le noeud correspondant � sommet y est pr�sent,
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
	
	//le checker construit la solution � partir du path
	public Checker (String fileName){
		    try {
		    	//on construit la forêt et la solution
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
	
	
	//change la solution et met � jour la for�t
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
	 * v�rifie la validit� de la solution en attribut du checker.
	 * ce checker v�rifie : 
	 * > si le nombre de feuilles de la solution est correct
	 * > si la solution n'induit pas de d�passements de capacit�s d'arcs
	 */
	//cette version de checkSolution �tablit elle m�me la fonction
	//objectif sans v�rifier celle d�j� existante
	public boolean checkSolution() throws SolutionIncorrecteException {
		boolean ok = true ; 
		
		//> le nombre de feuilles est il correct?
		int date=0;
		/*
		 *  on v�rifie simplement que le nombre de feuilles de la solution
		 *  est bien inf�rieur ou �gal au nombre de feuilles dans la for�t
		 */
		if (solution.nbFeuilleEvac > foret.nbFeuille) { 
			ok = false ;
			System.out.println("Erreur : nombre de feuilles à évacuer plus grand que le nombre de feuilles de la forêt!");
			throw new SolutionIncorrecteException();
		}
		//> une capacit� est elle d�pass�e?
		else { 

			/*
			 * Etape 1 : on parcourt toutes les feuilles de la solution
			 * et on cr�e tous les �v�nements
			 */
			try {
			for (int i=0 ; i<solution.nbFeuilleEvac ; i++) { 
				
				int txEvacSol = solution.feuilles.get(i).tauxEvac ; 
				int debut = solution.feuilles.get(i).dateDebut ;  
				int idFeuille = solution.feuilles.get(i).id ; 	
								
				//on récup le chemin d'évac
				Chemin cheminEvac = foret.recupChemin(idFeuille);
				
				//on récup la feuille
				Sommet feuille = cheminEvac.feuille;
				
				//on récup l'arc entre la feuille et le premier sommet du chemin d'évac
				Sommet sommetSuivant = cheminEvac.getNoeudAt(1);				
				Arc arc=null;
				try {
					arc = foret.recupArc(idFeuille, sommetSuivant.getId());
				} catch (NoSuchElementException e) {
					e.printStackTrace();
					System.out.println("PB en cherchant l'arc entre "+idFeuille+" et "+sommetSuivant.getId());
				}
				
				//on vérifie bien que le taux de la feuille < capa arc
				if (txEvacSol > arc.getCapa()) {
					ok=false;
	
					ArrayList<Integer> idFeuillesErreur = new ArrayList<Integer>();
					idFeuillesErreur.add(idFeuille);				
					throw new SolutionIncorrecteException(idFeuille, sommetSuivant.getId(), idFeuillesErreur,txEvacSol-arc.getCapa(), txEvacSol);
				}
				
				
				/*else if (debut > arc.getDateExp()) {
					ok=false;
					System.out.println("mauvaise date de début de la feuille "+idFeuille+" dans la solution");*/
				//}
				else { 				
					//on va créer tous les events					
					//compteur qui va être décrémenté à mesure que les events sont crées 
					int cptPopulation=feuille.getPop();
					
					//on crée le noeud de l'autre côté de l'arc
					Noeud noeud = new Noeud(sommetSuivant, cheminEvac);
					noeuds.add(noeud);
					
					//variable à être incrémentée
					int temps = debut+arc.duree;
					while (cptPopulation > txEvacSol) {
						noeud.addEvent(temps, new Event(txEvacSol, feuille.getId()));
						cptPopulation = cptPopulation - txEvacSol;
						temps = temps + 1;
					}
					
					/*
					 * il reste peut être quelques personnes à passer, dont le nombre
					 * est inférieur au taux
					 */				
					if (cptPopulation != 0) {
						noeud.addEvent(temps, new Event(txEvacSol, feuille.getId()));
						//on met cptPop à 0 pour modéliser que tout le monde est sorti
						cptPopulation=0;
					}			
				} 
			} 
			
			/*
			 * Etape 2 : maintenant que nous venons de cr�er tous les events,
			 * les op�rations suivantes vont �tre r�p�t�es jusqu'� ce que tous les AVL
			 * soient vides (i.e. qu'il n'y ait plus aucun event � propager) ou qu'il
			 * soit av�r� que la solution est invalide.
			 * 1. on cherche la date minimale parmi tous les events
			 * 2. on parcourt la liste des noeuds interm�diaires : 
			 * 		si leur AVL contient un event associ� � la date
			 * 		minimale, on v�rifie que son flux ne d�passe pas
			 * 		la capacit� de l'arc liant son noeud au suivant.
			 * 			si c'est le cas, on l�ve une SolutionIncorrecteException
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
				 * 2. on parcourt la liste des noeuds interm�diaires : 
				 * 		si leur AVL contient un event associ� � la date
				 * 		minimale, on v�rifie que son flux ne d�passe pas
				 * 		la capacit� de l'arc liant son noeud au suivant.
				 * 			si c'est le cas, on l�ve une SolutionIncorrecteException
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
							
							//on récupère l'arc qui le lie au noeud suivant
							Sommet sommetSuivant = noeudCourant.getChemin().getNoeudSuivant(noeudCourant.getSommet());
							if (sommetSuivant != null) {
								Arc arc=null;
								try {
									arc = foret.recupArc(noeudCourant.getSommet().getId(), sommetSuivant.getId());
								} catch (NoSuchElementException e) {
									e.printStackTrace();
									System.out.println("PB en cherchant les arcs des inter");
								}
								
								//on vérifie si les events respectent bien les règles de l'arc
								if (arc.capacite < flux) {
									ok=false;
									
									/*
									 * on écrit dans la variable idFeuillesErreur, spécifiant
									 * toutes les feuilles dont sont issues les populations à 
									 * l'origine de ce dépassement
									 */
									ArrayList<Integer> idFeuillesErreur = event.getSources();
									throw new SolutionIncorrecteException(noeudCourant.getSommet().getId(), sommetSuivant.getId(), idFeuillesErreur,flux -arc.capacite, flux);
								} 
								
								/*if (arc.dateExp < date) {
									ok=false;
									System.out.print("Arc expiré entre "+noeudCourant.getSommet().getId()+" et "+sommetSuivant.getId());
								}*/
								
								//on crée le noeud associé au noeud suivant s'il n'existe pas dans notre liste								
								int index=noeudExiste(sommetSuivant);
								if (index!=-1) {
									//si le noeud est déjà existant on y ajoute l'event avec date courante + la durée de l'arc
									noeuds.get(index).addEvent(date+arc.duree, event);
								} else {
									//sinon on le crée et on lui ajoute l'event avec date courant + la durée de l'arc
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
				//System.out.println(date);
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
	 



