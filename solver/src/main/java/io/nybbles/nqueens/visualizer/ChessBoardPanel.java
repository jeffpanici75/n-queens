package io.nybbles.nqueens.visualizer;

import io.nybbles.nqueens.Constants;
import io.nybbles.nqueens.solver.CombinedSolver;
import io.nybbles.nqueens.solver.ThreeInLineDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

public class ChessBoardPanel extends JPanel {
    private static final Logger s_logger = LoggerFactory.getLogger(ChessBoardPanel.class);
    private static final Color YELLOW = new Color(0xffffb2);
    private static final Color BROWN = new Color(0x99604b);
    private static final int SQUARE_SIZE = 64;
    private static final int QUEEN_SIZE = 32;
    private static final int ASCII_A = 65;
    private static final int ASCII_W = 87;

    private boolean _processingVisible = true;

    private final CombinedSolver _combinedSolver = new CombinedSolver();
    private List<CombinedSolver.CombinedSolution> _currentSolutions;
    private CombinedSolver.SolutionData _currentSolutionView;
    private ThreeInLineDetector.Result _threeInLineResult;
    private FilterType _filterType = FilterType.ALL;
    private ChangeListener _solutionChangeListener;
    private final byte[] _point1 = new byte[3];
    private final byte[] _point2 = new byte[3];
    private final byte[] _point3 = new byte[3];
    private boolean _showStraightLines = true;
    private int _currentSolutionIndex;
    private boolean _runningSolver;
    private boolean _showAllLines;
    private boolean _showMirror;
    private int _halfFontHeight;
    private Image _queenImage;
    private int _fontHeight;
    private int _fontWidth;
    private int _boardSize;
    private int _n;

    public enum FilterType {
        ALL,
        VALID,
        INVALID
    }

    public static final class SolverEvent {
        public SolverEvent(
                int count,
                boolean active,
                String formattedExecutionTime) {
            this.count = count;
            this.active = active;
            this.formattedExecutionTime = formattedExecutionTime;
        }
        public int count;
        public boolean active;
        public String formattedExecutionTime;
    }

    private final class SolverWorker extends SwingWorker<Void, Void> {
        private ActionListener _doneListener;
        private final int _n;

        public SolverWorker(int n, ActionListener doneListener) {
            _n = n;
            _doneListener = doneListener;
        }

        @Override
        protected void done() {
            super.done();
            try {
                _runningSolver = false;
                _doneListener.actionPerformed(new ActionEvent(
                        this,
                        0,
                        "solved"));
            } catch (Exception ignored) {
            }
        }

        @Override
        protected Void doInBackground() {
            _runningSolver = true;
            _combinedSolver.solve(_n);
            return null;
        }
    }

    private void drawPolyline(
            Graphics2D g,
            int sx,
            int sy,
            byte[] q1,
            byte[] q2,
            byte[] q3) {
        final var centerOfSquareOffset = ((SQUARE_SIZE - QUEEN_SIZE) / 2) + (QUEEN_SIZE / 2);
        var xs = new int[]{
                sx + (q1[1] * SQUARE_SIZE) + centerOfSquareOffset,
                sx + (q2[1] * SQUARE_SIZE) + centerOfSquareOffset,
                sx + (q3[1] * SQUARE_SIZE) + centerOfSquareOffset};
        var ys = new int[]{
                sy + ((_n - q1[0]) * SQUARE_SIZE) - centerOfSquareOffset,
                sy + ((_n - q2[0]) * SQUARE_SIZE) - centerOfSquareOffset,
                sy + ((_n - q3[0]) * SQUARE_SIZE) - centerOfSquareOffset,
        };
        g.drawPolyline(xs, ys, 3);
    }

    private void drawShadowedString(
            Graphics2D g2,
            String string,
            int x,
            int y,
            Color color) {
        g2.setColor(Color.BLACK);
        g2.drawString(string, x, y);
        g2.setColor(color);
        g2.drawString(string, x - 1, y - 1);
    }

