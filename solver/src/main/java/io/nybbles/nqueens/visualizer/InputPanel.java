package io.nybbles.nqueens.visualizer;

import io.nybbles.nqueens.Constants;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class InputPanel extends JPanel {
    private ActionListener _solveActionListener;
    private JButton _button = new JButton();
    private JSlider _slider = new JSlider();
    private ChangeListener _changeListener;
    private JLabel _label = new JLabel();

    public InputPanel() {
        super();
        setPreferredSize(new Dimension(800, 64));
        setLayout(new BorderLayout());

        _label.setText(String.format("N=%d", Constants.MINIMUM_N));
        _label.setBorder(BorderFactory.createEmptyBorder(
                0,
                10,
                0,
                10));
        _label.setHorizontalTextPosition(SwingConstants.CENTER);

        _slider.setOrientation(JSlider.HORIZONTAL);
        _slider.setMinimum(Constants.MINIMUM_N);
        _slider.setMaximum(Constants.MAXIMUM_N);
        _slider.setValue(Constants.MINIMUM_N);
        _slider.setSnapToTicks(true);
        _slider.addChangeListener(l -> {
            var slider = (JSlider) l.getSource();
            _label.setText(String.format("N=%d", slider.getValue()));
            _changeListener.stateChanged(new ChangeEvent(this));
        });

        _button.setText("Solve!");
        _button.setDefaultCapable(true);
        _button.setBorderPainted(true);
        _button.setHorizontalTextPosition(SwingConstants.CENTER);
        _button.addActionListener(l -> {
            setInputEnabled(false);
            _solveActionListener.actionPerformed(l);
        });

        add(_label, BorderLayout.WEST);
        add(_slider, BorderLayout.CENTER);
        add(_button, BorderLayout.EAST);
    }

    public int getN() {
        return _slider.getValue();
    }

    public void setInputEnabled(boolean flag) {
        _button.setEnabled(flag);
        _slider.setEnabled(flag);
    }

    public void setNChangeListener(ChangeListener l) {
        _changeListener = l;
    }

    public void setSolveActionListener(ActionListener l) {
        _solveActionListener = l;
    }
}
