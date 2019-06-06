package evac;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
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
		this.checker = new Checker(solution);
	}
	
	void setSolution(Solution s) {
		solution = s;
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
			// DEBUG System.out.println("Amas num : "+compteur);
			
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
							// DEBUG System.out.println("\n\n\nMeilleur voisin pris : "+minFctObj);
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
		boolean validite = false;
		int compteur = 0;
		checker = new Checker(solution);
		while (!validite) {
		try {
			checker.setSolution(solution);
			validite=checker.checkSolution();
		} catch (SolutionIncorrecteException e) {
			System.out.println("E : nbf : "+e.getIDFeuillesSource().size()+" ; dep : "+e.getDepassement()+" ; flux "+e.getFlux()+" ; capa "+(e.getFlux()-e.getDepassement()));
			solution.print();
			solution = correctionDateEtRate(e.getIDFeuillesSource(), 5, 5);
			//solution = correctionRate(e.getIDFeuillesSource(), e.getDepassement(), e.getFlux(), 5);
			compteur++;
		} 
		}
		System.out.println("VALIDE "+compteur);
	}
	
	
	//corrige des solutions en changeant le taux des feuilles en argument
	Solution correctionRate(ArrayList<Integer> idFeuillesSource, int depassement, int flux, int diff) {
		Solution newSolution = new Solution(solution) ; 
		Solution aux = newSolution;
		newSolution = this.solution;
		this.solution = aux;
		int capacite=flux-depassement;
		boolean over = false;
		int fluxCorr = flux;
		int cptIterations = 0;

		while (!over) {
			//on r�cup�re une feuille al�atoirement parmi les sources
			int indexAleatoire = -1;
			boolean test=true;
			if (!idFeuillesSource.isEmpty()) {
				indexAleatoire = randInt(0, idFeuillesSource.size()-1);
			}
			
			int idFeuille = idFeuillesSource.get(indexAleatoire);
			//System.out.println("Feuille "+idFeuille+" r�cup d'index "+indexAleatoire);
			//calcul fluxInduit actuel
			//int pop = foret.getFeuilleWithID(idFeuille).getPop();
			int rate = newSolution.getFeuilleWithID(idFeuille).tauxEvac;
			
			//System.out.println("Pop : "+pop+" ; rate : "+rate);
			
			//on regarde quel rate appliquer
			int nouveauRate=0;
			if (rate-diff <= 0) {
				idFeuillesSource.remove(indexAleatoire);
				nouveauRate=1;
			} else {
				nouveauRate=rate-diff;
			}
			newSolution.getFeuilleWithID(idFeuille).setTauxEvac(nouveauRate);
			
			//a-t-on assez chang� le rate?
			fluxCorr = fluxCorr - rate+nouveauRate;
			if (fluxCorr <= capacite) {
				over=true;
				System.out.println("Trouvé : "+fluxCorr);
				return newSolution;
				
			}
			
			/*if (cptIterations>50) {
				over=true;
				System.out.println("Trop d'it�rations");
				return null;
			}*/
			cptIterations++;
		}
		return null;
						
	}
	
	//corrige des solutions en changeant le taux des feuilles en argument
		Solution correctionDateEtRate(ArrayList<Integer> idFeuillesSource, int diffRate, int diffDate) {
			Solution newSolution = new Solution(solution) ; 
			Solution aux = newSolution;
			newSolution = this.solution;
			this.solution = aux;
			boolean over = false;
			int cptIterations = 0;

			
			while (!over) {
				//on r�cup�re une feuille al�atoirement parmi les sources
				int indexAleatoire = -1;
				boolean test=true;
				if (!idFeuillesSource.isEmpty()) {
					indexAleatoire = randInt(0, idFeuillesSource.size()-1);
				}
				
				int idFeuille = idFeuillesSource.get(indexAleatoire);
				//System.out.println("Feuille "+idFeuille+" r�cup d'index "+indexAleatoire);
				//calcul fluxInduit actuel
				//int pop = foret.getFeuilleWithID(idFeuille).getPop();
				int rate = newSolution.getFeuilleWithID(idFeuille).tauxEvac;
				int date = newSolution.getFeuilleWithID(idFeuille).tauxEvac;
				
				//System.out.println("Pop : "+pop+" ; rate : "+rate);
				
				//on regarde quel rate appliquer
				int nouveauRate=0;
				if (rate-diffRate <= 0) {
					idFeuillesSource.remove(indexAleatoire);
					nouveauRate=1;
				} else {
					nouveauRate=rate-diffRate;
				
				}
				
				newSolution.getFeuilleWithID(idFeuille).setTauxEvac(nouveauRate);
				newSolution.getFeuilleWithID(idFeuille).setDateDebut(date+diffDate);
				
				if (cptIterations>4) {
					over=true;
				}
				cptIterations++;
			}
			return newSolution;
							
		}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	static void testDescente(String nomInstance) {
		try {
		 	
			FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
			Foret foret=fa.processLineByLineForest();
			
			BorneCalc testBorne = new BorneCalc (foret) ;
			Solution sBorneSup = testBorne.borneSolution(nomInstance,1);
			Solution sBorneInf = testBorne.borneSolution(nomInstance, 0); 
			sBorneSup.comment = "calcul borne SUP";
			sBorneSup.getInFile(); 
			int vBorneSup = sBorneSup.fctObjectif;
			/*Checker checkert = new Checker(testBorneSup.borneSolution(nomInstance,1)) ; 
			boolean validite = checkert.checkSolution();
		 	System.out.println(validite);*/
			System.out.println("******************************************************* Nouvelle instance pour intensification : " + nomInstance + "*****************************************************");
			RLIntensification rli = new RLIntensification("Solution/"+nomInstance+"_sol.full");
			
			Instant start = Instant.now();
			rli.intensification();
			Instant end = Instant.now();
			rli.solution.tpsCalcul = (int) Duration.between(start, end).toMillis();
			rli.solution.methode ="Intensification depuis la borne supérieure" ;
			rli.solution.comment = "Intensification normale " ; 
			
			rli.solution.getInFile();
			System.out.println(" RESULTAT FIN D'INTENSIFICATION POUR : " + nomInstance);
			System.out.println("Borne inf : "+sBorneInf.fctObjectif+" ; après descente : "+rli.solution.fctObjectif + "  ;  Borne sup : " + vBorneSup);
			System.out.println(" Pour un temps de calcul de : " + rli.solution.tpsCalcul + " ms ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void testInvalid() {
		try {
			String nomInstance = "sparse_10_30_3_5_I" ;
		 	
			FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
			Foret foret=fa.processLineByLineForest();
			
			BorneCalc testBorneInf = new BorneCalc (foret) ;
			BorneCalc testBorneSup = new BorneCalc (foret) ;
			
			testBorneInf.borneSolution(nomInstance,0).comment = "calcul borne INF";
			int avant = testBorneInf.borneSolution(nomInstance,0).fctObjectif;
			testBorneInf.borneSolution(nomInstance,0).getInFile();
			
			int borneSup = testBorneSup.borneSolution(nomInstance, 1).fctObjectif;
			
			RLIntensification rli = new RLIntensification("Solution/"+nomInstance+"_sol.full");
			
			int minFctObj = Integer.MAX_VALUE;
			for (int i = 0 ; i < 500 ; i++) {
				rli.intensification();
				if (rli.solution.fctObjectif < minFctObj) {
					minFctObj = rli.solution.fctObjectif;
				}
			}
			rli.solution.getInFile();
			System.out.println("Avant : "+avant+" ; apres : "+minFctObj+" ; borne sup : "+borneSup);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	 public static void main(String[] args) {
		//testInvalid();
		testDescente("dense_10_30_3_10_I");
		 

	 }
	
}
