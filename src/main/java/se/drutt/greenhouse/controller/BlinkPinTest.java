package se.drutt.greenhouse.controller;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BlinkPinTest
{
    private static int MY_TEST_PIN = 17;
    private Context pi4j;
    private BlinkPin blinkPin;

    public static void main(String[] args)
    {
        BlinkPinTest blinkPinTest = new BlinkPinTest();
        blinkPinTest.setUp();
        blinkPinTest.allManualTests();
    }

    void setUp()
    {
        pi4j = Pi4J.newAutoContext();
        blinkPin = new BlinkPin(pi4j, MY_TEST_PIN);
    }


    void allManualTests()
    {
        start();
        getUserYesOrNo("Do pin #" + MY_TEST_PIN + " blink? (y/n) ");
        stop();
        getUserYesOrNo("Has pin #" + MY_TEST_PIN + " stopped blinking? (y/n) ");
    }

    void start()
    {
        blinkPin.start();
    }


    void stop()
    {
        blinkPin.stop();
    }

    boolean getUserYesOrNo(String question)
    {
        System.out.print(question);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        try
        {
            input = reader.readLine();
        }
        catch (IOException e)
        {
            System.err.println("Failed to read from System.in: " + e);
            return false;
        }

        return input.equalsIgnoreCase("y");
    }
}
