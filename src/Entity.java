import javax.swing.*;

abstract public class Entity{

    //Enum for animations
    public enum Animations {
        RIGHT, LEFT, UP, DOWN
    }

    Coordinates coordinates = new Coordinates(); //Real-time coordinates
    Coordinates spawn; //Spawn coordinates (in constructor)

    Animations    currentDirection;
    ImageIcon[][] animations;
    Thread animationTimer;
    ImageIcon currentIcon;
    int currentFrame;
    int speed;

    boolean ableToChangeSpeed;

    public Entity(int xPos, int yPos) {
        ableToChangeSpeed = true;
        this.coordinates.setX(xPos);
        this.coordinates.setY(yPos);
        this.currentFrame = 0;
        this.speed        = 170;
    }

    public Entity(){} //constructor without parameters
}
