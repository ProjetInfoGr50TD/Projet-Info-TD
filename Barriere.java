import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;

import java.io.File;

import javax.imageio.ImageIO;

public class Barriere extends Element{
	
	private static final String nomImage = "Barriere.png";
	public static final int PRIX = 50;

	public Barriere(int px, int py, Chemin chemin, Case[][] tabCases){
		super();
		
		try {
			image= ImageIO.read(new File(nomImage));
        } catch(Exception err){
			System.out.println(nomImage+" introuvable !");            
            System.exit(0);    
        }
        
        posx = px;
        posy = py;
        int j = (int)((posy + hauteur)/Case.LCASE);
		int i = (int)(posx/Case.LCASE);
		for(int c = i ; c <= i+1 ; c++){
			tabCases[c][j].setChemin(false);
			tabCases[c][j].setBarriere(true);
		}
		chemin.initBordures(tabCases);
			     		
		next = null;
		
		largeur = image.getWidth(null);
		hauteur = image.getHeight(null);
		
		cadre = new Rectangle(posx, posy, largeur, hauteur);
		
	}
	
}
