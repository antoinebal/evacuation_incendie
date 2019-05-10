package evac;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Checker {

	Solution solution;
	
	Foret foret;
	
	
	public Checker (String fileName){
		    FileAgent fa = new FileAgent(fileName);
		    try {
				solution = fa.processLineByLineSolution();

				FileAgent fa2 = new FileAgent("../InstancesInt/"+solution.nomInstance);
				foret=fa2.processLineByLineForest();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		  }
	
	public boolean checkSolution() {
		
		boolean ok = true ; 
		//
		if (solution.nbFeuilleEvac > foret.nbFeuille) { 
			ok = false ;
			System.out.println("Erreur : nombre de feuilles à évacuer plus grand que le nombre de feuilles de la forêt!"); 
		}
		else { 
			for (int i=0 ; i<solution.nbFeuilleEvac ; i++) {
				
				Sommet feuille = solution.feuilles.get(i) ; // rendre feuillesevac public !
				int txevacSol = solution.feuilles.get(i).tauxEvac ; 
				int maxRateFo = getMaxRate()
				if (solution.feuilles.get(i)>foret.
			}
			
		} 
		
	}
	
	 
	
	 


}
