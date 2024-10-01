import java.util.HashMap;
import java.util.Map;

class MyClass {
    public static void main(String[] args) {
        // Test case 1 JSON string
        String jsonString1 = "{\n" +
                "    \"keys\": {\n" +
                "        \"n\": 4,\n" +
                "        \"k\": 3\n" +
                "    },\n" +
                "    \"1\": {\n" +
                "        \"base\": \"10\",\n" +
                "        \"value\": \"4\"\n" +
                "    },\n" +
                "    \"2\": {\n" +
                "        \"base\": \"2\",\n" +
                "        \"value\": \"111\"\n" +
                "    },\n" +
                "    \"3\": {\n" +
                "        \"base\": \"10\",\n" +
                "        \"value\": \"12\"\n" +
                "    },\n" +
                "    \"4\": {\n" +
                "        \"base\": \"4\",\n" +
                "        \"value\": \"213\"\n" +
                "    }\n" +
                "}";

        // Test case 2 JSON string
        String jsonString2 = "{\n" +
                "    \"keys\": {\n" +
                "        \"n\": 4,\n" +
                "        \"k\": 3\n" +
                "    },\n" +
                "    \"1\": {\n" +
                "        \"base\": \"10\",\n" +
                "        \"value\": \"5\"\n" +
                "    },\n" +
                "    \"2\": {\n" +
                "        \"base\": \"2\",\n" +
                "        \"value\": \"101\"\n" +
                "    },\n" +
                "    \"3\": {\n" +
                "        \"base\": \"10\",\n" +
                "        \"value\": \"15\"\n" +
                "    },\n" +
                "    \"4\": {\n" +
                "        \"base\": \"3\",\n" +
                "        \"value\": \"100\"\n" +
                "    }\n" +
                "}";

        // Process both test cases
        System.out.println("Processing Test Case 1:");
        int secret1 = findSecret(jsonString1);
        System.out.println("The constant term (c) of the polynomial for Test Case 1 is: " + secret1);

        System.out.println("\nProcessing Test Case 2:");
        int secret2 = findSecret(jsonString2);
        System.out.println("The constant term (c) of the polynomial for Test Case 2 is: " + secret2);
    }

    private static int findSecret(String jsonString) {
        Map<Integer, Integer> decodedValues = new HashMap<>();

        // Extract n and k from JSON string
        int n = extractValue(jsonString, "\"n\":");
        int k = extractValue(jsonString, "\"k\":");

        System.out.println("Number of roots (n): " + n);
        System.out.println("Minimum required roots (k): " + k);

        // Parse each root value from the JSON string
        for (int i = 1; i <= n; i++) {  // Use n for dynamic root processing
            String key = String.valueOf(i);
            String base = extractFieldValue(jsonString, key, "base");
            String value = extractFieldValue(jsonString, key, "value");

            // Check if base and value are valid
            if (base != null && !base.isEmpty() && value != null && !value.isEmpty()) {
                try {
                    int x = Integer.parseInt(key);
                    int y = Integer.parseInt(value, Integer.parseInt(base));
                    decodedValues.put(x, y);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format for key: " + key);
                }
            } else {
                System.out.println("Base or value is empty for key: " + key);
            }
        }

        // Print the decoded (x, y) pairs
        System.out.println("Decoded (x, y) pairs:");
        for (Map.Entry<Integer, Integer> entry : decodedValues.entrySet()) {
            System.out.println("x = " + entry.getKey() + ", y = " + entry.getValue());
        }

        // Calculate the constant term 'c'
        return calculateConstantTerm(decodedValues);
    }

    private static int extractValue(String jsonString, String key) {
        int value = 0;
        String searchKey = key.trim();
        int startIndex = jsonString.indexOf(searchKey);
        if (startIndex != -1) {
            int endIndex = jsonString.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = jsonString.indexOf("}", startIndex);
            String valueString = jsonString.substring(startIndex, endIndex);
            value = Integer.parseInt(valueString.split(":")[1].trim().replaceAll("[^\\d]", ""));
        }
        return value; // Return extracted value
    }

    private static String extractFieldValue(String jsonString, String key, String field) {
        String searchKey = "\"" + key + "\": {";
        int startIndex = jsonString.indexOf(searchKey);
        if (startIndex == -1) return null;

        startIndex += searchKey.length();
        int endIndex = jsonString.indexOf("}", startIndex);
        String object = jsonString.substring(startIndex, endIndex);
        
        String[] fields = object.split(",");
        for (String f : fields) {
            if (f.contains(field)) {
                return f.split(":")[1].trim().replaceAll("[\" ]", "");
            }
        }
        return null; // Default if field is not found
    }

    private static int calculateConstantTerm(Map<Integer, Integer> points) {
        int c = 0; // This will store the constant term

        // Convert the map to arrays for easy access
        int[] xValues = points.keySet().stream().mapToInt(i -> i).toArray();
        int[] yValues = points.values().stream().mapToInt(i -> i).toArray();

        // Using Lagrange interpolation to calculate the constant term 'c'
        for (int i = 0; i < yValues.length; i++) {
            int L_i = 1; // Calculate L_i(0)
            for (int j = 0; j < yValues.length; j++) {
                if (i != j) {
                    L_i *= (0 - xValues[j]) / (xValues[i] - xValues[j]);
                }
            }
            c += yValues[i] * L_i; // Summing the pieces of the constant term
        }

        return c; // Return the secret constant term
    }
}
