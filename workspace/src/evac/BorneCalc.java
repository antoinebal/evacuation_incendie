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
		int max=0 ; 
		
		for (int i =0 ; i<listChemin.size() ; i++) { 
			int longTrain=0;
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
	
	
	
	
	ArrayList<Integer> borneSup () { 
		int total = 0 ; 
		ArrayList<Integer> datesDebut = new ArrayList<Integer> () ; 
		Chemin chem ;
		Sommet feuille ; 
		ArrayList<Chemin> listChemin = foret.cheminsEvac ; 
		int cpt,duree ;
		
		
		datesDebut.add(0) ;
		
		for (int i =0 ; i<listChemin.size() ; i++) { 
			int longTrain=0 ; 
			
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
			datesDebut.add(total); 
			
			}
		return datesDebut ; 
	}
	
	// int borne : 0 pour la borne inf, sinon c'est la sup  
	Solution borneSolution (String nomInstance, int borne) { 
		Solution s = new Solution () ; 
		Sommet feuille ;  
		ArrayList<Integer> res  ; 
		
		s.nomInstance = nomInstance ; 
		s.nbFeuilleEvac = foret.nbFeuille ;
	
		int i ; 
		if (borne==0) { 
		// borne inf
			Instant start = Instant.now();
			s.fctObjectif=borneInf() ; 
			Instant end = Instant.now();
			s.tpsCalcul = (int) Duration.between(start, end).toMillis();
			
			for ( i=0 ; i<s.nbFeuilleEvac ; i++) { 
				feuille = foret.cheminsEvac.get(i).getFeuille() ; 
				// ajout d'une feuille a evacuer avec son id, maxpack qui sera son taux d'evacuation effectif, 
				//et 0 sa date de début effective 
				FeuilleEvac fe = new FeuilleEvac (feuille.id_ ,feuille.maxPack_,0) ; 
				s.feuilles.add(fe) ;
				
			} 
			s.methode = "execution basique du calcul de borne inférieure " ; 
			s.comment = "BORNE INFERIEURE" ; 
			s.valid = "invalid";
			
			//Attention vérifier si la borne inf est valide
			//Checker checker = new Checker(s);
			//if (checker.checkSolution()) {
		//		s.valid = "valid";
		//	} 
		} else { 
			// borne sup 
			
			Instant start = Instant.now();
			res = borneSup() ; 
			Instant end = Instant.now();
			s.tpsCalcul = (int) Duration.between(start, end).toMillis();
			
			for ( i=0 ; i<s.nbFeuilleEvac ; i++) { 
				feuille = foret.cheminsEvac.get(i).getFeuille() ; 
				// ajout d'une feuille a evacuer avec son id, maxpack qui sera son taux d'evacuation effectif, 
				//et 0 sa date de début effective 
				FeuilleEvac fe = new FeuilleEvac (feuille.id_ ,feuille.maxPack_,res.get(i)) ; 
				s.feuilles.add(fe) ;
				
			} 
			s.fctObjectif = res.get(i) ; 
			s.valid = "valid" ;
			s.methode = "execution basique du calcul de borne supérieure " ; 
			s.comment = "BORNE SUPERIEURE" ; 
			
		} 
		return s ; 
	} 
	
	
	
	public static void main(String[] args) {
		
		
		try {
			FileAgent fa = new FileAgent("../InstancesInt/graphe-TD-sans-DL-data.txt");
			Foret foret=fa.processLineByLineForest();
			BorneCalc testBorneInf = new BorneCalc (foret) ;
			BorneCalc testBorneSup = new BorneCalc (foret) ;
			
			testBorneInf.borneSolution("graphe-TD-sans-DL-data",0).getInFile(); 
			testBorneSup.borneSolution("test_sup",1).getInFile(); 
		
			
			System.out.println(testBorneInf.borneInf());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
