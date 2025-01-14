
package theGame;
import theGame.XO.*;

/**
 *
 * @author tasneem
 */
public class GameBoard {
    
    private final XO[][] gameBoard;
    private XO winning;
    private final int boardSize = 3;
    private boolean cross;
    private boolean gameOver;
    private int availMoves;
    private boolean anotherGame = true;
    
    public GameBoard(){
    
        gameBoard = new XO[boardSize][boardSize];
        cross = anotherGame;
        gameOver = false;
        winning = XO.B;
            initCase();
        availMoves = boardSize * boardSize;
    }
    


    public XO getWining() {
        return winning;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public boolean isCross() {
        return cross;
    }

    public boolean isGameOver() {
        return gameOver;
    }
    
    
    public boolean isCellMarked(int row, int column){
    
        return gameBoard[row][column].isMarked();
    }
    
    public void switchPlayer(){cross =! cross;}
    
    public boolean isAvailMoves(){
    
        return availMoves > 0;
    }
    
    public XO markedAt(int row, int column){
        return gameBoard[row][column];
    }
    
    public void markAt(int row, int column, XO symbol){
    
        gameBoard[row][column] = symbol;
        
    }
    
    public XO ComputeWinner(int sum){
        // bngm3 l ASCII values bta3t l X wl O
        int Xwinner = XO.X.getSymbol() * boardSize;
        int Owinner = XO.O.getSymbol() * boardSize;
        
        if(sum == Xwinner){
            gameOver = true;
            winning = XO.X;
            return winning;
        }else if(sum == Owinner){
            gameOver = true;
            winning = XO.O;
            return winning;
        }else {
            return XO.B;
        }
    }
    
    
    
    
    public void initCase(){
        
        // bnCreate board fadya
    
        for (int row = 0; row <boardSize; row++) {
        
            for (int column = 0; column <boardSize; column++){
            
                gameBoard[row][column] = XO.B; 
            }
        
        }
        
    }
    
    public boolean placeXO(int row, int column){
    
        if(row < 0 || row >= boardSize || column < 0 || column >=boardSize
                || isCellMarked(row, column) || gameOver){return false;}
        
        availMoves--;
        gameBoard[row][column] = cross ? XO.X : XO.O;
        switchPlayer();
        winCheck(row, column);
        
        return true;
    
    }
    
    
    // case 1: l row sabt w bLoop 3la l column
    public void case1(int row, int column){
    
        int sum = 0;
        for (int i = 0; i < boardSize; i++) {
            sum += markedAt(row, i).getSymbol();
        }
        if (ComputeWinner(sum) != XO.B) {
            System.out.println(winning + " wins on case 1");
        }
    
    }
    
    // case 2: l column sabt w bLoop 3la l row
    public void case2(int row, int column){
    
        int sum = 0;
        sum = 0;
        for (int i = 0; i < boardSize; i++) {
            sum += markedAt(i, column).getSymbol();
        }
        if (ComputeWinner(sum) != XO.B) {
            System.out.println(winning + " wins on case 2");
        }
    
    }
    

    // diagonal cases
        
    // case3: TopLeft to bottomRight
    public void case3(int row, int column){
    
        int sum = 0;
        sum = 0;
        for (int i = 0; i < boardSize; i++) {
            sum += markedAt(i, i).getSymbol();
        }
        if (ComputeWinner(sum) != XO.B) {
            System.out.println(winning + " wins on case 3");
            return;
        }
    
    }
    
    // case 4: TopRight to bottomLeft
    public void case4(int row, int column){
    
        int sum = 0;
        sum = 0;
        int index = boardSize - 1;
        for (int i = 0; i <= index; i++) {
            sum += markedAt(i, index - i).getSymbol();
        }
        if (ComputeWinner(sum) != XO.B) {
            System.out.println(winning + " wins on case 4");
            return;
        }
    
    }
    
    public void winCheck(int row, int column) {
        
        case1(row, column);
        case2(row, column);
        case3(row, column);
        case4(row, column);
        
        switchPlayer();
        if (!isAvailMoves()) {
            gameOver = true;
            System.out.println("GAME OVER!");
        }
    
    }    

    // Evaluate the game board to calculate a score for the AI
    private int evaluate() {
         for (int row = 0; row < boardSize; row++) {
             // Row check
             if (gameBoard[row][0] == gameBoard[row][1] && gameBoard[row][1] == gameBoard[row][2]) {
                 if (gameBoard[row][0] == XO.X) return 10;
                 if (gameBoard[row][0] == XO.O) return -10;
             }

             // Column check
             if (gameBoard[0][row] == gameBoard[1][row] && gameBoard[1][row] == gameBoard[2][row]) {
                 if (gameBoard[0][row] == XO.X) return 10;
                 if (gameBoard[0][row] == XO.O) return -10;
             }
         }

         // Diagonal check
         if (gameBoard[0][0] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][2]) {
             if (gameBoard[0][0] == XO.X) return 10;
             if (gameBoard[0][0] == XO.O) return -10;
         }
         if (gameBoard[0][2] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][0]) {
             if (gameBoard[0][2] == XO.X) return 10;
             if (gameBoard[0][2] == XO.O) return -10;
         }

         return 0; // No winner
    }

    // Minimax function
    public int minimax(int depth, boolean isMaximizing) {
        int score = evaluate();
        if (score == 10 || score == -10) return score;
        if (!isAvailMoves()) return 0; // Draw

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (!isCellMarked(row, col)) {
                        gameBoard[row][col] = XO.X; // AI move
                        availMoves--;
                        best = Math.max(best, minimax(depth + 1, false));
                        gameBoard[row][col] = XO.B; // Undo move
                        availMoves++;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (!isCellMarked(row, col)) {
                        gameBoard[row][col] = XO.O; // Player move
                        availMoves--;
                        best = Math.min(best, minimax(depth + 1, true));
                        gameBoard[row][col] = XO.B; // Undo move
                        availMoves++;
                    }
                }
            }
            return best;
        }
    }

    // Find the best move for AI
    public int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (!isCellMarked(row, col)) {
                    gameBoard[row][col] = XO.X; // AI move
                    availMoves--;
                    int moveScore = minimax(0, false);
                    gameBoard[row][col] = XO.B; // Undo move
                    availMoves++;
                    if (moveScore > bestScore) {
                        bestScore = moveScore;
                        bestMove[0] = row;
                        bestMove[1] = col;
                    }
                }
            }
        }

        return bestMove;
    }
}
