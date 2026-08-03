package main;
import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import piece.Pawn;
import piece.Knight;
import piece.Rook;
import piece.Bishop;
import piece.Queen;
import piece.King;
import piece.Piece;
public class GamePanel extends JPanel implements Runnable{
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();
    Piece activeP;
    
    
    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;


    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setPieces();
        copyPieces(pieces, simPieces);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    //GAME LOOP
    @Override
    public void run(){
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            
            if(delta>=1){
                update();
                repaint();
                delta--;
            }
        }
    }

    public void launchGame(){
        gameThread = new Thread (this);
        gameThread.start();

    }

    public void setPieces(){
        // White team
        pieces.add(new Pawn(WHITE, 0,6));
        pieces.add(new Pawn(WHITE, 1,6));
        pieces.add(new Pawn(WHITE, 2,6));
        pieces.add(new Pawn(WHITE, 3,6));
        pieces.add(new Pawn(WHITE, 4,6));
        pieces.add(new Pawn(WHITE, 5,6));
        pieces.add(new Pawn(WHITE, 6,6));
        pieces.add(new Pawn(WHITE, 7,6));
        pieces.add(new Knight(WHITE, 1,7));
        pieces.add(new Knight(WHITE, 6,7));
        pieces.add(new Rook(WHITE, 0,7));
        pieces.add(new Rook(WHITE, 7,7));
        pieces.add(new Bishop(WHITE, 2,7));
        pieces.add(new Bishop(WHITE, 5,7));
        pieces.add(new Queen(WHITE, 3,7));
        pieces.add(new King(WHITE, 4,7));

        // White team
        pieces.add(new Pawn(BLACK, 0,1));
        pieces.add(new Pawn(BLACK, 1,1));
        pieces.add(new Pawn(BLACK, 2,1));
        pieces.add(new Pawn(BLACK, 3,1));
        pieces.add(new Pawn(BLACK, 4,1));
        pieces.add(new Pawn(BLACK, 5,1));
        pieces.add(new Pawn(BLACK, 6,1));
        pieces.add(new Pawn(BLACK, 7,1));
        pieces.add(new Knight(BLACK, 1,0));
        pieces.add(new Knight(BLACK, 6,0));
        pieces.add(new Rook(BLACK, 0,0));
        pieces.add(new Rook(BLACK, 7,0));
        pieces.add(new Bishop(BLACK, 2,0));
        pieces.add(new Bishop(BLACK, 5,0));
        pieces.add(new Queen(BLACK, 3,0));
        pieces.add(new King(BLACK, 4,0));
        
    }
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i =0; i<source.size(); i++){
            target.add(source.get(i));
        }
    }
    private void update(){
        if(mouse.pressed){
            //if the acrtive P i snull, check if you can pick up a piece
            if(activeP==null){
                for(Piece piece :simPieces){
                    //if the mouse is on an ally piece, pick it up as the active piece
                    if(piece.color == currentColor && piece.col== mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                        activeP = piece;
                    }
                }
                
            }
            else{
                // IF the player is holding a piece, simulate hte phase
                simulate();
            }
        }

    }

    private void simulate(){
        //If a piece is being held, update its position
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getCol(activeP.y);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2d = (Graphics2D)g;
        
        // BOARD
        board.draw(graphics2d);

        // PIECES
        for(Piece p : simPieces){
            p.draw(graphics2d);
        }
        if(activeP != null){
            graphics2d.setColor(Color.white);
            graphics2d.setComposite((AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)));
            graphics2d.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
            graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            //draw the active piece in the end so it wont be hidden by the boardo r hte colored square
            activeP.draw(graphics2d);

        }
        
    }
}
