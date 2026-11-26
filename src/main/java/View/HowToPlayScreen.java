package View;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;

/** Player guide for Easy, Medium, and planned rules. */
public final class HowToPlayScreen extends JPanel {

    private static final Color PURPLE = new Color(27, 12, 49);
    private static final Color PANEL_PURPLE = new Color(43, 22, 65);
    private static final Color GOLD = new Color(236, 187, 91);

    private static final String GUIDE = """
            <html>
            <head>
              <style type="text/css">
                body { font-family: SansSerif; font-size: 16px; color: #fff3cf; background-color: #2b1641; }
                h2 { font-family: Georgia, Serif; font-size: 22px; color: #ffda91; margin-top: 16px; margin-bottom: 6px; }
                p { margin-top: 0; margin-bottom: 8px; }
                .status { color: #ffda91; }
                .formula { color: #ffda91; font-weight: bold; }
              </style>
            </head>
            <body>
              <h2>Game Objective</h2>
              <p>Move all four of your pieces from your castle, around the board, and into the final home square
              at the end of your colour's home lane.</p>

              <h2>Starting a Game</h2>
              <p>Select <b>New Game</b>, choose 2–4 players, enter their names, and give each player a different colour.
              Choose <b>Easy</b> or <b>Medium</b>, then select <b>Start Game</b>.</p>

              <h2>Basic Dice Rules</h2>
              <p>The Regular Dice rolls 1–6. A Regular 6 lets you place one piece from your castle on your
              start square, or lets you move a piece already on the board if you have a legal move. A piece leaving the castle
              stops on its start square. If none of your pieces are on the board and you roll 1–5, the turn passes.</p>
              <p>A completed move on a Regular 6 grants an extra roll unless you finish all four pieces.
              On three consecutive sixes, the third roll is canceled and the turn passes to the next player.</p>

              <h2>Moving Pieces</h2>
              <p>Roll the dice and select one of your highlighted pieces. Pieces move clockwise and follow
              arrows of their own colour. To reach final home, your move must match the steps still needed.
              If the roll would take a piece past final home, choose another legal piece. If you have no legal move,
              the turn passes. In Medium, a Regular 6 with zero steps still grants another roll.</p>

              <h2>Captures and Protection</h2>
              <p>Landing on an unprotected track square occupied by exactly one opponent piece
              sends it back to its castle. Stars protect pieces from capture on both boards. In Medium,
              # start squares and surprise squares also protect pieces.</p>
              <p><b class="status">Planned.</b> A Double Block forms when two pieces of the same player share a square.
              Opponents cannot land on or pass through it. A player can break their own block by moving one of its pieces.</p>

              <h2>Difficulty Levels</h2>
              <p><b>Easy</b> is available now and uses the Regular Dice (1–6).</p>
              <p><b>Medium</b> is available now and adds the Boost Dice (1–3) to the Regular Dice.
              <b>Hard</b> is planned and uses the UltraBoost Dice (1–6) with the Regular Dice.</p>
              <p class="formula">Movement = Regular Dice × (Multiplier Die − 1)</p>
              <p>The formula applies to pieces already on the board. A multiplier die showing 1 gives zero steps.
              Only the Regular Dice determines castle exit. A Regular 6 can release a piece even when the
              multiplier gives zero steps.</p>

              <h2>Special Squares</h2>
              <p>A # on a coloured track square marks that colour's start. Coloured arrows guide matching pieces
              into their home lanes and, on the Medium board, onto the next inner track. The last coloured
              square in each home lane is final home.</p>
              <p><b class="status">Planned.</b> Question squares (?) draw a random question when a piece lands on them.
              Green is easy, yellow is medium, and red is hard. A correct answer moves the piece forward and
              a wrong answer moves it back by 1, 2, or 3 steps for the respective level. Question squares offer no protection.</p>
              <p><b class="status">Planned.</b> Surprise squares will also trigger a random effect on landing. A piece might go straight
              to final home, return to its castle, or move back three steps. The effect can also skip the player's
              next turn, involve choosing an opponent and moving one piece by half a Regular roll, or grant a
              Regular and UltraBoost roll regardless of the current streak of sixes.</p>

              <h2>Winning and Final Rankings</h2>
              <p>A player finishes when all four of their pieces reach final home.
              The first to finish wins, followed by the other finishers in order. The game ends when only one player
              is still unfinished, and that player ranks last.</p>
            </body>
            </html>
            """;

    public HowToPlayScreen(Runnable onBack) {
        setLayout(new BorderLayout(0, 16));
        setBackground(PURPLE);
        setBorder(new EmptyBorder(22, 32, 24, 32));

        JLabel title = new JLabel("How to Play");
        title.setFont(new Font("Georgia", Font.BOLD, 32));
        title.setForeground(GOLD);
        URL rabbit = HowToPlayScreen.class.getResource("/rabbit_logo.png");
        if (rabbit != null) {
            Image logo = new ImageIcon(rabbit).getImage().getScaledInstance(54, -1, Image.SCALE_SMOOTH);
            title.setIcon(new ImageIcon(logo));
            title.setIconTextGap(12);
        }

        JButton back = new HomeScreen.RoyalButton("Back to Home");
        back.setPreferredSize(new Dimension(136, 42));
        back.addActionListener(event -> onBack.run());

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);
        header.add(title, BorderLayout.CENTER);
        header.add(back, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JEditorPane content = new JEditorPane("text/html", GUIDE);
        content.setEditable(false);
        content.setBackground(PANEL_PURPLE);
        content.setBorder(new EmptyBorder(4, 24, 16, 24));
        content.getAccessibleContext().setAccessibleName("How to Play instructions");
        content.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createLineBorder(GOLD, 1, true));
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        add(scroll, BorderLayout.CENTER);
    }
}
