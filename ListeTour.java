public class ListeTour{

	public Tour root;
	
	public ListeTour(){
		root = null;
	}
	
	/*METHODE INSERTION EN TETE DERRIERE PIEGE 3*/
	public void insertTete(Tour tour){
		Tour prev = root;
		Tour cur = null;
		
		if(prev != null){
			cur = prev.next;
			while(cur != null && cur instanceof Piege3){
				prev = cur;
				cur = cur.next;
			}
			prev.next = tour;
			tour.next = cur;
		} else {
			root = tour;
		}
    }
    
    public void supprTete(){
		root = root.next;
	}
	
	public void suppr(Tour tourSuppr){
		Tour cur = root;
		Tour prev = null;
		
		while(cur != null){
			if(cur == tourSuppr){
				if(prev == null){
					root = cur.next;
				} else {
					prev.next = cur.next;
				}
			}
		prev = cur;
		cur = cur.next;
		}
	}
	
	public boolean find(Tour tour){
		boolean trouve = false;
		Tour cur = root;
		
		while(!trouve && cur!= null){
			if(cur == tour){
				trouve = true;
			}
			cur = cur.next;
		}
		return trouve;
	}
	
	public void display() {
        Tour cur = root;
        if (root == null) {
            System.out.println("liste vide kéké");
        } else {
            while (cur != null) {
                System.out.println(cur);
                cur = cur.next;
            }
        }
    }

}


