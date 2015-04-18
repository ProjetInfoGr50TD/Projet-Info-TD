import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Fenetre extends JFrame{

	private Rectangle ecran;
    private BufferedImage monBuf;
    private Timer timer;
    private Timer actu;
    private Timer spawn;
    private Tour1 eiffel;
    protected Joueur bizuth;
    private Sbire1 bob;
    private ListeEnnemis listeEnnemis;
    private ListeTour listeTour;
    protected Menu menuTest;
    private Case[][] tabCases;
    private ListeCases bordureDroite;
    private ListeCases bordureGauche;
    private ListeCases bordureHaut;
    private ListeCases bordureBas;
    public int nbSpawn;

	public Fenetre() {
        /* INIT FRAME */
        super("Tower Defens' Bro !");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1203,770);
        setResizable(true);
        ecran = new Rectangle(getInsets().left, getInsets().top+20, getSize().width-getInsets().right-300, getSize().height-getInsets().bottom-10);
        
        /* INIT BUFFER */
        Dimension dim = getSize();
        monBuf = new BufferedImage(dim.width-300,dim.height,BufferedImage.TYPE_INT_RGB); //-300 car le menu fait 300 de large
		
		/* INIT JOUEUR */
        bizuth = new Joueur("Bizuth1");
        
        /* INIT ENNEMIS */
        listeEnnemis = new ListeEnnemis();
		//spawnTest();
		
		/* INIT TOURS */
        listeTour = new ListeTour();
        eiffel = new Tour1(ecran, 240, 200);
        listeTour.insertTete(eiffel);
        
        /* INIT MENU */
        menuTest = new Menu(bizuth, this);
		getContentPane().add(menuTest);
		menuTest.getStart().addActionListener(new EcouteurBoutonStart());
		
        /* INIT TIMER */
        timer = new Timer(15, new letsDance());
        timer.start();
        actu = new Timer(100, new actuFuckingMenu());
        //actu.start();
        spawn = new Timer(500, new letsSpawn());
        //spawn.start();
        nbSpawn = 0;
       
        /* INIT QUADRILAGE DE LA CARTE
         * Uniquement sur la zone jouable
         */
        tabCases = new Case[(getWidth()-300)/Case.LCASE][getHeight()/Case.LCASE];
        GeneAleat.aleat2(tabCases);

		
		/* INIT BORDURES CHEMIN
		 * Une case ne doit pas appartenir a deux listes, sinon conflit (pointeurs)
		 * Donc insertion de clones et non de la case originel si elle est deja dans une autre liste
		 */
		bordureGauche = new ListeCases();
		bordureDroite = new ListeCases();
		bordureBas = new ListeCases();
		bordureHaut = new ListeCases();
		for(int i=0; i<tabCases.length-1; i++){
			for(int j=0; j<tabCases[0].length-1; j++){
				if(!tabCases[i][j].chemin && tabCases[i+1][j].chemin){
					if(!tabCases[i][j].bordure){
						bordureGauche.insertTete(tabCases[i][j]);
						tabCases[i][j].bordure = true;
					} else {
						Case nc = (Case)tabCases[i][j].clone();
						tabCases[i][j].hybride = true;
						nc.hybride = true;
						bordureGauche.insertTete(nc);
					}
				} else if(tabCases[i][j].chemin && !tabCases[i+1][j].chemin){
					if(!tabCases[i+1][j].bordure){
						bordureDroite.insertTete(tabCases[i+1][j]);
						tabCases[i+1][j].bordure = true;
					} else {
						Case nc = (Case)tabCases[i+1][j].clone();
						tabCases[i+1][j].hybride = true;
						nc.hybride = true;
						bordureDroite.insertTete(nc);
					}
				} else if(tabCases[i][j].chemin && !tabCases[i][j+1].chemin){
					if(!tabCases[i][j+1].bordure){
						bordureBas.insertTete(tabCases[i][j+1]);
						tabCases[i][j+1].bordure = true;
					} else {
						Case nc = (Case)tabCases[i][j+1].clone();
						tabCases[i][j+1].hybride = true;
						nc.hybride = true;
						bordureBas.insertTete(nc);
					}
				} else if(!tabCases[i][j].chemin && tabCases[i][j+1].chemin){
					if(!tabCases[i][j].bordure){
						bordureHaut.insertTete(tabCases[i][j]);
						tabCases[i][j].bordure = true;
					} else {
						Case nc = (Case)tabCases[i][j].clone();
						tabCases[i][j].hybride = true;
						nc.hybride = true;
						bordureHaut.insertTete(nc);
					}
				}
			}
		}
		//Cas particulier de la derniere ligne
		for(int i=0; i<tabCases.length-1; i++){
			if(!tabCases[i][tabCases[0].length -1].chemin && tabCases[i+1][tabCases[0].length -1].chemin){
				if(!tabCases[i][tabCases[0].length -1].bordure){
					bordureGauche.insertTete(tabCases[i][tabCases[0].length -1]);
					tabCases[i][tabCases[0].length -1].bordure = true;
				} else {
					Case nc = (Case)tabCases[i][tabCases[0].length -1].clone();
					nc.bordure = false;
					tabCases[i][tabCases[0].length -1].hybride = true;
					nc.hybride = true;
					bordureGauche.insertTete(nc);
				}
			} else if(tabCases[i][tabCases[0].length -1].chemin && !tabCases[i+1][tabCases[0].length -1].chemin){
				if(!tabCases[i+1][tabCases[0].length -1].bordure){
					bordureDroite.insertTete(tabCases[i+1][tabCases[0].length -1]);
					tabCases[i+1][tabCases[0].length -1].bordure = true;
				} else {
					Case nc = (Case)tabCases[i+1][tabCases[0].length -1].clone();
					nc.bordure = true;
					tabCases[i+1][tabCases[0].length -1].hybride = true;
					nc.hybride = true;
					bordureDroite.insertTete(nc);
				}
			}
		}

        this.addMouseListener(new EcouteurClicSouris());
        setVisible(true); // A mettre a la fin, sinon grosse erreur lors du premier dessin !
        
	}
	
	public void paint(Graphics g) {
		Graphics gb = monBuf.getGraphics();
		/* PEINTURE FOND */
		for(int i=0; i<tabCases.length; i++){
			for(int j=0; j<tabCases[0].length;j++){
				if((i+j)%2==0){
					gb.setColor(Color.darkGray);
				} else {
					gb.setColor(Color.black);
				}
				if (tabCases[i][j].isChemin()){
					gb.setColor(Color.lightGray);
				}
				tabCases[i][j].drawCase(gb);
			}
		}
		/* PEINTURE TESTS
		 * Visualisation bordures chemin
		 * Visualisation zones de contact
		 * Visualisation portee tours
		 */
		//showBordures(gb);
        //showContoursEnnemi(gb, bob);
		showPorteeTour(gb, eiffel);
		/* PEINTURE ENNEMIS */
		gb.setColor(Color.white);
		Ennemis cur = listeEnnemis.root;
		while(cur != null){
			//showContoursEnnemi(gb, cur);
			cur.draw(gb);
			gb.drawString(Integer.toString(cur.getVie()) , cur.getPosx() + 10, cur.getPosy());
			cur = cur.next;
		}
		/* PEINTURE TOURS */
		Tour cur2 = listeTour.root;
		while(cur2 != null){
			cur2.draw(gb);
			cur2 = cur2.next;
		}
		/* DESSIN GLOBAL */
        g.drawImage(monBuf,0,0,null);
        
    }
    
    public class letsDance implements ActionListener{
		public void actionPerformed(ActionEvent e){
			boucle();
		}
	}
	
	public class actuFuckingMenu implements ActionListener{
		public void actionPerformed(ActionEvent e){
			menuTest.update(getGraphics());
			actu.stop();
		}
	}
	
	public class letsSpawn implements ActionListener{
		public void actionPerformed(ActionEvent e){
			Sbire1 sbire = new Sbire1(ecran, 275 + (int)(100*Math.random()), 0, nbSpawn);
			sbire.addEnnemiListener(new EcouteurEnnemi());
			listeEnnemis.insertQueue(sbire);
			if(nbSpawn == 100){
				spawn.stop();
				nbSpawn = 0;
				menuTest.infoJeu.setText("Fin du Spawn !");
			} else {
				nbSpawn++;
			}
		}
	}
	
	public void boucle() {
		Ennemis cur = listeEnnemis.root;
		while(cur != null){
			cur.moveChemin(bordureGauche, bordureDroite, bordureHaut, bordureBas);
			cur = cur.next;
		}
		
		/* GESTION BOUSCULADE - ESSAI CHEMIN
		 * Essayons de se faire rentrer dedans les differents ennemis
		 * La tete de liste (=listeEnnemis.root = racine) contient le plus vieil ennemi,
		 * c'est a dire celui qui a spawn il y a le plus de temps,
		 * et qui n'est ni mort ni victorieux
		 * La priorite lui est donnee :
		 * il doit TOUJOURS se debloquer. Les autres attendent que se soit fait
		 * Pas de problemes avec la multi attente ; phenome de queue-leu-leu
		 * NB : fonctionne desormais a priori correctement, pour un timer de spawn de 500ms
		 * 		De plus gros bugs pour un timer inferieur, mais d'une autre nature que les initiaux
		 */
		Ennemis prev = listeEnnemis.root;
		if(prev != null){
			cur = prev.next;
		}
		//Exception de l'ennemi le plus vieux faite par la nature de cette double boucle
		//En effet, si prev == listeEnnemis.root, c'est bien listeEnnemis.root.next qu'on deplace
		while(prev != null){
			while(cur != null){
				cur.dposx = - cur.dposx;
				cur.dposy = - cur.dposy;
				//Si l'ennemi courant est coince par un ennemi plus vieux, on recule jusqu'a la FIN de l'intersection,
				//pour etre sur de ne jamais chevaucher un autre ennemi a la fin du deplacement
				while(prev.collision(cur)){
					cur.moveBasique(true, true);
				}
				//On remet l'ennemi dans le bon sens de mouvement
				cur.dposx = - cur.dposx;
				cur.dposy = - cur.dposy;
				if(cur != null){
					cur = cur.next;
				}
			}
			prev = prev.next;
			if(prev != null){
				cur = prev.next;
			}
		}
		/* FIN DE L'ESSAI */
		
		/* GESTION TIR - ESSAI AVEC FOCUS
		 * Essayons de faire perdre de la vie aux ennemis
		 * lorsqu'ils passent a proximite d'une tour
		 * C'est a dire lorsqu'ils sont dans le cercle portee des tours
		 * Gestion du focus par un simple break et le placement en tete de liste du sbire le plus vieux !
		 */
		Tour curT = listeTour.root;
		while(curT != null){
			Ennemis curE = listeEnnemis.root;
			while(curE != null){
				if(curT.collision(curE)){
					curE.setVie(curT.puissance);
					break;	//Permet le FOCUS d'un UNIQUE ennemi
							//Pour 2 ? Mettre un compteur et break a deux ! :)
							//On pourra differencier les cas suivant la nature des tours
				}
				if(curE != null){
					curE = curE.next;
				}
			}
			if(curT != null){
				curT = curT.next;
			}
		}
		/* FIN DE L'ESSAI */
		//menuTest.repaint();
		repaint();
	}
	
	private class EcouteurClicSouris implements MouseListener {
		//BUTTON1 = clic gauche
		//BUTTON2 = clic molette
		//BUTTON3 = clic droit
		
		public void mouseClicked(MouseEvent e){
			int boutonSouris = e.getButton();
			
			if(boutonSouris == e.BUTTON1){
				//Pour ne pas placer une tour hors de la zone de jeu :
				if(e.getX() < ecran.width - 12 && e.getY() < ecran.height - 25){
					
				switch(menuTest.getVariable()){
					//NB : 	les Tour1 sont placees par leur centre (d'ou le -12 -20)
					//		de meme pour les Piege1
					case 1:
						listeTour.insertTete(new Tour1(ecran, e.getX()-12, e.getY()-20));
						listeTour.display();
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour1 en position !");
						break;
					
					case 2:
						listeTour.insertTete(new Tour1(ecran, e.getX(), e.getY()));
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour2 en position !");
						break;
					
					case 3:
						listeTour.insertTete(new Tour1(ecran, e.getX(), e.getY()));
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour3 en position !");
						break;
					
					case 4:
						listeTour.insertTete(new Tour1(ecran, e.getX(), e.getY()));
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour4 en position !");
						break;
					
					case 5:
						listeTour.insertTete(new Piege1(e.getX()-20, e.getY()-20));
						bizuth.argent -= Piege1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Piege1 en position !");
						break;
						
					case 6:
						listeTour.insertTete(new Piege1(e.getX(), e.getY()));
						bizuth.argent -= Piege1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Piege2 en position !");
						break;
						
					case 7:
						listeTour.insertTete(new Piege1(e.getX(), e.getY()));
						bizuth.argent -= Piege1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Piege3 en position !");
						break;
						
					case 8:
						listeTour.insertTete(new Piege1(e.getX(), e.getY()));
						bizuth.argent -= Piege1.PRIX;
						menuTest.argent.setLabel("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Piege4 en position !");
						break;
						
				}
				
				}
				
			menuTest.setVariable(0);

			} else if(boutonSouris == e.BUTTON3){
				menuTest.setVariable(0);
			}
		}
		
		public void mouseExited(MouseEvent e){
			
		}
		
		public void mouseEntered(MouseEvent e){

		}
		
		public void mouseReleased(MouseEvent e){
			
		}
		
		public void mousePressed(MouseEvent e){
			
		}
		
	}
	
	public class EcouteurBoutonStart implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(((JButton)(e.getSource())).getText().equals(" ")){
				spawn.start();
				menuTest.infoJeu.setText("Le Spawn a commence !");
			}
		}
	}
	
	private class EcouteurEnnemi implements EnnemiListener {
		
		public void ennemiMort(EnnemiEvent e){
			System.out.print("Un ennemi est mort !! ");
			System.out.println(e.getEnnemi());
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			if(listeEnnemis.root == null){
				menuTest.infoJeu.setText("Place un truc !");
				System.out.println("Bug d'affichage sur infoJeu, je suis bien passe par la");
			}
		}
		
		public void ennemiVictorieux(EnnemiEvent e){
			System.out.print("Un ennemi est victorieux ! ");
			System.out.println(e.getEnnemi());
			bizuth.setVie(1);
			menuTest.vie.setLabel("PV : " + bizuth.vie);
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			if(listeEnnemis.root == null){
				menuTest.infoJeu.setText("Place un truc !");
				System.out.println("Bug d'affichage sur infoJeu, je suis bien passe par la");
			}
		}
	}
	
	public static void main(String[] args) {

		Fenetre game = new Fenetre();
		//game.menuTest.update(game.getGraphics());

    }
	
	/* METHODES DE DEBUGAGE */
	
	/* Visualisation bordures */
	private void showBordures(Graphics gb){
		gb.setColor(Color.green);
		Case curc = bordureBas.root;
		while(curc != null){
			curc.drawCase(gb);
			curc = curc.next;
		}
		gb.setColor(Color.pink);
		curc = bordureHaut.root;
		while(curc != null){
			curc.drawCase(gb);
			curc = curc.next;
		}
		gb.setColor(Color.orange);
		curc = bordureGauche.root;
		while(curc != null){
			curc.drawCase(gb);
			curc = curc.next;
		}
		gb.setColor(Color.red);
		curc = bordureDroite.root;
		while(curc != null){
			curc.drawCase(gb);
			curc = curc.next;
		}
	}
	
	/* Visualisation Rectangle + Arc2D entourant les ennemis */
	private void showContoursEnnemi(Graphics gb, Ennemis bob){
		gb.setColor(Color.red);
		gb.fillRect(bob.getPosx(),bob.getPosy(),bob.cadre.width,bob.cadre.height);
        gb.setColor(Color.blue);
        gb.fillArc(bob.getPosx(), bob.getPosy(), bob.cadre.width, bob.cadre.height, 0, 360);
	}
	
	/* Visualisation Arc2D portee des tours */
	private void showPorteeTour(Graphics gb, Tour eiffel){
		gb.setColor(Color.orange);
		gb.fillArc(eiffel.getPosx()-(int)eiffel.portee.getWidth()/2 + eiffel.cadre.width/2, eiffel.getPosy()-(int)eiffel.portee.getHeight()/2+ eiffel.cadre.height/2, (int)eiffel.portee.getWidth(), (int)eiffel.portee.getHeight(), 0, 360);
	}
    
    /* Spawn de quelques Ennemis dans le chemin pour voir leurs deplacements */
    private void spawnTest(){
		bob = new Sbire1(ecran, 550, 650, 0);
        listeEnnemis.insertTete(bob);
        bob.addEnnemiListener(new EcouteurEnnemi());
        for(int i = 1; i < 10; i++){
			Sbire1 sbire;
			if(i<7){
				sbire = new Sbire1(ecran, 360, (int)(30*i +10*Math.random()), i);
			} else if(i<9){
				sbire = new Sbire1(ecran, (int)(40 + 30*i +10*Math.random()), 360, i);
			} else {
				sbire = new Sbire1(ecran, 480, (int)(30*i +10*Math.random()), i);
			}
			sbire.addEnnemiListener(new EcouteurEnnemi());
			listeEnnemis.insertTete(sbire);
		}
	}
}
