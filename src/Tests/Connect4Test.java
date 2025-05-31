package Tests;

import Connect4.*;
import BaseGame.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class Connect4Test {

    private Connect4 game;


    @BeforeEach
    void setUp() {
        game = new Connect4();
    }


    private Move dropPiece(int col, Player player) {
        int row = game.findNextAvailableRow(col);
        Move move = new Move(row, col);
        game.makeMove(move, player);
        return move;
    }

    @Test
    void testMakeMoveAndPlayerSwitch() {

        assertEquals(Player.HUMAN, game.getCurrentPlayer(), "Game should start as HUMAN.");


        dropPiece(0, Player.HUMAN);
        assertEquals(Player.AI, game.getCurrentPlayer(), "After the move of HUMAN, the current player should be AI.");
        assertEquals(Player.HUMAN, game.getCell(Connect4.ROWS - 1, 0), "HUMAN piece should be at (5,0).");


        dropPiece(1, Player.AI);
        assertEquals(Player.HUMAN, game.getCurrentPlayer(), "After the move of AI, the current player should be HUMAN.");
        assertEquals(Player.AI, game.getCell(Connect4.ROWS - 1, 1), "AI piece should be at (5,1).");
    }


    @Test
    void testMakeMoveOnFullColumnThrowsException() {

        for (int i = 0; i < Connect4.ROWS; i++) {
            dropPiece(0, Player.HUMAN);


            game.setCurrentPlayer(game.getOpponent(game.getCurrentPlayer()));
        }


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            dropPiece(0, Player.HUMAN);
        }, "IllegalArgumentException should be thrown because of a full column.");

        assertTrue(thrown.getMessage().contains("Column is full"), "Error message should indicate a full collumn.");
    }


    @Test
    void testHorizontalWinCondition() {
        dropPiece(0, Player.HUMAN);
        dropPiece(0, Player.AI);
        dropPiece(1, Player.HUMAN);
        dropPiece(1, Player.AI);
        dropPiece(2, Player.HUMAN);
        dropPiece(2, Player.AI);

        game.setCurrentPlayer(Player.HUMAN);
        dropPiece(3, Player.HUMAN);

        assertTrue(game.isGameOver(), "Game should be over.");
        assertEquals(Player.HUMAN, game.getWinner(), "HUMAN should win with a horizontal.");
    }


    @Test
    void testVerticalWinCondition() {
        dropPiece(0, Player.HUMAN);
        dropPiece(1, Player.AI);
        dropPiece(0, Player.HUMAN);
        dropPiece(1, Player.AI);
        dropPiece(0, Player.HUMAN);
        dropPiece(1, Player.AI);

        dropPiece(0, Player.HUMAN);

        assertTrue(game.isGameOver(), "Game should be over.");
        assertEquals(Player.HUMAN, game.getWinner(), "HUMAN should win with a vertical.");
    }

    @Test
    void testDrawCondition() {
        for (int r = Connect4.ROWS - 1; r >= 0; r--) {
            for (int c = 0; c < Connect4.COLS; c++) {
                Player playerToMove = (r + c) % 2 == 0 ? Player.HUMAN : Player.AI;
                int actualRow = game.findNextAvailableRow(c);
                if (actualRow != -1) {
                    game.makeMove(new Move(actualRow, c), playerToMove);
                    game.setCurrentPlayer(game.getOpponent(playerToMove));
                }
            }
        }

        assertTrue(game.getValidMoves().isEmpty(), "Board should be full.");
        assertTrue(game.isGameOver(), "Game should be over.");
        assertEquals(Player.NONE, game.getWinner(), "The game should result in a draw.");
    }
}