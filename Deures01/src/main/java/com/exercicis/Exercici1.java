package com.exercicis;
import java.util.Random;
import java.util.Scanner;

/*
   Implementa una versió simplificada del joc 2048 en Java.
   
   - Constants i variables globals:
     > `SIZE` defineix la mida del taulell (4x4),
     > `board` representa el taulell com una matriu de enters
     > `random` s'utilitza per generar números i posicions aleatòries.

   - Mètodes:
     > `clearScreen()`: Neteja la consola per actualitzar la visualització del joc.
     > `printBoard()`: Mostra el taulell en format gràfic amb les caselles numerades.
     > `spawnTile()`: Afegeix una nova fitxa (2 o 4) en una casella buida del taulell.
     > `moveLeft`, `moveRight`, `moveUp`, `moveDown`: Implementen els moviments 
       del jugador amb les regles del joc, com moure fitxes, combinar números iguals
       i omplir els espais buits.
     > `isGameFinished()`: Determina si el joc ha acabat (victòria, derrota o continua).
     > `play(Scanner scanner)`: Gestiona el bucle principal del joc, demanant moviments
       al jugador i aplicant-los fins que s'acabi el joc.
     > `main(String[] args)`: Punt d'entrada del programa per inicialitzar el joc.
   
   Taulell:
     > El taulell està representat per una matriu de mida 4x4 (`board`), on cada casella
     conté un número (2, 4, ...), o 0 si és buida.
     > Els moviments possibles són cap a l'esquerra, dreta, amunt o avall. 
       A cada moviment:
         1. Les fitxes es desplacen fins que trobin un obstacle (una altra fitxa o el límit).
         2. Les fitxes adjacents amb el mateix valor es combinen, sumant el seu valor.
         3. Les caselles buides es reomplen després del moviment.
     > Després de cada moviment vàlid, es genera una nova fitxa (2 o 4) en una casella buida.
     > El joc acaba quan el jugador aconsegueix una fitxa amb valor 128 (victòria) o
       quan no queden moviments possibles (derrota).
   
   Funcionament del joc:
     1. Inicialment, el taulell conté dues fitxes amb valors aleatoris (2 o 4).
     2. El jugador introdueix un moviment (`left`, `up`, `right`, `down`) i el taulell
        s'actualitza segons les regles descrites.
     3. El joc continua fins que el jugador guanyi o perdi.
 */

public class Exercici1 {

    public static final int SIZE = 4;
    public static int[][] board = new int[SIZE][SIZE];
    public static final Random random = new Random();

    // Neteja la consola tenint en compte el sistema operatiu
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Dibuixa el taulell amb aquest estil, els 0 deixen la cel·la buida:
     * 
+----+----+----+----+
|   2|  16|    |   2|
+----+----+----+----+
|   8|    |    |    |
+----+----+----+----+
|   2|   2|    |    |
+----+----+----+----+
|    |    |    |    |
+----+----+----+----+
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPrintEmptyBoard"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPrintBoardWithSingleNumber"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPrintBoardWithMultipleNumbers"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPrintBoardWithFullRow"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPrintBoardWithLargeNumbers"
     */
    public static void printBoard() {
        System.out.println("+----+----+----+----+");
        for (int[] fila : board) {
            for (int celda : fila) {
                if ( celda == 0) {
                    System.out.printf("|    ", celda == 0 ? "" : celda);
                } else {
                    System.out.printf("|%4d", celda == 0 ? "" : celda);
                }
            }
            System.out.println("|");
            System.out.println("+----+----+----+----+");
        }
    }

    /**
     * Genera una nova fitxa en una posició
     * buida del taulell "board"
     * La fitxa té:
     * - 10% de probabilitat de generar un 4
     * - 90% de probabilitat de generar un 2
     * 
     * @test ./runTest.sh "com.exercicis.TestExercici1#testSpawnAddToEmptyBoard"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testSpawnOnPartiallyFilled"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testSpawnDoNotOverwriteExisting"
     */
    public static void spawnTile() {
        int a , b;
        do { 
            a = random.nextInt(SIZE);
            b = random.nextInt(SIZE);
        } while (board[a][b] != 0);

        board[a][b] = random.nextInt(10) == 0 ? 4 : 2;
    
    }

