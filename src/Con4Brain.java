public class Con4Brain {
    public static int findMove(char[][] board, char aiMove) {
        int bestScore = -999999999;
        int bestCollumn = 0;
        for (int i = 0; i < 7; i++) {
            int row = checkRowAvalability(board, i);
            if (row != -1) {
                board[row][i] = aiMove;
            }
        }
        return 0;
    }


    private static int checkRowAvalability(char[][] board, int column) {
        for (int i = 5; i >=0; i--) {
            if(board[i][column] == ' ') {
                return i;
            }
            }
        return -1;
    }

}
