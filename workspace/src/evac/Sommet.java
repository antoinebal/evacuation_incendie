package evac;

public class Sommet {
	
	
	// A voir si on ne met pas une classe fille pour les feuilles car seule
	// les feuilles ont les infos sur nbpers et maxpack. 
	
	
	int id_;
	//le nombre de personnes à évacuer
	int nbPers_=0;
	//nombre max de pers par paquet
	int maxPack_=0;
	
	Sommet(int id) {
		id_=id;
	}
	
	Sommet(int id, int nbPers, int maxPack) {
		id_=id;
		nbPers_=nbPers;
		maxPack_=maxPack;
	}
	
}
