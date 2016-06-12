package org.tkopec;


/**
 * Created by tkopec on 23/05/16.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 4){
            System.out.println("\nIncorrect number of arguments!\n" +
                    "\tNeed:\n\t\tArgument1: number of rows\n" +
                    "\t\tArgument2: number of columns\n" +
                    "\t\tArgument3: min weight\n" +
                    "\t\tArgument4: max weight\n");
            return;
        }

        new GraphGenerator().generateAndSave(Integer.parseInt(args[0]),
                Integer.parseInt(args[1]),
                Double.parseDouble(args[2]),
                Double.parseDouble(args[3]));

    }
}
