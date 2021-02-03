package com.dream.main;

import com.dream.world.World;

import java.awt.*;
import java.io.*;

public class Menu {

    public String[] options = {"novo jogo", "carregar jogo", "sair", "continuar"};

    public int currentOption = 0;
    public int maxOption = options.length -1;

    public boolean up, down, enter;

    public static boolean pause = false;

    public static boolean saveExists = false, saveGame = false;

    public void tick(){
        File file = new File("save.txt");
        if(file.exists()){
            saveExists = true;
        }
        else{
            saveExists = false;
        }

        if(up){
            up = false;
            currentOption--;
            if(currentOption < 0){
                currentOption = maxOption;
            }
        }

        if(down){
            down = false;
            currentOption++;
            if(currentOption > maxOption){
                currentOption = 0;
            }
        }

        if(enter){
            enter = false;
            if(options[currentOption] == "novo jogo" || options[currentOption] == "continuar"){
                Game.gameState = "NORMAL";
                pause = false;
                file = new File("save.txt");
                file.delete();
            }
            else if(options[currentOption] == "carregar jogo"){
                file = new File("save.txt");
                if(file.exists()){
                    String saver = loadGame(10);
                    applySave(saver);
                }
            }
            else if(options[currentOption] == "sair"){
                System.exit(1);
            }
        }
    }

    public static void applySave(String str){
        String[] spl = str.split("/");
        for(int i =0; i < spl.length; i++){
            String[] spl2 = spl[i].split(":");
            switch (spl2[0]){
                case "level":
                    World.restartGame("level" + spl2[1] + ".png");
                    Game.gameState = "NORMAL";
                    pause = false;
                    break;
                case "vida":
                    Game.player.life = Integer.parseInt(spl2[1]);
                    break;
            }
        }
    }

    public static String loadGame(int encode){
        String line = "";
        File file = new File("save.txt");
        if(file.exists()){
            try{
                String singleLine = null;
                BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
                try{
                    while((singleLine = reader.readLine()) != null){
                        String[] trans = singleLine.split(":");
                        char[] val = trans[1].toCharArray();
                        trans[1] = "";
                        for(int i = 0; i < val.length; i++){
                            val[i] -= encode;
                            trans[1] += val[i];
                        }
                        line += trans[0];
                        line += ":";
                        line += trans[1];
                        line += "/";
                    }
                }
                catch (IOException e){}
            }
            catch(FileNotFoundException e){}
        }

        return line;
    }

    public static void saveGame(String[] val1, int[] val2, int encode ){
        BufferedWriter write = null;
        try{
            write = new BufferedWriter(new FileWriter("save.txt"));
        }
        catch(IOException e){
            e.printStackTrace();
        }

        for(int i = 0; i < val1.length; i++){
            String current = val1[i];
            current += ":";
            char[] value = Integer.toString(val2[i]).toCharArray();
            for(int n = 0; n < value.length; n++){
                value[n] += encode;
                current += value[n];
            }
            try{
                write.write(current);
                if(i < val1.length - 1){
                    write.newLine();
                }
            }
            catch (IOException ex){

            }
        }
        try{
            write.flush();
            write.close();
        }
        catch(IOException ex){

        }
    }

    public void render(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0,0,0,100));
        g2.fillRect(0,0, Game.width * Game.scale, Game.height * Game.scale );
        g.setColor(Color.pink);
        g.setFont(Game.newFontLogo);
        g.drawString(">The Journey<", (Game.width * Game.scale) / 2 - 95, (Game.height * Game.scale) / 2 - 160);

        //Opções do menu
        g.setColor(Color.white);
        g.setFont(Game.newFontUI);
        g.drawString(!pause ? "Novo Jogo" : "Continuar Jogo", !pause ? (Game.width * Game.scale) / 2 - 45 : (Game.width * Game.scale) / 2 - 75,  160);
        g.drawString("Carregar Jogo", (Game.width * Game.scale) / 2 - 70,  200);
        g.drawString("Sair", (Game.width * Game.scale) / 2 - 15,  240);

        if(options[currentOption] == "novo jogo"){
            g.drawString(">", !pause ? (Game.width * Game.scale) / 2 - 70 : (Game.width * Game.scale) / 2 - 95,  160);
        }
        else if (options[currentOption] == "carregar jogo"){
            g.drawString(">", (Game.width * Game.scale) / 2 - 90,  200);
        }
        else if (options[currentOption] == "sair"){
            g.drawString(">", (Game.width * Game.scale) / 2 - 30,  240);
        }
    }
}
