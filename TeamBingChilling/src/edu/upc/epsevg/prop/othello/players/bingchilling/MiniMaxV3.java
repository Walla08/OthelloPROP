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
            return heuristica(s);
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

   public int heuristica(GameStatus s) {
        int FichasMias = 0;
        int FichasKk = 0;
        double ScoreFichasTot = 0;
        double ScoreEsquinas = 0;
        double ScoreTriangulo = 0;
        double ScoreFichas = 0;

        CellType YoSoyYo = s.getCurrentPlayer();
        int[][] Tablero = {
            {20, -3, 11, 8, 8, 11, -3, 20},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {20, -3, 11, 8, 8, 11, -3, 20}
        };
        CellType CriminalMalo = CellType.opposite(YoSoyYo);

        // Piece difference, frontier disks and disk squares
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(i,j) == YoSoyYo) {
                    ScoreFichas += Tablero[i][j];
                    FichasMias++;
                } else if (s.getPos(i,j) == CriminalMalo) {
                    ScoreFichas -= Tablero[i][j];
                    FichasKk++;
                }
            }
        }

        if (FichasMias > FichasKk) {
            ScoreFichasTot = (100.0 * FichasMias) / (FichasMias + FichasKk);
        } else if (FichasMias < FichasKk) {
            ScoreFichasTot = -(100.0 * FichasKk) / (FichasMias + FichasKk);
        } else {
            ScoreFichasTot = 0;
        }
        
        // Corner occupancy
        FichasMias = FichasKk = 0;
        if (s.getPos(0,0) == YoSoyYo) {
                FichasMias += 1;
            }
            else if (s.getPos(0,0) == (CriminalMalo)) {
                FichasKk += 1;
            }
            if (s.getPos(0,s.getSize()-1) == YoSoyYo) {
                FichasMias += 1;
            }
            else if (s.getPos(0,s.getSize()-1) == (CriminalMalo)) {
                FichasKk += 1;
            }
            if (s.getPos(s.getSize()-1,0) == YoSoyYo) {
                FichasMias += 1;
            }
            else if (s.getPos(s.getSize()-1,0) == (CriminalMalo)) {
                FichasKk += 1;
            }
            if (s.getPos(s.getSize()-1, s.getSize()-1) == YoSoyYo) {
                FichasMias += 1;
            }
            else if (s.getPos(s.getSize()-1, s.getSize()-1) == (CriminalMalo)) {
                FichasKk += 1;
            }
            
        ScoreEsquinas = 25 * (FichasMias - FichasKk);
        
        // Corner closeness
        FichasMias = FichasKk = 0;
        if ((s.getPos(0,0) == CellType.EMPTY)) {
            if (s.getPos(0,1) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(0,1) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(1,1) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(1,1) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(1,0) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(1,0) == CriminalMalo) {
                FichasKk++;
            }
        }
        if ((s.getPos(0,s.getSize()-1) == CellType.EMPTY)) {
            if (s.getPos(0,6) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(0,6) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(1,6) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(1,6) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(1,s.getSize()-1) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(1,s.getSize()-1) == CriminalMalo) {
                FichasKk++;
            }
        }
        if ((s.getPos(s.getSize()-1,0) == CellType.EMPTY)) {
            if (s.getPos(s.getSize()-1,1) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(s.getSize()-1,1) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(6,1) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(6,1) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(6,0) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(6,0) == CriminalMalo) {
                FichasKk++;
            }
        }
        if ((s.getPos(s.getSize()-1,s.getSize()-1) == CellType.EMPTY)) {
            if (s.getPos(6,s.getSize()-1) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(6,s.getSize()-1) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(6,6) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(6,6) == CriminalMalo) {
                FichasKk++;
            }
            if (s.getPos(s.getSize()-1,6) == YoSoyYo) {
                FichasMias++;
            } else if (s.getPos(s.getSize()-1,6) == CriminalMalo) {
                FichasKk++;
            }
        }
        if(s.getPos(0,0) == YoSoyYo || s.getPos(0,s.getSize()-1) == YoSoyYo || s.getPos(s.getSize()-1,0) == YoSoyYo
                || s.getPos(s.getSize()-1,s.getSize()-1) == YoSoyYo){
            ScoreTriangulo = 1323 * (FichasMias - FichasKk);
       
        } else{
            ScoreTriangulo = -25 * (FichasMias - FichasKk);
        }

        // Final weighted score
        int heuris = (int) ((10 * ScoreFichasTot + 1000 * ScoreEsquinas + 500 * ScoreTriangulo + 10 * ScoreFichas));

        return heuris ;
    }


}
