package com.game.src.main;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{
    
	
	public static final int WIDTH = 500;
	public static final int HEIGHT = WIDTH/ 12 * 9;
	public static final int SCALE = 2;
	public final String TITLE = "Shooting Galery";
	
	private boolean running = false;
	private Thread thread;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private BufferedImage spriteSheet = null;
	
	
	private Player p;
	
	
	public void init() {
		
		requestFocus();
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			
			spriteSheet = loader.loadImage("/tomato.png");
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		addKeyListener(new KeyInput(this));
		
		p = new Player(450,680,this);
		
		
	}
	
	private synchronized void start() {
		if(running)
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
		
	}
	
	private synchronized void stop() {
		if(!running)
			return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	public void run() {
		init();
		long lastTime = System.nanoTime();
		final double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) /ns;
			lastTime = now;
			if(delta >= 1){
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;
			
			if(System.currentTimeMillis() - timer >1000){
				timer += 1000;
				
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}
	
	private void tick(){
		
		p.tick();
		
	}
	
	private void render(){
		
		BufferStrategy bs = this.getBufferStrategy();
		
		if( bs == null){
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image,  0,  0,  getWidth(), getHeight(), this);
		
		p.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_RIGHT){
			p.setVelX(5);
		} else if (key == KeyEvent.VK_LEFT){
			p.setVelX(-5);
		}
	
	}
	
	public void keyReleased(KeyEvent e){
		
	int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_RIGHT){
			p.setVelX(0);
		} else if (key == KeyEvent.VK_LEFT){
			p.setVelX(0);
		}
	}
	
	public static void main(String args[]){
		Game game = new Game();
		
	    game.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
	    game.setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
	    game.setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
	    
	    Cursor crosshairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);  //
	    
	    JFrame frame = new JFrame(game.TITLE);
	    frame.add(game);
	    frame.pack();
	    
	    
	    frame.setCursor(crosshairCursor);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setResizable(false);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
	    game.start();
	}

	
	public BufferedImage getSpriteSheet(){
		return spriteSheet;
	}

}
