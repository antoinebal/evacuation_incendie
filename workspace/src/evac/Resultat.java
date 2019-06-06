package evac;

public class Resultat {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// ******************************************* TEST INTENSIFICATION ************************************************
		RLIntensification.testDescente ("graphe-TD-sans-DL-data") ; 
		RLIntensification.testDescente ("sparse_10_30_3_6_I") ; 
		RLIntensification.testDescente ("sparse_10_30_3_1_I") ; 
		RLIntensification.testDescente ("medium_10_30_3_8_I") ; 
		RLIntensification.testDescente ("medium_10_30_3_6_I") ; 
		RLIntensification.testDescente ("sparse_10_30_3_4_I") ; 
		RLIntensification.testDescente ("medium_10_30_3_9_I") ; 
		RLIntensification.testDescente ("dense_10_30_3_6_I") ; 
		RLIntensification.testDescente ("dense_10_30_3_4_I") ; 
		RLIntensification.testDescente ("dense_10_30_3_10_I") ; 
		
		//  ******************************************* TEST DIVERSIFICATION ************************************************
		
	/*	RLDiversification.rechercheLocale("graphe-TD-sans-DL-data") ; 
		RLDiversification.rechercheLocale ("sparse_10_30_3_6_I") ; 
		RLDiversification.rechercheLocale ("medium_10_30_3_9_I") ;
		RLDiversification.rechercheLocale ("dense_10_30_3_10_I") ; */
		
		

	}

}