    private void drawBoard(Graphics2D g2, int sx, int sy) {
        var cx = sx;
        var cy = sy;
        for (var y = 0; y < _n; ++y) {
            for (var x = 0; x < _n; ++x) {
                var color = (y + x + (_n - 1)) % 2 != 0 ? YELLOW :BROWN;
                g2.setColor(color);
                g2.fillRect(cx, cy, SQUARE_SIZE, SQUARE_SIZE);
                cx += SQUARE_SIZE;
            }
            cx = sx;
            cy += SQUARE_SIZE;
        }
    }

    private void drawQueens(Graphics2D g2, int sx, int sy) {
        var queenCentering = ((SQUARE_SIZE - QUEEN_SIZE) / 2);
        for (byte i = 0; i < _n; ++i) {
            var x = sx + _currentSolutionView.getQueenColumn(i) * SQUARE_SIZE;
            var y = sy + (_boardSize - SQUARE_SIZE) - (_currentSolutionView.getQueenRow(i) * SQUARE_SIZE);
            g2.drawImage(
                    _queenImage,
                    x + queenCentering,
                    y + queenCentering,
                    32,
                    32,
                    this);
        }
    }

    private void drawBoardLabels(Graphics2D g2, int sx, int sy) {
        var cx = sx + ((SQUARE_SIZE - _fontWidth) / 2);
        var cy = sy + (((SQUARE_SIZE - _halfFontHeight) / 2) + _halfFontHeight);
        g2.setColor(Color.WHITE);
        for (var i = 0; i < _n; ++i) {
            var columnLabel = Character.valueOf((char) (ASCII_A + i)).toString();
            drawShadowedString(
                    g2,
                    columnLabel,
                    cx,
                    sy - _halfFontHeight,
                    Color.WHITE);
            drawShadowedString(
                    g2,
                    columnLabel,
                    cx,
                    sy + _boardSize + _fontHeight,
                    Color.WHITE);
            cx += SQUARE_SIZE;

            var rowLabel = Integer.valueOf(_n - i).toString();
            drawShadowedString(
                    g2,
                    rowLabel,
                    sx - (_fontWidth + _halfFontHeight),
                    cy,
                    Color.WHITE);
            drawShadowedString(
                    g2,
                    rowLabel,
                    sx + _boardSize + _fontWidth,
                    cy,
                    Color.WHITE);
            cy += SQUARE_SIZE;
        }
        g2.setColor(Color.BLACK);
        g2.drawRect(sx, sy, _boardSize, _boardSize);
    }

    private void setCurrentSolutions(ArrayList<CombinedSolver.CombinedSolution> solutions) {
        _currentSolutionIndex = 0;
        int count = 0;
        boolean active = false;
        String timeValue = "N/A";

        if (solutions != null) {
            switch (_filterType) {
                case ALL     -> _currentSolutions = solutions;
                case VALID   -> _currentSolutions = solutions
                        .stream()
                        .filter(x -> !x.hasStraightLines)
                        .collect(Collectors.toList());
                case INVALID -> _currentSolutions = solutions
                        .stream()
                        .filter(x -> x.hasStraightLines)
                        .collect(Collectors.toList());
            }

            var elapsedTime = _combinedSolver.getExecutionTime();
            if (elapsedTime != null) {
                var minutes = elapsedTime.toMinutes();
                if (minutes > 0) {
                    timeValue = String.format("%d minutes", minutes);
                } else {
                    var seconds = elapsedTime.toSeconds();
                    if (seconds > 0) {
                        timeValue = String.format("%d seconds", seconds);
                    } else {
                        var milliSeconds = elapsedTime.toMillis();
                        if (milliSeconds > 0) {
                            timeValue = String.format("%d ms", milliSeconds);
                        } else {
                            var nanoSeconds = elapsedTime.toNanos();
                            timeValue = String.format("%d us", nanoSeconds / 1000L);
                        }
                    }
                }
            } else {
                timeValue = "N/A";
            }

            count = _currentSolutions.size();
            active = !solutions.isEmpty();
        }

        if (_solutionChangeListener != null) {
            _solutionChangeListener.stateChanged(new ChangeEvent(
                    new SolverEvent(count, active, timeValue)));
        }
    }

