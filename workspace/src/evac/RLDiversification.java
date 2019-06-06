package evac;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class RLDiversification {
	Solution solution;
	Checker checker;
	
	int DELTA_RANGE_INF=50;
	int DELTA_RANGE_SUP=300;
	
	//on ne s'autorise pas à générer des voisins
	//avec des seuils aussi bas car inefficace
	static int SEUIL_MIN_RATE=7;
	static int ITERATIONS_DIVERSIFICATION=50;
	
	RLDiversification() {}
	
	RLDiversification(String fichierSolution) {
		FileAgent fa = new FileAgent(fichierSolution);
		try {
			solution = fa.processLineByLineSolution();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.checker = new Checker(solution);
	}
	
	RLDiversification(Solution solution) {
		this.solution = solution;
		this.checker = new Checker(solution);
	}
	
	RLDiversification(Solution solution, Foret foret) {
		this.solution = solution;
	}
	
	
	/*
	 * génère des voisins : 
	 * > contenant une date de départ inférieure pour un site
	 * > contenant un taux inférieur pour un site
	 */
	public ArrayList<Solution> genereVoisinage() {
		ArrayList<Solution> voisinage = new ArrayList<Solution>();
		
		for (int i = 0 ; i < solution.nbFeuilleEvac ; i++) {
			
			for (int delta = DELTA_RANGE_INF ; delta < DELTA_RANGE_SUP ; delta++) {
			Solution voisin1 = genereVoisinDate(i, delta);
			if (voisin1 != null) {
				voisinage.add(voisin1);
				
			} 
			Solution voisin2 = genereVoisinRate(i, delta);
			if (voisin2 != null) {
				voisinage.add(voisin2);	
			}
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
	
	//génère un voisin au taux de départ inférieur
	public Solution genereVoisinRate(int numeroFeuille, int delta) {
		Solution newSolution = new Solution(solution) ; 
		Solution aux = newSolution;
		newSolution = this.solution;
		this.solution = aux;
		// Reduire nombre paquet 
		// Taux evac solution + delta <= maxPack de la feuille
		if ((solution.feuilles.get(numeroFeuille).tauxEvac - delta >  SEUIL_MIN_RATE)) { 
			//on enlève delta au rate de cette feuille
			newSolution.setRateFeuilleNo(numeroFeuille, delta); 
			return newSolution;
		}
		return null ; 
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	
	Solution diversification() {
		int compteur = 0;
		boolean found=false;
		Solution voisinChoisi=null;
		while (!found) {
		
		ArrayList<Solution> voisins = genereVoisinage();
		//on choisi un voisin aléatoire
		int indexAleatoire = randInt(0, voisins.size());
		voisins.remove(indexAleatoire);
		voisinChoisi = voisins.get(indexAleatoire);
		checker.setSolution(voisinChoisi);
		try {
			//si le voisin est valide, on a notre solution
			boolean validite = checker.checkSolution();
			found = true;
		} catch (SolutionIncorrecteException e) {
			
		}
	
		}
		return voisinChoisi;
		 
	}
	
	private Solution diversification(Solution solution, int maxIterations, int numIteration) {
		if (numIteration >= maxIterations) {
			return solution;
		} else {
			
		System.out.println("Diversification à partir de "+solution.fctObjectif);
		RLIntensification rli = new RLIntensification(solution);
		boolean found=false;
		Solution voisinChoisi=null;
		this.solution=solution;
		ArrayList<Solution> voisins = genereVoisinage();
		
		while (!found&&!voisins.isEmpty()) {
			
			//on choisit un voisin aléatoire
			int indexAleatoire = randInt(0, voisins.size()-1);
			voisinChoisi = voisins.get(indexAleatoire);
			//on l'enlève pour ne pas le choisir deux fois
			voisins.remove(indexAleatoire);
			checker.setSolution(voisinChoisi);
			try {
				//si le voisin est valide, on l'intensifie
				checker.checkSolution();
				rli.setSolution(voisinChoisi);
				rli.intensification();
				//si la solution issue de l'intensification est meilleure, on arrête
				if (rli.solution.fctObjectif < solution.fctObjectif) {
					found = true;
					Solution sDiv = diversification(rli.solution, maxIterations, numIteration+1);
					if (sDiv==null) {
						return rli.solution;
					} else {
						return sDiv; 
					}
				}
			} catch (SolutionIncorrecteException e) {
				
			}
			
			
	
		}
		return null;
		
		}
		 
	}
	
	static void rechercheLocale(String nomInstance) {
		FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
		Foret foret=null;
		System.out.println("******************************************************* Nouvelle instance pour intensification + diversification  : " + nomInstance + "*****************************************************");
		try {
			foret = fa.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		BorneCalc BCSup = new BorneCalc (foret) ;
		 
		Solution sBorneSup = BCSup.borneSolution(nomInstance,1);
		Solution sBorneInf = BCSup.borneSolution(nomInstance, 0) ;
		sBorneSup.comment="calcul borne SUP";
		sBorneSup.getInFile();
	 
		RLDiversification rld = new RLDiversification(sBorneSup);
		RLIntensification rli = new RLIntensification(sBorneSup, foret);
		
		System.out.println("****************** Intensification : " + nomInstance + "**********************");
		Instant start = Instant.now();
		rli.intensification();
		Instant end = Instant.now();
		int tpsIntensification = (int) Duration.between(start, end).toMillis();
		
		int meilleureFctObjLocale = rli.solution.fctObjectif;
		System.out.println("****************** Diversification : " + nomInstance + "**********************");
		Instant start2 = Instant.now();
		Solution sDiv = rld.diversification(rli.solution, 1, 0);
		Instant end2 = Instant.now();
		int tpsDiversification = (int) Duration.between(start2, end2).toMillis();
		int tpsTotal = tpsIntensification + tpsDiversification ; 
		int foDiv=0;
		if (sDiv!=null) {
			/*
			 * si la solution issue de la diversification est
			 * meilleure, on la conserve
			 */
			sDiv.tpsCalcul = tpsTotal  ;
			sDiv.comment = "intensification puis diversification" ; 
			sDiv.methode = "DIVERSIFICATION" ;
			sDiv.getInFile();
			foDiv=sDiv.fctObjectif;
			
		} else {
			//sinon on garde celle de l'intensification
			rli.solution.getInFile();
			foDiv=rli.solution.fctObjectif;
		}
		System.out.println(" RESULTAT FIN DE DIVERSIFICATION POUR : " + nomInstance);
		System.out.println("Borne INF : "+sBorneInf.fctObjectif + " ; Après une intensification : "+meilleureFctObjLocale+" ; Après diversification "+foDiv+ "Borne SUP : "+sBorneSup.fctObjectif);
		System.out.println(" Pour un temps de calcul de : " + tpsIntensification + " ms pour l'intensification " + tpsDiversification + "ms pour la diversification , soit un temps de calcul total de : "+ tpsTotal + " ms ");
	
	
		/*for (int i = 0 ; i < ITERATIONS_DIVERSIFICATION ; i++) {
			Solution voisin = rld.diversification();
			if (voisin != null) {
				
			}*/
		
	}
	
	
	static void testVoisinage() {
		String nomInstance = "sparse_10_30_3_1_I";
		 
		FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
		Foret foret=null;
		try {
			foret = fa.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 BorneCalc testBorneSup = new BorneCalc (foret) ;
		 
	 Solution borneSup = testBorneSup.borneSolution(nomInstance,1);
	 borneSup.comment="calcul borne SUP";
	 borneSup.getInFile();
	 
	 RLDiversification rld = new RLDiversification("Solution/"+nomInstance+"_sol.full");
	 
	 rld.diversification();
	 
	}
	
	static void testRL() {
		String nomInstance = "sparse_10_30_3_1_I";
		FileAgent fa = new FileAgent("InstancesInt/"+nomInstance+".full");
		Foret foret=null;
		try {
			foret = fa.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BorneCalc testBorneSup = new BorneCalc (foret) ;
		 
		Solution borneSup = testBorneSup.borneSolution(nomInstance,1);
		borneSup.comment="calcul borne SUP";
		borneSup.getInFile();
		 
		RLDiversification rld = new RLDiversification(borneSup);
		RLIntensification rli = new RLIntensification(borneSup, foret);
		
	}
	
	 public static void main(String[] args) {
		rechercheLocale("dense_10_30_3_10_I");
	 }
	
	
	
}
