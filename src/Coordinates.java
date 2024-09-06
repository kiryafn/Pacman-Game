public class Coordinates
{
    private int x;
    private int y;

    Coordinates(){
        x = 0;
        y = 0;
    }
    Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){return x;}
    public int getY(){return y;}

    public void setX(int x){this.x = x;}
    public void setY(int y){this.y = y;}

    public boolean equals(Coordinates c){
        return x == c.getX() && y == c.getY();
    }

    public void set(Coordinates c){
        x = c.getX();
        y = c.getY();
    }
}
