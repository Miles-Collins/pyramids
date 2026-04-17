package com.pyramids;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class App {

    private static final String DATA_DIRECTORY = "src/main/java/com/pyramids";

    protected Pharaoh[] pharaohsArray;
    protected Pyramid[] pyramidsArray;
    protected Map<Integer, Pharaoh> pharaohById;
    protected Map<String, Pharaoh> pharaohByHieroglyphic;
    protected Map<Integer, Pyramid> pyramidById;
    protected Set<Integer> requestedPyramidId;

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }

    public App() {
        pharaohById = new LinkedHashMap<>();
        pharaohByHieroglyphic = new LinkedHashMap<>();
        pyramidById = new LinkedHashMap<>();
        requestedPyramidId = new LinkedHashSet<>();

        JSONArray pharaohJSONArray = JSONFile.readArray(resolveDataPath("pharaoh.json"));
        initializePharaoh(pharaohJSONArray);

        JSONArray pyramidJSONArray = JSONFile.readArray(resolveDataPath("pyramid.json"));
        initializePyramid(pyramidJSONArray);
    }

    private String resolveDataPath(String fileName) {
        Path filePath = Paths.get(DATA_DIRECTORY, fileName);
        return filePath.toString();
    }

    // main loop for app
    public void start() {
        Scanner scan = new Scanner(System.in);
        Character command = '_';

        // loop until user quits
        while (command != 'q') {
            printMenu();
            System.out.print("Enter a command: ");
            command = menuGetCommand(scan);

            executeCommand(scan, command);
        }
    }

    // initialize the pharaoh array
    private void initializePharaoh(JSONArray pharaohJSONArray) {
        // create array and hash map
        pharaohsArray = new Pharaoh[pharaohJSONArray.size()];

        // initalize the array
        for (int i = 0; i < pharaohJSONArray.size(); i++) {
            // get the object
            JSONObject o = (JSONObject) pharaohJSONArray.get(i);

            // parse the json object
            Integer id = toInteger(o, "id");
            String name = o.get("name").toString();
            Integer begin = toInteger(o, "begin");
            Integer end = toInteger(o, "end");
            Integer contribution = toInteger(o, "contribution");
            String hieroglyphic = o.get("hieroglyphic").toString();

            // add a new pharoah to array
            Pharaoh pharaoh = new Pharaoh(id, name, begin, end, contribution, hieroglyphic);
            pharaohsArray[i] = pharaoh;
            pharaohById.put(id, pharaoh);
            pharaohByHieroglyphic.put(hieroglyphic, pharaoh);
        }
    }

    // initialize the pyramid array
    private void initializePyramid(JSONArray pyramidJSONArray) {
        // create array and hash map
        pyramidsArray = new Pyramid[pyramidJSONArray.size()];

        // initalize the array
        for (int i = 0; i < pyramidJSONArray.size(); i++) {
            // get the object
            JSONObject o = (JSONObject) pyramidJSONArray.get(i);

            // parse the json object
            Integer id = toInteger(o, "id");
            String name = o.get("name").toString();
            JSONArray contributorsJSONArray = (JSONArray) o.get("contributors");
            String[] contributors = new String[contributorsJSONArray.size()];

            for (int j = 0; j < contributorsJSONArray.size(); j++) {
                contributors[j] = contributorsJSONArray.get(j).toString();
            }

            // add a new pyramid to array
            Pyramid pyramid = new Pyramid(id, name, contributors);
            pyramidsArray[i] = pyramid;
        }
    }

    // get a integer from a json object, and parse it
    private Integer toInteger(JSONObject o, String key) {
        return ((Number) o.get(key)).intValue();
    }

    // get first character from input
    private static Character menuGetCommand(Scanner scan) {
        Character command = '_';
        String rawInput = scan.nextLine();

        if (!rawInput.isEmpty()) {
            rawInput = rawInput.toLowerCase();
            command = rawInput.charAt(0);
        }

        return command;
    }

    private Boolean executeCommand(Scanner scan, Character command) {
        Boolean success = true;

        if (scan == null || command == null) {
            return false;
        }

        switch (command) {
            case '1':
                printAllPharaohs();
                break;
            case '2':
                promptForPharaohId(scan);
                break;
            case '3':
                printAllPyramids();
                break;
            case '4':
                promptForPyramidId();
                break;
            case '5':
                printRequestedPyramidReport();
                break;
            case 'q':
                System.out.println("Thank you for using Nassef's Egyptian Pyramid App!");
                break;
            default:
                System.out.println("ERROR: Unknown commmand");
                success = false;
        }

        return success;
    }

    private static void printMenuCommand(Character command, String desc) {
        System.out.printf("%s\t\t%s\n", command, desc);
    }

    private static void printMenuLine() {
        System.out.println("--------------------------------------------------------------------------");
    }

    // prints the menu
    public static void printMenu() {
        printMenuLine();
        System.out.println("Nassef's Egyptian Pyramid App");
        printMenuLine();
        System.out.printf("Command\t\tDescription\n");
        System.out.printf("-------\t\t-----------\n");
        printMenuCommand('1', "List all the pharaohs");
        printMenuCommand('2', "Display a specific pharaoh");
        printMenuCommand('3', "List all the pyramids");
        printMenuCommand('4', "Display a specific pyramid");
        printMenuCommand('5', "Display a report about a specific pyramid");
        printMenuCommand('q', "Quit");
        printMenuLine();
    }

    private void printAllPharaohs() {
        for (Pharaoh pharaoh : pharaohsArray) {
            printMenuLine();
            pharaoh.print();

            if (pharaohsArray.length > 0) {
                printMenuLine();
            }
        }
    }

    private void promptForPharaohId(Scanner scan) {
        System.out.println("Please enter the id of the pharaoh you want to see.");
    }

    private void printAllPyramids() {
        System.out.println("This command will print all the pyramids in the database.");
    }

    private void promptForPyramidId(Scanner scan) {
        System.out.println("Please enter the id of the pyramid you want to see.");
    }

    private void printRequestedPyramidReport() {
        System.out.println(
                "This command will print a report about the requested pyramid, including its "
                + "name, contributors, and the pharaohs that contributed to it."
        );
    }
}
