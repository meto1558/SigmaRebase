package com.mentalfrostbyte.jello.util.game.player.rotation;

import com.mentalfrostbyte.Client;

import java.io.*;
import java.util.Random;

/**
 * Neural network implementation for JelloAI
 */
public class NeuralNetwork {
    private static final Random random = new Random();

    // Neural Network parameters
    public static final int INPUT_SIZE = 8;
    public static final int HIDDEN_SIZE = 16;
    public static final int OUTPUT_SIZE = 2;

    // Neural Network weights
    private float[][] weightsInputToHidden = new float[INPUT_SIZE][HIDDEN_SIZE];
    private float[][] weightsHiddenToOutput = new float[HIDDEN_SIZE][OUTPUT_SIZE];

    // Biases
    private float[] hiddenBiases = new float[HIDDEN_SIZE];
    private float[] outputBiases = new float[OUTPUT_SIZE];

    // Learning parameters
    private static final float LEARNING_RATE = 0.01f;
    private static final float MOMENTUM = 0.9f;

    // Previous weight changes (for momentum)
    private float[][] prevDeltaInputHidden = new float[INPUT_SIZE][HIDDEN_SIZE];
    private float[][] prevDeltaHiddenOutput = new float[HIDDEN_SIZE][OUTPUT_SIZE];

    // File paths for saving/loading the neural network
    private static final String WEIGHTS_FILE = "jelloai_weights.dat";

    /**
     * Initialize the neural network
     */
    public void initialize() {
        // Try to load existing weights
        if (!loadWeights()) {
            // Initialize with random weights if loading fails
            initializeRandomWeights();
        }
    }

