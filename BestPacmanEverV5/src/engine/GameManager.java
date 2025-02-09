package engine;





import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import core.Cookie;
import core.Ghost;
import core.Maze;
import core.Pacman;
import core.Score;

public class GameManager {

    private Pacman pacman;
    private Group root;
    private Set<Cookie> cookieSet;
    private Set<Ghost> ghosts;
    private AnimationTimer leftPacmanAnimation;
    private AnimationTimer rightPacmanAnimation;
    private AnimationTimer upPacmanAnimation;
    private AnimationTimer downPacmanAnimation;
    private Maze maze;
    private int lifes;
    private int score;
    private Score scoreBoard;
    private boolean gameEnded;
    private int cookiesEaten;

    /**
     * Constructor
     * Sets up the settings for pacman 
     */
    public GameManager(Group root) {
        this.root = root;
        this.maze = new Maze();
        this.pacman = new Pacman(2.5 * BarObstacle.THICKNESS, 2.5 * BarObstacle.THICKNESS);
        this.cookieSet = new HashSet<>();
        this.ghosts = new HashSet<>();
        this.leftPacmanAnimation = this.createAnimation("left");
        this.rightPacmanAnimation = this.createAnimation("right");
        this.upPacmanAnimation = this.createAnimation("up");
        this.downPacmanAnimation = this.createAnimation("down");
        this.lifes = 3;
        this.score = 0;
        this.cookiesEaten = 0;
    }
//public void colorPicker() {
//	root.setBackground(new Background(new BackgroundFill(Color.backgroundbtn1,null,null)));
//}
    /**
     * Set one life less
     * And outputs a sound when pacman dies
     */
    private void lifeLost() {
        this.leftPacmanAnimation.stop();
        this.rightPacmanAnimation.stop();
        this.upPacmanAnimation.stop();
        this.downPacmanAnimation.stop();
        for (Ghost ghost : ghosts) {
            ghost.getAnimation().stop();
        }
        this.pacman.setCenterX(2.5 * BarObstacle.THICKNESS);
        this.pacman.setCenterY(2.5 * BarObstacle.THICKNESS);
        lifes--;
        score -= 10;
        this.scoreBoard.lifes.setText("Lifes: " + this.lifes);
        this.scoreBoard.score.setText("Score: " + this.score);
        
        //plays pc speaker BEEP when you die!! 
        java.awt.Toolkit.getDefaultToolkit().beep(); 

        try {
      		java.applet.AudioClip clip =
      		java.applet.Applet.newAudioClip(
      		new java.net.URL("https://www.thesoundarchive.com/play-wav-files.asp?sound=ringtones/pacman_death.wav"));
      		clip.play();
      		} catch (java.net.MalformedURLException murle) {
      		System.out.println(murle);
      		}
        if (lifes == 0) {
        	

        	createWindow();
        	saveScore();
        	Highscore();
        	this.endGame();
        	
        }
    	
        }
    
    /*
     * Create a window that Displays the score of the player
     */
   private void createWindow() {
    	      //Create and set up the window.
    	      JFrame frame = new JFrame("Your score");
    	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	      JLabel textLabel = new JLabel("Score:"+score,SwingConstants.CENTER);
    	      textLabel.setPreferredSize(new Dimension(300, 100));
    	      frame.getContentPane().add(textLabel, BorderLayout.NORTH);
    	      //Display the window.
    	      
    	      frame.setLocationRelativeTo(null);
    	      frame.pack();
    	      frame.setVisible(true);
    	   }
   
   /*
    * Create a window that Displays the all the scores of the player
    * With the ability to delete the file with the high scores
    */
   public static void Highscore()
   {
       final JTextArea edit = new JTextArea(30, 60);
       

       JButton read = new JButton("Show HighScore");
       read.addActionListener( new ActionListener()
       {
           public void actionPerformed(ActionEvent e)
           {
               try
               {
                   FileReader reader = new FileReader( "BestPacmanEverV5\\src\\SCORES.txt" );
                   BufferedReader br = new BufferedReader(reader);
                   edit.read( br, null );
                   br.close();
                   edit.requestFocus();
                   
               }
               catch(Exception e2) { System.out.println(e2); }
           }
       });
       JButton Delete = new JButton("Delete");
       Delete.addActionListener( new ActionListener()
       {
    	

		@Override
		public void actionPerformed(ActionEvent e) {
			   Delete();
		}
       });

      

       JFrame frame = new JFrame("HighScore");
       frame.getContentPane().add( new JScrollPane(edit), BorderLayout.NORTH );
       frame.getContentPane().add(read, BorderLayout.WEST);
       frame.getContentPane().add(Delete, BorderLayout.EAST);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.pack();
       frame.setLocationRelativeTo( null );
       frame.setVisible(true);
   }
   
