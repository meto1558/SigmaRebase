package jaco.mp3.resources;

public final class Equalizer {
    public static final float BAND_NOT_PRESENT = Float.NEGATIVE_INFINITY;
    public static final Equalizer PASS_THRU_EQ = new Equalizer();
    private static final int BANDS = 32;
    private final float[] settings = new float[32];

    public Equalizer() {
    }

    public Equalizer(float[] settings) {
        this.setFrom(settings);
    }

    public Equalizer(Equalizer.EQFunction eq) {
        this.setFrom(eq);
    }

    public void setFrom(float[] eq) {
        this.reset();
        int max = Math.min(eq.length, BANDS);

        for (int i = 0; i < max; ++i) {
            this.settings[i] = this.limit(eq[i]);
        }
    }

    public void setFrom(Equalizer.EQFunction eq) {
        this.reset();

        for (int i = 0; i < BANDS; ++i) {
            this.settings[i] = this.limit(eq.getBand(i));
        }
    }

    public void setFrom(Equalizer eq) {
        if (eq != this) {
            this.setFrom(eq.settings);
        }
    }

    public void reset() {
        for (int i = 0; i < BANDS; ++i) {
            this.settings[i] = 0.0F;
        }
    }

    public int getBandCount() {
        return this.settings.length;
    }

    public float setBand(int band, float neweq) {
        float eq = 0.0F;
        if (band >= 0 && band < BANDS) {
            eq = this.settings[band];
            this.settings[band] = this.limit(neweq);
        }

        return eq;
    }

    public float getBand(int band) {
        float eq = 0.0F;
        if (band >= 0 && band < BANDS) {
            eq = this.settings[band];
        }

        return eq;
    }

    private float limit(float eq) {
        if (eq == BAND_NOT_PRESENT) {
            return eq;
        } else if (eq > 1.0F) {
            return 1.0F;
        } else {
            return Math.max(eq, -1.0F);
        }
    }

    float[] getBandFactors() {
        float[] factors = new float[32];
        int i = 0;

        for (; i < (byte) BANDS; ++i) {
            factors[i] = this.getBandFactor(this.settings[i]);
        }

        return factors;
    }

    float getBandFactor(float eq) {
        if (eq == BAND_NOT_PRESENT) {
            return 0.0F;
        } else {
            return (float) Math.pow(2.0D, (double) eq);
        }
    }

    public abstract static class EQFunction {
        public float getBand(int band) {
            return 0.0F;
        }
    }
}
