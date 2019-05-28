package evac;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;



public class Solution {
	
	String nomInstance ; 
	
	int nbFeuilleEvac ; 
	
// FEUILLE EVAC CLASSE APPART POUR POUVOIR CREER DES OBJETS ! 
	
	ArrayList<FeuilleEvac> feuilles ; 
		
	String valid ; 
	
	int fctObjectif ; 
	
	int tpsCalcul ; 
	
	String methode ; 
	
	String comment ; 
	
	Solution(){
		feuilles=new ArrayList<FeuilleEvac>();
	}
	
	Solution(String nomInstance, int nbFeuilleEvac, String valid) {}
	
	Solution(Solution copy){
		this.nomInstance = copy.nomInstance ; 
		this.nbFeuilleEvac = copy.nbFeuilleEvac ; 
		this.feuilles = new ArrayList<FeuilleEvac>(copy.feuilles) ; 
		this.valid = copy.valid ; 
		this.fctObjectif = copy.fctObjectif ; 
		this.tpsCalcul = copy.tpsCalcul ; 
		this.methode = copy.methode ; 
		this.comment = copy.comment ; 
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
	
	void setRateFeuilleNo(int i, int delta) {
		feuilles.get(i).tauxEvac+=delta;
	}
	
	void setDateFeuilleNo(int i, int delta) {
		feuilles.get(i).dateDebut-=delta;
	}
	
	void getInFile () { 
		
		PrintWriter writer;
		try {
			// a voir ici : le path pour le fichier a créer et son nom , 
			//si nom instance s'utilise avec ou sans this
			// et s'il contient le .txt/full ou pas , apparement non 
			
			writer = new PrintWriter("../Solution/"+nomInstance+"_sol.full", "UTF-8");
		
			writer.println(nomInstance);
			writer.println(nbFeuilleEvac);
			
			for (int i=0 ; i<nbFeuilleEvac ; i++) {
				writer.println(feuilles.get(i).id+" "+feuilles.get(i).tauxEvac+" "+feuilles.get(i).dateDebut);
			}
			writer.println(valid);
			writer.println(fctObjectif);
			writer.println(tpsCalcul);
			writer.println(methode);
			writer.println(comment);
			writer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
/*	GET IN FILE TEST 
	
	 public static void main(String[] args) {
		 
		 FileAgent fa = new FileAgent("../Solution/graphe-TD-sans-DL-sol.txt");
			try {
				Solution solution = fa.processLineByLineSolution();
				
				solution.getInFile(); 
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 }
	*/
	
}
	
