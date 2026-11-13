
package View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;

/** Royal Ludo home screen: all UI elements are real Swing/Graphics2D components.
 *  Only the central rabbit illustration is loaded from royal_rabbit.png. */
public final class HomeScreen extends JPanel {
    private static final int W = 1150, H = 589;
    private static final Color GOLD = new Color(236,187,91), CREAM = new Color(255,243,207);
    private static final Color PURPLE = new Color(27,12,49);
    private BufferedImage rabbit;
    private final BufferedImage rabbitLogo = loadImage("rabbit_logo.png");
    private final RoyalButton play = new RoyalButton("NEW GAME", true);
    private final RoyalButton history = new RoyalButton("GAME HISTORY", false);
    private final RoyalButton questions = new RoyalButton("QUESTION MANAGEMENT", false);
    private final Board board = new Board();
    private final Card[] cards = {
        new Card("01", "EASY", "Start your royal journey", "CLASSIC LUDO  •  LEARN THE RULES", 0),
        new Card("02", "MEDIUM", "A challenge awaits", "MORE QUESTIONS  •  MORE STRATEGY", 1),
        new Card("03", "HARD", "For the bravest players", "MASTER THE BOARD  •  COMING SOON", 2)
    };
    private final JComponent rabbitArt = new JComponent() {
        @Override protected void paintComponent(Graphics gr) {
            if (rabbit == null) return;
            Graphics2D g = (Graphics2D)gr.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            // Image is the mascot only; never the entire screen or its buttons.
            double s = Math.min(getWidth()/(double)rabbit.getWidth(), getHeight()/(double)rabbit.getHeight());
            int iw=(int)(rabbit.getWidth()*s), ih=(int)(rabbit.getHeight()*s);
            g.drawImage(rabbit,(getWidth()-iw)/2,(getHeight()-ih)/2,iw,ih,null);
            g.dispose();
        }
    };

