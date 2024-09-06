import javax.swing.*;
import java.util.ArrayList;

public class Ghost extends Entity implements Moveable, Animable {
    Thread movingTimer;
    Thread upgradeSpawnerTimer;

    Coordinates shift = new Coordinates(); //for line 214

    static ArrayList<Upgrade> upgrades;
    ImageIcon toReplace = null;

    //Enum for ghost colors, @Override toString for every enum
enum Colors{
    RED()   {public String toString(){return "Ghosts/Red/";}},
    CYAN()  {public String toString(){return "Ghosts/Cyan/";}},
    PINK()  {public String toString(){return "Ghosts/Pink/";}},
    ORANGE(){public String toString(){return "Ghosts/Orange/";}}
}

    Ghost(Colors color, int x, int y){
        super(x, y);
        upgrades = new ArrayList<>();
        spawn = new Coordinates(x,y);
        currentDirection = Animations.UP;

        try {
            animations = new ImageIcon[][]{
                    {new ImageIcon(color + "Right1.png"), new ImageIcon(color + "Right2.png")},
                    {new ImageIcon(color + "Left1.png"),  new ImageIcon(color + "Left2.png")},
                    {new ImageIcon(color + "Up1.png"),    new ImageIcon(color + "Up2.png")},
                    {new ImageIcon(color + "Down1.png"),  new ImageIcon(color + "Down2.png")},
                    {new ImageIcon(color + "Dead1.png"),  new ImageIcon(color + "Dead2.png"), new ImageIcon(color + "Dead3.png"),   new ImageIcon(color + "Dead4.png")}
            };
        }catch (Exception e){
            e.printStackTrace();
        }

        updateAnimation();
        updateMoving();
        spawnUpgrade();
    }

    public Coordinates pickDirection() {
        ArrayList<Coordinates> branches = new ArrayList<>();

        if (coordinates.getX() == Game.gameMap.COLS - 1) return new Coordinates(-1, 0);
        else if (coordinates.getX() == 0) return new Coordinates(1, 0);
        else if (coordinates.getY() == Game.gameMap.ROWS - 1) return new Coordinates(0, -1);
        else if (coordinates.getY() == 0) return new Coordinates(0, 1);

        if (currentDirection != Animations.LEFT) {
            if (Game.gameMap.map[coordinates.getY()][coordinates.getX() + 1] != 1
            && Game.gameMap.map[coordinates.getY()][coordinates.getX() + 1] != 6) {
                branches.add(new Coordinates(1, 0));
            }
        }

        if (currentDirection != Animations.RIGHT) {
            if (Game.gameMap.map[coordinates.getY()][coordinates.getX() - 1] != 1
            && Game.gameMap.map[coordinates.getY()][coordinates.getX() - 1] != 6) {
                branches.add(new Coordinates(-1, 0));
            }
        }

        if (currentDirection != Animations.UP) {
            if (Game.gameMap.map[coordinates.getY() + 1][coordinates.getX()] != 1
            && Game.gameMap.map[coordinates.getY() + 1][coordinates.getX()] != 6) {
                branches.add(new Coordinates(0, 1));
            }
        }

        if (currentDirection != Animations.DOWN) {
            if (Game.gameMap.map[coordinates.getY() - 1][coordinates.getX()] != 1
            && Game.gameMap.map[coordinates.getY() - 1][coordinates.getX()] != 6) {
                branches.add(new Coordinates(0, -1));
            }
        }

        if (branches.isEmpty()) return new Coordinates();
        else {
            int i = (int) (Math.random() * branches.size());
            return branches.get(i);
        }
    }

    @Override
    public void move(Coordinates direction) {
        //changing direction
        if      (direction.getX() == 1)  currentDirection = Animations.RIGHT;
        else if (direction.getX() == -1) currentDirection = Animations.LEFT;
        else if (direction.getY() == 1)  currentDirection = Animations.DOWN;
        else if (direction.getY() == -1) currentDirection = Animations.UP;

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
            //if next cell isn`t a wall (1) we can move
        else if (Game.gameMap.map[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()] != 1
        && Game.gameMap.map[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()] != 6) {

            //sets icon of
            Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(toReplace);

            //If there are more than 1 ghost in a cell, then:
            if (containsGhostTexture(direction)){
                toReplace = null;
            }

            else if (Game.gameMap.labels[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()].getIcon() == Game.pacman.currentIcon){
                toReplace = null;
            }
            //Get an icon of next cell to assing it to "next" cell after this method called again
            else toReplace = (ImageIcon) Game.gameMap.labels[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()].getIcon();

            //update icon for next label with pacman (moving) AND WE UPDATE THE PACMAN ICON IN updateAnimation every 0,1 sec
            Game.gameMap.labels[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()].setIcon(currentIcon);

            //update coordinates after moving
            coordinates.setX(coordinates.getX() + direction.getX());
            coordinates.setY(coordinates.getY() + direction.getY());
        }
    }

    //This function is needed because when ghosts met each other they can create duplicates.
    //To avoid this I check if current cell consists of any other ghosts
    public boolean containsGhostTexture(Coordinates direction){
        for(Ghost ghost : Game.enemiesArray){
            if (Game.gameMap.labels[coordinates.getY() + direction.getY()][coordinates.getX() + direction.getX()].getIcon() == ghost.currentIcon){
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void updateAnimation(){
        animationTimer = new Thread(() ->{
            while(Game.IS_STARTED){
                try {
                    Thread.sleep(100);
                    currentIcon = (animations[currentDirection.ordinal()][currentFrame]);
                    SwingUtilities.invokeLater(() -> {
                        Game.gameMap.labels[coordinates.getY()][coordinates.getX()].setIcon(currentIcon);
                    });
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

    //This is a ghost thread which will move him according to logic every ... idk maybe 0.1 seconds
    @Override
    public synchronized void updateMoving(){
        movingTimer = new Thread(() ->{
            while(Game.IS_STARTED){
                try {
                    Thread.sleep(speed);
                     //FIX!!!!!!!!!
                    Coordinates dir = pickDirection();
                    if (dir.getY() != 0 ^ dir.getX() != 0) {
                        SwingUtilities.invokeLater(() -> move(dir));
                        shift.set(dir);
                    }


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        movingTimer.setDaemon(true); //sets thread to work on background
        movingTimer.start();
    }

    public synchronized void spawnUpgrade(){
    Upgrade.Fruits[] fruits = Upgrade.Fruits.values();
        upgradeSpawnerTimer = new Thread(() ->{
            while(Game.IS_STARTED){
                try {
                    Thread.sleep(5000);
                    int randomUpgrade = (int)(Math.random() * fruits.length);
                    int probability = (int)(Math.random() * 4);

                    // $probability can be {0, 1, 2, 3), any element from this set has 1/4 (25%) change (I choose 1)
                    if (probability == 1) {
                        Upgrade upgrade;
                        //here I spawn an upgrade one cell after ghost
                        upgrade = new Upgrade(fruits[randomUpgrade], coordinates.getX() - shift.getX(),coordinates.getY() - shift.getY());

                        if (Game.gameMap.map[upgrade.coordinates.getY()][upgrade.coordinates.getX()] != 1 && Game.gameMap.map[upgrade.coordinates.getY()][upgrade.coordinates.getX()] != 3){
                            Game.gameMap.labels[upgrade.coordinates.getY()][upgrade.coordinates.getX()].setIcon(upgrade.currentIcon);
                            upgrades.add(upgrade);
                        }
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        upgradeSpawnerTimer.setDaemon(true); //sets thread to work on background
        upgradeSpawnerTimer.start();
    }
}
