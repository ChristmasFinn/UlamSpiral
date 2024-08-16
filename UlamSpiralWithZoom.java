import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;
import java.util.Set;


public class UlamSpiralWithZoom extends JFrame {

    private JFreeChart chart;
    private XYPlot plot;
    private XYSeries ulamSeries;
    private double scaleFactor = 1.1; // Zoom scale factor
    private int maxPoints = 10000; // Initial number of points
    private double currentZoomFactor = 1.0;

    public UlamSpiralWithZoom(String title) {
        super(title);

        ulamSeries = new XYSeries("Prime Numbers");
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ulamSeries);

        chart = ChartFactory.createScatterPlot(
                "Ulam Spiral",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);

        // Set point color to black
        renderer.setSeriesPaint(0, Color.BLACK);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 1200));
        setContentPane(chartPanel);

        chartPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomFactor = (e.getWheelRotation() > 0) ? 1 / scaleFactor : scaleFactor;

                currentZoomFactor *= zoomFactor;

                int newMaxPoints = (int) (maxPoints * currentZoomFactor);
                generateUlamSpiral(newMaxPoints);
                updateAxisRange();

                double xRange = plot.getDomainAxis().getRange().getLength();
                double yRange = plot.getRangeAxis().getRange().getLength();

                plot.getDomainAxis().setRange(
                        plot.getDomainAxis().getLowerBound() - (xRange * (zoomFactor - 1) / 2),
                        plot.getDomainAxis().getUpperBound() + (xRange * (zoomFactor - 1) / 2)
                );
                plot.getRangeAxis().setRange(
                        plot.getRangeAxis().getLowerBound() - (yRange * (zoomFactor - 1) / 2),
                        plot.getRangeAxis().getUpperBound() + (yRange * (zoomFactor - 1) / 2)
                );

                updatePointSize(zoomFactor);
            }
        });

        generateUlamSpiral(maxPoints);
        updateAxisRange();
    }

    private void generateUlamSpiral(int maxNumber) {
        ulamSeries.clear(); // Очистка предыдущих данных
        Set<Integer> primes = generatePrimes(maxNumber);

        int x = 0;
        int y = 0;
        int dx = 1; // Начинаем движение вправо
        int dy = 0;
        int number = 1;

        int step = 1; // Количество шагов в текущем направлении
        boolean increasing = true; // Флаг для увеличения шагов после каждого второго поворота

        while (number <= maxNumber) {
            for (int i = 0; i < step; i++) {
                if (number > maxNumber) break;

                if (primes.contains(number)) {
                    ulamSeries.add(x, y); // Добавление точки на график
                }

                // Перемещение
                x += dx;
                y += dy;
                number++;
            }

            // Поворот направления по часовой стрелке
            int temp = dx;
            dx = dy;
            dy = -temp;

            if (increasing) {
                step++; // Увеличиваем шаг после каждого второго поворота
            }
            increasing = !increasing;
        }
    }

    private Set<Integer> generatePrimes(int max) {
        Set<Integer> primes = new HashSet<>();
        boolean[] isPrime = new boolean[max + 1];
        for (int i = 2; i <= max; i++) {
            isPrime[i] = true;
        }
        for (int i = 2; i * i <= max; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= max; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        for (int i = 2; i <= max; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }
        return primes;
    }

    private void updateAxisRange() {
        double minX = ulamSeries.getMinX();
        double maxX = ulamSeries.getMaxX();
        double minY = ulamSeries.getMinY();
        double maxY = ulamSeries.getMaxY();

        plot.getDomainAxis().setRange(minX - 10, maxX + 10);
        plot.getRangeAxis().setRange(minY - 10, maxY + 10);
    }

    private void updatePointSize(double zoomFactor) {
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        double pointSize = 1.0 / zoomFactor;
        renderer.setSeriesShape(0, new Ellipse2D.Double(-pointSize, -pointSize, 2 * pointSize, 2 * pointSize));
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            UlamSpiralWithZoom example = new UlamSpiralWithZoom("Ulam Spiral");
            example.setSize(1200, 1200);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}