    private void updateCurrentSolution() {
        if (_currentSolutions == null)
            return;

        if (_currentSolutions.isEmpty())
            _currentSolutionView = null;
        else {
            var solution = _currentSolutions.get(_currentSolutionIndex);
            _currentSolutionView = solution.data;
            _currentSolutionView.update(_showMirror);
            _threeInLineResult = _currentSolutionView.getThreeInLineResult();
        }

        repaint();
    }

    public ChessBoardPanel() {
        super();
        setBorder(BorderFactory.createLoweredBevelBorder());
        setBackground(Color.DARK_GRAY);
        setN(Constants.MINIMUM_N);

        var metrics = getFontMetrics(getFont());
        _fontHeight = metrics.getHeight();
        _halfFontHeight = _fontHeight / 2;
        _fontWidth = metrics.getWidths()[ASCII_W];

        try {
            var resource = ChessBoardPanel.class
                    .getClassLoader()
                    .getResource("queen.png");
            if (resource != null)
                _queenImage = ImageIO.read(resource);
        } catch (IOException e) {
            s_logger.error("Unable to load queen.png", e);
        }

        var blinkTimer = new Timer(
                350,
                l -> {
                    _processingVisible = !_processingVisible;
                    if (_runningSolver)
                        repaint();
                });
        blinkTimer.setRepeats(true);
        blinkTimer.start();
    }

    public void solveForN() {
        _currentSolutions = null;
        _currentSolutionView = null;

        if (_solutionChangeListener != null) {
            _solutionChangeListener.stateChanged(
                    new ChangeEvent(new SolverEvent(
                            0,
                            false,
                            "N/A")));
        }

        new SolverWorker(
                _n,
                l -> EventQueue.invokeLater(() -> {
                    setCurrentSolutions(_combinedSolver.getSolutions());
                    updateCurrentSolution();
                })).execute();

        repaint();
    }

    public void setN(int n) {
        _n = n;
        _currentSolutions = null;
        _currentSolutionView = null;
        _boardSize = _n * SQUARE_SIZE;

        if (_solutionChangeListener != null) {
            _solutionChangeListener.stateChanged(
                    new ChangeEvent(new SolverEvent(
                            0,
                            false,
                            "N/A")));
        }

        repaint();
    }

    public void toggleShowMirror() {
        _showMirror = !_showMirror;
        updateCurrentSolution();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        var g2 = (Graphics2D) g;
        var size = getSize();
        var sx = (size.width - _boardSize) / 2;
        var sy = (size.height - _boardSize) / 2;
        drawBoard(g2, sx, sy);
        drawBoardLabels(g2, sx, sy);
        if (_currentSolutionView != null)
            drawQueens(g2, sx, sy);
        if (_runningSolver) {
            final var ry = (int) (size.getHeight() - 80) / 2;
            g2.setComposite(AlphaComposite.SrcOver.derive(0.6f));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, ry - 52, (int) size.getWidth(), 80);
            g2.setComposite(AlphaComposite.SrcOver);
            if (_processingVisible) {
                var originalFont = g2.getFont();
                var tempFont = originalFont.deriveFont(originalFont.getSize() * 4F);
                g2.setFont(tempFont);

                final var text = ".: RUNNING SOLVER :.";
                var bounds = g2.getFontMetrics().getStringBounds(text, g2);

                final var tx = (int) (size.getWidth() - bounds.getWidth()) / 2;
                final var ty = (int) (size.getHeight() - bounds.getHeight()) / 2;
                drawShadowedString(g2, text, tx, ty, Color.GREEN);

                g2.setFont(originalFont);
            }
        } else {
            var originalFont = g2.getFont();
            var tempFont = originalFont.deriveFont(originalFont.getSize() * 2F);
            g2.setFont(tempFont);

            g2.setComposite(AlphaComposite.SrcOver.derive(0.6f));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, (int) size.getWidth(), 45);
            g2.setComposite(AlphaComposite.SrcOver);

