package evac;

import java.io.IOException;
import java.util.ArrayList;

public class RLIntensification {
	
	Solution solution;
	Checker checker;
	Foret foret;
	
	
	int delta=1;
	
	RLIntensification(String fichierSolution) {
		FileAgent fa = new FileAgent(fichierSolution);
		try {
			solution = fa.processLineByLineSolution();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.checker = new Checker(solution);
		FileAgent fa2 = new FileAgent("../InstancesInt/"+solution.nomInstance+".txt");
		try {
			foret = fa2.processLineByLineForest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	RLIntensification(RLIntensification object)
	{
		
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
	
	public ArrayList<Solution> genereVoisinage() {
		ArrayList<Solution> voisinage = new ArrayList<Solution>();
		
		for (int i = 0 ; i < solution.nbFeuilleEvac ; i++) {
			voisinage.add(genereVoisinDate(i));
			voisinage.add(genereVoisinRate(i));
		}
		
		return voisinage;
	}
	
	// Faire partir plus tot 
	public Solution genereVoisinDate(int numeroFeuille) {
		Solution newSolution = new Solution(solution) ;              
	//	Sommet feuille = this.solution.feuilles.get(numeroFeuille) ; 
		
		if ((solution.feuilles.get(numeroFeuille).dateDebut - delta)>=0) {
			newSolution.setDateFeuilleNo(numeroFeuille,delta);
		}
		
		return newSolution ; 
	}
	
	public Solution genereVoisinRate(int numeroFeuille) {
		Solution newSolution = new Solution(solution);
		// Reduire nombre paquet 
		// Taux evac solution + delta <= maxPack de la feuille
		if ((solution.feuilles.get(numeroFeuille).tauxEvac + delta <=  foret.recupChemin(solution.feuilles.get(numeroFeuille).id).getNoeudAt(0).maxPack_)) { 
			newSolution.setRateFeuilleNo(numeroFeuille, delta); 
		}
		
		return newSolution ; 
	}
	
	void intensificationValid() {
		genereVoisinage () ; 
		
	}
	
	void intensificationInvalid() {
		
	}
	
	
	 public static void main(String[] args) {
		RLIntensification rli = new RLIntensification("../Solution/graphe-TD-sans-DL-sol.txt");
		ArrayList<Solution> voisinage=rli.genereVoisinage();
	

		 
		}
	
	
}
