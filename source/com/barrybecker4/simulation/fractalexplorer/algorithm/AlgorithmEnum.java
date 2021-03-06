package com.barrybecker4.simulation.fractalexplorer.algorithm;

import com.barrybecker4.common.app.AppContext;

/**
 * Type of fractal generation algorithm to use.
 *
 * @author Barry Becker
 */
public enum AlgorithmEnum {

    MANDELBROT,
    JULIA;

    private String label;

    /**
     * Private constructor
     */
    AlgorithmEnum() {
        this.label = AppContext.getLabel(this.name());
    }

    public String getLabel() {
        return label;
    }

    /**
     * Create an instance of the algorithm given the controller and a refreshable.
     */
    public FractalAlgorithm createInstance(FractalModel model) {

        switch (this) {
            case MANDELBROT :
                return new MandelbrotAlgorithm(model);
            case JULIA :
                return new JuliaAlgorithm(model);
        }
        return null;
    }

}
