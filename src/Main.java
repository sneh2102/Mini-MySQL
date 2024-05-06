import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

public class Main {
    private static String currentDatabase = "";
    private static String originalDatabase = "";
    private static String[] schemaColumns;
    private static ArrayList<String> tableData = new ArrayList<>();
    private static final String BUFFER_DIRECTORY = "./DBMS/Buffer";
    private static final String DBMS_DIRECTORY = "./DBMS";


    public static void main(String[] args) {
        ddl_dml();

//        while (true) {
//            System.out.println("Select an option:");
//            System.out.println("1. Login");
//            System.out.println("2. Sign Up");
//            System.out.println("3. Exit");
//            int choice = scanner.nextInt();
//
//            int captcha = random.nextInt(10000);
//
//
//            switch (choice) {
//                case 1:
//
//                        System.out.print("Enter your username: ");
//                        String loginUsername = scanner.next();
//                        System.out.print("Enter your password: ");
//                        String loginPassword = scanner.next();
//                        System.out.println("Captcha: " + captcha);
//                        System.out.print("Enter the captcha: ");
//                    int enteredCaptcha = scanner.nextInt();
//                        if (enteredCaptcha == captcha) {
//                        // Check if username and hashed password match
//                        if (userDatabase.authenticateUser(loginUsername, loginPassword)) {
//                            System.out.println("Authentication successful.");
//
//                        } else {
//                            System.out.println("Authentication failed.");
//                        }
//                    } else {
//                        System.out.println("Captcha incorrect. Authentication failed.");
//                    }
//                    break;
//                case 2:
//
//                        System.out.print("Enter a username: ");
//                        String signupUsername = scanner.next();
//                        System.out.print("Enter a password: ");
//                        String signupPassword = scanner.next();
//                        System.out.println("Captcha: " + captcha);
//                        System.out.print("Enter the captcha: ");
//                    System.out.print("Enter the captcha: ");
//                    int enteredCaptchaSignup = scanner.nextInt();
//                    if (enteredCaptchaSignup == captcha) {
//                        userDatabase.addUser(new User(signupUsername, signupPassword));
//                        System.out.println("User registered successfully.");
//                    } else {
//                        System.out.println("Captcha incorrect. User registration failed.");
//                    }
//                    break;
//                case 3:
//                    System.exit(0);
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
    }
    private static void ddl_dml(){
        while(true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("DBMS> ");
            String command = scanner.nextLine();
            if(!executeQuery(command)){
                break;
            }
        }
    }

    private static boolean executeQuery(String command){

            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(now);

            logQuery(command, currentTime);

            if (command.startsWith("create database ")) {
                String dbName = command.substring("create database ".length());
                createDatabase(dbName);
            } else if (command.startsWith("use ")) {
                String dbName = command.substring("use ".length());
                useDatabase(dbName);
            } else if (command.startsWith("create table ")) {
                String tableName = command.split("\\s+")[2];
                String schema = command.substring(command.indexOf("(") , command.length());
                createTable(tableName, schema);
            } else if (command.startsWith("insert into ")) {
                String tableName = command.split("\\s+")[2];
                String values = command.substring(command.indexOf("values(") + 7, command.length() - 1);
                insertIntoTable(tableName, values);
            } else if (command.startsWith("select * from ")) {
                String tableName = command.split("\\s+")[3];
                String whereClause = extractWhereClause(command);
                selectFromTable(tableName, null, whereClause);
            } else if (command.startsWith("select ")) {
                String[] parts = command.split("\\s+");
                String tableName = parts[parts.length - 1];
                String[] columns = Arrays.copyOfRange(parts, 1, parts.length - 2);
                String whereClause = extractWhereClause(command);
                selectFromTable(tableName, columns, whereClause);
            } else if (command.startsWith("delete from ")) {
                String tableName = command.split("\\s+")[2];
                String whereClause = extractWhereClause(command);
                deleteFromTable(tableName, whereClause);
            }else if (command.startsWith("update ")) {
                String tableName = command.split("\\s+")[1];
                String setClause = extractSetClause(command);
                String whereClause = extractWhereClause(command);
                updateTable(tableName, setClause, whereClause);
            } else if (command.startsWith("start transaction")) {
                startTransaction();
            } else if (command.startsWith("commit")) {
                commitTransaction();
            } else if (command.startsWith("rollback")) {
                rollbackTransaction();
            } else if (command.equals("exit")) {
                System.out.println("Exiting DBMS.");
                return false;
            } else {
                System.out.println("Invalid command. Please use 'create database', 'use database', 'create table', 'insert into', 'select', or 'exit'.");
            }
            return true;
        }

