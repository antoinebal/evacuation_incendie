package evac;
public class Arc {
	Sommet s1;
	Sommet s2;
	
	long dateExp;
	
	int duree;
	int capacite;
	
	
	Arc(Sommet s1, Sommet s2, long dateExp, int duree, int capacite) {
		this.s1=s1;
		this.s2=s2;
		this.dateExp=dateExp;
		this.duree=duree;
		this.capacite=capacite;
	}
	
	Sommet getS1() {return s1;}
	Sommet getS2() {return s2;}
	int getCapa() {return capacite;}
	int getDuree() {return duree;}
	long getDateExp() {return dateExp;}
	
	public void printArc() {
		System.out.println("S1 : "+s1.id_);
		System.out.println("S2 : "+s2.id_);
		System.out.println("Date exp : "+dateExp);
		System.out.println("Duree : "+duree);
		System.out.println("Capacité : "+capacite);
		
	}
	
	public String toString() {
		return "ID1 : "+s1.getId() + " ; ID2 : "+s2.getId();
	}
	
	public static void main(String[] args) {
		
	}
}
