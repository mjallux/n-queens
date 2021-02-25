import jdk.jfr.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.stream.*;
import java.time.*;

public class chessBoard {

    //////////////////////////////////////////////////////////////
    // https://gist.github.com/AlbertoImpl/3fbf55d5310e0b185e9a //
    //////////////////////////////////////////////////////////////
    @FunctionalInterface
    public interface Trampoline<V> {

        V trampoline();

        default V call() {
            Object trampoline = this;
            while (trampoline instanceof Trampoline) {
                trampoline = ((Trampoline) trampoline).trampoline();
            }
            V value = (V) trampoline;
            return value;
        }
    }
    ///////////////////////////////////////////////////////////////

    // Board is a bitset, where 0 is empty square 1 is queen

    // Representing possible queen moves as bitboard masks
    // (need to generate this to calculate intersections)

    private BitSet board;
    private BitSet[] masks;
    private int n;
    private int solutions;
    private Node<Integer> root;

    public chessBoard(int n) {
        this.n = n;

        board = new BitSet(n*n);
        masks = new BitSet[n*n];

        root = new Node<>(null);
        root.moves = new BitSet(n);
        root.depth = -1;

        generateMasks();
    }

    public void begin() {
        genTree(root).call();
    }

    private Trampoline genTree(Node<Integer> node) {
        return () -> {
            if(node.depth == n - 1) {
                solutions++;
                //printBoard(board);
                //System.out.println(solutions);
                //System.out.println();
            };

            //System.out.println(String.format("depth %d, data %d, children %s", node.depth, node.data, node.children));
            BitSet validMoves = validMoves();
            int nextMove = nextMove(node);

            // maxmove = clamp next move to current row
            int maxMove = (node.depth + 1) * n + n;
            maxMove = maxMove > n*n ? n*n : maxMove;

            // when next move possible -->
            if(nextMove < maxMove && nextMove < n*n) {
                board.set(nextMove);
                validMoves.set(nextMove);

                //  --- next node
                Node<Integer> nextNode = new Node<>(nextMove % n);
                if(node.moves == null) node.moves = validMoves.get(maxMove - n, maxMove);
                node.addChild(nextNode);
                // ---

                return genTree(nextNode);

            } else {
                // backtracking

                // if no more positions to check
                if(node == root && node.moves.cardinality() == n) {
                    System.out.println("Completed, solutions: " + solutions);
                    return solutions;
                }
                // unset board piece in current row & current position explored
                board.set(node.data + n * node.depth, false);
                node.parent.moves.set(node.data, true);
                node.children = null;
                //node.parent.removeChild(node);

                return genTree(node.parent);
            }
        };
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
        BitSet mask = (BitSet)board.clone();

        // better performace from just iterating
        for (int i = board.nextSetBit(0); i != -1; i = board.nextSetBit(i + 1)) {
            mask.or(masks[i]);
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
        chessBoard board = new chessBoard(16);

        long t1 = System.nanoTime();
        board.begin();
        long t2 = System.nanoTime();

        System.out.println((t2-t1)/1_000_000_000 + "ms");
    }
}