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
public class PlayerID implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    int profundidad = 1;
    boolean timeout;
    int prof_alc = 0;
    
    private static final int GANADOR = 999999;
    private static final int PERDEDOR = -999999;

    public PlayerID(String name) {
        this.name = name;
    }

    @Override
    public void timeout() {
        timeout = true;
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
        timeout = false;
        this.prof_alc = 0;
        ArrayList<Point> moves =  s.getMoves();
        int max = Integer.MIN_VALUE;
        Point millormov = null;
        int cont = 0;
        if (timeout) this.profundidad = 1;

        
        if(moves.isEmpty() && timeout)
        {
            // no podem moure, el moviment (de tipus Point) es passa null.
            return new Move(null, 0L,0,  SearchType.RANDOM); 
        } else {
            while(!timeout){
                System.out.println("ABABABA");
                for(int movi = 0; movi < moves.size(); ++movi){
                    GameStatus x = new GameStatus(s);
                    if(x.canMove(moves.get(movi), s.getCurrentPlayer())){
                        x.movePiece(moves.get(movi));
                        System.out.println(this.profundidad);
                        int actual = minimax(x, this.profundidad, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                        ++this.profundidad;

                        if (actual > max) {
                            max = actual;
                            cont = movi;
                            millormov = moves.get(cont);

                        }
                    }
                    //timeout=true;
                }
            }
        }
        return new Move( millormov,0 , this.profundidad, SearchType.RANDOM);
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
        if (profunditat == 0 || s.currentPlayerCanMove() == false || timeout) {
            System.out.println("heu");
            return heuristica(s, moves);
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
        /*Point a = new Point(0,0);
        Point b = new Point (0,7);
        Point c = new Point (7,0);
        Point d = new Point (7,7);
        for (int movi = 0; movi < moves.size(); movi++) {
            if (moves.get(movi) == a || moves.get(movi) == b || moves.get(movi) == c || moves.get(movi) == d){
                heuris = 100000;
            }
            /*else if (moves.get(movi).){
                heuris = 1000;
            }
            else{*/
                heuris = s.getScore(s.getCurrentPlayer());
            //}
        //}
        return heuris;
    }


}

