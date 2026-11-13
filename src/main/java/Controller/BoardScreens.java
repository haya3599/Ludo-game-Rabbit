
package Controller;

import Model.Dice;
import Model.Game;
import Model.GameConfig;
import View.GameScreen;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public final class BoardScreens {

    private static final Color PURPLE = new Color(40, 15, 58);
    private static final Color GOLD = new Color(245, 190, 85);
    private static final Color LIGHT_GOLD = new Color(255, 225, 160);

    private BoardScreens() {
    }

    public static void register(AppController app) {
        app.setGameStartHandler(config -> open(app, config));
    }

    private static void open(AppController app, GameConfig config) {
        Game game = Game.start(config, Dice.random());
        GameScreen screen = new GameScreen(game.getPlayers());

        new GameplayController(game, screen);

        screen.setOnLeave(() -> {
    if (View.RoyalLeaveDialog.confirm(app.getFrame())) {
        app.show(ScreenId.HOME);
    }
});

        app.register(ScreenId.BOARD, screen);
        app.show(ScreenId.BOARD);
    }

    private static boolean confirmLeave(JFrame owner) {
        final boolean[] confirmed = {false};

        JDialog dialog = new JDialog(
                owner, "Royal Ludo Academy", true
        );
        dialog.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(PURPLE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(GOLD, 2, true),
                new javax.swing.border.EmptyBorder(28, 36, 28, 36)
        ));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel crown = new JLabel("\u265B");
        crown.setFont(new Font("Serif", Font.BOLD, 40));
        crown.setForeground(GOLD);
        crown.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("LEAVING THE KINGDOM?");
        title.setFont(new Font("Georgia", Font.BOLD, 23));
        title.setForeground(LIGHT_GOLD);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel question = new JLabel(
                "Are you sure you want to leave this game?"
        );
        question.setFont(new Font("SansSerif", Font.PLAIN, 15));
        question.setForeground(new Color(235, 215, 240));
        question.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel note = new JLabel(
                "Your current game progress will be lost."
        );
        note.setFont(new Font("SansSerif", Font.PLAIN, 13));
        note.setForeground(new Color(190, 165, 200));
        note.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(crown);
        content.add(Box.createVerticalStrut(8));
        content.add(title);
        content.add(Box.createVerticalStrut(18));
        content.add(question);
        content.add(Box.createVerticalStrut(7));
        content.add(note);

        JButton stay = new JButton("Stay in Game");
        styleButton(stay, GOLD, PURPLE);

        JButton leave = new JButton("Yes, Leave");
        styleButton(
                leave,
                new Color(65, 31, 82),
                LIGHT_GOLD
        );

        stay.addActionListener(e -> dialog.dispose());

        leave.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 15, 0
        ));
        buttons.setOpaque(false);
        buttons.add(stay);
        buttons.add(leave);

        panel.add(content, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane().setDefaultButton(stay);
        dialog.setVisible(true);

        return confirmed[0];
    }

    private static void styleButton(
            JButton button,
            Color background,
            Color foreground) {

        button.setFont(new Font(
                "SansSerif", Font.BOLD, 14
        ));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 43));
        button.setBorder(new LineBorder(GOLD, 2, true));
        button.setOpaque(true);
    }
}
