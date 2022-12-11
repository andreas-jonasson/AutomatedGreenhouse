package se.drutt.greenhouse.controller;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.platform.Platforms;

public class Pi4jMinimalExample
{
    // Adapted from https://pi4j.com/getting-started/minimal-example-application/
    private static int pressCount = 0;
    private static final int PIN_BUTTON = 17; // PIN 11 = BCM 17
    private static final int PIN_LED = 22; // PIN 15 = BCM 22
    private Context pi4j;


    public Pi4jMinimalExample()
    {
        pi4j = Pi4J.newAutoContext();
        Platforms platforms = pi4j.platforms();
        printPlatforms(platforms);
        setUpInputButton();
        setUpLed();
    }

    private static void printPlatforms(Platforms platforms)
    {
        System.out.println("Pi4J PLATFORMS");
        platforms.describe().print(System.out);
        System.out.println();
    }

    private void setUpInputButton()
    {
        var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
                .id("button")
                .name("Press button")
                .address(PIN_BUTTON)
                .pull(PullResistance.PULL_DOWN)
                .debounce(3000L)
                .provider("pigpio-digital-input");

        var button = pi4j.create(buttonConfig);

        button.addListener(e -> {
            if (e.state() == DigitalState.LOW) {
                pressCount++;
                System.out.println("Button was pressed for the " + pressCount + "th time");
            }
        });
    }

    private void setUpLed()
    {
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led")
                .name("LED Flasher")
                .address(PIN_LED)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");

        var led = pi4j.create(ledConfig);

        while (pressCount < 5) {
            if (led.equals(DigitalState.HIGH)) {
                led.low();
            } else {
                led.high();
            }
            try {
                Thread.sleep(500 / (pressCount + 1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
