package io.nybbles.nqueens.visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static io.nybbles.nqueens.visualizer.ChessBoardPanel.FilterType.*;

public class SolutionsPanel extends JPanel {
    private final ChessBoardPanel _chessBoardPanel = new ChessBoardPanel();
    private ActionListener _solverCompletionListener;

    private final class ControlsPanel extends JPanel {
        private int _solutionCount;
        private boolean _solutionActive;
        private JButton _nextButton = new JButton("Next");
        private JButton _prevButton = new JButton("Prev");
        private JCheckBox _showMirror = new JCheckBox("Show Mirror");
        private JLabel _countLabel = new JLabel("Solution Count: 0");
        private JCheckBox _showAllLines = new JCheckBox("Show All Lines");
        private JLabel _solutionNumberLabel = new JLabel("Solution #: 0");
        private JLabel _executionTimeLabel = new JLabel("Execution Time: 0ms");
        private JCheckBox _showStraightLines = new JCheckBox("Show Straight Lines");
        private JRadioButton _allSolutionsButton = new JRadioButton("All solutions");
        private JRadioButton _validSolutionsButton = new JRadioButton("Valid solutions only");
        private JRadioButton _invalidSolutionsButton = new JRadioButton("Invalid solutions only");

        private void setButtonState(int index) {
            var text = "Solution #: 0";

            _showMirror.setEnabled(_solutionCount > 0);
            _showAllLines.setEnabled(_solutionCount > 0);
            _showStraightLines.setEnabled(_solutionCount > 0);
            _allSolutionsButton.setEnabled(_solutionActive);
            _validSolutionsButton.setEnabled(_solutionActive);
            _invalidSolutionsButton.setEnabled(_solutionActive);

            if (_solutionCount <= 1) {
                _nextButton.setEnabled(false);
                _prevButton.setEnabled(false);
            } else {
                _prevButton.setEnabled(index > 0);
                _nextButton.setEnabled(index < _solutionCount - 1);
                text = String.format(
                        "Solution #: %d",
                        _chessBoardPanel.getCurrentSolutionIndex() + 1);
            }

            _solutionNumberLabel.setText(text);
        }

        public ControlsPanel() {
            super();
            setLayout(new FlowLayout());
            setBorder(BorderFactory.createLoweredBevelBorder());

            var labelPanel = new JPanel();
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
            labelPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            labelPanel.add(_countLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            labelPanel.add(_solutionNumberLabel);
            labelPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            labelPanel.add(_executionTimeLabel);
            add(labelPanel);

            var buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            buttonPanel.add(_prevButton);
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            buttonPanel.add(_nextButton);
            add(buttonPanel);

            var checkBoxPanel = new JPanel();
            checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
            checkBoxPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            checkBoxPanel.add(_showAllLines);
            checkBoxPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            checkBoxPanel.add(_showStraightLines);
            checkBoxPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            checkBoxPanel.add(_showMirror);
            add(checkBoxPanel);

            var filterPanel = new JPanel();
            filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
            filterPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            filterPanel.add(_allSolutionsButton);
            filterPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            filterPanel.add(_validSolutionsButton);
            filterPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            filterPanel.add(_invalidSolutionsButton);
            add(filterPanel);

            _allSolutionsButton.setSelected(true);
            _allSolutionsButton.setEnabled(false);
            _allSolutionsButton.addActionListener(l -> _chessBoardPanel.setFilterType(ALL));

            _validSolutionsButton.setSelected(false);
            _validSolutionsButton.setEnabled(false);
            _validSolutionsButton.addActionListener(l -> _chessBoardPanel.setFilterType(VALID));

            _invalidSolutionsButton.setSelected(false);
            _invalidSolutionsButton.setEnabled(false);
            _invalidSolutionsButton.addActionListener(l -> _chessBoardPanel.setFilterType(INVALID));

            var filterButtonGroup = new ButtonGroup();
            filterButtonGroup.add(_allSolutionsButton);
            filterButtonGroup.add(_validSolutionsButton);
            filterButtonGroup.add(_invalidSolutionsButton);

            _showAllLines.setSelected(_chessBoardPanel.getShowAllLines());
            _showAllLines.setEnabled(false);
            _showAllLines.addActionListener(l -> _chessBoardPanel.toggleShowAllLines());

            _showStraightLines.setSelected(_chessBoardPanel.getShowStraightLines());
            _showStraightLines.setEnabled(false);
            _showStraightLines.addActionListener(l -> _chessBoardPanel.toggleShowStraightLines());

            _showMirror.setSelected(_chessBoardPanel.getShowMirror());
            _showMirror.setEnabled(false);
            _showMirror.addActionListener(l -> _chessBoardPanel.toggleShowMirror());

            _prevButton.setEnabled(false);
            _prevButton.addActionListener(l ->  {
                _chessBoardPanel.moveToPrevSolution();
                setButtonState(_chessBoardPanel.getCurrentSolutionIndex());
            });

            _nextButton.setEnabled(false);
            _nextButton.addActionListener(l -> {
                _chessBoardPanel.moveToNextSolution();
                setButtonState(_chessBoardPanel.getCurrentSolutionIndex());
            });

            _chessBoardPanel.setSolutionChangeListener(l -> {
                var source = (ChessBoardPanel.SolverEvent) l.getSource();
                _countLabel.setText(String.format("Count: %d", source.count));
                _executionTimeLabel.setText(String.format(
                        "Execution Time: %s",
                        source.formattedExecutionTime));
                _solutionCount = source.count;
                _solutionActive = source.active;
                setButtonState(_chessBoardPanel.getCurrentSolutionIndex());
                if (_solverCompletionListener != null) {
                    _solverCompletionListener.actionPerformed(new ActionEvent(
                            this,
                            0,
                            "solved"));
                }
            });
        }
    }

    public SolutionsPanel() {
        super();
        setBorder(BorderFactory.createLoweredBevelBorder());
        setLayout(new BorderLayout());
        add(_chessBoardPanel, BorderLayout.CENTER);
        var controlsPanel = new ControlsPanel();
        add(controlsPanel, BorderLayout.SOUTH);
    }

    public void setN(int n) {
        _chessBoardPanel.setN(n);
    }

    public void solveForN() {
        _chessBoardPanel.solveForN();
    }

    public void setSolverCompletionListener(ActionListener l) {
        _solverCompletionListener = l;
    }
}
