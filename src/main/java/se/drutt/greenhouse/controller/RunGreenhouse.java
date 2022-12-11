package se.drutt.greenhouse.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunGreenhouse
{
    public static void main(String[] args)
    {
        new Pi4jMinimalExample();
        //waitForUserInput("Hit enter to exit.");
    }

    private static String waitForUserInput(String message)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(message);
        String line = null;

        try
        {
            line = br.readLine();
        }
        catch (IOException e) {
            System.out.println("Failed to read from Standard.in: " + e);
        }

        return line;
    }
}
