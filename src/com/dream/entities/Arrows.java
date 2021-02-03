package com.dream.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.dream.main.Game;
import com.dream.world.Camera;

public class Arrows extends Entity{

	private double dx;
	private double dy;
	private double spd = 8;
	
	private int life = 30, curLife = 0;
	
	public Arrows(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}
	
	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		curLife++;
		if(curLife == life) {
			Game.arrows.remove(this);
			return;
		}
	}
	
	public void render(Graphics g) {
		g.drawImage(Entity.ARROW, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
