package com.dream.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.dream.graficos.Spritesheet;
import com.dream.main.Game;
import com.dream.world.Camera;
import com.dream.world.World;

public class Player extends Entity{
	
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1, down_dir = 2, up_dir = 3;
	public int dir = right_dir;
	public double speed = 1.4;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	
	private boolean moved = false;
	
	private BufferedImage[] rightPlayer;
	
	private BufferedImage[] leftPlayer;
	
	private BufferedImage[] downPlayer;
	
	private BufferedImage[] upPlayer;
	
	public double life = 100, maxLife = 100;
	
	public int municao = 0;
	
	private BufferedImage playerDamageRight, playerDamageLeft, playerDamageUp, playerDamageDown;
	
	public boolean isDamage = false;
	
	private int damageFrames = 0;
	
	private boolean hasGun = false;
	
	private boolean hasAmmo = false;
	
	public boolean shoot = false, mouseShoot = false;
	
	public int mx, my;

	public boolean jump = false, isJumping = false, jumpUp= false, jumpDown = false;

	public int z = 0, jumpSpeed = 2;

	public int jumpFrames = 40, jumpCur = 0;
	
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		downPlayer = new BufferedImage[4];
		upPlayer = new BufferedImage[4];
		
		playerDamageRight = Game.spritesheet.getSprite(16,16,16,16);
		playerDamageLeft = Game.spritesheet.getSprite(16,32,16,16);
		playerDamageUp = Game.spritesheet.getSprite(64,80,16,16);
		playerDamageDown = Game.spritesheet.getSprite(80,80,16,16);
		
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}
		
		for(int i = 0; i < 4; i++) {
			downPlayer[i] = Game.spritesheet.getSprite(48 + (i*16), 48, 16, 16);
		}
		
		for(int i = 0; i < 4; i++) {
			upPlayer[i] = Game.spritesheet.getSprite(64 + (i*16), 64, 16, 16);
		}
		
	}
	
	public void tick() {

		if(jump){
			if(!isJumping){
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}

		if(isJumping){
			if(jumpUp){
				jumpCur+=2;
			}
			else if(jumpDown){
				jumpCur-=2;
				if(jumpCur <= 0){
					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}
			}
			z = jumpCur;
			if(jumpCur >= jumpFrames){
				jumpUp = false;
				jumpDown = true;
			}
		}

		moved = false;
		if(right && World.isFree((int)(x+speed), this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		else if(left && World.isFree((int)(x-speed), this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			dir = down_dir;
			y+=speed;
		}
		else if(up && World.isFree(this.getX(), (int)(y-speed))) {
			moved = true;
			dir = up_dir;
			y-=speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		this.checkItems();
		this.checkCollisionAmmo();
		this.checkCollisionGun();
		
		if(isDamage) {
			this.damageFrames++;
			if(this.damageFrames == 10) {
				this.damageFrames = 0;
				isDamage = false;
			}
		}
		
		if(shoot && hasGun && hasAmmo && municao > 0) {
			municao--;
			shoot = false;
			int dx = 0;
			if(dir == right_dir) {
				dx = 1;
			}
			else {
				dx = -1;
			}
			
			Arrows arrow = new Arrows(this.getX(), this.getY(), 16, 16, Entity.ARROW, dx,0);
			Game.arrows.add(arrow);
		}
		
		if(mouseShoot && hasGun && hasAmmo && municao > 0) {
			municao--;
			mouseShoot = false;
			
			double angle = Math.atan2(my - (this.getY()+8 - Camera.y), mx - (this.getX()+8 - Camera.x));
			
			int px = 0, py = 0;

			if(dir == right_dir) {
				px = 15;
				angle = Math.atan2(my - (this.getY()+px - Camera.y), mx - (this.getX()+8 - Camera.x));
				
			}
			else {
				px = -8;
				angle = Math.atan2(my - (this.getY()+px - Camera.y), mx - (this.getX()+8 - Camera.x));
				
			}
			
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			
			Arrows arrow = new Arrows(this.getX() + px, this.getY() + py, 16, 16, Entity.ARROW, dx,dy);
			Game.arrows.add(arrow);
		}
		
		if(life <=0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}

		updateCamera();
	}

	public void updateCamera(){
		Camera.x =  Camera.clamp(this.getX() - (Game.width/2), 0, World.WIDTH*16 - Game.width);
		Camera.y =  Camera.clamp(this.getY() - (Game.height/2), 0, World.HEIGHT*16 - Game.height);
	}
	
	public void checkCollisionGun() {
		for(int i =0; i< Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColidding(this, atual)) {
					hasGun = true;
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkCollisionAmmo() {
		for(int i =0; i< Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColidding(this, atual)) {
					municao+=5;
					Game.entities.remove(atual);
					hasAmmo = true;
				}
			}
		}
	}
	
	public void checkItems() {
		for(int i =0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof LifePack) {
				if(Entity.isColidding(this, e)) {
					life+=8;
					if(life >= 100) {
						life = 100;
					}
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamage) {
			if(dir == right_dir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0) {
					g.drawImage(Entity.GUN_RIGHT, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_RIGHT_AMMO, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_RIGHT, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
			else if(dir == left_dir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0) {
					g.drawImage(Entity.GUN_LEFT, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_LEFT_AMMO, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_LEFT, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
			else if(dir == down_dir) {
				g.drawImage(downPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0) {
					g.drawImage(Entity.GUN_RIGHT, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_RIGHT_AMMO, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_RIGHT, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
			else if(dir == up_dir) {
				g.drawImage(upPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0) {
					g.drawImage(Entity.GUN_LEFT, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_LEFT_AMMO, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_LEFT, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
		}
		else {
			if(dir == right_dir) {
				g.drawImage(playerDamageRight, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0) {
					g.drawImage(Entity.GUN_RIGHT_WHITE, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_RIGHT_AMMO_WHITE, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_RIGHT_WHITE, this.getX() + 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
			else if(dir == left_dir) {
				g.drawImage(playerDamageLeft, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0)  {
					g.drawImage(Entity.GUN_LEFT_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_LEFT_AMMO_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_LEFT_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
			else if(dir == up_dir) {
				g.drawImage(playerDamageUp, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0)  {
					g.drawImage(Entity.GUN_LEFT_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_LEFT_AMMO_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_LEFT_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
			else if(dir == down_dir) {
				g.drawImage(playerDamageDown, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun && !hasAmmo && municao == 0)  {
					g.drawImage(Entity.GUN_LEFT_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun && hasAmmo && municao > 0) {
					g.drawImage(Entity.GUN_LEFT_AMMO_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
				else if(hasGun) {
					g.drawImage(Entity.GUN_LEFT_WHITE, this.getX() - 11 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
		}
	}
}

