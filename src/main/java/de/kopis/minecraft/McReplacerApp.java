package de.kopis.minecraft;

import java.io.IOException;

public class McReplacerApp {
    //TODO move into properties and filter from POM
    private static final String version = "0.0.1";;

    public static void main(String... args) throws IOException {
        System.out.println("MCReplacer v" + version);
        if(args.length != 2) {
            printUsage();
            System.exit(-1);
        }

        new RegionPrinter().printRegions(args[0], args[1]);
    }

    private static void printUsage() {
        System.out.println("USAGE: java -jar mcreplacer.jar PATH_TO_SAVEGAMES NAME_OF_WORLD");
    }
}
