import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;

public class Leaderboards extends JFrame implements KeyListener {

    private static final File file = new File("leaderboards.ser");

    Leaderboards(){
        setTitle("Leaderboards");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);

        drawLeaderboards();
        setFocusable(true);
        addKeyListener(this);
    }

    private void drawLeaderboards(){
        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());

        JLabel label = new JLabel("Leaderboards");
        label.setPreferredSize(new Dimension(0, 100));
        label.setFont(Menu.pacmanFont);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        top.add(label, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel centre = new JPanel();
        centre.setLayout(new BorderLayout());

        MyTable myTable = new MyTable();

        for (String[] data : readData()) myTable.add(data);

        JTable table = new JTable(myTable);
        table.setForeground(Color.RED);
        centre.add(table, BorderLayout.CENTER);

        table.addKeyListener(this);

        JScrollPane scorePane = new JScrollPane(table);
        centre.add(scorePane, BorderLayout.CENTER);

        add(centre, BorderLayout.CENTER);

        //Borders
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(50, 0));
        add(left, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(50, 0));
        add(right, BorderLayout.EAST);

        JPanel bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(0, 50));
        add(bottom, BorderLayout.SOUTH);


    }


    @Override
    //Back to menu
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            new Menu();
            dispose();
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    //Writes data to a file
    public static void writeData(String username, int score, int seconds) {
        String[] dataBase = new String[]{username, String.valueOf(score), String.valueOf(seconds)};

        ArrayList<String[]> temporary = readData();
        temporary.add(dataBase);

        try (FileOutputStream fos = new FileOutputStream(file.getPath());
             ObjectOutputStream oos = new ObjectOutputStream(fos)){

            oos.writeObject(temporary);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Reads data from a file
    public static ArrayList<String[]> readData() {
        ArrayList<String[]> data;
        try {
            FileInputStream fis = new FileInputStream(file.getPath());
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (ArrayList<String[]>) ois.readObject();

        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            return new ArrayList<>();
        }
        data.sort((f, s) -> -Integer.compare(Integer.parseInt(f[1]), Integer.parseInt(s[1])));
        return data;
    }

}