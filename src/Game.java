import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener{
    static Map gameMap;

    Thread timerAnimation;
    Thread scoreAnimation;
    Thread heartAnimation;
    Thread gameOverAnimation;

    static boolean IS_STARTED;

    static int foodNumber;
    static int score;
    static int hearts;
    int seconds;

    static ArrayList<Ghost> enemiesArray;
    static Pacman pacman;

    JLabel scoreLabel;
    JLabel heartsLabel;
    JLabel timerLabel;

    Game(int index){
        IS_STARTED = true;

        setTitle("Pac-Man");
        setSize(840, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setVisible(true);

        //Assign values to variables
        score      = 0;
        foodNumber = 0;
        hearts     = 3;
        seconds    = 0;
        enemiesArray = new ArrayList<>(0);

        //Draw the visual
        drawGame(index);
        addGhostsAndPacman();

        //Starting threads
        updateTime();
        updateScore();
        updateHearts();
        takeDamageAndStopGame();

        addKeyListener(this);

    }

    public void drawGame(int index){
        gameMap = new Map(index);           //creating a game map (index variable is the size index)
        add(gameMap, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(1,3));

        scoreLabel  = new JLabel("Score: " + score);
        heartsLabel = new JLabel("Hearts: " + hearts);
        timerLabel  = new JLabel(seconds + " s");

        JLabel[] options = {scoreLabel, heartsLabel, timerLabel};

        for(var label : options){
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(Menu.pacmanFont);
            bottom.add(label);
        }

        bottom.setBackground(new Color(0x5421FF));
        bottom.setOpaque(true);

        add(bottom, BorderLayout.SOUTH);
    }

    private synchronized void updateTime() {
        timerAnimation = new Thread(() -> {
            while (IS_STARTED) {
                try {
                    Thread.sleep(1000);
                    seconds++;
                    //To update seconds on screen every second
                    SwingUtilities.invokeLater(() -> timerLabel.setText(seconds + " s"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        timerAnimation.setDaemon(true);
        timerAnimation.start();
    }

    private synchronized void updateScore() {
        scoreAnimation = new Thread(() -> {
            while (IS_STARTED) {
                try {
                    Thread.sleep(100);
                    SwingUtilities.invokeLater(() -> scoreLabel.setText("Score: " + score));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scoreAnimation.setDaemon(true);
        scoreAnimation.start();
    }

    private synchronized void updateHearts() {
        heartAnimation = new Thread(() -> {
            while (IS_STARTED) {
                try {
                    Thread.sleep(150);
                    SwingUtilities.invokeLater(() -> heartsLabel.setText("Hearts: " + hearts));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        heartAnimation.setDaemon(true);
        heartAnimation.start();
    }

    //Checks are there any more food on the map
    private synchronized boolean noFood() {
        ImageIcon ball = new ImageIcon("GameResourses/ball.png");
        for (int i = 0; i < gameMap.ROWS - 1; i++) {
            for (int j = 0; j < gameMap.COLS - 1; j++) {
                ImageIcon icon = (ImageIcon) gameMap.labels[i][j].getIcon();

                //Compare pathes to the texture of food
                //getDescription() returns path to file
                if (icon != null && icon.getDescription().equals(ball.getDescription())){
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void takeDamageAndStopGame(){
        gameOverAnimation = new Thread(() -> {
            while (IS_STARTED){
                try {
                    Thread.sleep(20);

                    //Stop game if Pacman dies or wins
                    if (hearts == 0 || noFood()) {
                        IS_STARTED = false;
                        new Menu();
                        String username = JOptionPane.showInputDialog("Enter your name: ");
                        Leaderboards.writeData(username, score, seconds);
                        dispose();
                        restart();
                    }

                    else{
                        for (Ghost ghost : enemiesArray) {
                            if (pacman.coordinates.equals(ghost.coordinates)) {
                                hearts--;
                                ghost.toReplace = null;
                                ghost.coordinates.set(ghost.spawn);
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        gameOverAnimation.setDaemon(true);
        gameOverAnimation.start();
    }


    public void addGhostsAndPacman(){
        Ghost.Colors[] colors = Ghost.Colors.values();
        int ind = -1;

        for (int i = 0; i < gameMap.ROWS; i++) {
            for (int j = 0; j < gameMap.COLS; j++) {
                if (gameMap.map[i][j] == 4){
                    gameMap.labels[i][j].setOpaque(true);
                    gameMap.labels[i][j].setBackground(Color.BLACK);
                    Ghost ghost = new Ghost(colors[++ind % colors.length], j, i);
                    gameMap.labels[i][j].setIcon(ghost.currentIcon);
                    enemiesArray.add(ghost);
                    gameMap.map[i][j] = 2;
                }
                else if (gameMap.map[i][j] == 5){
                    gameMap.labels[i][j].setOpaque(true);
                    gameMap.labels[i][j].setBackground(Color.BLACK);
                    pacman = new Pacman(j,i);
                    pacman.coordinates.setX(j);
                    pacman.coordinates.setY(i);
                    gameMap.labels[i][j].setIcon(pacman.currentIcon);
                    gameMap.map[i][j] = 2;
                }
            }
        }
    }

    public void keyTyped(KeyEvent e) {}    //have to be implemented cuz interface (but I don`t need it)
    public void keyReleased(KeyEvent e) {} //have to be implemented cuz interface (but I don`t need it)

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                restart();
                new Menu();
                dispose();
                break;

            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:

                if (gameMap.map[pacman.coordinates.getY() - 1][pacman.coordinates.getX()] != 1){
                    pacman.currentDirection = Entity.Animations.UP;
                    Pacman.toMove = new Coordinates(0, -1);
                }
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:

                if (gameMap.map[pacman.coordinates.getY() + 1][pacman.coordinates.getX()] != 1) {
                    pacman.currentDirection = Entity.Animations.DOWN;
                    Pacman.toMove = new Coordinates(0, 1);
                }
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:

                if (gameMap.map[pacman.coordinates.getY()][pacman.coordinates.getX() + 1] != 1) {
                    pacman.currentDirection = Entity.Animations.RIGHT;
                    Pacman.toMove = new Coordinates(1, 0);
                }
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:

                if (gameMap.map[pacman.coordinates.getY()][pacman.coordinates.getX() - 1] != 1) {
                    pacman.currentDirection = Entity.Animations.LEFT;
                    Pacman.toMove = new Coordinates(-1, 0);
                }
                break;
        }
    }

    //restarts all important values
    public void restart(){
        IS_STARTED = false;
        hearts = 3;
        score = 0;
        seconds = 0;
        foodNumber = 0;
        enemiesArray = new ArrayList<>();
    }
}
