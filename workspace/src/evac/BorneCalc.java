package evac;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
			
			duree= chem.dureeChemin(foret) + longTrain;// duree chemin ne prend pas en compte la date debut
			
			if (duree>max) { 
				max=duree ; 
				
			}
		} 
	//	res.add(max) ; 
	//	res.add(longTrain) ;
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
			
			duree= chem.dureeChemin(foret) + longTrain; // duree chemin ne prend pas en compte la date debut
			total=total+duree ; 
			}
		return total ; 
	}
	
	// int borne : 0 pour la borne inf, sinon c'est la sup  
	Solution borneSolution (String nomInstance, int borne) { 
		Solution s = new Solution () ; 
		Sommet feuille ;  
		
		
		s.nomInstance = nomInstance ; 
		s.nbFeuilleEvac = foret.nbFeuille ;
		
		for (int i=0 ; i<s.nbFeuilleEvac ; i++) { 
			feuille = foret.cheminsEvac.get(i).getFeuille() ; 
			// ajout d'une feuille a evacuer avec son id, maxpack qui sera son taux d'evacuation effectif, 
			//et 0 sa date de début effective 
			FeuilleEvac fe = new FeuilleEvac (feuille.id_ ,feuille.maxPack_,0) ; 
			s.feuilles.add(fe) ;
			
		} 
		
		s.valid = "valid" ;
		
		if (borne==0) { 
		// borne inf
			Instant start = Instant.now();
			s.fctObjectif=borneInf() ; 
			Instant end = Instant.now();
			s.tpsCalcul = (int) Duration.between(start, end).toMillis();
			s.methode = "execution basique du calcul de borne inférieure " ; 
			s.comment = "BORNE INFERIEURE" ; 
		} else { 
			// borne sup 
			Instant start = Instant.now();
			s.fctObjectif=borneInf() ; 
			Instant end = Instant.now();
			s.tpsCalcul = (int) Duration.between(start, end).toMillis();
			s.methode = "execution basique du calcul de borne supérieure " ; 
			s.comment = "BORNE SUPERIEURE" ; 
			
		} 
		return s ; 
	} 
	
	
	
	public static void main(String[] args) {
		
		
		try {
			FileAgent fa = new FileAgent("../InstancesInt/dense_10_30_3_10_I.full");
			Foret foret=fa.processLineByLineForest();
			BorneCalc testBorneInf = new BorneCalc (foret) ;
			BorneCalc testBorneSup = new BorneCalc (foret) ;
			
			testBorneInf.borneSolution("dense_10_30_3_10_I",0) ; 
			testBorneSup.borneSolution("dense_10_30_3_10_I",1) ; 
			
			
			System.out.println(testBorneInf.borneInf());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
