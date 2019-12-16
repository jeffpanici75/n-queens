package io.nybbles.nqueens;

import io.nybbles.nqueens.visualizer.VisualizerFrame;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(VisualizerFrame::new);
    }
}
