package com.dream.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.dream.main.Game;
import com.dream.world.Camera;
import com.dream.world.World;

public class EnemyV2 extends Entity{

	private double speed = 0.4;
	
	private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	
	private int life = 4;
	
	private boolean isDamage = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	public EnemyV2(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(32, 64, 16, 16);	
		sprites[1] = Game.spritesheet.getSprite(32+16, 64, 16, 16);	
	}
	
	public void tick() {

		if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 40){
			if(isColiddingWithPlayer() == false) {

				if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
						&& !isColidding((int)(x+speed), this.getY())) {
					x+=speed;
				}
				else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
						&& !isColidding((int)(x-speed), this.getY())) {
					x-=speed;
				}
				if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
						&& !isColidding(this.getX(), (int)(y+speed))) {
					y+=speed;
				}
				else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
						&& !isColidding(this.getX(), (int)(y-speed))) {
					y-=speed;
				}
			}
			else{
				if(Game.rand.nextInt(100) < 10) {
					Game.player.life-=Game.rand.nextInt(3);
					Game.player.isDamage = true;

					//System.out.println("Vida: "+Game.player.life);
				}
			}
		}
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		collisionArrow();
		
		if(life <=0) {
			destroySelf();
			return;
		}
		
		if(isDamage) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamage = false;
			}
		}
	}
	
	public void destroySelf() {
		Game.enemiesV2.remove(this);
		Game.entities.remove(this);
	}
	
	public void collisionArrow() {
		
		for(int i = 0; i < Game.arrows.size(); i++) {
			Entity e = Game.arrows.get(i);
			if(e instanceof Arrows) {
				if(Entity.isColidding(this, e)) {
					isDamage = true;
					life--;
					Game.arrows.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isColiddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16 , 16);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColidding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
		for(int i = 0; i < Game.enemiesV2.size(); i ++) {
			EnemyV2 e = Game.enemiesV2.get(i);
			if(e == this)
				continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void render (Graphics g) {
		if(!isDamage) {
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		else {
			g.drawImage(Entity.ENEMYV2_EN_WHITE, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
		
	}

}
