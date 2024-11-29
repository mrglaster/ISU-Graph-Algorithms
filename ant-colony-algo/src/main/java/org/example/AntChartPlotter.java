package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.List;

public class AntChartPlotter {

    public static void plotCharts(List<Integer> chartYData, List<Integer> iterationResults, int antNumber) {

        // Создаем первый набор данных (для chartYData)
        System.out.println("SIZE IS: " + chartYData.size());
        XYSeries series1 = new XYSeries("Data");
        for (int i = 0; i < chartYData.size(); i++) {
            series1.add(i, chartYData.get(i));
        }
        XYSeriesCollection dataset1 = new XYSeriesCollection(series1);

        JFreeChart lineChart1 = ChartFactory.createXYLineChart(
                "Best result on each iteration",
                "Iteration (X)",
                "Best Path Length (Y)",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );

        // Настраиваем ось X для первого графика
        XYPlot plot1 = lineChart1.getXYPlot();
        NumberAxis domainAxis1 = new NumberAxis("Iteration (X)");
        domainAxis1.setTickUnit(new NumberTickUnit(1000)); // Устанавливаем интервал в 1000
        plot1.setDomainAxis(domainAxis1);
        plot1.setRenderer(new XYLineAndShapeRenderer());

        // Создаем второй набор данных (для iterationResults)
        XYSeries series2 = new XYSeries("Results");
        for (int i = 0; i < iterationResults.size(); i++) {
            series2.add(i, iterationResults.get(i));
        }
        XYSeriesCollection dataset2 = new XYSeriesCollection(series2);

        String chartTitle = String.valueOf(antNumber) + "st ant path length";
        JFreeChart lineChart2 = ChartFactory.createXYLineChart(
                chartTitle,
                "Iteration (X)",
                "Path length (Y)",
                dataset2,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Настраиваем ось X для второго графика
        XYPlot plot2 = lineChart2.getXYPlot();
        NumberAxis domainAxis2 = new NumberAxis("Successful iteration (X)");
        domainAxis2.setTickUnit(new NumberTickUnit(1000)); // Устанавливаем интервал в 1000
        plot2.setDomainAxis(domainAxis2);
        plot2.setRenderer(new XYLineAndShapeRenderer());

        ChartPanel chartPanel1 = new ChartPanel(lineChart1);
        ChartPanel chartPanel2 = new ChartPanel(lineChart2);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new java.awt.GridLayout(1, 2)); // Два столбца для графиков
        mainPanel.add(chartPanel1);
        mainPanel.add(chartPanel2);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Ant Colony Path Finding Iterations");
        frame.setSize(1600, 600); // Увеличиваем ширину для двух графиков
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
}
