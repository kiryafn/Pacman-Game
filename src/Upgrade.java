import javax.swing.*;

public class Upgrade extends Entity{

    enum Fruits{
        CHERRY()   {public String toString(){return "Cherry.png";}},
        STRAWBERRY()  {public String toString(){return "Strawberry.png";}},
        PEACH()  {public String toString(){return "Peach.png";}},
        APPLE(){public String toString(){return "Apple.png";}},
        SUPERFRUIT()  {public String toString(){return "Superfruit.png";}}
    }

    Fruits fruit;
    Ghost ghost;

    public Upgrade(Fruits fruit, int x, int y){
        super(x, y);

        try {
            currentIcon = new ImageIcon("Upgrades/" + fruit);
        }catch (Exception e){
            e.printStackTrace();
        }

        this.fruit = fruit;
    }
}