    /**
     * Initialize weights with small random values
     */
    private void initializeRandomWeights() {
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                weightsInputToHidden[i][j] = (random.nextFloat() - 0.5f) * 0.1f;
                prevDeltaInputHidden[i][j] = 0;
            }
        }

        for (int i = 0; i < HIDDEN_SIZE; i++) {
            hiddenBiases[i] = (random.nextFloat() - 0.5f) * 0.1f;

            for (int j = 0; j < OUTPUT_SIZE; j++) {
                weightsHiddenToOutput[i][j] = (random.nextFloat() - 0.5f) * 0.1f;
                prevDeltaHiddenOutput[i][j] = 0;
            }
        }

        for (int i = 0; i < OUTPUT_SIZE; i++) {
            outputBiases[i] = (random.nextFloat() - 0.5f) * 0.1f;
        }
    }

    /**
     * Forward pass through the neural network
     */
    public float[] forwardPass(float[] inputs) {
        // Hidden layer
        float[] hiddenOutputs = new float[HIDDEN_SIZE];
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            float sum = hiddenBiases[i];
            for (int j = 0; j < INPUT_SIZE; j++) {
                sum += inputs[j] * weightsInputToHidden[j][i];
            }
            hiddenOutputs[i] = sigmoid(sum);
        }

        // Output layer
        float[] outputs = new float[OUTPUT_SIZE];
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            float sum = outputBiases[i];
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                sum += hiddenOutputs[j] * weightsHiddenToOutput[j][i];
            }
            outputs[i] = sigmoid(sum);
        }

        return outputs;
    }

    /**
     * Train the neural network using backpropagation
     */
    public void trainNetwork(TrainingSample[] samples) {
        if (samples.length == 0) return;

        // Train for multiple epochs
        for (int epoch = 0; epoch < 5; epoch++) {
            // Shuffle samples
            shuffleArray(samples);

            // Process each sample
            for (TrainingSample sample : samples) {
                // Forward pass
                float[] hiddenOutputs = new float[HIDDEN_SIZE];
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    float sum = hiddenBiases[i];
                    for (int j = 0; j < INPUT_SIZE; j++) {
                        sum += sample.inputs[j] * weightsInputToHidden[j][i];
                    }
                    hiddenOutputs[i] = sigmoid(sum);
                }

                float[] outputs = new float[OUTPUT_SIZE];
                for (int i = 0; i < OUTPUT_SIZE; i++) {
                    float sum = outputBiases[i];
                    for (int j = 0; j < HIDDEN_SIZE; j++) {
                        sum += hiddenOutputs[j] * weightsHiddenToOutput[j][i];
                    }
                    outputs[i] = sigmoid(sum);
                }

                // Backpropagation with weight factor
                // Output layer error
                float[] outputErrors = new float[OUTPUT_SIZE];
                for (int i = 0; i < OUTPUT_SIZE; i++) {
                    outputErrors[i] = (sample.expectedOutputs[i] - outputs[i]) * sigmoidDerivative(outputs[i]) * sample.weight;
                }

                // Hidden layer error
                float[] hiddenErrors = new float[HIDDEN_SIZE];
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    float error = 0;
                    for (int j = 0; j < OUTPUT_SIZE; j++) {
                        error += outputErrors[j] * weightsHiddenToOutput[i][j];
                    }
                    hiddenErrors[i] = error * sigmoidDerivative(hiddenOutputs[i]);
                }

                // Update weights and biases with momentum
                // Hidden to output weights
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    for (int j = 0; j < OUTPUT_SIZE; j++) {
                        float delta = LEARNING_RATE * outputErrors[j] * hiddenOutputs[i] + MOMENTUM * prevDeltaHiddenOutput[i][j];
                        weightsHiddenToOutput[i][j] += delta;
                        prevDeltaHiddenOutput[i][j] = delta;
                    }
                }

                // Output biases
                for (int i = 0; i < OUTPUT_SIZE; i++) {
                    outputBiases[i] += LEARNING_RATE * outputErrors[i];
                }

                // Input to hidden weights
                for (int i = 0; i < INPUT_SIZE; i++) {
                    for (int j = 0; j < HIDDEN_SIZE; j++) {
                        float delta = LEARNING_RATE * hiddenErrors[j] * sample.inputs[i] + MOMENTUM * prevDeltaInputHidden[i][j];
                        weightsInputToHidden[i][j] += delta;
                        prevDeltaInputHidden[i][j] = delta;
                    }
                }

                // Hidden biases
                for (int i = 0; i < HIDDEN_SIZE; i++) {
                    hiddenBiases[i] += LEARNING_RATE * hiddenErrors[i];
                }
            }
        }
    }

    /**
     * Train the network immediately on a single sample
     */
    public void trainNetworkImmediate(float[] inputs, float[] expectedOutputs, float weight) {
        // Forward pass
        float[] hiddenOutputs = new float[HIDDEN_SIZE];
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            float sum = hiddenBiases[i];
            for (int j = 0; j < INPUT_SIZE; j++) {
                sum += inputs[j] * weightsInputToHidden[j][i];
            }
            hiddenOutputs[i] = sigmoid(sum);
        }

        float[] outputs = new float[OUTPUT_SIZE];
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            float sum = outputBiases[i];
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                sum += hiddenOutputs[j] * weightsHiddenToOutput[j][i];
            }
            outputs[i] = sigmoid(sum);
        }

        // Backpropagation with weight factor
        // Output layer error
        float[] outputErrors = new float[OUTPUT_SIZE];
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            outputErrors[i] = (expectedOutputs[i] - outputs[i]) * sigmoidDerivative(outputs[i]) * weight;
        }

        // Hidden layer error
        float[] hiddenErrors = new float[HIDDEN_SIZE];
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            float error = 0;
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                error += outputErrors[j] * weightsHiddenToOutput[i][j];
            }
            hiddenErrors[i] = error * sigmoidDerivative(hiddenOutputs[i]);
        }

        // Update weights and biases with momentum
        // Hidden to output weights
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                float delta = LEARNING_RATE * outputErrors[j] * hiddenOutputs[i] + MOMENTUM * prevDeltaHiddenOutput[i][j];
                weightsHiddenToOutput[i][j] += delta;
                prevDeltaHiddenOutput[i][j] = delta;
            }
        }

        // Output biases
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            outputBiases[i] += LEARNING_RATE * outputErrors[i];
        }

        // Input to hidden weights
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                float delta = LEARNING_RATE * hiddenErrors[j] * inputs[i] + MOMENTUM * prevDeltaInputHidden[i][j];
                weightsInputToHidden[i][j] += delta;
                prevDeltaInputHidden[i][j] = delta;
            }
        }

        // Hidden biases
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            hiddenBiases[i] += LEARNING_RATE * hiddenErrors[i];
        }
    }

    /**
     * Save neural network weights to file
     */
    public void saveWeights() {
        try {
            File file = new File(Client.getInstance().file, WEIGHTS_FILE);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            // Save input to hidden weights
            for (int i = 0; i < INPUT_SIZE; i++) {
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    out.writeFloat(weightsInputToHidden[i][j]);
                }
            }

            // Save hidden biases
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                out.writeFloat(hiddenBiases[i]);
            }

            // Save hidden to output weights
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                for (int j = 0; j < OUTPUT_SIZE; j++) {
                    out.writeFloat(weightsHiddenToOutput[i][j]);
                }
            }

            // Save output biases
            for (int i = 0; i < OUTPUT_SIZE; i++) {
                out.writeFloat(outputBiases[i]);
            }

            out.close();
        } catch (Exception e) {
            Client.logger.error("Error saving JelloAI weights", e);
        }
    }

    /**
     * Load neural network weights from file
     */
    private boolean loadWeights() {
        try {
            File file = new File(Client.getInstance().file, WEIGHTS_FILE);
            if (!file.exists()) return false;

            DataInputStream in = new DataInputStream(new FileInputStream(file));

            // Load input to hidden weights
            for (int i = 0; i < INPUT_SIZE; i++) {
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    weightsInputToHidden[i][j] = in.readFloat();
                }
            }

            // Load hidden biases
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                hiddenBiases[i] = in.readFloat();
            }

            // Load hidden to output weights
            for (int i = 0; i < HIDDEN_SIZE; i++) {
                for (int j = 0; j < OUTPUT_SIZE; j++) {
                    weightsHiddenToOutput[i][j] = in.readFloat();
                }
            }

            // Load output biases
            for (int i = 0; i < OUTPUT_SIZE; i++) {
                outputBiases[i] = in.readFloat();
            }

            in.close();
            return true;
        } catch (Exception e) {
            Client.logger.error("Error loading JelloAI weights", e);
            return false;
        }
    }

    // Helper methods
    private float sigmoid(float x) {
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }

    private float sigmoidDerivative(float x) {
        return x * (1 - x);
    }

    private void shuffleArray(Object[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Object temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}