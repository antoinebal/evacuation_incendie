package evac;

import java.io.IOException;
import java.util.ArrayList;

public class RLIntensification {
	
	Solution solution;
	Checker checker;
	Foret foret;
	
	int DELTA_RANGE_INF=50;
	int DELTA_RANGE_SUP=150;
	
	RLIntensification() {}
	
	RLIntensification(String fichierSolution) {
		FileAgent fa = new FileAgent(fichierSolution);
		try {
			solution = fa.processLineByLineSolution();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.checker = new Checker(solution);
		FileAgent fa2 = new FileAgent("InstancesInt/"+solution.nomInstance+".full");
		try {
			foret = fa2.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	RLIntensification(Solution solution) {
		this.solution = solution;
		this.checker = new Checker(solution);
		FileAgent fa2 = new FileAgent("InstancesInt/"+solution.nomInstance+".full");
		try {
			foret = fa2.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	RLIntensification(Solution solution, Foret foret) {
		this.solution = solution;
		this.foret = foret;
	}
	
	
	
	/*
	 * on distingue deux types de solution : 
	 * > pour les valids, on générer un voisinage, choisir
	 * parmi lui une solution pour répéter l'opération.
	 * On s'arrête quand la solution génératrice du voisinage
	 * en est la meilleure
	 * > pour les invalids, on va chercher à résoudre un conflit.
	 * On va chercher sur quel arc il y a dépassement, et quels feuilles
	 * en sont à l'origine pour changer leurs paramètres
	 */
	void intensification() {
		if (solution.valid.equals("valid")) {
			intensificationValid();
		} else {
			intensificationInvalid();
		}
	}
	
	
	/*
	 * on a généré un voisinage composé que d'un seul
	 * voisin car tous les autres étaient non valables
	 */
	public ArrayList<Solution> genereVoisinage() {
		int echecVoisin=0;
		ArrayList<Solution> voisinage = new ArrayList<Solution>();
		
		for (int i = 0 ; i < solution.nbFeuilleEvac ; i++) {
			
			for (int delta = DELTA_RANGE_INF ; delta < DELTA_RANGE_SUP ; delta++) {
			Solution voisin1 = genereVoisinDate(i, delta);
			if (voisin1 != null) {
				voisinage.add(voisin1);
				
			} 
			/*Solution voisin2 = genereVoisinRate(i, delta);
			if (voisin2 != null) {
				voisinage.add(voisin2);	
			}*/
			}
		}
		return voisinage;
	}
	
	// Faire partir plus tot 
	/*
	 * si le delta produit une valeur négative, on retourne nul
	 */
	public Solution genereVoisinDate(int numeroFeuille, int delta) {
		Solution newSolution = new Solution(solution) ; 
		Solution aux = newSolution;
		newSolution = this.solution;
		this.solution = aux;
	//	Sommet feuille = this.solution.feuilles.get(numeroFeuille) ; 
		
		if ((solution.feuilles.get(numeroFeuille).dateDebut - delta)>=0) {
			newSolution.setDateFeuilleNo(numeroFeuille,delta);
			return newSolution;
		}
		
		return null ; 
	}
	
	/*
	 * si le delta produit une valeur négative, on retourne nul
	 */
	public Solution genereVoisinRate(int numeroFeuille, int delta) {
		Solution newSolution = new Solution(solution) ; 
		Solution aux = newSolution;
		newSolution = this.solution;
		this.solution = aux;
		// Reduire nombre paquet 
		// Taux evac solution + delta <= maxPack de la feuille
		if ((solution.feuilles.get(numeroFeuille).tauxEvac + delta <=  foret.recupChemin(solution.feuilles.get(numeroFeuille).id).getNoeudAt(0).maxPack_)) { 
			newSolution.setRateFeuilleNo(numeroFeuille, delta); 
			return newSolution;
		}
		return null ; 
	}
	
	void intensificationValid() {
		int compteur = 0;
		boolean over=false;
		while (!over) {
			compteur++;
			ArrayList<Solution> voisins = genereVoisinage();
			System.out.println("Amas num : "+compteur);
			
			int minFctObj = solution.fctObjectif;
			int indexMeilleurVoisin = -1;
			for (int i = 0 ; i < voisins.size() ; i++) {
				checker.setSolution(voisins.get(i));
				boolean validite=false;
				try {
					validite = checker.checkSolution();
					if (validite) {
						if (voisins.get(i).fctObjectif < minFctObj) {
							//ce voisin valide est le meilleur
							minFctObj = voisins.get(i).fctObjectif;
							indexMeilleurVoisin = i;
							System.out.println("\n\n\nMeilleur voisin pris : "+minFctObj);
						}
					}
				} catch (SolutionIncorrecteException e) {
					
				}
				
			}
			
			//si aucun voisin meilleur trouvé, on arrête
			if (indexMeilleurVoisin == -1) {
				System.out.println("Descente finie ; meilleure solution : "+solution.fctObjectif);
				over=true;
			} else {
			//sinon le meilleur voisin devient la nouvelle solution
				this.solution = voisins.get(indexMeilleurVoisin);
			}		
		}
		 
	}
	
	void intensificationInvalid() {
		checker = new Checker(solution);
		
		try {
			if (checker.checkSolution()) {
				System.out.println("Cette solution est sensée être invalide");
			}
			System.out.println("Cette solution est sensée être invalide");
		} catch (SolutionIncorrecteException e) {
			e.printStackTrace();
			
			correction(e.getIDFeuillesSource(), e.getDepassement(), e.getFlux());
		} 
	}
	
	void correction(ArrayList<Integer> idFeuillesSource, int depassement, int flux) {
		Solution newSolution = new Solution(solution) ; 
		Solution aux = newSolution;
		newSolution = this.solution;
		this.solution = aux;
		
		//pour chaque feuille source, voir leur "part de responsabilité" dans le dépassement
		for (int i = 0 ; i < idFeuillesSource.size() ; i++) {
			int idFeuille = idFeuillesSource.get(i);
			int rate = solution.getFeuilleWithID(idFeuille).tauxEvac;
			
		}
		
	}
	
	static void testDescente() {
		try {

		 	System.out.println("Working Directory = " +
			System.getProperty("user.dir"));
		 	String nomInstance = "medium_10_30_3_5_I" ; 
		 	
			FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
			Foret foret=fa.processLineByLineForest();
			
			BorneCalc testBorneSup = new BorneCalc (foret) ;
			
			testBorneSup.borneSolution(nomInstance,1).comment = "calcul borne SUP";
			testBorneSup.borneSolution(nomInstance,1).getInFile();
			
			/*Checker checkert = new Checker(testBorneSup.borneSolution(nomInstance,1)) ; 
			boolean validite = checkert.checkSolution();
		 	System.out.println(validite);*/
		 	
			RLIntensification rli = new RLIntensification("Solution/"+nomInstance+"_sol.full");
			
			rli.intensification();
			
			rli.solution.getInFile();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void testInvalid() {
		try {
			String nomInstance = "medium_10_30_3_5_I" ;
		 	
			FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
			Foret foret=fa.processLineByLineForest();
			
			BorneCalc testBorneInf = new BorneCalc (foret) ;
			
			testBorneInf.borneSolution(nomInstance,0).comment = "calcul borne INF";
			testBorneInf.borneSolution(nomInstance,0).getInFile();
			
			/*Checker checkert = new Checker(testBorneSup.borneSolution(nomInstance,1)) ; 
			boolean validite = checkert.checkSolution();
		 	System.out.println(validite);*/
		 	
			RLIntensification rli = new RLIntensification("Solution/"+nomInstance+"_sol.full");
			
			rli.intensification();
			rli.solution.getInFile();		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	 public static void main(String[] args) {
		 testInvalid();
		
	 }
	
}
