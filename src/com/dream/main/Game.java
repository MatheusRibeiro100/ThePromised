package com.dream.main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.dream.entities.Arrows;
import com.dream.entities.Enemy;
import com.dream.entities.EnemyV2;
import com.dream.entities.Entity;
import com.dream.entities.Player;
import com.dream.graficos.Spritesheet;
import com.dream.graficos.UI;
import com.dream.world.Camera;
import com.dream.world.World;

public class Game extends Canvas implements Runnable, MouseListener, KeyListener {


	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	public static final int width = 240;
	public static final int height = 160;
	public static final int scale = 3;
	private Thread thread;
	private boolean isRunning = true;
	private BufferedImage image;

	public static List<Entity> entities;
	
	public static List<Enemy> enemies;
	
	public static List<EnemyV2> enemiesV2;
	
	public static List<Arrows> arrows;
	
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI Ui;
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 3;
	
	public static String gameState = "MENU";
	
	public boolean showMessageGameOver = false;
	
	private int framesGameOver = 0;
	
	private boolean restartGame = false, saveGame = false;

	public Menu menu;

	public InputStream streamFontLogo = ClassLoader.getSystemClassLoader().getResourceAsStream("fonts/fontLogo.TTF");
	public InputStream streamFontUI = ClassLoader.getSystemClassLoader().getResourceAsStream("fonts/fontUI.TTF");
	public InputStream streamFont = ClassLoader.getSystemClassLoader().getResourceAsStream("fonts/font.TTF");

	public static Font newFontLogo;
	public static Font newFontUI;
	public static Font newFont;

	public Game() {

		rand = new Random();
		
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(width*scale, height*scale));
		initFrame();
		
		Ui = new UI();
		image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		arrows = new ArrayList<Arrows>();
		enemies = new ArrayList<Enemy>();
		enemiesV2 = new ArrayList<EnemyV2>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");

		try{
			newFontLogo = Font.createFont(Font.TRUETYPE_FONT, streamFontLogo).deriveFont(20f);

			newFontUI = Font.createFont(Font.TRUETYPE_FONT, streamFontUI).deriveFont(20f);

			newFont = Font.createFont(Font.TRUETYPE_FONT, streamFont).deriveFont(20f);
		}
		catch(FontFormatException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}

		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame();
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	

	public void tick() {
		if(gameState == "NORMAL") {
			Sound.musicMenu.stop();
			if(this.saveGame){
				this.saveGame = false;
				String[] opt1 = {"level", "vida"};
				int[] opt2 = {this.CUR_LEVEL, (int) player.life};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo salvo");
			}
			this.restartGame = false;
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < arrows.size(); i++) {
				arrows.get(i).tick();
			}
			
			if(enemies.size() == 0 && enemiesV2.size() == 0) {
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		}
		else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver) {
					this.showMessageGameOver = false;
				}
				else {
					this.showMessageGameOver = true;
				}
			}
			
			if(restartGame) {
				Sound.musicGame.loop();
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}

		}
		else if(gameState == "MENU"){
			Sound.musicMenu.loop();
			menu.tick();
		}
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,width,height);
		
		world.render(g);
		//Graphics2D g2 = (Graphics2D) g;
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < arrows.size(); i++) {
			arrows.get(i).render(g);
		}
		Ui.render(g);
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, width*scale, height*scale, null);
		g.setFont(Game.newFont);
		g.setColor(Color.white);
		g.drawString("Munição: " + player.municao, 25, 55);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,200));
			g2.fillRect(0, 0, width*scale, height*scale);
			g.setFont(new Font("Arial", Font.BOLD, 36));
			g.setColor(Color.white);
			g.drawString("Game Over", (width*scale) / 2 -80, (height*scale) / 2 - 20);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			if(showMessageGameOver)
			g.drawString(">Pressione 'Enter' para reiniciar<", (width*scale) / 2 -140, (height*scale) / 2 + 20);
		}
		else if(gameState == "MENU"){
			menu.render(g);
		}
		bs.show();
	}
	
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			player.jump = true;
		}

		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;

			if(gameState == "MENU"){
				menu.up = true;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;

			if(gameState == "MENU"){
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		
		if(e.getKeyChar() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(gameState == "MENU"){
				menu.enter = true;
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			gameState = "MENU";
			menu.pause = true;
		}

		if(e.getKeyCode() == KeyEvent.VK_Q){
			if(gameState == "NORMAL"){
				this.saveGame = true;
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
