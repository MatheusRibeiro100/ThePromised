package com.dream.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.dream.entities.Player;
import com.dream.main.Game;

public class UI {

	public void render(Graphics g) {
		g.setColor(Color.decode("#d11959"));
		g.fillRect(8, 4,70,8);
		g.setColor(Color.decode("#2bb577"));
		g.fillRect(8, 4,(int)((Game.player.life/Game.player.maxLife)*70),8);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 8));
		g.drawString((int)Game.player.life+"/"+(int)Game.player.maxLife,30, 11);
	}
}
