import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.stream.*;

public class chessBoard {

    // Board is a bitset, where 0 is empty square 1 is queen

    // Representing possible queen moves as bitboard masks
    // (need to generate this to calculate intersections)

    private BitSet board;
    private BitSet[] masks;
    private int n;
    private int solutions;
    Node<Integer> root;

    public chessBoard(int n) {
        this.n = n;

        board = new BitSet(n*n);
        masks = new BitSet[n*n];

        root = new Node<>(null);
        root.moves = new BitSet(n);
        root.depth = -1;

        generateMasks();

        genTree(root);
    }

    private void genTree(Node<Integer> node) {
        // todo take into consideration moves when generating valid moves

        BitSet validMoves = validMoves();
        int nextMove = nextMove(node);

        // maxmove = clamp next move to current row
        int maxMove = (node.depth + 1) * n + n;
        maxMove = maxMove > n*n ? n*n : maxMove;

        if(node.depth == n - 1) {
            solutions++;
            //printBoard();
            //System.out.println();
        };

        // when next move possible -->
        if(nextMove < maxMove && nextMove < n*n) {
            board.set(nextMove);
            validMoves.set(nextMove);

            //  --- next node
            Node<Integer> nextNode = new Node<>(nextMove % n);
            if(node.moves == null) node.moves = validMoves.get(maxMove - n, maxMove);

            node.addChild(nextNode);
            // --- end

            genTree(nextNode);

        // backtrack
        } else {
            if(node == root && node.moves.cardinality() == n) {
                System.out.println("couldnt find more");
                System.out.println("solutions: " + solutions);
                return;
            }
            board.set(node.data + n * node.depth, false);
            // mark current move as explored
            node.parent.moves.set(node.data, true);
            genTree(node.parent);
        }
    }

    public void printBoard() {
        printBoard(board);
    }

    private void printBoard(BitSet board) {

        // Converts bitboard to string, then inserts  "\n" every n bits (n = nQueens)

        // BitSet to binary string, modified a bit from: https://stackoverflow.com/questions/34748006/how-to-convert-bitset-to-binary-string-effectively
        String out = IntStream
            .range(0, n*n)
            .mapToObj(i -> board.get(i) ? '█' : '*')
            .collect(
                    () -> new StringBuilder(n*n),
                    StringBuilder::append,
                    StringBuilder::append)
            .toString()
            .replaceAll(".".repeat(n) + "(?!$)", "$0\n");
        System.out.println(out);
    }

    private void generateMasks() {

        // n * n squares in chess board and every position needs its own mask
        for(int i = 0; i < n * n; i++) {
            BitSet mask = new BitSet(n*n);

            // map 1d coordinates to 2d coordinates
            int x = i % n;
            int y = i / n;

            int rowStart = i - x;
            int rowEnd = rowStart + n;

            // Left and right ray
            mask.set(rowStart, rowEnd);

            // Up & down
            for (int j = 0; j < n; j++) {
                if (j == 0) mask.set(x);
                else mask.set(x + j * n);
            }

            // Diagonals
            // n - x, n - y, y + 1, x + 1 are distances to edges of the board
            // Math.min(..., ...) is steps to either edge

            // bottom right
            for (int j = 0; j < Math.min(n - x, n - y); j++) {
                mask.set(i + j + n * j);
            }

            // top right
            for(int j = 0; j < Math.min(n - x, y + 1); j++) {
                mask.set(i + j - n * j);
            }

            // bottom left
            for(int j = 0; j < Math.min(n - y, x + 1); j++) {
                mask.set(i - j + n * j);
            }

            // top left
            for(int j = 0; j < Math.min(y + 1, x + 1); j++) {
                mask.set(i - j - n * j);
            }

            // Remove self from mask
            mask.set(i, false);

            masks[i] = mask;
        }
    }
    private BitSet validMoves() {
        BitSet mask = new BitSet(n*n);
        mask.or(board);

        // create copy of mask (cant mutate while using the stream) -> stream -> apply mask for every element
        //mask.get(0, mask.size()).stream().forEach(el -> mask.or(masks[el]));

        for(int i = 0; i < board.length(); i++) {
            if(board.get(i)) mask.or(masks[i]);
        }
        return mask;
    }

    private int nextMove(Node<Integer> node){
        int next;
        if(node.moves != null) {
            // translate to 1d move e.g. [3][1] --> [6] i = x + width * y
            if(node.moves.nextClearBit(0) == n) {
                return n*n;
            }
            next = node.moves.nextClearBit(0) + n * node.depth + n;
        }
        else {
            next = validMoves().nextClearBit(0);
        }

        return next == -1 ? n*n : next;
    }

    public static void main(String[] args) {
        chessBoard board = new chessBoard(9);
    }
}