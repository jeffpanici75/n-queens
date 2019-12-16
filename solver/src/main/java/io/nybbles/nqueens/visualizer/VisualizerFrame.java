package io.nybbles.nqueens.visualizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class VisualizerFrame extends JFrame {
    private static final Logger s_logger = LoggerFactory.getLogger(VisualizerFrame.class);
    private final SolutionsPanel _solutionsPanel = new SolutionsPanel();

    public VisualizerFrame() {
        super();
        try {
            System.setProperty(
                    "apple.laf.useScreenMenuBar",
                    "true");
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name",
                    "N Queens Visualizer");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            s_logger.error("Unable to configure custom look and feel", ex);
        }

        var inputPanel = new InputPanel();
        inputPanel.setNChangeListener(l -> {
            var panel = (InputPanel) l.getSource();
            _solutionsPanel.setN(panel.getN());
        });
        inputPanel.setSolveActionListener(l -> _solutionsPanel.solveForN());
        _solutionsPanel.setSolverCompletionListener(l -> inputPanel.setInputEnabled(true));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("N Queens Visualizer");
        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(1280, 1024));
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(_solutionsPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