    private static List<String> getTableNames() {
        return Collections.emptyList();
    }
    private static void createDatabase(String dbName) {
        Path databasePath = Paths.get("./DBMS", dbName);

        if (Files.exists(databasePath)) {
            System.out.println("Database '" + dbName + "' already exists.");
        } else {
            try {
                Files.createDirectory(databasePath);
                System.out.println("Database '" + dbName + "' created.");
            } catch (IOException e) {
                System.err.println("Error creating the database: " + e.getMessage());
            }
        }
    }

    private static void useDatabase(String dbName) {
        Path databasePath = Paths.get("./DBMS", dbName);

        if (Files.exists(databasePath) && Files.isDirectory(databasePath)) {
            currentDatabase = dbName;
            // Set schemaColumns based on the existing table schema
            schemaColumns = loadSchemaColumns();
            System.out.println("Using database '" + dbName + "'.");
        } else {
            System.out.println("Database '" + dbName + "' does not exist.");
        }
    }

    private static String[] loadSchemaColumns() {
        // Load schema columns from the current database directory
        List<String> columns = new ArrayList<>();
        if (!currentDatabase.isEmpty()) {
            try {
                Path tablePath = Paths.get("./DBMS", currentDatabase);
                Files.walk(tablePath)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                List<String> lines = Files.readAllLines(file);
                                if (!lines.isEmpty()) {
                                    String schema = lines.get(0);
                                    String[] schemaParts = schema.split(",\\s*");
                                    columns.addAll(Arrays.asList(schemaParts));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return columns.toArray(new String[0]);
    }

    private static void startTransaction() {
        try {
            originalDatabase = currentDatabase;

            // Create buffer directory if it doesn't exist
            Path bufferDir = Paths.get(BUFFER_DIRECTORY);
            if (!Files.exists(bufferDir)) {
                Files.createDirectory(bufferDir);
            }

            // Copy text files from current database directory to buffer directory
            if (!currentDatabase.isEmpty()) {
                Path currentDir = Paths.get("./DBMS", currentDatabase);
                Files.walk(currentDir)
                        .filter(Files::isRegularFile)
                        .filter(file -> file.toString().endsWith(".txt"))
                        .forEach(file -> {
                            Path targetFile = bufferDir.resolve(currentDir.relativize(file));
                            try {
                                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.err.println("Error copying file: " + e.getMessage());
                            }
                        });
                System.out.println("Transaction started.");
                currentDatabase="Buffer";
            } else {
                System.out.println("No database selected.");
            }
        } catch (IOException e) {
            System.err.println("Error starting transaction: " + e.getMessage());
        }
    }


    private static void commitTransaction() {
        try {
            if (!currentDatabase.isEmpty() && !originalDatabase.isEmpty()) {
                // Move files from buffer directory to original database directory
                Path sourceDir = Paths.get(BUFFER_DIRECTORY, currentDatabase);
                Path targetDir = Paths.get("./DBMS", originalDatabase);
                Files.walk(sourceDir)
                        .forEach(sourceFile -> {
                            Path targetFile = targetDir.resolve(sourceDir.relativize(sourceFile));
                            try {
                                Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.err.println("Error moving file: " + e.getMessage());
                            }
                        });
                System.out.println("partial");

                // Delete buffer directory
                Files.walk(Paths.get(BUFFER_DIRECTORY))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);

                System.out.println("Transaction committed.");
            } else {
                System.out.println("No database selected or original database not set.");
            }
        } catch (IOException e) {
            System.err.println("Error committing transaction: " + e.getMessage());
        }
    }




    private static void rollbackTransaction() {
        try {
            // Delete buffer directory
            Files.walk(Paths.get(BUFFER_DIRECTORY))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            System.out.println("Transaction rolled back.");

            // Reset current database to original database
            currentDatabase = originalDatabase;
        } catch (IOException e) {
            System.err.println("Error rolling back transaction: " + e.getMessage());
        }
    }
    private static void createTable(String tableName, String command) {
        if (currentDatabase.isEmpty()) {
            System.out.println("No database selected. Please use 'use database' to select a database.");
            return;
        }

        // Find the schema between parentheses
        int startIndex = command.indexOf("(");
        int endIndex = command.lastIndexOf(")");

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            System.out.println("Invalid schema format.");
            return;
        }

        String schema = command.substring(startIndex + 1, endIndex).trim();
        schemaColumns = schema.split(",\\s*");

        Path tablePath = Paths.get("./DBMS", currentDatabase, tableName + ".txt");

        if (Files.exists(tablePath)) {
            System.out.println("Table '" + tableName + "' already exists in the current database.");
        } else {
            try {
                Files.createFile(tablePath);
                FileWriter writer = new FileWriter(tablePath.toFile());
                writer.write(schema);
                writer.close();
                System.out.println("Table '" + tableName + "' created with the schema: " + schema);
            } catch (IOException e) {
                System.err.println("Error creating the table: " + e.getMessage());
            }
        }
    }

    private static void insertIntoTable(String tableName, String values) {
        if (currentDatabase.isEmpty()) {
            System.out.println("No database selected. Please use 'use database' to select a database.");
            return;
        }

        Path tablePath = Paths.get("./DBMS", currentDatabase, tableName + ".txt");

        if (!Files.exists(tablePath)) {
            System.out.println("Table '" + tableName + "' does not exist in the current database.");
            return;
        }

        try {
            String[] inputValues = values.split(",\\s*");

            if (schemaColumns.length != inputValues.length) {
                System.out.println("Number of values does not match the table schema.");
                return;
            }

            FileWriter writer = new FileWriter(tablePath.toFile(), true);
            writer.write("\n" + values);
            writer.close();
            System.out.println("Inserted values into table '" + tableName + "'.");
        } catch (IOException e) {
            System.err.println("Error inserting data into the table: " + e.getMessage());
        }
    }

    private static String extractWhereClause(String command) {
        Pattern pattern = Pattern.compile("where\\s+(.+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void selectFromTable(String tableName, String[] columns, String whereClause) {
        if (currentDatabase.isEmpty()) {
            System.out.println("No database selected. Please use 'use database' to select a database.");
            return;
        }

        Path tablePath = Paths.get("./DBMS", currentDatabase, tableName + ".txt");

        if (!Files.exists(tablePath)) {
            System.out.println("Table '" + tableName + "' does not exist in the current database.");
            return;
        }

        try {
            List<String> rows = Files.readAllLines(tablePath);
            String schema = rows.get(0);
            schemaColumns = schema.split(",\\s*");

            if (columns == null) {

                for (String row : rows) {
                    String[] values = row.split(",\\s*");
                    if (whereClause == null || evaluateWhereClause(values, whereClause)) {
                        for (String value : values) {
                            System.out.print(value + "\t");
                        }
                        System.out.println();
                    }
                }
            } else {

                Map<String, Integer> columnIndexMap = new HashMap<>();
                for (int i = 0; i < schemaColumns.length; i++) {
                    columnIndexMap.put(schemaColumns[i], i);
                }

                for (String column : columns) {
                    if (!columnIndexMap.containsKey(column)) {
                        System.out.println("Column '" + column + "' does not exist in the table schema.");
                        return;
                    }
                }

                for (String row : rows) {
                    String[] values = row.split(",\\s*");
                    if (whereClause == null || evaluateWhereClause(values, whereClause)) {
                        for (String column : columns) {
                            int index = columnIndexMap.get(column);
                            System.out.print(values[index] + "\t");
                        }
                        System.out.println();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading data from the table: " + e.getMessage());
        }
    }

    private static void deleteFromTable(String tableName, String whereClause) {
        if (currentDatabase.isEmpty()) {
            System.out.println("No database selected. Please use 'use database' to select a database.");
            return;
        }

        Path tablePath = Paths.get("./DBMS", currentDatabase, tableName + ".txt");

        if (!Files.exists(tablePath)) {
            System.out.println("Table '" + tableName + "' does not exist in the current database.");
            return;
        }

        try {

            tableData = new ArrayList<>(Files.readAllLines(tablePath));

            if (tableData.size() > 1) {

                schemaColumns = tableData.get(0).split(",\\s*");


                List<Integer> rowsToDelete = new ArrayList<>();
                for (int i = 1; i < tableData.size(); i++) {
                    String[] values = tableData.get(i).split(",\\s*");
                    if (evaluateWhereClause(values, whereClause)) {
                        rowsToDelete.add(i);
                    }
                }

                if (!rowsToDelete.isEmpty()) {

                    Collections.reverse(rowsToDelete);
                    for (int index : rowsToDelete) {
                        tableData.remove(index);
                    }


                    Files.write(tablePath, tableData);

                    System.out.println("Deleted " + rowsToDelete.size() + " row(s) from table '" + tableName + "'.");
                } else {
                    System.out.println("No rows match the delete condition.");
                }
            } else {
                System.out.println("The table is empty.");
            }
        } catch (IOException e) {
            System.err.println("Error deleting data from the table: " + e.getMessage());
        }
    }


    private static boolean evaluateWhereClause(String[] values, String whereClause) {
        if (whereClause != null && schemaColumns != null) {
            String[] conditions = whereClause.split("\\s+");
            if (conditions.length == 3) {
                String column = conditions[0];
                String operator = conditions[1];
                String value = conditions[2];

                Map<String, Integer> columnIndexMap = new HashMap<>();
                for (int i = 0; i < schemaColumns.length; i++) {
                    columnIndexMap.put(schemaColumns[i], i);
                }

                if (columnIndexMap.containsKey(column)) {
                    int index = columnIndexMap.get(column);
                    String cellValue = values[index];

                    if ("=".equals(operator) && cellValue.equals(value)) {
                        return true;
                    } else if (">".equals(operator) && isNumeric(cellValue) && Integer.parseInt(cellValue) > Integer.parseInt(value)) {
                        return true;
                    } else if ("<".equals(operator) && isNumeric(cellValue) && Integer.parseInt(cellValue) < Integer.parseInt(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String extractSetClause(String command) {
        Pattern pattern = Pattern.compile("set\\s+(.+)\\s+where", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void updateTable(String tableName, String setClause, String whereClause) {
        if (currentDatabase.isEmpty()) {
            System.out.println("No database selected. Please use 'use database' to select a database.");
            return;
        }

        Path tablePath = Paths.get("./DBMS", currentDatabase, tableName + ".txt");

        if (!Files.exists(tablePath)) {
            System.out.println("Table '" + tableName + "' does not exist in the current database.");
            return;
        }

        try {

            tableData = new ArrayList<>(Files.readAllLines(tablePath));

            if (tableData.size() > 1) {

                schemaColumns = tableData.get(0).split(",\\s*");


                for (int i = 1; i < tableData.size(); i++) {
                    String[] values = tableData.get(i).split(",\\s*");
                    if (evaluateWhereClause(values, whereClause)) {
                        String[] setValues = setClause.split(",\\s*");
                        for (String setValue : setValues) {
                            String[] parts = setValue.split("=");
                            if (parts.length == 2) {
                                String column = parts[0].trim();
                                String value = parts[1].trim();
                                Map<String, Integer> columnIndexMap = new HashMap<>();
                                for (int j = 0; j < schemaColumns.length; j++) {
                                    columnIndexMap.put(schemaColumns[j], j);
                                }
                                if (columnIndexMap.containsKey(column)) {
                                    int index = columnIndexMap.get(column);
                                    values[index] = value;
                                }
                            }
                        }
                        tableData.set(i, String.join(", ", values));
                    }
                }

                Files.write(tablePath, tableData);

                System.out.println("Updated rows in table '" + tableName + "'.");
            } else {
                System.out.println("The table is empty.");
            }
        } catch (IOException e) {
            System.err.println("Error updating data in the table: " + e.getMessage());
        }
    }



    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private static void logQuery(String query, String time) {
        try {
            Path logPath = Paths.get("./DBMS", "log.txt");

            if (!Files.exists(logPath)) {
                Files.createFile(logPath);
            }

            Files.write(logPath, ("[Query] "+query + " at " + time + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error logging the query: " + e.getMessage());
        }
    }



}
