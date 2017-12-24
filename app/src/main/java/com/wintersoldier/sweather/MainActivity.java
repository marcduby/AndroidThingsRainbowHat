package com.wintersoldier.sweather;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    // rainbow hat display
    private AlphanumericDisplay alphanumericDisplay;
    private Apa102 ledStrip;

    // Default LED brightness
    private static final int LEDSTRIP_BRIGHTNESS = 1;
    private static final int LEDSTRIP_BRIGHTNESS_OFF = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // log start
        Log.d(TAG, "Weather station started");

        // initialize the 7 segment display
        try {
            alphanumericDisplay = RainbowHat.openDisplay();
            alphanumericDisplay.setEnabled(true);
            alphanumericDisplay.display("4321");
            Log.d(TAG, "Initialized the 7 digit display");

        } catch (IOException exception) {
            throw new RuntimeException("Error initializing 7 digit display: " + exception.getMessage());
        }

        // initialize the LED strip
        try {
            this.ledStrip = RainbowHat.openLedStrip();
            this.ledStrip.setBrightness(LEDSTRIP_BRIGHTNESS);

            int[] colors = new int[7];
            Arrays.fill(colors, Color.RED);
            this.ledStrip.write(colors);

            // again due to known issue
            this.ledStrip.write(colors);

            // loop
            for (int i = 0; i < colors.length; i++) {
                Thread.sleep(500);
                colors = new int[7];
                colors[i] = Color.RED;
                this.ledStrip.write(colors);
                Thread.sleep(500);
            }

            // turn LEDs off
            this.ledStrip.write(new int[7]);

            // log
            Log.d(TAG, "Initialized LED strip");

        } catch (InterruptedException exception) {
            throw new RuntimeException("Error initializing LED strip loop: " + exception.getMessage());

        } catch (IOException exception) {
            throw new RuntimeException("Error initializing LED strip: " + exception.getMessage());
        }

//        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // close the 7 digit display
        if (this.alphanumericDisplay != null) {
            try {
                this.alphanumericDisplay.clear();
                this.alphanumericDisplay.setEnabled(false);
                this.alphanumericDisplay.close();

            } catch (IOException exception) {
                Log.e(TAG, "Error closing 7 digit display: " + exception.getMessage());

            } finally {
                this.alphanumericDisplay = null;
            }
        }

        // close the LED strip
        if (this.ledStrip != null) {
            try {
                this.ledStrip.setBrightness(LEDSTRIP_BRIGHTNESS_OFF);
                this.ledStrip.write(new int[7]);
                this.ledStrip.close();

            } catch (IOException exception) {
                Log.e(TAG, "Error closing LED strip: " + exception.getMessage());

            } finally {
                this.ledStrip = null;
            }
        }
    }
}