            if (_currentSolutionView == null) {
                final var text = _currentSolutions == null ?
                        "Click Solve! to begin" :
                        "No solutions in filtered list";
                var bounds = g2.getFontMetrics().getStringBounds(text, g2);
                drawShadowedString(
                        g2,
                        text,
                        (int) (size.getWidth() - bounds.getWidth()) / 2,
                        30,
                        Color.YELLOW);
            } else {
                if (_showAllLines) {
                    g2.setColor(Color.GREEN);
                    for (var ids : _threeInLineResult.testLines) {
                        _point1[0] = _currentSolutionView.getQueenRow(ids[0]);
                        _point1[1] = _currentSolutionView.getQueenColumn(ids[0]);

                        _point2[0] = _currentSolutionView.getQueenRow(ids[1]);
                        _point2[1] = _currentSolutionView.getQueenColumn(ids[1]);

                        _point3[0] = _currentSolutionView.getQueenRow(ids[2]);
                        _point3[1] = _currentSolutionView.getQueenColumn(ids[2]);

                        drawPolyline(g2, sx, sy, _point1, _point2, _point3);
                    }
                }

                if (_threeInLineResult.straightLineCount == 0) {
                    var text = "Solution has NO straight lines";
                    var bounds = g2.getFontMetrics().getStringBounds(text, g2);
                    drawShadowedString(
                            g2,
                            text,
                            (int) (size.getWidth() - bounds.getWidth()) / 2,
                            30,
                            Color.GREEN);
                } else {
                    if (_showStraightLines) {
                        g2.setColor(Color.RED);
                        var originalStroke = g2.getStroke();
                        var dashed = new BasicStroke(
                                3,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_BEVEL,
                                0,
                                new float[]{9},
                                0);
                        g2.setStroke(dashed);
                        for (var i = 0; i < _threeInLineResult.straightLineCount; ++i) {
                            var ids = _threeInLineResult.straightLines[i];

                            _point1[0] = _currentSolutionView.getQueenRow(ids[0]);
                            _point1[1] = _currentSolutionView.getQueenColumn(ids[0]);

                            _point2[0] = _currentSolutionView.getQueenRow(ids[1]);
                            _point2[1] = _currentSolutionView.getQueenColumn(ids[1]);

                            _point3[0] = _currentSolutionView.getQueenRow(ids[2]);
                            _point3[1] = _currentSolutionView.getQueenColumn(ids[2]);

                            drawPolyline(g2, sx, sy, _point1, _point2, _point3);
                        }
                        g2.setStroke(originalStroke);
                    }

                    final var text = "Solution HAS straight lines";
                    var bounds = g2.getFontMetrics().getStringBounds(text, g2);
                    drawShadowedString(
                            g2,
                            text,
                            (int) (size.getWidth() - bounds.getWidth()) / 2,
                            30,
                            Color.RED);
                }
            }

            g2.setFont(originalFont);
        }
    }

    public boolean getShowMirror() {
        return _showMirror;
    }

    public boolean getShowAllLines() {
        return _showAllLines;
    }

    public void toggleShowAllLines() {
        _showAllLines = !_showAllLines;
        repaint();
    }

    public void moveToPrevSolution() {
        if (_currentSolutionIndex > 0) {
            _currentSolutionIndex--;
            updateCurrentSolution();
        }
    }

    public void moveToNextSolution() {
        if (_currentSolutions == null)
            return;
        if (_currentSolutionIndex + 1 < _currentSolutions.size()) {
            _currentSolutionIndex++;
            updateCurrentSolution();
        }
    }

    public FilterType getFilterType() {
        return _filterType;
    }

    public int getCurrentSolutionIndex() {
        return _currentSolutions == null ? - 1 :_currentSolutionIndex;
    }

    public boolean getShowStraightLines() {
        return _showStraightLines;
    }

    public void toggleShowStraightLines() {
        _showStraightLines = !_showStraightLines;
        repaint();
    }

    public void setFilterType(FilterType type) {
        _filterType = type;
        setCurrentSolutions(_combinedSolver.getSolutions());
        updateCurrentSolution();
    }

    public void setSolutionChangeListener(ChangeListener l) {
        _solutionChangeListener = l;
    }
}
