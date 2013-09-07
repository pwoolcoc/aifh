package com.heatonresearch.aifh.learning;

import com.heatonresearch.aifh.learning.score.ScoreFunction;

/**
 * Train using hill climbing.  Hill climbing can be used to optimize the long term memory of a Machine Learning
 * Algorithm. This is done by moving the current long term memory values to a new location if that new location
 * gives a better score from the scoring function.
 * <p/>
 * http://en.wikipedia.org/wiki/Hill_climbing
 */
public class TrainHillClimb implements LearningAlgorithm {
    /**
     * The machine learning algorithm to optimize.
     */
    private final MachineLearningAlgorithm algorithm;

    /**
     * The last result from the score function.
     */
    private double lastError;

    /**
     * The score function.
     */
    private final ScoreFunction score;

    /**
     * The candidate moves.
     */
    private final double[] candidate = new double[5];

    /**
     * The current step size.
     */
    private final double[] stepSize;

    /**
     * True, if we want to minimize the score function.
     */
    private final boolean shouldMinimize;

    /**
     * Construct a hill climbing algorithm.
     *
     * @param theShouldMinimize True, if we should minimize.
     * @param theAlgorithm      The algorithm to optimize.
     * @param theScore          The scoring function.
     * @param acceleration      The acceleration for step sizes.
     * @param stepSize          The initial step sizes.
     */
    public TrainHillClimb(boolean theShouldMinimize, MachineLearningAlgorithm theAlgorithm, ScoreFunction theScore,
                          double acceleration, double stepSize) {
        this.algorithm = theAlgorithm;
        this.score = theScore;
        this.shouldMinimize = theShouldMinimize;

        this.stepSize = new double[theAlgorithm.getLongTermMemory().length];
        for (int i = 0; i < theAlgorithm.getLongTermMemory().length; i++) {
            this.stepSize[i] = stepSize;
        }

        candidate[0] = -acceleration;
        candidate[1] = -1 / acceleration;
        candidate[2] = 0;
        candidate[3] = 1 / acceleration;
        candidate[4] = acceleration;

        // Set the last error to a really bad value so it will be reset on the first iteration.
        if (this.shouldMinimize) {
            this.lastError = Double.POSITIVE_INFINITY;
        } else {
            this.lastError = Double.NEGATIVE_INFINITY;
        }
    }

    /**
     * Construct a hill climbing algorithm. Use acceleration of 1.2 and initial step size of 1.
     *
     * @param theShouldMinimize True, if we should minimize.
     * @param theAlgorithm      The algorithm to optimize.
     * @param theScore          The scoring function.
     */
    public TrainHillClimb(boolean theShouldMinimize, MachineLearningAlgorithm theAlgorithm, ScoreFunction theScore) {
        this(theShouldMinimize, theAlgorithm, theScore, 1.2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iteration() {
        int len = this.algorithm.getLongTermMemory().length;

        for (int i = 0; i < len; i++) {
            int best = -1;
            double bestScore = this.shouldMinimize ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

            for (int j = 0; j < candidate.length; j++) {
                this.algorithm.getLongTermMemory()[i] += stepSize[i] * candidate[j];
                double temp = score.calculateScore(this.algorithm);
                this.algorithm.getLongTermMemory()[i] -= stepSize[i] * candidate[j];

                if ((temp < bestScore) ? shouldMinimize : !shouldMinimize) {

                    bestScore = temp;
                    this.lastError = bestScore;
                    best = j;
                }
            }

            if (best != -1) {
                this.algorithm.getLongTermMemory()[i] += stepSize[i] * candidate[best];
                stepSize[i] = stepSize[i] * candidate[best];
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean done() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLastError() {
        return this.lastError;
    }

    /**
     * {@inheritDoc}
     */
    public String getStatus() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finishTraining() {

    }
}
