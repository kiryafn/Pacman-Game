import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Menu extends JFrame implements ActionListener{
    JButton startButton = new JButton("Start Game");
    JButton scoreButton = new JButton("High Scores");
    JButton exitButton  = new JButton("Exit");
    JComboBox comboBox;

    static Font pacmanFont;

    public Menu() {
        setTitle("Pac-Man Menu");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Window icon
        ImageIcon icon = new ImageIcon("Icons/pacman-2.png");
        setIconImage(icon.getImage());

        setLayout(new GridLayout(3, 3, 10,10));
        drawMenu();
    }

    public void drawMenu(){
        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0,1, 10, 10));



        try {
            // Assigning font and icons to buttons and setting focusable to false
            startButton.setIcon(new ImageIcon("Icons/playIcon.png"));
            scoreButton.setIcon(new ImageIcon("Icons/scoreIcon.png"));
            exitButton.setIcon (new ImageIcon("Icons/exitIcon.png"));

            // Loading font from files
            pacmanFont = Font.createFont(Font.TRUETYPE_FONT, new File("Icons/Pacman.ttf")).deriveFont(24f);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        // Assigning it to a buttons;
        scoreButton.setFont(pacmanFont);
        startButton.setFont(pacmanFont);
        exitButton.setFont(pacmanFont);

        startButton.setFocusable(true);
        scoreButton.setFocusable(false);
        exitButton.setFocusable (false);

        // Addind buttons to panel
        buttonPanel.add(startButton);
        buttonPanel.add(scoreButton);
        buttonPanel.add(exitButton);

        // Creating a label with logo
        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon("Icons/pacman-2.png"));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        // Placing buttons into the centre
        for (int i = 0; i < 9; i++) {
            if (i == 1) add(logo);
            else if (i == 4) add(buttonPanel);
            else add(new JLabel());
        }
        // Creating a combo box for grid lvl size
        String[] size = {"25x29", "23x25", "15x17", "20x21", "31x28 (original)"};
        comboBox = new JComboBox(size);

        // Here it is invisible
        comboBox.setVisible(false);
        comboBox.setEnabled(false);

        buttonPanel.add(comboBox);

        // Adding ActionListeners to buttons
        exitButton.addActionListener(this);
        startButton.addActionListener(this);
        scoreButton.addActionListener(this);
        comboBox.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {

            // Hide all buttons
            startButton.setEnabled(false);
            scoreButton.setEnabled(false);
            exitButton.setEnabled(false);
            startButton.setVisible(false);
            scoreButton.setVisible(false);
            exitButton.setVisible(false);

            // Show combo box for lvl size
            comboBox.setVisible(true);
            comboBox.setEnabled(true);

        }
        else if (e.getSource() == scoreButton){

            //Open a leaderboards window
            new Leaderboards();
            dispose();
        }

        else if (e.getSource() == comboBox) {

            // Create a new game window with selected board size and closing this menu window
            new Game(comboBox.getSelectedIndex());
            dispose();
        }
        else if (e.getSource() == exitButton) System.exit(0); //Just exit if clicked
    }
}
