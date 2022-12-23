/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.othello.players.bingchilling;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Walter&Yisus
 */
public class PlayerMiniMax implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    private int profundidad;
       private CellType myType;
    private CellType hisType;
    
    private static final int GANADOR = 999999;
    private static final int PERDEDOR = -999999;

    public PlayerMiniMax(String name, int profundidad) {
        this.name = name;
        this.profundidad = profundidad;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @param profundidad
     * @return el moviment que fa el jugador.
     */
    public Move move(GameStatus s) {

        ArrayList<Point> moves =  s.getMoves();
        int max = Integer.MIN_VALUE;
        Point millormov = null;
        
        if(moves.isEmpty())
        {
            // no podem moure, el moviment (de tipus Point) es passa null.
            return new Move(null, 0L,0,  SearchType.RANDOM); 
        } else {
            for(int movi = 0; movi < moves.size(); ++movi){
                GameStatus x = new GameStatus(s);
                if(x.canMove(moves.get(movi), s.getCurrentPlayer())){
                    x.movePiece(moves.get(movi));
                    int actual = minimax(x, (profundidad - 1), Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    if (actual > max) {
                    max = actual;
                    millormov = moves.get(movi);
                    }
                }
            }         
        }
        System.out.println("Mov final: "+millormov);
        return new Move( millormov,0 , 0, SearchType.RANDOM);
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Helao(" + name + ")";
    }
    
     public int minimax(GameStatus s, int profunditat, int alpha, int beta, boolean esBendicion) {
        int valor;
        ArrayList<Point> moves =  s.getMoves();
        // Si la profundidad ya ha llegado a su limite no habra mas movimientos
        // posibles, devolvemos la heurisica del tablero.
        if (profunditat == 0 || s.currentPlayerCanMove() == false) {
            return heuristica(s,moves);
        }
        // Inicializamos variables en funcion si es min o max
        CellType c = s.getCurrentPlayer();
        
        if (esBendicion) { // Max
            // Establecemos el valor inicial como al minimo asi cualquier valor sera
            // superior.
            valor = Integer.MIN_VALUE;
        } else { // Min
            // Establecemos el valor inicial como al maximo asi cualquier valor sera
            // inferior.
            valor = Integer.MAX_VALUE;
            // Obtenemos el color del oponente que es el contrario al nuestro.
            /*if (currentp.name() == "HELAO") {
                oponent = "?";
            } else {
                oponent = "?";
            }*/
        }
        // Para cada columna del tablero
        for (int movi = 0; movi < moves.size(); movi++) {
            // Copia del tablero
            GameStatus x = new GameStatus(s);
            // Si se puede realizar un movimiento en la columna que estamos posicionados
            if (x.canMove(moves.get(movi), s.getCurrentPlayer())) {
                // Sumamos 1 a l numero de casos explorados

                if (esBendicion) {
                    // Realizamos el movimiento seleccionado con el color del jugador.
                    x.movePiece(moves.get(movi));

                    if (x.checkGameOver() && x.GetWinner() == s.getCurrentPlayer()) {
                        return GANADOR;
                    }
                    // Calculamos heuristica. Si esta es supererior al valor actual, substituimos
                    // valor por esta.
                    valor = Math.max(valor, minimax(x, profunditat - 1, alpha, beta, false));

                    // Poda alfa-beta
                    if (beta <= valor) {
                        return valor;
                    }

                    alpha = Math.max(valor, alpha);
                } else { // min
                    // Realizamos el movimiento con el color del oponente ya que estamos en la capa
                    // Min
                    x.movePiece(moves.get(movi));

                    if (x.checkGameOver() && x.GetWinner() == s.getCurrentPlayer()) {
                        return PERDEDOR;
                    }
                    // Calculamos heuristica. Si esta es menor al valor actual substituimos por esta
                    valor = Math.min(valor, minimax(x, profunditat - 1, alpha, beta, true));

                    // Realitzem la poda alpha-beta
                    if (valor <= alpha) {
                        return valor;
                    }

                    beta = Math.min(valor, beta);
                }
            }
        }
        // Devolvemos heuristica
        return valor;

    }

   public int heuristica(GameStatus s, ArrayList<Point> moves) {
        int heuris = 0;
        int max = Integer.MIN_VALUE;
        Point a = new Point(0,0);
        Point b = new Point (0,7);
        Point c = new Point (7,0);
        Point d = new Point (7,7);
        for (int movi = 0; movi < moves.size(); movi++) {
            if(s.canMove(moves.get(movi), s.getCurrentPlayer())){
                System.out.println("Movimiento sexy: "+moves.get(movi));
                if (moves.get(movi).x == a.x && moves.get(movi).y == a.y){
                    heuris = 1000;
                    System.out.println("Esquina1");
                    break;
                }
                else if (moves.get(movi).x == b.x && moves.get(movi).y == b.y){
                    heuris = 1000;
                    System.out.println("Esquina2");
                    break;
                }
               else if (moves.get(movi).x == c.x && moves.get(movi).y == c.y){
                    heuris = 1000;
                    System.out.println("Esquina3");
                    break;
                }
               else if (moves.get(movi).x == d.x && moves.get(movi).y == d.y){
                    heuris = 1000;
                    System.out.println("Esquina4");
                }
                else{

                    heuris = s.getScore(s.getCurrentPlayer());

                     //System.out.println("heuristica "+heuris+""+s.getCurrentPlayer());
                }
                if (heuris>max){
                    max = heuris;
                }
            }
        }
        System.out.println("Heuristica: "+heuris);
        return heuris;
    }


}