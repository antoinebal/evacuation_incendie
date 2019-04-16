
public class Arc {
	Sommet s1;
	Sommet s2;
	
	int dateExp;
	
	int duree;
	int capacite;
	
	Arc(Sommet s1, Sommet s2, int dateExp, int duree, int capacite) {
		this.s1=s1;
		this.s2=s2;
		this.dateExp=dateExp;
		this.duree=duree;
		this.capacite=capacite;
	}
}
