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
	
	public void printArc() {
		System.out.println("S1 : "+s1.id_);
		System.out.println("S2 : "+s2.id_);
		System.out.println("Date exp : "+dateExp);
		System.out.println("Duree : "+duree);
		System.out.println("Capacité : "+capacite);
		
	}
}
