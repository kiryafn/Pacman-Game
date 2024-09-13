import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Pacman extends Entity implements Moveable, Animable {
    Thread movingTimer;
    //direction
    static Coordinates toMove;
    UpgradeEventHandling upgradeEventHandling = new UpgradeEventHandling();

    public Pacman(int x, int y) {
        super(x, y);
        spawn = new Coordinates(x, y);
        toMove = new Coordinates(1, 0);
        currentDirection = Animations.RIGHT;

        try {
            animations = new ImageIcon[][]{
                    {new ImageIcon("Pacman/Right1.png"), new ImageIcon("Pacman/Right2.png"), new ImageIcon("Pacman/Right3.png"), new ImageIcon("Pacman/Right2.png")},
                    {new ImageIcon("Pacman/Left1.png"),  new ImageIcon("Pacman/Left2.png"),  new ImageIcon("Pacman/Left3.png"),  new ImageIcon("Pacman/Left2.png")},
                    {new ImageIcon("Pacman/Up1.png"),    new ImageIcon("Pacman/Up2.png"),    new ImageIcon("Pacman/Up3.png"),    new ImageIcon("Pacman/Up2.png")},
                    {new ImageIcon("Pacman/Down1.png"),  new ImageIcon("Pacman/Down2.png"),  new ImageIcon("Pacman/Down3.png"),  new ImageIcon("Pacman/Down2.png")}
            };
        }catch (Exception e) {
            e.printStackTrace();
        }

        updateAnimation();
        updateMoving();

        //This is an object that will manage picking up upgrades

    }

    @Override
    public void move(Coordinates direction) {

        //for tp from right side to left side
        if (coordinates.getX() + direction.getX() >= Game.gameMap.COLS) {
            Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(null);
            Game.gameMap.labels[coordinates.getY()][0].setIcon(currentIcon);
            coordinates.setX(0);
        }

        //for tp from bottom to the top
        else if (coordinates.getY() + direction.getY() >= Game.gameMap.ROWS) {
            Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(null);
            Game.gameMap.labels[0][coordinates.getX()].setIcon(currentIcon);
            coordinates.setY(0);
        }

        //for tp from top to the bottom
        else if (coordinates.getY() + direction.getY() < 0) {
            Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(null);
            Game.gameMap.labels[Game.gameMap.ROWS - 1][coordinates.getX()].setIcon(currentIcon);
            coordinates.setY(Game.gameMap.ROWS - 1);
        }

        //for tp from left side to right side
        else if (coordinates.getX() + direction.getX() < 0){
            Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(null);
            Game.gameMap.labels[coordinates.getY()][Game.gameMap.COLS - 1].setIcon(currentIcon);
            coordinates.setX(Game.gameMap.COLS-1);
        }

        //to move in all directions
        //if next cell isn`t a wall (1) or a door (3) we can move
        else if (Game.gameMap.map[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()] != 1 && Game.gameMap.map[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()] != 3) {
            Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(null);

            //to collect balls
            if (Game.gameMap.map[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()] == 0 && Game.gameMap.labels[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()].getIcon() != null) {
                Game.score++;
            }
            //update icon for next label with pacman (moving) AND WE UPDATE THE PACMAN ICON IN updateAnimation every 0,1 sec
            Game.gameMap.labels[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()].setIcon(currentIcon);

            //update coordinates after moving
            coordinates.setX(coordinates.getX() + direction.getX());
            coordinates.setY(coordinates.getY() + direction.getY());
        }
    }

    @Override
    public void updateAnimation(){
        animationTimer = new Thread(() ->{
            while(Game.IS_STARTED){
                try {
                    Thread.sleep(100);
                        currentIcon = (animations[currentDirection.ordinal()][currentFrame]);
                        Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(currentIcon);
                    //just looping the frames :)
                    currentFrame = ++currentFrame % animations[currentDirection.ordinal()].length;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        animationTimer.setDaemon(true); //sets thread to work on background
        animationTimer.start();
    }

    @Override
    public void updateMoving(){
         movingTimer = new Thread(() ->{
            while(Game.IS_STARTED){
                try {
                    Thread.sleep(speed);
                    SwingUtilities.invokeLater(() -> move(toMove));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        movingTimer.setDaemon(true); //sets thread to work on background
        movingTimer.start();
    }

}