    /**
     * Mou totes les fitxes cap a l'esquerra:
     * 1. Mou tots els números (!= 0) cap a l'esquerra eliminant els espais buits
     * 2. Combina els números adjacents iguals (2+2=4, 4+4=8, etc.)
     * 3. Torna a moure tot a l'esquerra després de les combinacions

    Exemple 0:
+----+----+----+----+                          +----+----+----+----+
|   2|  16|   4|   4|                          |   2|  16|   8|    |
+----+----+----+----+                          +----+----+----+----+
|   8|    |    |    |                          |   8|    |    |    |
+----+----+----+----+ després de "moveLeft" -> +----+----+----+----+
|   4|    |   4|    |                          |   8|    |    |    |
+----+----+----+----+                          +----+----+----+----+
|    |    |    |    |                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+

    Exemple 1:
+----+----+----+----+                          +----+----+----+----+
|    |   2|    |   2|                          |   4|    |    |    |
+----+----+----+----+                          +----+----+----+----+
|   4|   4|   4|    |                          |   8|   4|    |    |
+----+----+----+----+ després de "moveLeft" -> +----+----+----+----+
|   2|    |    |   4|                          |   2|   4|    |    |
+----+----+----+----+                          +----+----+----+----+
|    |    |    |    |                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+
     * 
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveLeftSimpleMove"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveLeftWithMerge"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveLeftNoMergeWithEmptySpaces"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveLeftEmptyRow"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveLeftFullRowWithoutMerge"
     */
    public static void moveLeft() {
        for (int fila = 0 ; fila < SIZE; fila++) {
            int[] fila_nova = new int[SIZE];
            int index_nou = 0;
            for (int col = 0 ; col < SIZE; col++) {
                if (board[fila][col]!= 0) {
                    fila_nova[index_nou] = board[fila][col];
                    index_nou++;
                }
            }
            for (int i = 0 ; i < SIZE; i++) {
                if (fila_nova[i] != 0 && fila_nova[i] == fila_nova[i + 1]) {
                    fila_nova[i] *= 2;
                    fila_nova[i + 1] = 0;
                }
            }
            
            int[] fila_final = new int[SIZE];
            int finalIndex = 0;
            for (int i = 0;i < SIZE; i++) {
                if (fila_nova[i] != 0) {
                    fila_final[finalIndex] = fila_nova[i];
                    finalIndex++;
                }
            }
            board[fila] = fila_final;
        }

    }

    /**
     * Mou totes les fitxes cap a la dreta:
     * 1. Mou tots els números (!= 0) cap a la dreta eliminant els espais buits
     * 2. Combina els números adjacents iguals (2+2=4, 4+4=8, etc.)
     * 3. Torna a moure tot a la dreta després de les combinacions

    Exemple 0:
+----+----+----+----+                          +----+----+----+----+
|   2|  16|   4|   4|                          |    |   2|  16|   8|
+----+----+----+----+                          +----+----+----+----+
|   8|    |    |    |                          |    |    |    |   8|
+----+----+----+----+ després de "moveRight" -> +----+----+----+----+
|   4|    |   4|    |                          |    |    |    |   8|
+----+----+----+----+                          +----+----+----+----+
|    |    |    |    |                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+

    Exemple 1:
+----+----+----+----+                          +----+----+----+----+
|    |   2|    |   2|                          |    |    |    |   4|
+----+----+----+----+                          +----+----+----+----+
|   4|   4|   4|    |                          |    |    |   4|   8|
+----+----+----+----+ després de "moveRight" -> +----+----+----+----+
|   2|    |    |   4|                          |    |    |   2|   4|
+----+----+----+----+                          +----+----+----+----+
|    |    |    |    |                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+
     * 
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveRightSimpleMove"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveRightWithMerge"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveRightNoMergeWithEmptySpaces"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveRightEmptyColumn"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveRightFullColumnWithoutMerge"
     */
    public static void moveRight() {
        for (int fila = 0; fila < SIZE; fila++) {
            int[] fila_nova = new int[SIZE];
            int index_nou = SIZE - 1;
            
            for (int col = SIZE -1; col >=0; col--) {
                if (board[fila][col] != 0) {
                    fila_nova[index_nou] = board[fila][col];
                    index_nou--;
                }
            }
            for (int i = SIZE -1; i> 0; i--) {
                if (fila_nova[i] != 0 && fila_nova[i] == fila_nova[i - 1]) {
                    fila_nova[i] *= 2;
                    fila_nova[i - 1] = 0;
                }
            }
            int[] fila_final = new int[SIZE];
            int index_final = SIZE - 1;
            for (int i = SIZE - 1; i >= 0; i--) {
                if (fila_nova[i] != 0) {
                    fila_final[index_final] = fila_nova[i];
                    index_final--;
                }
            }
            board[fila] = fila_final;
        
        }
    }

