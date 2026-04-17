package com.pyramids;

import java.nio.file.Files;
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
    private static final String NESTED_DATA_DIRECTORY = "pyramids/src/main/java/com/pyramids";

    protected Pharaoh[] pharaohsArray;
    protected Pyramid[] pyramidsArray;
    protected Map<Integer, Pharaoh> pharaohById;
    protected Map<String, Pharaoh> pharaohByHieroglyphic;
    protected Map<Integer, Pyramid> pyramidById;
    protected Set<Integer> requestedPyramidIds;

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }

    public App() {
        pharaohById = new LinkedHashMap<>();
        pharaohByHieroglyphic = new LinkedHashMap<>();
        pyramidById = new LinkedHashMap<>();
        requestedPyramidIds = new LinkedHashSet<>();

        JSONArray pharaohJSONArray = JSONFile.readArray(resolveDataPath("pharaoh.json"));
        initializePharaoh(pharaohJSONArray);

        JSONArray pyramidJSONArray = JSONFile.readArray(resolveDataPath("pyramid.json"));
        initializePyramid(pyramidJSONArray);
    }

    private String resolveDataPath(String fileName) {
        Path projectRootPath = Paths.get(DATA_DIRECTORY, fileName);

        if (Files.exists(projectRootPath)) {
            return projectRootPath.toString();
        }

        Path nestedRootPath = Paths.get(NESTED_DATA_DIRECTORY, fileName);

        if (Files.exists(nestedRootPath)) {
            return nestedRootPath.toString();
        }

        return projectRootPath.toString();
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
            pyramidById.put(id, pyramid);
        }
    }

    // get a integer from a json object, and parse it
    private Integer toInteger(JSONObject o, String key) {
        Object value = o.get(key);

        if (value == null) {
            throw new IllegalStateException("Missing required JSON field: " + key);
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return Integer.valueOf(value.toString());
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
                promptForPyramidId(scan);
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
        if (pharaohsArray == null || pharaohsArray.length == 0) {
            System.out.println("No Pharaohs available.");
            return;
        }

        for (Pharaoh pharaoh : pharaohsArray) {
            printMenuLine();
            pharaoh.print();

            if (pharaohsArray.length > 0) {
                printMenuLine();
            }
        }
    }

    private void promptForPharaohId(Scanner scan) {
        System.out.print("Enter a Pharaoh's id: ");

        try {
            Integer id = Integer.valueOf(scan.nextLine());
            Pharaoh pharaoh = pharaohById.get(id);

            if (pharaoh != null) {
                printMenuLine();
                pharaoh.print();
                printMenuLine();
            } else {
                System.out.println("Pharaoh not found.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid Pharaoh's id.");
        }
    }

    private void printAllPyramids() {
        if (pyramidsArray == null || pyramidsArray.length == 0) {
            System.out.println("No pyramids available.");
            return;
        }

        for (Pyramid pyramid : pyramidsArray) {
            printMenuLine();
            printPyramidSummary(pyramid);
        }

        if (pyramidsArray.length > 0) {
            printMenuLine();
        }
    }

    private void promptForPyramidId(Scanner scan) {
        System.out.print("Enter a pyramid id: ");

        try {
            Integer id = Integer.valueOf(scan.nextLine());
            Pyramid pyramid = pyramidById.get(id);

            if (pyramid == null) {
                System.out.println("No pyramid found with that id.");
                return;
            }

            requestedPyramidIds.add(id);
            printMenuLine();
            printPyramidDetails(pyramid);
            printMenuLine();
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid pyramid id.");
        }
    }

    private void printRequestedPyramidReport() {
        if (requestedPyramidIds.isEmpty()) {
            System.out.println("No pyramids have been requested yet.");
            return;
        }

        printMenuLine();
        System.out.println("Requested Pyramids");

        for (Integer pyramidId : requestedPyramidIds) {
            Pyramid pyramid = pyramidById.get(pyramidId);

            if (pyramid != null) {
                System.out.printf("%d\t%s\n", pyramid.getId(), pyramid.getName());
            }
        }

        printMenuLine();
    }

    private void printPyramidSummary(Pyramid pyramid) {
        System.out.printf("Pyramid %s\n", pyramid.getName());
        System.out.printf("\tid: %d\n", pyramid.getId());
        System.out.println("\tcontributors:");

        for (String contributorHash : pyramid.getContributors()) {
            Pharaoh pharaoh = pharaohByHieroglyphic.get(contributorHash);
            if (pharaoh != null) {
                System.out.printf("\t- %s\n", pharaoh.getName());
            } else {
                System.out.printf("\t- %s\n", contributorHash);
            }
        }
    }

    private void printPyramidDetails(Pyramid pyramid) {
        System.out.printf("Pyramid %s\n", pyramid.getName());
        System.out.printf("\tid: %d\n", pyramid.getId());

        int totalContribution = 0;

        for (String contributorHash : pyramid.getContributors()) {
            Pharaoh pharaoh = pharaohByHieroglyphic.get(contributorHash);
            if (pharaoh == null) {
                System.out.printf("\t- %s\n", contributorHash);
                continue;
            }

            totalContribution += pharaoh.getContribution();
            System.out.printf("\t- %s\n", pharaoh.getName());
            System.out.printf("\t  gold: %d\n", pharaoh.getContribution());
        }

        System.out.printf("\ttotal contribution: %d gold coins\n", totalContribution);
    }
}
