package evac;

import java.util.ArrayList;

public class SolutionIncorrecteException extends Exception {
    
	/*
	 * variable remplie quand solution non valide pour
	 * corriger la corriger.
	 * contient les ids des feuilles dont sont issues
	 * les populations à l'origine des dépassement
	 */
	private ArrayList<Integer> idFeuillesSource;
	
	/*
	 * indique de combien la capacité a été dépassée
	 */
	private int depassement;
	private int flux;
	
	private static final long serialVersionUID = 1L;
	
	public SolutionIncorrecteException() {
		idFeuillesSource=null;
		this.depassement=0;
		this.flux = 0;
	}

	public SolutionIncorrecteException(int id1, int id2, ArrayList<Integer> idfs, int depassement,int flux) {
	//System.out.println("D�passement de "+depassement+" entre "+id1+" et "+id2+".");
	idFeuillesSource=idfs;
	this.depassement=depassement;
	this.flux = flux;
    }
	
	public ArrayList<Integer> getIDFeuillesSource() {return idFeuillesSource;}
	public int getDepassement() {return depassement;}
	public int getFlux() {return flux;}
}