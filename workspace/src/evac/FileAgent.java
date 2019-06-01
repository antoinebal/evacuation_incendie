package evac;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class FileAgent {
	FileAgent() {}
	 final static Charset ENCODING = StandardCharsets.UTF_8;
	 Path filePath;
	 
	 int state=0;
	 
	 Foret foret = new Foret () ;  
	 
	 Solution solution = new Solution () ; 
	 
	 public FileAgent(String fileName){
		    filePath = Paths.get(fileName);
		  }
	
	  Foret processLineByLineForest() throws IOException {
		  state=0;
		    try (Scanner scanner =  new Scanner(filePath, ENCODING.name())){
		      while (scanner.hasNextLine()){
		        processLineForest(scanner.nextLine());
		      }      
		    }
		    
		    return foret;
		  }

		protected void processLineForest(String line) throws IOException{
		    //use a second Scanner to parse the content of each line 
			if (!line.isEmpty()) {
			if (line.charAt(0)!='c') {
		    try(Scanner scanner = new Scanner(line)){
		      scanner.useDelimiter(" ");
		      
		      // On a passé un c, on est sur la ligne 2 du fichier 
		      if (state==1) {
		    	  //si on est dans la première ligne on remplit le nb de chemins et le safe
		    	   if (scanner.hasNext()){
				       foret.setNbFeuille(Integer.parseInt(scanner.next()));  
				       foret.setSafe(new Sommet(Integer.parseInt(scanner.next())));
				       state++;
		    	   }
		    	   
		    	   else {
		    		   // Rajout d'une expression pimpé comme quoi la ligne est vide ou invalide 
					    throw new IOException() ; 
					   }
		    	   
		    //on remplit les chemins, on arrive sur les lignes après 3. 
		      } else if (state==2) {
		    	  if (scanner.hasNext()){
		    		ArrayList<Sommet> chemin = new ArrayList<Sommet>();
		    		//on rajoute le noeud à évacuer
		    		int idFeuille = Integer.parseInt(scanner.next());
		    		
		    		//on rajoute la population
		    		int population = Integer.parseInt(scanner.next());
		    		
		    		//on récupère le maxRate
		    		int maxRate = Integer.parseInt(scanner.next());
		    		
		    		Sommet feuille = new Sommet(idFeuille, population, maxRate);
		    		chemin.add(feuille);
		    		
		    		//on récupère la taille du chemin
		    		int tailleChemin = Integer.parseInt(scanner.next());
		    		
		    		//on ajoute les autres noeuds du chemin
		    		while(scanner.hasNext()) {
		    			int idSommet = Integer.parseInt(scanner.next());
		    			chemin.add(new Sommet(idSommet));
		    		}
		    		
		    		//on ajoute le chemin à la forêt
		    		foret.ajoutChemin(new Chemin(feuille, tailleChemin, chemin));
		    	  }
		      } else if (state==3) {
		    	  if (scanner.hasNext()){
		    		foret.setNbSommet(Integer.parseInt(scanner.next()));
		    		foret.setNbArc(Integer.parseInt(scanner.next()));
		    		state++ ; 
		    	  } 
		    	 
		     } // ON EN EST LA FAIRE LE STATE 4 !! Attention, length est en distance et pas en temps (demander la conversion)
		        
		      else if (state==4) {
		    	  if (scanner.hasNext()){
		    		  int id1 = Integer.parseInt(scanner.next()); 
		    		  int id2 = Integer.parseInt(scanner.next()); 
		    		  // arc id , id , date exp, dureee, capacte 
		    		  long dateExp = Long.parseLong(scanner.next()); 
		    		  int duree = Integer.parseInt(scanner.next()); 
		    		  int capa = Integer.parseInt(scanner.next()); 
		    		  
		    		  
		    		  Arc arc = new Arc (new Sommet (id1),new Sommet(id2),dateExp,duree,capa) ; 
		    		  foret.ajoutArc(arc);
		    	  }
		    	  
		      }
		      
		    }
		    
			} else {
				state++ ; 
			}
			}
		}
		
		public void printForet() {foret.printForet();}
		
		
		
		//::::::::::::::::::::::::::: Solution ::::::::::::::::::::::::::::::::::::
			
		 Solution processLineByLineSolution() throws IOException {
			  state=0;
			    try (Scanner scanner =  new Scanner(filePath, ENCODING.name())){
			      while (scanner.hasNextLine()){
			        processLineSolution(scanner.nextLine());
			      }      
			    }
			    
			    return solution;
			  }
			  
		 
		 
		 protected void processLineSolution(String line) { 
			 try(Scanner scanner = new Scanner(line)){
			      scanner.useDelimiter(" "); 
			 
			 
			 if (state==0) {
				 solution.setNomInstance(scanner.next());
				 state++ ; 
			 }
			 
			 else if (state==1) { 
				solution.setNbFeuilleEvac(Integer.parseInt(scanner.next()));
				state++ ;  
			 } 
			 
			 else if ((state==2) && (line.charAt(0)!='v') && (line.charAt(0)!='i'))  { 
				 
				 int id = Integer.parseInt(scanner.next()) ; 
				 int tauxEvac =Integer.parseInt(scanner.next())  ; 
				 int dateDebut = Integer.parseInt(scanner.next()) ; 
				 
				 solution.ajoutFeuille(id, tauxEvac, dateDebut);
			 } 
			 
			 else if ((state==2) && ((line.charAt(0)=='v') || (line.charAt(0)!='i')))  { 
				 solution.setValid(scanner.next());
				 state++;
			 }
			 
			 else if (state==3) {
				 solution.setFctObjectif(Integer.parseInt(scanner.next()));
				 state++;
			 }
			 
			 else if (state==4) {
				 solution.setTpsCalcul(Integer.parseInt(scanner.next()));
				 state++;
			 }
			 
			 else if (state==5) {
				 String methode = "";
				 methode = scanner.next();
				 while (scanner.hasNext()) {
					 methode = methode +" "+ scanner.next();
				 }
				 solution.setMethode(methode);
				 state++;
			 }
			 
			 else if (state==6) {
				 
				 String comment = "";
				 comment = scanner.next();
				 while (scanner.hasNext()) {
					 comment = comment +" "+ scanner.next();
				 }
				 solution.setComment(comment);
				 state++;
			 }
			 
			 
		 }
		 }
		

		
	 public static void main(String[] args) {
			FileAgent fa = new FileAgent("../InstancesInt/dense_10_30_3_2_I.full");
			try {
				fa.processLineByLineForest();
				fa.printForet();
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	
}
