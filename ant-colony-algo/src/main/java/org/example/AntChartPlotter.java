package org.example;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.List;

public class AntChartPlotter {

    public static void plotChart(List<Integer> chartYData) {

        // Создаем набор данных
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Заполняем данные (X = индекс, Y = значение)
        for (int i = 0; i < chartYData.size(); i++) {
            dataset.addValue(chartYData.get(i), "Data", Integer.toString(i));
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Ant colony path finding iterations",      // Заголовок графика
                "Iteration (X)",          // Метка оси X
                "Best path length (Y)",          // Метка оси Y
                dataset               // Набор данных
        );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Chart Example");
        frame.setSize(800, 600);
        ChartPanel chartPanel = new ChartPanel(lineChart);
        frame.setContentPane(chartPanel);
        frame.setVisible(true);
    }
}