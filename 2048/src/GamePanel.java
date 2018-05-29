import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements KeyListener{
    int x = 0;
    int y = 0;
    final static int screen_X = 520;
    final static int screen_Y = 650;
    final static int offset = 10;
    final static int windowSize = 500;
    final static int tileSize = 125;
    Color ltgray = new Color(230,230,230);
    Color eggshell = new Color(240, 234, 214);
    Font tileFont = new Font("TimesRoman", Font.PLAIN, 50);
    Font gameOverFont = new Font("TimesRoman",Font.BOLD,100);
    boolean gameOver=false;
    boolean continuing = false;
    int actionTaken;
    boolean aiRunning=false;
    int bestActionIfFull;
    String aiString = "AI Off";
    int repeatCounter=0;
    //0 up, 1 right, 2 down, 3 left
    Random rand = new Random();
    int[] tiles = new int[16];
	boolean gameWon;
	int animationDirection = -1;
    int[] edge=new int[4];
    int[] offsets;
    int movementCounter;
    private int moveStep=125;
    boolean[] justSkipped = new boolean[4];
    int[] edgeValue = new int[4];
    GamePanel(){
    	setBackground(Color.WHITE);
    	initTiles();
    	utility.initUtil();
//    	int[]a={10,10,0,0,
//    			0,0,0,0,
//    			0,0,0,0,
//    			0,0,0,0};
//    	tiles=a;
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(screen_X, screen_Y);
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);
        g.setFont(tileFont);
        if(animationDirection>-1){
        	if(animationDirection%3==0) drawMovingTilesUp(g, animationDirection==3);
        	else drawMovingTilesDown(g, animationDirection==1);
        } else{
        	drawTiles(g);
        }
        drawLines(g);
        g.drawString(aiString, 10, 600);
        if(gameOver){
        	g.setFont(gameOverFont);
        	g.drawString("YOU SUCK",0,300);
        }
        if(gameWon){
        	g.setFont(gameOverFont);
        	g.drawString("YOU WIN", 0, 300);
        }
    }
    public void drawBackground(Graphics g){
    	g.setColor(ltgray);
    	g.fillRect(offset, offset, windowSize, windowSize);
    }
    public void drawLines(Graphics g){
    	g.setColor(Color.black);
    	for(int i=0;i<5;i++){
    		g.drawLine(offset, offset+i*tileSize, offset+windowSize, offset+i*tileSize);
    		g.drawLine(offset+i*tileSize, offset, offset+i*tileSize, offset+windowSize);
    	}
    }

    public int act(int action){
    	actionTaken=action;
    	int[] oldTiles = tiles.clone();
    	tiles = utility.massMove(action, tiles);
    	int[] empties = new int[16];
    	int emptyCount = 0;
    	for(int i=0;i<16;i++){
    		if(!continuing && tiles[i]==11){
    			won();
    			break;
    		}
    		if(tiles[i]==0){ empties[emptyCount]=i; emptyCount++;}
    	}
    	if(emptyCount==0){
    		boolean same = true;
    		boolean repeat=true;
    		for(int i=0;i<tiles.length;i++) if(tiles[i]!=oldTiles[i]) repeat=false;
    		if(repeat) repeatCounter++;
    		float highestScore=-1;
    		for(int i=0;i<4;i++){
    			boolean thisActionSame=true;
    			int[] newTiles = utility.massMove(i, tiles.clone());
    			for(int j=0;j<tiles.length;j++) {
    				if(tiles[j]!=newTiles[j]) {same=false; thisActionSame=false;}
    			}
    			if(!thisActionSame){
    				float newScore=utility.judgeTiles(newTiles);
    				if(newScore>highestScore){
    					highestScore=newScore;
    					bestActionIfFull=i;
    				}
    			}
    		}
    		if(same){
    			gameOver=true;
    			aiString="Game Over";
    			aiRunning=false;
    			repaint();
    		}
    	}
    	else {
    		tiles[empties[rand.nextInt(emptyCount)]] = Math.random()<0.9 ? 1 : 2;
    	}
    	repaint();
    	return action;
    }
    
    private void won() {
		gameWon=true;
		aiString="Continue?";
		aiRunning=false;
		
	}
	public void aiAct(){
    	if(repeatCounter>3){
    		animateTiles(bestActionIfFull);
    		repeatCounter=0;
    	} else{
    		animateTiles(utility.bestMove(tiles));
    	}
    }
    
    
    private void initTiles(){
    	int firstTwo = rand.nextInt(16);
        int secondTwo;
        do{
        	secondTwo = rand.nextInt(16);
        } while(firstTwo==secondTwo);
        tiles[firstTwo]=Math.random()<0.9 ? 1 : 2;
        tiles[secondTwo]=Math.random()<0.9 ? 1 : 2;
    }
    int stillgoingint = 0;
    
    public void aiLoop(){
    	Timer timer = new Timer(100, null); //TODO fix
    	timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(!gameOver && aiRunning){ 
            		if(animationDirection==-1){
            			stillgoingint++;
            			System.out.println("move "+stillgoingint);
            			aiAct();
            			repaint();
            		}
            	} else{
            		timer.stop();
            	}
            }
        });
        timer.start();
    }
    
    public void drawTiles(Graphics g){
    	g.setColor(Color.black);
    	for(int j = 0;j<4;j++){
    		for(int i = 0;i<4;i++){
    			int tileValue = tiles[4*j+i];
    			if(tileValue>0) drawTile(g, tileValue, tileSize*i, tileSize*j);
    		}
    	}
    }
    
    public void drawMovingTilesDown(Graphics g, boolean horizontal){
    	for(int j = 3;j>-1;j--) for(int i = 3;i>-1;i--){
    			int position = 4*j+i;
    			int value=tiles[position];
    			if(value>0){
    				int inLinePos = horizontal ? i : j, linePos = horizontal ? j : i, myOff;
    				if(offsets[position]==0){
    					if(tileSize*inLinePos + movementCounter + 1 >= edge[linePos]){
    						if(inLinePos<3 && !justSkipped[linePos] && tiles[position]==edgeValue[linePos]){
    							edge[linePos]+=tileSize;
    							justSkipped[linePos]=true;
    							
    						} else{
    							edge[linePos] = tileSize*inLinePos-tileSize+movementCounter;
    							if(justSkipped[linePos]){
    								edgeValue[linePos]=0;
    								justSkipped[linePos]=false;
    							} else edgeValue[linePos]=value;
    							offsets[position] = movementCounter;
    						}
    					}
    					myOff = movementCounter;
    				} else{
    					myOff = offsets[position];
    				}
    				drawTile(g, value, (horizontal?myOff:0) + tileSize*i, (horizontal?0:myOff) + tileSize*j);
    			}
    		}
    	movementCounter+=moveStep;
    }
    
    public void drawMovingTilesUp(Graphics g, boolean horizontal){
    	for(int j = 0;j<4;j++) for(int i=0;i<4;i++){
    			int position = 4*j+i;
    			int value=tiles[position];
    			if(value>0){
    				int inLinePos = horizontal ? i : j, linePos = horizontal ? j : i, myOff;
    				if(offsets[position]==0){
    					if(tileSize*inLinePos + movementCounter - 1 <= edge[linePos]){
    						if(inLinePos>0 && !justSkipped[linePos] && tiles[position]==edgeValue[linePos]){
    							edge[linePos]-=tileSize;
    							justSkipped[linePos]=true;
    						} else{
    							edge[linePos] = tileSize*inLinePos+tileSize+movementCounter;
    							if(justSkipped[linePos]){
    								edgeValue[linePos]=0;
    								justSkipped[linePos]=false;
    							} else edgeValue[linePos]=value;
    							offsets[position] = movementCounter;
    						}
    					}
    					myOff = movementCounter;
    				} else{
    					myOff = offsets[position];
    				}
    				drawTile(g, value, (horizontal?myOff:0) + tileSize*i, (horizontal?0:myOff)+ tileSize*j);
    			}
    		}
    	movementCounter-=moveStep;
    }
    
    public void drawTile(Graphics g, int value, int x, int y){
    	x+=offset;
    	y+=offset;
    	g.setColor(eggshell);
    	g.fillRect(x,y,tileSize, tileSize);
    	g.setColor(Color.BLACK);
    	g.drawRect(x, y, tileSize, tileSize);
    	if(value>9) x-=tileSize/2;
    	else if(value>3)x-=tileSize/4;
    	g.drawString(""+ (int) Math.pow(2, value), tileSize/2 + x, offset+tileSize/2 + y);
    	
    }
    
    public void endAnimation(){
    	animationDirection = -1;
    	repaint();
    }
    public void animateTiles(int direction){
    	animationDirection = direction;
    	offsets = new int[16];
    	justSkipped = new boolean[4];
        edgeValue= new int[4];
    	if(direction%3==0){
    		for(int i=0;i<4;i++) edge[i] = 0;
        	movementCounter = 1;
    	} else{
    		for(int i=0;i<4;i++) edge[i] = windowSize-tileSize;
        	movementCounter = -1;
    	}
    	Timer timer = new Timer(30, null);
		timer.addActionListener(new ActionListener() {
			int a =0;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(a==3){
					timer.stop();
					endAnimation();
					act(direction);
					repaint();
				}
				a++;
				repaint();
			}
		});
		timer.start();
		
    }
    
	@Override
	public void keyTyped(KeyEvent e) {		
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_R){
			aiRunning=false;
			gameOver=false;
			gameWon=false;
			aiString="AI Off"; 
			tiles = new int[16];
			stillgoingint=0;
			initTiles();
			repaint();
		} else {
			if(gameWon) {
				gameWon=false; 
				aiString="AI Off"; 
				continuing = true; 
				repaint();
			}
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP:
				animateTiles(0);
				break;
			case KeyEvent.VK_RIGHT:
				animateTiles(1);
				break;
			case KeyEvent.VK_DOWN:
				animateTiles(2);
				break;
			case KeyEvent.VK_LEFT:
				animateTiles(3);
				break;
			case KeyEvent.VK_A:
				if(aiRunning){
					aiRunning=false;
					aiString="AI Off";
					repaint();

				} else{
					aiRunning=true;
					System.out.println("loaded");
					aiString="AI On";
					repaint();
					aiLoop();
				}
				break;
			}
		}
	}

}