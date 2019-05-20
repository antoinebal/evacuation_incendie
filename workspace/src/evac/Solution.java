package evac;

import java.util.ArrayList;



public class Solution {
	
	String nomInstance ; 
	
	int nbFeuilleEvac ; 
	
	public class FeuilleEvac { 
		int id ; 
		int tauxEvac ; 
		int dateDebut ; 
		
		 FeuilleEvac(int id, int tauxEvac, int dateDebut) { 
			this.id=id ; 
			this.tauxEvac=tauxEvac ; 
			this.dateDebut=dateDebut; 
		}
		}
	
	ArrayList<FeuilleEvac> feuilles ; 
		
	String valid ; 
	
	int fctObjectif ; 
	
	int tpsCalcul ; 
	
	String methode ; 
	
	String comment ; 
	
	Solution(){
		feuilles=new ArrayList<FeuilleEvac>();
	}
	
	void setNomInstance (String nomInstance) { 
		this.nomInstance=nomInstance ; 
	}
	
	void setNbFeuilleEvac (int nbFeuilleEvac) { 
		this.nbFeuilleEvac = nbFeuilleEvac ;
	}
	
	void ajoutFeuille (int id, int tauxEvac, int dateDebut) { 
		feuilles.add(new FeuilleEvac(id,tauxEvac,dateDebut)) ; 
	}
	
	void setValid (String valid) { 
		this.valid = valid ; 
	} 
	
	void setFctObjectif (int fctObjectif) { 
		this.fctObjectif = fctObjectif; 
	} 	
	
	void setTpsCalcul (int tpsCalcul) { 
		this.tpsCalcul=tpsCalcul ; 
	}
	
	void setMethode (String methode) { 
		this.methode=methode ; 
	}
	
	void setComment (String comment) { 
		this.comment=comment ; 
	}
	
	
}
	