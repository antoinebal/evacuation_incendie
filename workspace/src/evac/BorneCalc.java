package evac;

import java.io.IOException;
import java.util.ArrayList;

public class BorneCalc {
	
	Foret foret ; 
	
	BorneCalc (Foret foret) { this.foret = foret ;  }
	
	int borneInf () { 
		// La seule contrainte est le taux d'évacuation des feuilles
		// On suppose ensuite que l'evacuation n'est que la somme des temps de traversée 
		// du chemin d'évacuation + la longueur du train 
		
		Chemin chem ;
		Sommet feuille ; 
		ArrayList<Chemin> listChemin = foret.cheminsEvac ; 
		int cpt,duree ;
		int longTrain=0,max=0 ; 
		
		for (int i =0 ; i<listChemin.size() ; i++) { 
			
			chem = listChemin.get(i) ;
			feuille = chem.getFeuille() ; 
			cpt = feuille.getPop() ; 
			while (cpt > feuille.maxPack_) {
				cpt = cpt - feuille.maxPack_;
				longTrain ++ ; 
			}
			 /* il reste peut être quelques personnes à passer, dont le nombre
			 * est inférieur au taux
			 */				
			
			if (cpt!=0) { longTrain++; } 
			
			duree= chem.dureeChemin(foret) + longTrain;
			
			if (duree>max) { 
				max=duree ; 
			}
		} 
		return max; 
	} 
	
	
	
	int borneSup () { 
		int total = 0 ; 
		Chemin chem ;
		Sommet feuille ; 
		ArrayList<Chemin> listChemin = foret.cheminsEvac ; 
		int cpt,duree ;
		int longTrain=0 ; 
		
		for (int i =0 ; i<listChemin.size() ; i++) { 
			
			chem = listChemin.get(i) ;
			feuille = chem.getFeuille() ; 
			cpt = feuille.getPop() ; 
			while (cpt > feuille.maxPack_) {
				cpt = cpt - feuille.maxPack_;
				longTrain ++ ; 
			}
			 /* il reste peut être quelques personnes à passer, dont le nombre
			 * est inférieur au taux
			 */				
			
			if (cpt!=0) { longTrain++; 
			} 
			
			duree= chem.dureeChemin(foret) + longTrain;
			total=total+duree ; 
			}
		return total ; 
	}
	
	
	
	public static void main(String[] args) {
		
		
		try {
			FileAgent fa = new FileAgent("../InstancesInt/graphe-TD-sans-DL-data.txt");
			Foret foret=fa.processLineByLineForest();
			BorneCalc testBorneInf = new BorneCalc (foret) ; 
			System.out.println(testBorneInf.borneInf());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