    public HomeScreen() {
        setLayout(null);
        setBackground(PURPLE);
        setOpaque(true);
        loadRabbit();
        add(rabbitArt);
        add(board);
        add(play); add(history); add(questions);
        for(Card c:cards) add(c);
        history.addActionListener(e -> ComingSoon.show(this));
        questions.addActionListener(e -> ComingSoon.show(this));
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { position(); }
        });
    }
    public void setOnNewGame(Runnable action) {
        play.addActionListener(e -> { if(action!=null) action.run(); });
    }
    private void loadRabbit() {
        try(InputStream in=HomeScreen.class.getResourceAsStream("/royal_rabbit.png")) {
            if(in!=null) rabbit=ImageIO.read(in);
        } catch(IOException e) { e.printStackTrace(); }
        if(rabbit==null) try { rabbit=ImageIO.read(new File("src/main/resources/royal_rabbit.png")); }
        catch(IOException ignored) { }
    }
    private Rectangle canvas() {
        double s=Math.min(getWidth()/(double)W,getHeight()/(double)H);
        int w=(int)Math.round(W*s),h=(int)Math.round(H*s);
        return new Rectangle((getWidth()-w)/2,(getHeight()-h)/2,w,h);
    }
    private void place(JComponent c,int x,int y,int w,int h) {
        Rectangle a=canvas(); double s=a.width/(double)W;
        c.setBounds(a.x+(int)Math.round(x*s),a.y+(int)Math.round(y*s),
                (int)Math.round(w*s),(int)Math.round(h*s));
        if(c instanceof RoyalButton) ((RoyalButton)c).setScale(s);
        if(c instanceof Card) ((Card)c).setScale(s);
    }
    private void position() {
        place(rabbitArt,433,39,278,375);
        place(board,727,82,337,333);
        place(play,78,302,338,48);
        place(history,78,359,155,40);
        place(questions,243,359,184,40);
        place(cards[0],35,445,347,124);
        place(cards[1],398,445,354,124);
        place(cards[2],769,445,348,124);
        repaint();
    }
    @Override public void addNotify() { super.addNotify(); SwingUtilities.invokeLater(this::position); }

    @Override protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Rectangle a=canvas();
        Graphics2D g=(Graphics2D)gr.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0,0,new Color(18,7,36),getWidth(),getHeight(),new Color(57,21,78)));
        g.fillRect(0,0,getWidth(),getHeight());
        g.translate(a.x,a.y); g.scale(a.width/(double)W,a.height/(double)H);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(575,250),430,
                new float[]{0,1},new Color[]{new Color(122,56,147,100),new Color(40,15,70,0)}));
        g.fillRect(0,0,W,H);
        // Silk ribbons, kept behind all actual controls.
        Path2D ribbon=new Path2D.Double();
        ribbon.moveTo(0,92);ribbon.curveTo(140,-5,278,48,402,-22);
        ribbon.curveTo(495,-70,560,-16,620,-38);
        g.setColor(new Color(160,73,177,35));g.setStroke(new BasicStroke(34));g.draw(ribbon);
        g.setColor(new Color(239,157,185,75));g.setStroke(new BasicStroke(3));g.draw(ribbon);
        Path2D right=new Path2D.Double();right.moveTo(1120,0);right.curveTo(1070,45,1150,83,1115,144);
        g.setColor(new Color(176,79,175,75));g.setStroke(new BasicStroke(16));g.draw(right);
        // Refined royal team identity: proportional transparent logo + elegant wordmark.
        if (rabbitLogo != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int maxW = 58, maxH = 55;
            double ratio = Math.min(maxW / (double) rabbitLogo.getWidth(),
                                    maxH / (double) rabbitLogo.getHeight());
            int logoW = (int) Math.round(rabbitLogo.getWidth() * ratio);
            int logoH = (int) Math.round(rabbitLogo.getHeight() * ratio);
            g.drawImage(rabbitLogo, 34 + (maxW-logoW)/2,
                    3 + (maxH-logoH)/2, logoW, logoH, null);
        } else {
            drawSmallRabbit(g, 48, 17);
        }
        // TEAM RABBIT: warm gold wordmark with a subtle luminous flourish.
        g.setFont(new Font("Georgia", Font.BOLD, 15));
        g.setColor(new Color(60, 24, 44, 150));
        g.drawString("TEAM RABBIT", 102, 35);
        g.setColor(new Color(255, 225, 158));
        g.drawString("TEAM RABBIT", 101, 34);

        Graphics2D brandLine = (Graphics2D) g.create();
        brandLine.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        brandLine.setColor(new Color(255, 183, 83, 32));
        brandLine.draw(new Line2D.Double(101, 44, 271, 44));
        brandLine.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        brandLine.setColor(new Color(255, 214, 135, 205));
        brandLine.draw(new Line2D.Double(101, 44, 271, 44));
        brandLine.setColor(new Color(255, 231, 169, 235));
        brandLine.setStroke(new BasicStroke(1.4f));
        brandLine.drawLine(274, 37, 274, 51);
        brandLine.drawLine(267, 44, 281, 44);
        brandLine.setColor(new Color(255, 245, 207));
        brandLine.fillOval(272, 42, 4, 4);
        brandLine.dispose();
        centered(g,"L U D O  A C A D E M Y",994,36,new Font("Georgia",Font.BOLD,19),GOLD);
        centered(g,"R O Y A L   E D I T I O N",994,54,new Font("SansSerif",Font.BOLD,9),CREAM);
        g.setColor(new Color(235,180,95,115));g.setStroke(new BasicStroke(.8f));g.drawLine(42,65,426,65);
        g.drawLine(900,64,1092,64);
        // Left copy
        centered(g,"✦  W E L C O M E  T O  T H E  R O Y A L  E D I T I O N  ✦",255,88,
                new Font("SansSerif",Font.BOLD,11),GOLD);
        goldText(g,"LUDO",252,155,73);
        goldText(g,"ACADEMY",252,200,40);
        drawLaurel(g,94,159,false);drawLaurel(g,400,159,true);
        // Decorative folded royal banner (not a button).
        drawRoyalBanner(g, 247, 228);
        centered(g,"Roll the dice. Answer questions. Conquer the board.",250,272,
                new Font("Georgia",Font.ITALIC,13),CREAM);
        centered(g,"Every move brings you closer to victory.",250,288,
                new Font("Georgia",Font.ITALIC,12),CREAM);
        centered(g,"FOUR COLORS   |   ROYAL CHALLENGES   |   LEARN & PLAY",250,429,
                new Font("SansSerif",Font.BOLD,10),GOLD);
        // Rabbit halo and ornamental stars are behind the PNG image.
        g.setColor(new Color(250,191,84,150));g.setStroke(new BasicStroke(1.6f));
        g.draw(new Arc2D.Double(439,100,270,280,15,300,Arc2D.OPEN));
        for(int i=0;i<15;i++) {
            double theta=i*2.399;int x=(int)(576+151*Math.cos(theta)),y=(int)(230+154*Math.sin(theta));
            sparkle(g,x,y,i%4==0?5:2);
        }
        // Divider
        g.setColor(new Color(243,178,87,105));g.drawLine(34,437,1117,437);
        g.dispose();
    }
    private static void drawRoyalBanner(Graphics2D g, int cx, int cy) {
        Graphics2D z = (Graphics2D) g.create();
        int x = cx - 144, y = cy - 11, w = 288, h = 23;
        Path2D left = new Path2D.Double();
        left.moveTo(x + 18, y + 3); left.lineTo(x - 12, y + 3);
        left.lineTo(x - 5, y + h / 2.0); left.lineTo(x - 12, y + h - 2);
        left.lineTo(x + 18, y + h - 2); left.closePath();
        Path2D right = new Path2D.Double();
        right.moveTo(x + w - 18, y + 3); right.lineTo(x + w + 12, y + 3);
        right.lineTo(x + w + 5, y + h / 2.0);
        right.lineTo(x + w + 12, y + h - 2);
        right.lineTo(x + w - 18, y + h - 2); right.closePath();
        z.setPaint(new GradientPaint(0, y, new Color(232,174,87), 0, y+h, new Color(142,80,43)));
        z.fill(left); z.fill(right);
        z.setColor(new Color(104,53,43,190));
        z.fillPolygon(new int[]{x+18,x+27,x+18},new int[]{y+3,y+8,y+h-2},3);
        z.fillPolygon(new int[]{x+w-18,x+w-27,x+w-18},new int[]{y+3,y+8,y+h-2},3);
        z.setPaint(new GradientPaint(0,y,new Color(255,238,176),0,y+h,new Color(213,154,75)));
        z.fill(new Rectangle2D.Double(x+18,y,w-36,h-2));
        z.setColor(new Color(255,245,200,160)); z.setStroke(new BasicStroke(.8f));
        z.drawLine(x+21,y+2,x+w-21,y+2);
        centered(z,"THE ULTIMATE EDUCATIONAL ADVENTURE",cx,cy+4,
                new Font("SansSerif",Font.BOLD,10),new Color(52,29,44));
        z.dispose();
    }

    private static void centered(Graphics2D g,String text,int cx,int y,Font f,Color color) {
        g.setFont(f);g.setColor(color);g.drawString(text,cx-g.getFontMetrics().stringWidth(text)/2,y);
    }
    private static void goldText(Graphics2D g,String s,int cx,int baseline,int size) {
        Font f=new Font("Georgia",Font.BOLD,size);g.setFont(f);
        int x=cx-g.getFontMetrics().stringWidth(s)/2;
        g.setColor(new Color(4,0,16,95));g.drawString(s,x+2,baseline+2);
        g.setPaint(new GradientPaint(0,baseline-size,new Color(255,255,224),0,baseline,new Color(222,152,59)));
        g.drawString(s,x,baseline);
    }
    private static void sparkle(Graphics2D g,int x,int y,int r) {
        g.setColor(new Color(255,209,113,175));g.drawLine(x-r,y,x+r,y);g.drawLine(x,y-r,x,y+r);
    }
    // Symmetrical royal laurel branches, with individually shaped gold leaves.
    private static void drawLaurel(Graphics2D source, int x, int y, boolean flip) {
        Graphics2D g = (Graphics2D) source.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);
        if (flip) g.scale(-1, 1);

        // Branch rises outward and curves gently back toward the title.
        Path2D stem = new Path2D.Double();
        stem.moveTo(24, 43);
        stem.curveTo(-7, 23, -18, -12, 6, -55);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setPaint(new GradientPaint(-10, -55, new Color(255, 226, 142),
                                      24, 43, new Color(177, 105, 38)));
        g.draw(stem);

        // Leaves are actual curved botanical shapes, not rotated ovals.
        for (int i = 0; i < 7; i++) {
            double t = 0.12 + i * 0.105;
            double mt = 1 - t;
            double px = mt*mt*mt*24 + 3*mt*mt*t*(-7)
                      + 3*mt*t*t*(-18) + t*t*t*6;
            double py = mt*mt*mt*43 + 3*mt*mt*t*23
                      + 3*mt*t*t*(-12) + t*t*t*(-55);
            double dx = 3*mt*mt*(-31) + 6*mt*t*(-11) + 3*t*t*24;
            double dy = 3*mt*mt*(-20) + 6*mt*t*(-35) + 3*t*t*(-43);
            double len = Math.hypot(dx, dy);
            double nx = -dy/len, ny = dx/len;
            double leafLength = 16 - i*.55;
            double leafWidth = 5.3 - i*.18;
            for (int side : new int[]{-1, 1}) {
                if (i == 7 && side == -1) continue;
                double ox = px + nx*side*2, oy = py + ny*side*2;
                double tipX = ox + nx*side*leafLength + dx/len*5;
                double tipY = oy + ny*side*leafLength + dy/len*5;
                Path2D leaf = new Path2D.Double();
                leaf.moveTo(ox, oy);
                leaf.quadTo(ox + nx*side*leafLength*.53 - dx/len*leafWidth,
                            oy + ny*side*leafLength*.53 - dy/len*leafWidth, tipX, tipY);
                leaf.quadTo(ox + nx*side*leafLength*.53 + dx/len*leafWidth,
                            oy + ny*side*leafLength*.53 + dy/len*leafWidth, ox, oy);
                leaf.closePath();
                g.setPaint(new GradientPaint((float)ox,(float)oy,
                        new Color(174,103,39),(float)tipX,(float)tipY,
                        new Color(255,218,117)));
                g.fill(leaf);
                g.setColor(new Color(255,234,167,115));
                g.setStroke(new BasicStroke(.55f));
                g.draw(leaf);
            }
        }
        g.dispose();
    }
    // Royal rabbit brand mark, drawn in Java (not a screenshot or a UI image).
    private static void drawSmallRabbit(Graphics2D source, int x, int y) {
        Graphics2D g = (Graphics2D) source.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x-2, y-14);
        g.setColor(new Color(240,187,86,40));
        g.fillOval(-3, 7, 31, 31);
        g.setColor(new Color(242,191,91));
        g.setStroke(new BasicStroke(1.1f));
        g.drawOval(-3, 7, 31, 31);
        // Curved ears with warm rose centers.
        g.setColor(new Color(255,238,211));
        g.fill(new Ellipse2D.Double(4,-9,8,28));
        g.fill(new Ellipse2D.Double(15,-10,8,29));
        g.setColor(new Color(228,142,153));
        g.fill(new Ellipse2D.Double(6,-5,4,19));
        g.fill(new Ellipse2D.Double(17,-6,4,20));
        g.setColor(new Color(255,239,210));
        g.fill(new Ellipse2D.Double(0,13,27,23));
        g.setColor(new Color(231,181,95));
        g.setStroke(new BasicStroke(.8f));
        g.draw(new Ellipse2D.Double(0,13,27,23));
        g.setColor(new Color(59,34,58));
        g.fillOval(7,23,2,3);
        g.fillOval(18,23,2,3);
        g.setColor(new Color(230,141,151));
        g.fillOval(12,27,4,3);
        g.setColor(new Color(244,162,160,95));
        g.fillOval(3,27,5,3);
        g.fillOval(19,27,5,3);
        // Tiny jewel crown above the rabbit's forehead.
        Polygon tiara = new Polygon(new int[]{7,10,13,16,20,19,8},
                new int[]{17,13,16,12,17,20,20},7);
        g.setPaint(new GradientPaint(0,12,new Color(255,241,162),0,21,new Color(206,130,39)));
        g.fill(tiara);
        g.setColor(new Color(107,48,88));
        g.fillOval(12,16,3,3);
        g.dispose();
    }

    private static void crown(Graphics2D g,int x,int y,int w) {
        int h=(int)(w*.58);
        Polygon p=new Polygon(new int[]{x,x+w/5,x+w/2,x+4*w/5,x+w,x+9*w/10,x+w/10},
                new int[]{y+h/4,y+h/2,y,y+h/2,y+h/4,y+h,y+h},7);
        g.setPaint(new GradientPaint(x,y,CREAM,x,y+h,new Color(196,128,38)));g.fillPolygon(p);
        g.setColor(new Color(88,44,33));g.setStroke(new BasicStroke(1));g.drawPolygon(p);
    }
    private static void drawDie(Graphics2D g, int x, int y, int size) {
        g.setColor(new Color(241,191,94));
        g.fillRoundRect(x-size/2,y-size/2,size,size,3,3);
        g.setColor(new Color(63,32,62));
        int dot=Math.max(2,size/5);
        for(int[] p:new int[][]{{-3,-3},{3,3},{0,0}})
            g.fillOval(x+p[0]-dot/2,y+p[1]-dot/2,dot,dot);
    }
    private static void drawClock(Graphics2D g,int x,int y,int r) {
        g.setColor(GOLD);g.setStroke(new BasicStroke(1.6f));
        g.drawOval(x-r,y-r,r*2,r*2);
        g.drawLine(x,y,x,y-r+3);g.drawLine(x,y,x+4,y+2);
    }
    private static void drawGear(Graphics2D g,int x,int y,int r) {
        Graphics2D z=(Graphics2D)g.create();z.translate(x,y);
        z.setColor(GOLD);z.setStroke(new BasicStroke(2f));
        z.drawOval(-r+2,-r+2,2*r-4,2*r-4);
        for(int i=0;i<8;i++){
            double a=i*Math.PI/4;
            z.drawLine((int)((r-1)*Math.cos(a)),(int)((r-1)*Math.sin(a)),
                    (int)((r+3)*Math.cos(a)),(int)((r+3)*Math.sin(a)));
        }
        z.fillOval(-2,-2,4,4);z.dispose();
    }
    private static class RoyalButton extends JButton {
        final boolean primary;double scale=1;
        RoyalButton(String s,boolean p) {super(s);primary=p;setOpaque(false);setContentAreaFilled(false);
            setBorderPainted(false);setFocusPainted(false);setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));}
        void setScale(double s){scale=s;repaint();}
        @Override protected void paintComponent(Graphics gr) {
            Graphics2D g=(Graphics2D)gr.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight();int arc=(int)(15*scale);
            if(primary) {
                g.setColor(new Color(245,175,62,70));g.fillRoundRect(0,0,w,h,arc,arc);
                g.setPaint(new GradientPaint(0,2,new Color(255,243,183),0,h,new Color(199,131,46)));
            } else g.setPaint(new GradientPaint(0,0,new Color(58,35,84),0,h,new Color(18,17,49)));
            g.fillRoundRect(3,3,w-6,h-6,arc,arc);
            g.setColor(getModel().isRollover()?new Color(255,250,193):GOLD);
            g.setStroke(new BasicStroke((float)Math.max(1,1.4*scale)));g.drawRoundRect(3,3,w-7,h-7,arc,arc);
            g.setColor(new Color(255,237,185,110));g.drawRoundRect(6,6,w-13,h-13,arc,arc);
            if(primary) {crown(g,(int)(75*scale),(h-(int)(24*scale))/2,(int)(26*scale));}
            else {
                if (getText().equals("GAME HISTORY")) drawClock(g,(int)(17*scale),h/2,(int)(7*scale));
                else drawGear(g,(int)(17*scale),h/2,(int)(7*scale));
            }
            g.setFont(new Font("SansSerif",Font.BOLD,Math.max(9,(int)((primary?19:11)*scale))));
            g.setColor(primary?new Color(32,20,42):CREAM);
            FontMetrics fm=g.getFontMetrics();int tx=(w-fm.stringWidth(getText()))/2+(primary?(int)(10*scale):0);
            if (!primary) tx=Math.max(tx,(int)(36*scale));
            g.drawString(getText(),tx,(h-fm.getHeight())/2+fm.getAscent());g.dispose();
        }
    }
    private static BufferedImage loadImage(String name) {
        try (InputStream in = HomeScreen.class.getResourceAsStream("/" + name)) {
            if (in != null) return ImageIO.read(in);
        } catch (IOException ignored) { }
        try { return ImageIO.read(new File("src/main/resources/" + name)); }
        catch (IOException ignored) { return null; }
    }

    private static class Card extends JComponent {
        private static final BufferedImage[] ICONS = {
            loadImage("easy_icon.png"),
            loadImage("medium_icon.png"),
            loadImage("hard_icon.png")
        };
        final String num,title,subtitle,detail;final int type;double scale=1;
        Card(String n,String t,String s,String d,int k){num=n;title=t;subtitle=s;detail=d;type=k;setOpaque(false);}
        void setScale(double s){scale=s;repaint();}
        @Override protected void paintComponent(Graphics gr){
            Graphics2D g=(Graphics2D)gr.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.scale(getWidth()/350.0,getHeight()/125.0);
            g.setPaint(new GradientPaint(0,0,new Color(54,30,76),350,125,new Color(40,19,62)));
            g.fillRoundRect(1,1,348,123,18,18);
            g.setColor(new Color(250,194,110,75));g.setStroke(new BasicStroke(5));g.drawRoundRect(3,3,344,119,17,17);
            g.setColor(GOLD);g.setStroke(new BasicStroke(1.2f));g.drawRoundRect(2,2,346,121,17,17);
            g.setColor(new Color(255,207,112,100));g.drawLine(18,83,333,83);
            g.setColor(type==0?new Color(84,211,145):type==1?GOLD:new Color(242,116,153));g.fillOval(322,13,9,9);
            g.setFont(new Font("Georgia",Font.BOLD,17));g.setColor(GOLD);g.drawString(num,20,23); sparkle(g,55,17,3);
            g.setFont(new Font("Georgia",Font.BOLD,21));g.setColor(CREAM);g.drawString(title,20,49);
            g.setFont(new Font("SansSerif",Font.PLAIN,12));g.drawString(subtitle,20,66);
            g.setFont(new Font("SansSerif",Font.BOLD,10));g.setColor(GOLD);drawDie(g,25,102,11);
            g.setColor(new Color(255,208,111));
            g.drawString(detail,43,107);
            // Transparent difficulty badges: genuine PNG assets, not painted placeholders.
            BufferedImage icon = ICONS[type];
            if (icon != null) {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
int maxW = 112;
int maxH = 78;

// Preserve the original image proportions
double ratio = Math.min(
    maxW / (double) icon.getWidth(),
    maxH / (double) icon.getHeight()
);

int imgW = (int) Math.round(icon.getWidth() * ratio);
int imgH = (int) Math.round(icon.getHeight() * ratio);

// Center the icon in the right side of the card
int x = 290 - imgW / 2;
int y = 45 - imgH / 2;

g.drawImage(icon, x, y, imgW, imgH, null);            } else {
                // A subtle fallback if resources have not yet been copied.
                g.setColor(new Color(236,187,91,120));
                crown(g,250,29,58);
            }
            g.dispose();
        }
    }
    private static class Board extends JComponent {
        private static final Color[] P={new Color(168,51,73),new Color(45,143,110),new Color(47,85,183),new Color(219,166,56)};
        @Override protected void paintComponent(Graphics gr){
            Graphics2D g=(Graphics2D)gr.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            double s=Math.min(getWidth(),getHeight())/540.0;
            g.translate((getWidth()-540*s)/2,(getHeight()-540*s)/2);g.scale(s,s);
            g.setColor(new Color(247,183,76,55));g.fillRoundRect(-3,-3,546,546,28,28);
            g.setColor(new Color(255,206,104));g.fillRoundRect(0,0,540,540,25,25);
            g.setColor(new Color(22,22,48));g.fillRoundRect(7,7,526,526,20,20);
            int[] bx={10,324,10,324},by={10,10,324,324};
            for(int q=0;q<4;q++){
                g.setPaint(new GradientPaint(bx[q],by[q],P[q].brighter(),bx[q]+205,by[q]+205,P[q].darker()));
                g.fillRoundRect(bx[q],by[q],206,206,19,19);
                g.setColor(new Color(255,218,132));g.setStroke(new BasicStroke(3));
                g.drawRoundRect(bx[q]+5,by[q]+5,196,196,16,16);
                for(int i=0;i<4;i++){
                    int x=bx[q]+52+(i%2)*102,y=by[q]+52+(i/2)*102;
                    g.setColor(new Color(19,21,40,210));g.fillOval(x-20,y-20,40,40);
                    g.setColor(GOLD);g.setStroke(new BasicStroke(2));g.drawOval(x-20,y-20,40,40);
                }
                crown(g,bx[q]+86,by[q]+82,36);
            }
            // 15x15 Ludo track, real Java-drawn cells.
            for(int row=0;row<15;row++)for(int col=0;col<15;col++){
                if(!((row>=6&&row<=8)||(col>=6&&col<=8)))continue;
                if(row>=6&&row<=8&&col>=6&&col<=8)continue;
                Color c=new Color(255,247,224);
                if(col==7&&row>=1&&row<=5)c=P[1];
                if(row==7&&col>=1&&col<=5)c=P[0];
                if(col==7&&row>=9&&row<=13)c=P[2];
                if(row==7&&col>=9&&col<=13)c=P[3];
                int x=9+col*35,y=9+row*35;
                g.setColor(c);g.fillRect(x,y,34,34);
                g.setColor(new Color(32,30,46));g.setStroke(new BasicStroke(1.6f));g.drawRect(x,y,34,34);
            }
            Polygon t=new Polygon(new int[]{219,321,270},new int[]{219,219,270},3);
            Polygon r=new Polygon(new int[]{321,321,270},new int[]{219,321,270},3);
            Polygon b=new Polygon(new int[]{219,321,270},new int[]{321,321,270},3);
            Polygon l=new Polygon(new int[]{219,219,270},new int[]{219,321,270},3);
            g.setColor(P[1]);g.fillPolygon(t);g.setColor(P[3]);g.fillPolygon(r);
            g.setColor(P[2]);g.fillPolygon(b);g.setColor(P[0]);g.fillPolygon(l);
            g.setColor(new Color(20,20,43));g.fillOval(243,243,54,54);
            g.setColor(GOLD);g.setStroke(new BasicStroke(4));g.drawOval(243,243,54,54);
            crown(g,254,255,32);g.dispose();
        }
    }
}
