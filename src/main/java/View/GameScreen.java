package View;

import Model.Piece;
import Model.Player;
import Model.PlayerColor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.function.Consumer;

/**
 * Shows the Easy board and the information needed to follow a turn.
 * Elapsed time is measured here because it is display state, not a movement rule.
 */
public final class GameScreen extends JPanel {

    private final BoardPanel board = new BoardPanel();
    private final JLabel turnLabel = new JLabel("Turn 1");
    private final JPanel colorChip = new JPanel();
    private final JLabel playerLabel = new JLabel(" ");
    private final JLabel diceCaption = new JLabel("Latest roll");
    private final DiceFacePanel diceFace = new DiceFacePanel();
    private final JLabel timeLabel = new JLabel("00:00");
    private final JTextArea message = new JTextArea(" ");
    private final JButton rollButton = UiKit.button("Roll Dice", UiKit.GOLD, UiKit.INK);
    private final JButton leaveButton = UiKit.button("Back to Home", Color.WHITE, UiKit.INK);
    private final Timer clock;
    private final long startedAt = System.currentTimeMillis();
    private Runnable onRoll = () -> { };
    private Runnable onLeave = () -> { };

    public GameScreen(List<Player> players) {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(244, 236, 224));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        board.setPlayers(players);

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(UiKit.CARD);
        side.setBorder(new EmptyBorder(18, 16, 18, 16));
        side.setPreferredSize(new Dimension(270, 0));

        JLabel mode = caption("EASY GAME");
        mode.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        mode.setForeground(UiKit.PLUM);
        turnLabel.setFont(new Font(Font.SERIF, Font.BOLD, 28));
        turnLabel.setForeground(UiKit.NAVY);

        JLabel currentCaption = caption("Current player");
        JPanel playerRow = new JPanel();
        playerRow.setOpaque(false);
        playerRow.setLayout(new BoxLayout(playerRow, BoxLayout.X_AXIS));
        colorChip.setOpaque(true);
        colorChip.setMinimumSize(new Dimension(18, 18));
        colorChip.setPreferredSize(new Dimension(18, 18));
        colorChip.setMaximumSize(new Dimension(18, 18));
        colorChip.setBorder(javax.swing.BorderFactory.createLineBorder(UiKit.INK));
        playerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        playerRow.add(colorChip);
        playerRow.add(Box.createHorizontalStrut(8));
        playerRow.add(playerLabel);

        diceCaption.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        diceCaption.setForeground(new Color(70, 74, 90));
        timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
        timeLabel.setForeground(UiKit.NAVY);

        message.setEditable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setOpaque(false);
        message.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        message.setForeground(UiKit.INK);
        message.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        rollButton.addActionListener(event -> onRoll.run());
        leaveButton.addActionListener(event -> onLeave.run());
        pin(rollButton, 230, 46);
        pin(leaveButton, 230, 46);
        message.setRows(4);
        pin(message, 230, 88);
        pin(diceFace, 84, 84);
        pin(timeLabel, 230, 32);
        pin(playerRow, 230, 32);

        align(mode);
        align(turnLabel);
        align(currentCaption);
        align(playerRow);
        align(diceCaption);
        align(diceFace);
        align(timeLabel);
        align(message);

        side.add(mode);
        side.add(Box.createVerticalStrut(4));
        side.add(turnLabel);
        side.add(Box.createVerticalStrut(16));
        side.add(currentCaption);
        side.add(Box.createVerticalStrut(4));
        side.add(playerRow);
        side.add(Box.createVerticalStrut(16));
        side.add(diceCaption);
        side.add(Box.createVerticalStrut(6));
        side.add(diceFace);
        side.add(Box.createVerticalStrut(16));
        side.add(caption("Elapsed time"));
        side.add(Box.createVerticalStrut(4));
        side.add(timeLabel);
        side.add(Box.createVerticalStrut(16));
        side.add(rollButton);
        side.add(Box.createVerticalStrut(10));
        side.add(message);
        side.add(Box.createVerticalGlue());
        side.add(leaveButton);

        add(board, BorderLayout.CENTER);
        add(side, BorderLayout.EAST);

        if (!players.isEmpty()) {
            setCurrentPlayer(players.get(0));
        }
        clock = new Timer(1000, event -> updateClock());
        clock.setInitialDelay(0);
        clock.start();
    }

    public void setTurnNumber(int turnNumber) {
        turnLabel.setText("Turn " + turnNumber);
    }

    public void setCurrentPlayer(Player player) {
        playerLabel.setText(player.getName());
        PlayerColor color = player.getColor();
        colorChip.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue()));
        colorChip.setToolTipText(color.getDisplayName());
    }

    public void setDiceValue(int diceValue) {
        diceFace.setValue(diceValue);
        diceCaption.setText(diceValue == 0 ? "Latest roll" : "Latest roll: " + diceValue);
    }

    public void setMessage(String text) {
        message.setText(text == null ? " " : text);
    }

    public void setRollEnabled(boolean enabled) {
        rollButton.setEnabled(enabled);
    }

    public void setHighlights(List<Piece> pieces) {
        board.setHighlights(pieces);
    }

    public void refreshBoard() {
        board.repaint();
    }

    public void setOnRoll(Runnable onRoll) {
        this.onRoll = onRoll;
    }

    public void setOnPieceSelected(Consumer<Piece> onPieceSelected) {
        board.setOnPieceSelected(onPieceSelected);
    }

    public void setOnLeave(Runnable onLeave) {
        this.onLeave = onLeave;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (clock == null) {
            return;
        }
        if (visible) {
            clock.start();
        } else {
            clock.stop();
        }
    }

    @Override
    public void removeNotify() {
        if (clock != null) {
            clock.stop();
        }
        super.removeNotify();
    }

    private void updateClock() {
        long elapsedSeconds = Math.max(0, (System.currentTimeMillis() - startedAt) / 1000);
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        label.setForeground(new Color(70, 74, 90));
        align(label);
        return label;
    }

    private static void align(JComponent component) {
        component.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }

    private static void pin(JComponent component, int width, int height) {
        align(component);
        component.setMaximumSize(new Dimension(width, height));
    }
}
