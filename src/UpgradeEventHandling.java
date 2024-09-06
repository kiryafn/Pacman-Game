import java.awt.font.GlyphMetrics;

public class UpgradeEventHandling {
    Thread upgradeHandling;

    UpgradeEventHandling() {
        upgradeEventHandling();
    }

    public void updatePacmanSpeed(){
        Thread speedUpdate  = new Thread(() -> {
            try {
                Game.pacman.ableToChangeSpeed = !Game.pacman.ableToChangeSpeed;
                Game.pacman.speed -= 100;
                Thread.sleep(5000);
                Game.pacman.speed += 100;
                Game.pacman.ableToChangeSpeed = !Game.pacman.ableToChangeSpeed;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        speedUpdate.setDaemon(true); //sets thread to work on background
        speedUpdate.start();
    }

    public void updateGhostsSpeed(){
        Thread speedUpdate  = new Thread(() -> {
            try {
                for (Ghost ghost : Game.enemiesArray) {
                    if (ghost.ableToChangeSpeed) {
                        ghost.ableToChangeSpeed = !ghost.ableToChangeSpeed;
                        ghost.speed += 500;
                    }
                }
                    Thread.sleep(4000);

                for (Ghost ghost : Game.enemiesArray){
                    if (!ghost.ableToChangeSpeed) {
                        ghost.speed -= 500;
                        ghost.ableToChangeSpeed = !ghost.ableToChangeSpeed;
                    }

                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        speedUpdate.setDaemon(true); //sets thread to work on background
        speedUpdate.start();
    }

    public void upgradeEventHandling(){
        upgradeHandling  = new Thread(() -> {
            while (Game.IS_STARTED){
                try {
                    Thread.sleep(50);

                    //If pacman have eaten cherry - increase score by 100
                    if (checkForCherry()) Game.score += 100;

                    //If pacman have eaten strawberry - increase his own speed for 5 sec
                    else if (checkForStrawberry() && Game.pacman.ableToChangeSpeed) updatePacmanSpeed();

                    //If pacman have eaten superfruit - add 1 heart
                    else if (checkForSuperfruit()) Game.hearts++;

                    //If pacman have eaten cherry - make ghosts very slow for 4 sec
                    else if (checkForPeach()) updateGhostsSpeed();

                    //If pacman have eaten apple - randomly tp him
                    else if (checkForApple()) randomTP(); //сделать рандомную тепепортацию пакмана (НЕ В СТЕНУ ТОЛЬКО)


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        upgradeHandling.setDaemon(true); //sets thread to work on background
        upgradeHandling.start();
    }

    public void randomTP(){
        int newX = 0;
        int newY = 0;
        while (Game.gameMap.map[newY][newX] != 0 && Game.gameMap.map[newY][newX] != 2 && Game.gameMap.map[newY][newX] != 6 && Game.gameMap.map[newY][newX] != 4){
            newX = (int)(Math.random() * Game.gameMap.COLS);
            newY = (int)(Math.random() * Game.gameMap.ROWS);
        }
        Game.gameMap.labels[Game.pacman.coordinates.getY()][Game.pacman.coordinates.getX()].setIcon(null);
        Game.gameMap.labels[newY][newX].setIcon(Game.pacman.currentIcon);
        Game.pacman.coordinates.setX(newX);
        Game.pacman.coordinates.setY(newY);
    }



    //Checks if pacman intersects with any "Cherry" at this moment of time and if true I delete this object (and get it`s power)
    public boolean checkForCherry(){
        for (Upgrade upgrade : Ghost.upgrades){
            if (upgrade != null && Game.pacman.coordinates.equals(upgrade.coordinates) && upgrade.fruit == Upgrade.Fruits.CHERRY){
                Ghost.upgrades.remove(upgrade);
                return true;
            }
        }
        return false;
    }

    //Checks if pacman intersects with any "Apple" at this moment of time and if true I delete this object (and get it`s power)
    public boolean checkForApple(){
        for (Upgrade upgrade : Ghost.upgrades){
            if (upgrade != null && Game.pacman.coordinates.equals(upgrade.coordinates) && upgrade.fruit == Upgrade.Fruits.APPLE){
                Ghost.upgrades.remove(upgrade);
                return true;
            }
        }
        return false;
    }

    //Checks if pacman intersects with any "Strawberry" at this moment of time and if true I delete this object (and get it`s power)
    public boolean checkForStrawberry(){
        for (Upgrade upgrade : Ghost.upgrades){
            if (upgrade != null && Game.pacman.coordinates.equals(upgrade.coordinates) && upgrade.fruit == Upgrade.Fruits.STRAWBERRY){
                Ghost.upgrades.remove(upgrade);
                return true;
            }
        }
        return false;
    }

    //Checks if pacman intersects with any "Superfruit" at this moment of time and if true I delete this object (and get it`s power)
    public boolean checkForSuperfruit(){
        for (Upgrade upgrade : Ghost.upgrades){
            if (upgrade != null && Game.pacman.coordinates.equals(upgrade.coordinates) && upgrade.fruit == Upgrade.Fruits.SUPERFRUIT){
                Ghost.upgrades.remove(upgrade);
                return true;
            }
        }
        return false;
    }

    //Checks if pacman intersects with any "Peach" at this moment of time and if true I delete this object (and get it`s power)
    public boolean checkForPeach(){
        for (Upgrade upgrade : Ghost.upgrades){
            if (upgrade != null && Game.pacman.coordinates.equals(upgrade.coordinates) && upgrade.fruit == Upgrade.Fruits.PEACH){
                Ghost.upgrades.remove(upgrade);
                return true;
            }
        }
        return false;
    }
}
