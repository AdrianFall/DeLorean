package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Adrian on 02/09/2015.
 */
public class Main {

    public static void main(String[] args) {

        boolean running = true;
        DeLorean deLorean = new DeLorean();

        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));

        do {

            System.out.println("Enter command : ");
            try {
                String command = bufferReader.readLine();
                if (command.equals("QUIT")) {
                    //System.out.println("Quitting DeLorean.");
                    running = false;
                }

                else
                    deLorean.processCommand(command);


            } catch (IOException ioe) {
                System.out.println("ERR IOException");
                ioe.printStackTrace();
                running = false;
            }

        } while (running);
    }


}
