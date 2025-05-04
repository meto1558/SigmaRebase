package com.mentalfrostbyte.jello.util.game.player.rotation;

import java.io.Serializable;

/**
 * Represents a training sample for the neural network
 */
public class TrainingSample implements Serializable {
    private static final long serialVersionUID = 1L;

    public final float[] inputs;
    public final float[] expectedOutputs;
    public final float weight;
    public final long timestamp;

    /**
     * Create a new training sample
     * @param inputs Input values for the neural network
     * @param expectedOutputs Expected output values
     * @param weight Importance weight of this sample
     */
    public TrainingSample(float[] inputs, float[] expectedOutputs, float weight) {
        this.inputs = inputs;
        this.expectedOutputs = expectedOutputs;
        this.weight = weight;
        this.timestamp = System.currentTimeMillis();
    }
}