   /*
    * The Delete function for the high scores
    */
   public static void Delete() 
   { 
       File file = new File("BestPacmanEverV5\\src\\SCORES.txt"); 
         
       if(file.delete()) 
       { 
           System.out.println("File deleted successfully"); 
       } 
       else
       { 
           System.out.println("Failed to delete the file"); 
       } 
   } 

		
	        
	    
	
   
    /**
     * Ends the game
     */
    private void endGame() {
        this.gameEnded = true;
        root.getChildren().remove(pacman);
        for (Ghost ghost : ghosts) {
            root.getChildren().remove(ghost);
        }
        javafx.scene.text.Text endGame = new javafx.scene.text.Text("Game Over, press ESC to restart");
        endGame.setX(BarObstacle.THICKNESS * 3);
        endGame.setY(BarObstacle.THICKNESS * 28);
        endGame.setFont(Font.font("Arial", 40));
        endGame.setFill(Color.ROYALBLUE);
        root.getChildren().remove(this.scoreBoard.score);
        root.getChildren().remove(this.scoreBoard.lifes);
       
        root.getChildren().add(endGame);
    }

    /**
     * Restart the game
     * @param event
     */
    public void restartGame(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE && gameEnded) {
            root.getChildren().clear();
            this.cookieSet.clear();
            this.ghosts.clear();
            this.drawBoard();
            this.pacman.setCenterX(2.5 * BarObstacle.THICKNESS);
            this.pacman.setCenterY(2.5 * BarObstacle.THICKNESS);
            this.lifes = 3;
            this.score = 0;
            this.cookiesEaten = 0;
            gameEnded = false;
        }
    }

    /**
     * Draws the board of the game with the cookies and the Pacman
     * by adding everything into an array 
     */
    public void drawBoard() {
        this.maze.CreateMaze(root);
        // 1st line
        Integer skip[] = {5, 17};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2*i) + 2.5) * BarObstacle.THICKNESS, 2.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 2nd line
        skip = new Integer[]{1, 2, 3, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 20, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2*i) + 2.5) * BarObstacle.THICKNESS, 4.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 3rd line
        skip = new Integer[]{1, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2*i) + 2.5) * BarObstacle.THICKNESS, 6.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 4th line
        skip = new Integer[]{1, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 18, 19, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2 * i) + 2.5) * BarObstacle.THICKNESS, 8.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 5th line
        skip = new Integer[]{1, 7, 8, 9, 10, 11, 12, 13, 14, 15, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2*i) + 2.5) * BarObstacle.THICKNESS, 10.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 6th line
        skip = new Integer[]{3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 18, 19};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2*i) + 2.5) * BarObstacle.THICKNESS, 12.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 7th line
        skip = new Integer[]{1, 7, 8, 9, 10, 11, 12, 13, 14, 15, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2 * i) + 2.5) * BarObstacle.THICKNESS, 14.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 8th line
        skip = new Integer[]{1, 3, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 18, 19, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2 * i) + 2.5) * BarObstacle.THICKNESS, 16.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 9th line
        skip = new Integer[]{1, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2 * i) + 2.5) * BarObstacle.THICKNESS, 18.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 10th line
        skip = new Integer[]{1, 2, 3, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 20, 21};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2*i) + 2.5) * BarObstacle.THICKNESS, 20.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }

        // 11th line
        skip = new Integer[]{5, 17};
        for (int i = 0; i < 23; i++) {
            if (!Arrays.asList(skip).contains(i)) {
                Cookie cookie = new Cookie(((2 * i) + 2.5) * BarObstacle.THICKNESS, 22.5 * BarObstacle.THICKNESS);
                this.cookieSet.add(cookie);
                root.getChildren().add(cookie);
            }
        }
        root.getChildren().add(this.pacman);
        this.generateGhosts();
        root.getChildren().addAll(this.ghosts);
        this.scoreBoard = new Score(root);
    }

    /**
     * Generates the ghosts for the pacman!
     * Adds gifs for sprites
     */
    public void generateGhosts() {
    
//    	ImagePattern ghost1 = new ImagePattern (new javafx.scene.image.Image(getClass().getResource("icon/ghost1.png").toExternalForm()));
//    	ImagePattern ghost2 = new ImagePattern (new javafx.scene.image.Image(getClass().getResource("icon/ghost2.png").toExternalForm()));
//    	ImagePattern ghost3 = new ImagePattern (new javafx.scene.image.Image(getClass().getResource("icon/ghost3.png").toExternalForm()));
//    	ImagePattern ghost4 = new ImagePattern (new javafx.scene.image.Image(getClass().getResource("icon/ghost4.png").toExternalForm()));
    	
        this.ghosts.add(new Ghost(18.5 * BarObstacle.THICKNESS, 12.5 * BarObstacle.THICKNESS, "ghost1" , maze, this));
        this.ghosts.add(new Ghost(22.5 * BarObstacle.THICKNESS, 12.5 * BarObstacle.THICKNESS, "ghost2", maze, this));
        this.ghosts.add(new Ghost(28.5 * BarObstacle.THICKNESS, 9.5 * BarObstacle.THICKNESS, "ghost3", maze, this));
        this.ghosts.add(new Ghost(28.5 * BarObstacle.THICKNESS, 9.5 * BarObstacle.THICKNESS, "ghost4", maze, this));

//      this.ghosts.add(new Ghost(18.5 * BarObstacle.THICKNESS, 12.5 * BarObstacle.THICKNESS, Color.DEEPPINK, maze, this));
//        this.ghosts.add(new Ghost(22.5 * BarObstacle.THICKNESS, 12.5 * BarObstacle.THICKNESS, Color.GREENYELLOW, maze, this));
//        this.ghosts.add(new Ghost(28.5 * BarObstacle.THICKNESS, 10.5 * BarObstacle.THICKNESS, Color.BLACK, maze, this));
//        this.ghosts.add(new Ghost(28.5 * BarObstacle.THICKNESS, 9.5 * BarObstacle.THICKNESS, Color.SPRINGGREEN, maze, this));
    }

    /**
     * Moves the pacman
     * @param event
     */
    public void movePacman(KeyEvent event) {
        for (Ghost ghost : this.ghosts) {
            ghost.run();
        }
        switch(event.getCode()) {
            case RIGHT:
                this.rightPacmanAnimation.start();
                break;
            case LEFT:
                this.leftPacmanAnimation.start();
                break;
            case UP:
                this.upPacmanAnimation.start();
                break;
            case DOWN:
                this.downPacmanAnimation.start();      
                break;
            case ESCAPE:
            	restartGame(event);
		default:
			break;
        }
    }

    /**
     * Stops the pacman according to the users input
     * @param event
     */
    public void stopPacman(KeyEvent event) {
        switch(event.getCode()) {
            case RIGHT:
                this.rightPacmanAnimation.stop();
                break;
            case LEFT:
                this.leftPacmanAnimation.stop();
                break;
            case UP:
                this.upPacmanAnimation.stop();
                break;
            case DOWN:
                this.downPacmanAnimation.stop();
                break;
            case ESCAPE:
            	restartGame(event);
		default:
			break;
        }
    }

    /**
     * Creates an animation of the movement.
     * @param direction
     * @return
     */
    private AnimationTimer createAnimation(String direction) {
        double step = 5;
        return new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
            switch (direction) {
                case "left":
                    if (!maze.isTouching(pacman.getCenterX() - pacman.getRadius(), pacman.getCenterY(), 15)) {
                        pacman.setCenterX(pacman.getCenterX() - step);
                        if (pacman.getCenterX()<0) {
                        	pacman.setCenterX(1225);
                        }
                        checkCookieCoalition(pacman, "x");
                        checkGhostCoalition();
                    }
                    break;
                case "right":
                    if (!maze.isTouching(pacman.getCenterX() + pacman.getRadius(), pacman.getCenterY(), 15)) {
                        pacman.setCenterX(pacman.getCenterX() + step);
                        if (pacman.getCenterX()>1225) {
                        	pacman.setCenterX(0);
                        }
                        checkCookieCoalition(pacman, "x");
                        checkGhostCoalition();
                    }
                    break;
                case "up":
                    if (!maze.isTouching(pacman.getCenterX(), pacman.getCenterY() - pacman.getRadius(), 15)) {
                        pacman.setCenterY(pacman.getCenterY() - step);
                        checkCookieCoalition(pacman, "y");
                        checkGhostCoalition();
                    }
                    break;
                case "down":
                   if (!maze.isTouching(pacman.getCenterX(), pacman.getCenterY() + pacman.getRadius(), 15)) {
                       pacman.setCenterY(pacman.getCenterY() + step);
                       checkCookieCoalition(pacman, "y");
                       checkGhostCoalition();
                   }
                   break;
            }
            }
        };
    }

    /**
     * Checks if the Pacman touches cookies.
     * and if he eats the cookies plays the PC'S internal speaker 
     * OR the windows/osx default sound if it does not have one
     * @param pacman
     * @param axis
     */
    private void checkCookieCoalition(Pacman pacman, String axis) {
        double pacmanCenterY = pacman.getCenterY();
        double pacmanCenterX = pacman.getCenterX();
        double pacmanLeftEdge = pacmanCenterX - pacman.getRadius();
        double pacmanRightEdge = pacmanCenterX + pacman.getRadius();
        double pacmanTopEdge = pacmanCenterY - pacman.getRadius();
        double pacmanBottomEdge = pacmanCenterY + pacman.getRadius();
        for (Cookie cookie:cookieSet) {
            double cookieCenterX = cookie.getCenterX();
            double cookieCenterY = cookie.getCenterY();
            double cookieLeftEdge = cookieCenterX - cookie.getRadius();
            double cookieRightEdge = cookieCenterX + cookie.getRadius();
            double cookieTopEdge = cookieCenterY - cookie.getRadius();
            double cookieBottomEdge = cookieCenterY + cookie.getRadius();
            if (axis.equals("x")) {
                // pacman goes right
                if ((cookieCenterY >= pacmanTopEdge && cookieCenterY <= pacmanBottomEdge) && (pacmanRightEdge >= cookieLeftEdge && pacmanRightEdge <= cookieRightEdge)) {
                    if (cookie.isVisible()) {
                        this.score += cookie.getValue();
                        this.cookiesEaten++;
                        java.awt.Toolkit.getDefaultToolkit().beep(); 
                    }
                    cookie.hide();
                }
                // pacman goes left
                if ((cookieCenterY >= pacmanTopEdge && cookieCenterY <= pacmanBottomEdge) && (pacmanLeftEdge >= cookieLeftEdge && pacmanLeftEdge <= cookieRightEdge)) {
                    if (cookie.isVisible()) {
                        this.score += cookie.getValue();
                        this.cookiesEaten++;
                        java.awt.Toolkit.getDefaultToolkit().beep(); 
                    }
                    cookie.hide();
                }
            } else {
                // pacman goes up
                if ((cookieCenterX >= pacmanLeftEdge && cookieCenterX <= pacmanRightEdge) && (pacmanBottomEdge >= cookieTopEdge && pacmanBottomEdge <= cookieBottomEdge)) {
                    if (cookie.isVisible()) {
                        this.score += cookie.getValue();
                        this.cookiesEaten++;
                        java.awt.Toolkit.getDefaultToolkit().beep(); 
                    }
                    cookie.hide();
                }
                // pacman goes down
                if ((cookieCenterX >= pacmanLeftEdge && cookieCenterX <= pacmanRightEdge) && (pacmanTopEdge <= cookieBottomEdge && pacmanTopEdge >= cookieTopEdge)) {
                    if (cookie.isVisible()) {
                        this.score += cookie.getValue();
                        this.cookiesEaten++;
                        java.awt.Toolkit.getDefaultToolkit().beep(); 
                    }
                    
                    cookie.hide();
                    

                   
                }
            }
            this.scoreBoard.score.setText("Score: " + this.score);
            if (this.cookiesEaten == this.cookieSet.size()) {
//            	if (score == 700) {
//            		popscore();
//            	}
            	 
                this.endGame();
            }
        }
    }
    //Saves scores to file 
    public void saveScore() {

        try {
            FileWriter scoresFile = new FileWriter("BestPacmanEverV5\\src\\SCORES.txt",true);
            BufferedWriter writer = new BufferedWriter(scoresFile);
           writer.append(String.format(score + "\n"));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    
   
       

	/**
     * Checks if pacman is touching a ghost
     */
    public void checkGhostCoalition() {
        double pacmanCenterY = pacman.getCenterY();
        double pacmanCenterX = pacman.getCenterX();
        double pacmanLeftEdge = pacmanCenterX - pacman.getRadius();
        double pacmanRightEdge = pacmanCenterX + pacman.getRadius();
        double pacmanTopEdge = pacmanCenterY - pacman.getRadius();
        double pacmanBottomEdge = pacmanCenterY + pacman.getRadius();
        for (Ghost ghost : ghosts) {
            double ghostLeftEdge = ghost.getX();
            double ghostRightEdge = ghost.getX() + ghost.getWidth();
            double ghostTopEdge = ghost.getY();
            double ghostBottomEdge = ghost.getY() + ghost.getHeight();
            if ((pacmanLeftEdge <= ghostRightEdge && pacmanLeftEdge >= ghostLeftEdge) || 
            		(pacmanRightEdge >= ghostLeftEdge && pacmanRightEdge <= ghostRightEdge))
            {
                if ((pacmanTopEdge <= ghostBottomEdge && pacmanTopEdge >= ghostTopEdge) 
                		|| (pacmanBottomEdge >= ghostTopEdge && pacmanBottomEdge <= ghostBottomEdge)) {
                    lifeLost();
                }
            }
        }
    }

}
