package com.bsuir.nikitayasiulevich;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarRaceApp extends JFrame {

    private List<JLabel> carLabels;
    private JButton startButton;
    private JLabel resultLabel;
    private JPanel raceTrackPanel;
    private List<Integer> carPositions;
    private int finishLine;

    private List<Thread> carThreads;

    public CarRaceApp() {
        setTitle("Car Race App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        raceTrackPanel = new JPanel();
        raceTrackPanel.setLayout(new BoxLayout(raceTrackPanel, BoxLayout.Y_AXIS));
        raceTrackPanel.setPreferredSize(new Dimension(300, 200));

        carLabels = new ArrayList<>();
        carLabels.add(new JLabel(new ImageIcon(getClass().getResource("/small_car1.png"))));
        carLabels.add(new JLabel(new ImageIcon(getClass().getResource("/small_car2.png"))));
        // Add more car labels as needed

        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRace();
            }
        });

        resultLabel = new JLabel();

        for (JLabel carLabel : carLabels) {
            JPanel stripe = new JPanel();
            stripe.setBackground(Color.GREEN);
            stripe.setPreferredSize(new Dimension(300, 50));
            stripe.add(carLabel, BorderLayout.SOUTH);
            raceTrackPanel.add(stripe);
        }

        add(raceTrackPanel, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);
        add(resultLabel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void startRace() {
        resultLabel.setText("");
        startButton.setEnabled(false);

        int numCars = carLabels.size();
        carPositions = new ArrayList<>(numCars);
        for (int i = 0; i < numCars; i++) {
            carPositions.add(0);
        }
        finishLine = raceTrackPanel.getWidth() - carLabels.get(0).getWidth();

        carThreads = new ArrayList<>(numCars);
        for (int i = 0; i < numCars; i++) {
            carThreads.add(new Thread(new CarRunnable(i)));
            carThreads.get(i).start();
        }
    }

    private boolean updateCarPosition(int carNumber, int position) {
        carPositions.set(carNumber, position);
        carLabels.get(carNumber).setLocation(position, carLabels.get(carNumber).getY());
        if (position >= finishLine) {
            carLabels.get(carNumber).setLocation(finishLine, carLabels.get(carNumber).getY());
            displayResult("Car " + (carNumber + 1) + " Wins!");
            carThreads.get(carNumber).interrupt();
            return true;
        }
        return false;
    }

    private void displayResult(String result) {
        if (resultLabel.getText().isEmpty()) {
            resultLabel.setText(result);
        }
        startButton.setEnabled(true);
    }

    private class CarRunnable implements Runnable {

        private int carNumber;
        private Random random;

        public CarRunnable(int carNumber) {
            this.carNumber = carNumber;
            this.random = new Random();
        }

        @Override
        public void run() {
            boolean isFinished = false;
            while (!isFinished) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int speed = random.nextInt(10) + 2;
                isFinished = updateCarPosition(carNumber, carPositions.get(carNumber) + speed);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CarRaceApp();
            }
        });
    }
}