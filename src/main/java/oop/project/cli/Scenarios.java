package oop.project.cli;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Scenarios {

    /**
     * Parses and returns the arguments of a command (one of the scenarios
     * below) into a Map of names to values. This method is provided as a
     * starting point that works for most groups, but depending on your command
     * structure and requirements you may need to make changes to adapt it to
     * your needs - use whatever is convenient for your design.
     */
    public static Map<String, Object> parse(String command) {
        //This assumes commands follow a similar structure to unix commands,
        //e.g. `command [arguments...]`. If your project uses a different
        //structure, e.g. Lisp syntax like `(command [arguments...])`, you may
        //need to adjust this a bit to work as expected.
        var split = command.split(" ", 2);
        var base = split[0];
        var arguments = split.length == 2 ? split[1] : "";
        return switch (base) {
            case "add" -> add(arguments);
            case "sub" -> sub(arguments);
            case "sqrt" -> sqrt(arguments);
            case "calc" -> calc(arguments);
            case "date" -> date(arguments);
            default -> throw new IllegalArgumentException("Unknown command.");
        };
    }

    /**
     * Takes two positional arguments:
     *  - {@code left: <your integer type>}
     *  - {@code right: <your integer type>}
     */
//    private static Map<String, Object> add(String arguments) {
//        //TODO: Parse arguments and extract values.
//        int left = 0; //or BigInteger, etc.
//        int right = 0;
//        return Map.of("left", left, "right", right);
//    }
    private static Map<String, Object> add(String arguments) {
        String[] parts = arguments.split("\\s+");
        int left = 0; // Default value for left if not provided
        int right = 0; // Default value for right if not provided

        if (parts.length > 0) {
            try {
                left = Integer.parseInt(parts[0]); // Try parsing the first argument
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("First argument must be an integer.");
            }
        }

        if (parts.length > 1) {
            try {
                right = Integer.parseInt(parts[1]); // Try parsing the second argument
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Second argument must be an integer.");
            }
        }

        return Map.of("left", left, "right", right); // Return the results with potentially default values
    }




    /**
     * Takes two <em>named</em> arguments:
     *  - {@code left: <your decimal type>} (optional)
     *     - If your project supports default arguments, you could also parse
     *       this as a non-optional decimal value using a default of 0.0.
     *  - {@code right: <your decimal type>} (required)
     */
//    static Map<String, Object> sub(String arguments) {
//        //TODO: Parse arguments and extract values.
//        Optional<Double> left = Optional.empty();
//        double right = 0.0;
//        return Map.of("left", left, "right", right);
//    }
    static Map<String, Object> sub(String arguments) {
        Map<String, String> argsMap = parseFlagArguments(arguments);

        double left = 0.0; // Default value for left
        double right; // Right is required, no default specified directly here

        if (!argsMap.containsKey("right")) {
            throw new IllegalArgumentException("Argument 'right' is required."); // Right is required
        }

        try {
            right = Double.parseDouble(argsMap.get("right")); // Parse the 'right' argument
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Right argument must be a valid number."); // Error if non-numeric
        }

        if (argsMap.containsKey("left")) {
            try {
                left = Double.parseDouble(argsMap.get("left")); // Parse the 'left' argument if it exists
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Left argument must be a valid number."); // Error if non-numeric
            }
        } // Use default value for 'left' if not specified

        Map<String, Object> results = new HashMap<>();
        results.put("left", left);
        results.put("right", right); // Construct results with 'left' and 'right'

        argsMap.remove("right"); // Clean up the argument map
        argsMap.remove("left");
        if (!argsMap.isEmpty()) {
            throw new IllegalArgumentException("Extraneous or incorrect arguments provided."); // Handle extra unexpected args
        }

        return results;
    }

    private static Map<String, String> parseFlagArguments(String arguments) {
        Map<String, String> argsMap = new HashMap<>();
        String[] tokens = arguments.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].startsWith("--")) {
                if (i + 1 < tokens.length && !tokens[i + 1].startsWith("--")) {
                    argsMap.put(tokens[i].substring(2), tokens[i + 1]);
                    i++;  // Skip the value to avoid double-processing
                } else {
                    throw new IllegalArgumentException("Expected a value after " + tokens[i]);
                }
            } else {
                // If a token does not start with '--', it's unexpected
                throw new IllegalArgumentException("Unexpected argument: " + tokens[i]);
            }
        }
        return argsMap;
    }

    /**
     * Takes one positional argument:
     *  - {@code number: <your integer type>} where {@code number >= 0}
     */
//    static Map<String, Object> sqrt(String arguments) {
//        //TODO: Parse arguments and extract values.
//        int number = 0;
//        return Map.of("number", number);
//    }
    static Map<String, Object> sqrt(String arguments) {
        int number = Integer.parseInt(arguments.trim());
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }
        return Map.of("number", number);
    }


    /**
     * Takes one positional argument:
     *  - {@code subcommand: "add" | "div" | "sqrt" }, aka one of these values.
     *     - Note: Not all projects support subcommands, but if yours does you
     *       may want to take advantage of this scenario for that.
     */
//    static Map<String, Object> calc(String arguments) {
//        //TODO: Parse arguments and extract values.
//        String subcommand = "";
//        return Map.of("subcommand", subcommand);
//    }
    static Map<String, Object> calc(String arguments) {
        // This should handle inputs like "calc add", "calc sub", "calc sqrt"
        if (arguments == null || arguments.trim().isEmpty()) {
            // Handling case where no subcommand is provided, e.g., "calc"
            throw new IllegalArgumentException("No subcommand provided.");
        }

        String[] parts = arguments.trim().split("\\s+", 2);
        String subcommand = parts[0];

        // We expect only the subcommand name for the test cases.
        return switch (subcommand) {
            case "add", "sub", "sqrt", "date" -> Map.of("subcommand", subcommand);
            default -> throw new IllegalArgumentException("Unknown subcommand: " + subcommand);
        };
    }



    /**
     * Takes one positional argument:
     *  - {@code date: Date}, a custom type representing a {@code LocalDate}
     *    object (say at least yyyy-mm-dd, or whatever you prefer).
     *     - Note: Consider this a type that CANNOT be supported by your library
     *       out of the box and requires a custom type to be defined.
     */
//    static Map<String, Object> date(String arguments) {
//        //TODO: Parse arguments and extract values.
//        LocalDate date = LocalDate.EPOCH;
//        return Map.of("date", date);
//    }
    static Map<String, Object> date(String arguments) {
        try {
            LocalDate date = LocalDate.parse(arguments.trim());
            return Map.of("date", date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
        }
    }

    private static Map<String, String> parseNamedArguments(String arguments) {
        Map<String, String> argsMap = new HashMap<>();
        String[] tokens = arguments.split("\\s+");
        for (String token : tokens) {
            String[] parts = token.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Named arguments must be in the form name=value");
            }
            argsMap.put(parts[0], parts[1]);
        }
        return argsMap;
    }



    //TODO: Add your own scenarios based on your software design writeup. You
    //should have a couple from pain points at least, and likely some others
    //for notable features. This doesn't need to be exhaustive, but this is a
    //good place to test/showcase your functionality in context.

}