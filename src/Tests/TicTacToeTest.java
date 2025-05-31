package Tests;

import BaseGame.*;
import TicTacToe.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TicTacToeTest {

    private TicTacToe game;

    @BeforeEach
    void setUp() {
        game = new TicTacToe();
    }

    @Test
    void testInitialBoardStateAndCurrentPlayer() {
        for (int r = 0; r < TicTacToe.BOARD_SIZE; r++) {
            for (int c = 0; c < TicTacToe.BOARD_SIZE; c++) {
                assertEquals(Player.NONE, game.getCell(r, c), "All cells should be empty.");
            }
        }
        assertEquals(Player.HUMAN, game.getCurrentPlayer(), "First player should be HUMAN.");
    }


    @Test
    void testMakeValidMoveAndSwitchPlayer() {
        Move move = new Move(0, 0);
        game.makeMove(move, Player.HUMAN);

        assertEquals(Player.HUMAN, game.getCell(0, 0), "Cell (0,0) should be owned by HUMAN.");
        assertEquals(Player.AI, game.getCurrentPlayer(), "After the move of HUMAN, the active player should be AI.");

        Move aiMove = new Move(1, 1);
        game.makeMove(aiMove, Player.AI);
        assertEquals(Player.AI, game.getCell(1, 1), "Cell (1,1) should be owned by AI.");
        assertEquals(Player.HUMAN, game.getCurrentPlayer(), "After the move of AI, the player should be HUMAN.");
    }


    @Test

    void testIsValidMoveForOccupiedCell() {
        Move move = new Move(0, 0);
        game.makeMove(move, Player.HUMAN);

        assertFalse(game.isValidMove(move), "Move at (0,0) shouldn't be valid, because it is occupied.");
    }


    @Test
    void testHorizontalWinCondition() {

        game.makeMove(new Move(0, 0), Player.HUMAN);
        game.makeMove(new Move(1, 0), Player.AI);
        game.makeMove(new Move(0, 1), Player.HUMAN);
        game.makeMove(new Move(1, 1), Player.AI);
        game.makeMove(new Move(0, 2), Player.HUMAN);

        assertTrue(game.isGameOver(), "Game should be over.");
        assertEquals(Player.HUMAN, game.getWinner(), "HUMAN should win with a horizontal.");
    }


    @Test
    void testDiagonalWinConditionTopLeft() {

        game.setCurrentPlayer(Player.AI);
        game.makeMove(new Move(0, 0), Player.AI);
        game.makeMove(new Move(0, 1), Player.HUMAN);
        game.makeMove(new Move(1, 1), Player.AI);
        game.makeMove(new Move(0, 2), Player.HUMAN);
        game.makeMove(new Move(2, 2), Player.AI);

        assertTrue(game.isGameOver(), "Game should be over.");
        assertEquals(Player.AI, game.getWinner(), "AI should win with a diagonal.");
    }


    @Test
    void testDrawCondition() {

        game.makeMove(new Move(0, 0), Player.HUMAN); // H
        game.makeMove(new Move(0, 1), Player.AI);    // A
        game.makeMove(new Move(0, 2), Player.HUMAN); // H

        game.makeMove(new Move(1, 1), Player.AI);    // A
        game.makeMove(new Move(1, 0), Player.HUMAN); // H
        game.makeMove(new Move(1, 2), Player.AI);    // A

        game.makeMove(new Move(2, 1), Player.HUMAN); // H
        game.makeMove(new Move(2, 2), Player.AI);    // A
        game.makeMove(new Move(2, 0), Player.HUMAN); // H

        assertTrue(game.getValidMoves().isEmpty(), "Board should be full");
        assertTrue(game.isGameOver(), "Game should be over.");
        assertEquals(Player.NONE, game.getWinner(), "Game should be a draw.");
    }

    @Test
    void testUndoMove() {
        Move move1 = new Move(0, 0);
        game.makeMove(move1, Player.HUMAN);

        assertEquals(Player.HUMAN, game.getCell(0, 0), "Cell (0,0) should be owned by HUMAN.");
        assertEquals(Player.AI, game.getCurrentPlayer(), "Current player should be AI.");

        game.undoMove(move1);

        assertEquals(Player.NONE, game.getCell(0, 0), "Cell (0,0) should be empty after the undo.");
        assertEquals(Player.HUMAN, game.getCurrentPlayer(), "Current player should be HUMAN again.");


        game.undoMove(null);
        assertEquals(Player.HUMAN, game.getCurrentPlayer(), "Undo of null move shouldn't change player.");
    }
}