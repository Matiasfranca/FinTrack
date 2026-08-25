package ui.javafx.components.charts.monthlyOverview;

import java.util.ArrayList;
import java.util.List;

public class AxisScale {

    public final double maxTick;
    public final List<Double> ticks;

    public AxisScale(double maxAbsValue, int desiredSteps) {

        if (maxAbsValue <= 0)
            maxAbsValue = 1;

        double rawStep = maxAbsValue / desiredSteps;
        double magnitude = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double residual = rawStep / magnitude;

        double niceStep;
        if (residual > 5)
            niceStep = 10 * magnitude;
        else if (residual > 2)
            niceStep = 5 * magnitude;
        else if (residual > 1)
            niceStep = 2 * magnitude;
        else
            niceStep = magnitude;

        this.maxTick = Math.ceil(maxAbsValue / niceStep) * niceStep;

        this.ticks = new ArrayList<>();
        for (double v = 0; v <= this.maxTick + 0.0001; v += niceStep) {
            ticks.add(v);
        }
    }
}