    /**
     * Mou totes les fitxes cap amunt:
     * 1. Mou tots els números (!= 0) cap amunt eliminant els espais buits
     * 2. Combina els números adjacents iguals (2+2=4, 4+4=8, etc.)
     * 3. Torna a moure tot amunt després de les combinacions

    Exemple 0:
+----+----+----+----+                          +----+----+----+----+
|   2|  16|   4|   4|                          |   2|  16|   8|   4|
+----+----+----+----+                          +----+----+----+----+
|   8|    |    |    |                          |   8|    |    |    |
+----+----+----+----+ després de "moveUp" ->   +----+----+----+----+
|   4|    |   4|    |                          |   4|    |    |    |
+----+----+----+----+                          +----+----+----+----+
|    |    |    |    |                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+

    Exemple 1:
+----+----+----+----+                          +----+----+----+----+
|   2|  16|    |   4|                          |   4|  16|   8|   8|
+----+----+----+----+                          +----+----+----+----+
|    |    |   4|   4|                          |    |   8|    |   2|
+----+----+----+----+ després de "moveUp" ->   +----+----+----+----+
|   2|   8|    |    |                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+
|    |    |   4|   2|                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+
     * 
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveUpSimpleMove"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveUpWithMerge"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveUpNoMergeWithEmptySpaces"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveUpEmptyColumn"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveUpFullColumnWithoutMerge"
     */
    public static void moveUp() {
        for (int col = 0; col < SIZE; col++) {
            int[] col_nova = new int[SIZE];
            int index_nou = 0;
            
            for (int fila = 0; fila < SIZE; fila++) {
                if (board[fila][col] != 0) {
                    col_nova[index_nou] = board[fila][col];
                    index_nou++;
                }
            }
            
            for (int i = 0; i < SIZE - 1; i++) {
                if (col_nova[i] != 0 && col_nova[i] == col_nova[i + 1]) {
                    col_nova[i] *= 2;
                    col_nova[i + 1] = 0;
                }
            }
            
            int[] col_final = new int[SIZE];
            int index_final = 0;
            for (int i = 0; i < SIZE; i++) {
                if (col_nova[i] != 0) {
                    col_final[index_final] = col_nova[i];
                    index_final++;
                }
            }
            
            for (int fila = 0; fila < SIZE; fila++) {
                board[fila][col] = col_final[fila];
            }
        }    
    }

    /**
     * Mou totes les fitxes cap avall:
     * 1. Mou tots els números (!= 0) cap avall eliminant els espais buits
     * 2. Combina els números adjacents iguals (2+2=4, 4+4=8, etc.)
     * 3. Torna a moure tot avall després de les combinacions

    Exemple 0:
+----+----+----+----+                          +----+----+----+----+
|   2|  16|   4|   2|                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+
|   8|    |    |   2|                          |   2|    |    |    |
+----+----+----+----+ després de "moveDown" -> +----+----+----+----+
|   4|    |   4|  16|                          |   8|    |    |   4|
+----+----+----+----+                          +----+----+----+----+
|    |    |    |    |                          |   4|  16|   8|  16|
+----+----+----+----+                          +----+----+----+----+

    Exemple 1:
+----+----+----+----+                          +----+----+----+----+
|    |   2|    |   2|                          |    |    |    |    |
+----+----+----+----+                          +----+----+----+----+
|   4|   4|   4|    |                          |    |    |    |    |
+----+----+----+----+ després de "moveDown" -> +----+----+----+----+
|   2|    |    |   4|                          |   4|   2|    |   2|
+----+----+----+----+                          +----+----+----+----+
|    |    |    |    |                          |   2|   4|   4|   4|
+----+----+----+----+                          +----+----+----+----+
     * 
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveDownSimpleMove"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveDownWithMerge"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveDownNoMergeWithEmptySpaces"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveDownEmptyColumn"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testMoveDownFullColumnWithoutMerge"
     */
    public static void moveDown() {
        for (int col = 0; col < SIZE; col++) {

            int[] col_nova = new int[SIZE];
            int index_nou = SIZE - 1;

            for (int fila = SIZE - 1; fila >= 0; fila--) {
                if (board[fila][col] != 0) {
                    col_nova[index_nou] = board[fila][col];
                    index_nou--;
                }
            }
        
            for (int i = SIZE - 1; i > 0; i--) {
                if (col_nova[i] != 0 && col_nova[i] == col_nova[i - 1]) {
                    col_nova[i] *= 2;
                    col_nova[i - 1] = 0;
                }
            }
            
            int[] col_final = new int[SIZE];
            int index_final = SIZE - 1;
            for (int i = SIZE - 1; i >= 0; i--) {
                if (col_nova[i] != 0) {
                    col_final[index_final] = col_nova[i];
                    index_final--;
                }
            }
            
            for (int fila = 0; fila < SIZE; fila++) {
                board[fila][col] = col_final[fila];
            }
        }
    }

