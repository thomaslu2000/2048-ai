import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;


class Game {
    public Game() {
    	EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("2048");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                GamePanel game=new GamePanel();
                game.addKeyListener(game);
                game.setFocusable(true);
                frame.add(game);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    public static void main(String[] args) {
        new Game();
    }
}
