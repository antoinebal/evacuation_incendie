package evac;

public class FeuilleEvac {
		int id ; 
		int tauxEvac ; 
		int dateDebut ; 
		
		 FeuilleEvac(int id, int tauxEvac, int dateDebut) { 
			this.id=id ; 
			this.tauxEvac=tauxEvac ; 
			this.dateDebut=dateDebut; 
		}
		 
		 FeuilleEvac(FeuilleEvac object) {
			this.id=object.id ; 
			this.tauxEvac=object.tauxEvac ; 
			this.dateDebut=object.dateDebut; 
		 }
		 
		 void setTauxEvac(int taux) {
			 tauxEvac=taux;
		 }
		 
		 void setDateDebut(int date) {
			 dateDebut=date;
		 }
		 
		 public String toString() {
			 return "id : "+id+" ; taux evac : "+tauxEvac+" ; date debut : "+dateDebut;
		 }
}

