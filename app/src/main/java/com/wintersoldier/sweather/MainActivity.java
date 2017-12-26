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
import java.util.Random;

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
            alphanumericDisplay.display("1224");
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

            // set x-mas colors
            colors = new int[7];
            for (int i = 0; i < colors.length; i++) {
                if ((i % 2) == 0) {
                    if ((i % 4) == 0) {
                        colors[i] = Color.RED;

                    } else {
                        colors[i] = Color.GREEN;
                    }
                }
            }
            this.ledStrip.write(colors);
            Thread.sleep(1000);

            // set blinking colors
//            int clockCounter = 10;
//            for (int clock = 0; clock < clockCounter; clock++) {
//                colors = new int[7];
//
//                for (int i = 0; i < colors.length; i++) {
//                    int modNumber = (i + (clock % 2));
//                    if ((modNumber % 2) == 0) {
//                        if ((modNumber % 4) == 0) {
//                            colors[i] = Color.RED;
//
//                        } else {
//                            colors[i] = Color.GREEN;
//                        }
//                    }
//                }
//
//                // log
//                Log.d(TAG, "Colors are: " + colors.toString());
//
//                // set colors
//                this.ledStrip.write(colors);
//                Thread.sleep(1000);
//            }

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

                // log
                Log.d(TAG, "Closing 7 digit display");

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

                // log
                Log.d(TAG, "Closing LED strip");

            } catch (IOException exception) {
                Log.e(TAG, "Error closing LED strip: " + exception.getMessage());

            } finally {
                this.ledStrip = null;
            }
        }
    }
}
