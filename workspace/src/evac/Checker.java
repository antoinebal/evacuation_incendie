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
	
	
    
	public class Event { 
		int nbPers ;
		ArrayList<Integer> idFeuillesSource ; 
		
		Event (int nbPers, int idFeuilleSource) {
			this.nbPers = nbPers;
			idFeuillesSource = new ArrayList<Integer>();
			idFeuillesSource.add(idFeuilleSource);
		}
		
		void merge(Event e) {
			this.nbPers+=e.nbPers;
			this.idFeuillesSource.addAll(e.idFeuillesSource);
		}
		
		int getNbPers() {
			return nbPers;
		}
		ArrayList<Integer> getSources() {
			return idFeuillesSource;
		}
	}
	
	
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
	
	
	//change la foret et la solution
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
	
	
	public boolean checkSolution() throws SolutionIncorrecteException {
		
		//System.out.println("CHECK : ");
		boolean ok = true ; 
		//I : le nombre de feuilles est il correct?
		int date=0;
		if (solution.nbFeuilleEvac > foret.nbFeuille) { 
			ok = false ;
			System.out.println("Erreur : nombre de feuilles à évacuer plus grand que le nombre de feuilles de la forêt!"); 
		}
		else { 
			//on parcourt toutes les feuilles de la solution et
			//on crée tous les évènements
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
				
				//cheminEvac.printChemin();
				
				Arc arc=null;
				try {
					arc = foret.recupArc(idFeuille, sommetSuivant.getId());
				} catch (NoSuchElementException e) {
					e.printStackTrace();
					System.out.println("PB en cherchant l'arc entre "+idFeuille+" et "+sommetSuivant.getId());
				}
				
				//on vérifie bien que le taux < capa arc
				if (txEvacSol > arc.getCapa()) {
					ok=false;
	
					//on remplit idFeuillesErreur pour une éventuelle correction
					ArrayList<Integer> idFeuillesErreur = new ArrayList<Integer>();
					idFeuillesErreur.add(idFeuille);
					
					//on throw l'exception
					throw new SolutionIncorrecteException(idFeuille, sommetSuivant.getId(), idFeuillesErreur,txEvacSol-arc.getCapa(), txEvacSol);
					//System.out.println("mauvais taux d'évac de la feuille "+idFeuille+" dans la solution");
				}
				
				//on vérifie bien que le duedate > dateDebut
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
						//System.out.println("Event ["+temps+" , "+txEvacSol+"] généré pour noeud "+noeud.getSommet().getId());
						temps = temps + 1;
						
					}
					
					/*
					 * il reste peut être quelques personnes à passer, dont le nombre
					 * est inférieur au taux
					 */				
					if (cptPopulation != 0) {
						noeud.addEvent(temps, new Event(txEvacSol, feuille.getId()));
						//System.out.println("Event ["+temps+" , "+cptPopulation+"] généré pour noeud "+noeud.getSommet().getId());
						
						//on met cptPop à 0 pour modéliser que tout le monde est sorti
						cptPopulation=0;
					}			
				} 
			} 
			
			//on vient de parcourir les feuilles
			int compteurAVLVides=0;
			while (ok&&(compteurAVLVides!=noeuds.size())) {
				compteurAVLVides=0;
				int dateMin=Integer.MAX_VALUE;
				
				//on cherche la date minimale dans les events
				for (int i=0 ; i < noeuds.size() ; i++) {
					Noeud noeudCourant = noeuds.get(i);
					TreeMap<Integer, Event> avlNC = noeudCourant.getAVL();
					if (!(avlNC.isEmpty())) {
						date = avlNC.firstKey();
						if (date < dateMin) {
							dateMin=date;
						}
					}
				}
				
				for (int i=0 ; i < noeuds.size() ; i++) {
					
					Noeud noeudCourant = noeuds.get(i);
					TreeMap<Integer, Event> avlNC = noeudCourant.getAVL();
					
					/*
					 * le nombre de personnes partant depuis ce noeud
					 * au temps courant
					 */
					if (avlNC.isEmpty()) {
						compteurAVLVides++;
					} else {
						date = avlNC.firstKey();
						
						
						//ce noeud contient des events de la date minimale : on les traite
						if (date==dateMin) {
							//System.out.println("Date : "+dateMin);
							//int flux = avlNC.remove(date);
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
									//System.out.println("Capa dépassée ("+flux+") entre "+noeudCourant.getSommet().getId()+" et "+sommetSuivant.getId());
								} else {
									//System.out.println("Capa OK ("+flux+") entre "+noeudCourant.getSommet().getId()+" et "+sommetSuivant.getId());
								}
								
								/*if (arc.dateExp < date) {
									ok=false;
									System.out.print("Arc expiré entre "+noeudCourant.getSommet().getId()+" et "+sommetSuivant.getId());
								}*/
								
								//on crée le noeud associé au noeud suivant s'il n'existe pas
								
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
							} else {
								//System.out.println("On est au noeud safe");
							}
						}				
					}
				}
			}
			
			if (ok) {
				date++;
				//System.out.println("END DATE = "+date);
				//on change la fonction objectif
				solution.fctObjectif = date;
				//System.out.println(date);
				solution.comment = "tchazzzou" ; 
				return ok;
			}
				
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
			
		}
		return ok;
		
	} 
	
	
	 public static void main(String[] args) {
		 String nomInstance="graphe-TD-sans-DL-data";
		 FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
		Foret foret=null;
		try {
			foret = fa.processLineByLineForest();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			BorneCalc testBorneInf = new BorneCalc (foret) ;
			
			
			testBorneInf.borneSolution(nomInstance,0).getInFile(); 
		 Checker checker = new Checker("Solution/"+nomInstance+"_sol.full");
		 
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
	 