    /**
     * Comprova l'estat del joc:
     * - Si hi ha un 128 al taulell, el joc s'ha guanyat
     * - Si no hi ha moviments possibles (no hi ha caselles buides ni
     *   números adjacents iguals), el joc s'ha perdut
     * - En qualsevol altre cas, el joc continua
     * 
     * @return "win" si s'ha guanyat, "lost" si s'ha perdut, "continue" si el joc continua
     * 
     * @test ./runTest.sh "com.exercicis.TestExercici1#testGameWin"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testGameContinueWithEmptyCell"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testGameContinueWithAdjacentHoriz"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testGameContinueWithAdjacentVert"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testGameLost"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testGameContinueWithMultipleConditions"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testGameWinWithMultipleConditions"
     */
    public static String isGameFinished() {
        for (int fila = 0; fila < SIZE; fila++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[fila][col] == 128) return "win";
            }
        }
        for(int fila = 0; fila < SIZE; fila++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[fila][col] == 0) return "continue";
                if (fila > 0 && board[fila][col] == board[fila - 1][col]) return "continue";
                if (col > 0 && board[fila][col] == board[fila][col - 1]) return "continue";      
            }
        }
        return "lost";
    }

    /**
     * Gestiona el bucle principal del joc:
     * 1. Genera una fitxa inicial
     * 2. Entra en un bucle que:
     *    - Genera una nova fitxa
     *    - Neteja la pantalla (amb clearScreen)
     *    - Mostra el taulell
     *    - Mostra missatges d'error si n'hi ha
     *    - Comprova si el joc ha acabat (guanyat o perdut)
     *    - Demana i processa el següent moviment del jugador
     *      · 'l' o 'left': Mou les fitxes cap a l'esquerra
     *      · 'r' o 'right': Mou les fitxes cap a la dreta
     *      · 'u' o 'up': Mou les fitxes cap amunt
     *      · 'd' o 'down': Mou les fitxes cap avall  
     *      · 'exit': Surt del joc
     *    - Si el moviment no és vàlid, mostra un missatge d'error
     * 
     * @param scanner Scanner per llegir l'input del jugador
     * 
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPlayExitGame"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPlayInvalidMove"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPlayValidMoves"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPlayShortCommands"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPlayWinCondition"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPlayLoseCondition"
     * @test ./runTest.sh "com.exercicis.TestExercici1#testPlayMixedCaseCommands"
     */
    public static void play(Scanner scanner) {
        String msg = "";

        spawnTile();
        while (true) {
            spawnTile();
            clearScreen();
            printBoard();

            if (msg.compareTo("") != -1) {
                System.out.println(msg);
            }

            String finish = isGameFinished();
            if(finish.equals("win")) {
                System.out.println("You win, congrats!");
                break;
            }
            if (finish.equals("lost")) {
                System.out.println("Game over, you are a loser!");
                break;
            }
            System.out.print("Your move (left, up, right, down, exit): ");
            String mv = scanner.nextLine().toLowerCase();

            switch (mv) {
                case "l":
                case "left":
                    moveLeft();
                    break;
                case "r":
                case "right":
                    moveRight();
                    break;
                case "u":
                case "up":
                    moveUp();
                    break;
                case "d":
                case "down":
                    moveDown();
                    break;
                case "exit":
                    System.out.println("Exit game ...");
                    return;
                default:
                    msg = "Invalid move!";
            }
        }
    }

    /**
     * 
     * @run ./run.sh "com.exercicis.Exercici1"
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        play(scanner);
        scanner.close();
    }
}

