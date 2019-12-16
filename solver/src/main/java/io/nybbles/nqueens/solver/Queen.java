package io.nybbles.nqueens.solver;

public class Queen {
    public Queen(byte id, byte row, byte column) {
        this.id = id;
        this.row = row;
        this.column = column;
    }
    public byte id;
    public byte row;
    public byte column;

    @Override
    public String toString() {
        return String.format("%d,%d", column, row);
    }
}
