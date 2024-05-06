import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Authenticator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDatabase userDatabase = new UserDatabase();
        Random random = new Random();

        while (true) {
            System.out.println("Select an option:");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();

            // Generate a random captcha
            int captcha = random.nextInt(10000);
            System.out.println("Captcha: " + captcha);

            switch (choice) {
                case 1:
                    // Login
                    System.out.print("Enter the captcha: ");
                    int enteredCaptcha = scanner.nextInt();
                    if (enteredCaptcha == captcha) {
                        System.out.print("Enter your username: ");
                        String loginUsername = scanner.next();
                        System.out.print("Enter your password: ");
                        String loginPassword = scanner.next();

                        // Check if username and hashed password match
                        if (userDatabase.authenticateUser(loginUsername, loginPassword)) {
                            System.out.println("Authentication successful.");
                        } else {
                            System.out.println("Authentication failed.");
                        }
                    } else {
                        System.out.println("Captcha incorrect. Authentication failed.");
                    }
                    break;
                case 2:
                    // Sign Up
                    System.out.print("Enter the captcha: ");
                    int enteredCaptchaSignup = scanner.nextInt();
                    if (enteredCaptchaSignup == captcha) {
                        System.out.print("Enter a username: ");
                        String signupUsername = scanner.next();
                        System.out.print("Enter a password: ");
                        String signupPassword = scanner.next();

                        userDatabase.addUser(new User(signupUsername, signupPassword));
                        System.out.println("User registered successfully.");
                    } else {
                        System.out.println("Captcha incorrect. User registration failed.");
                    }
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}


class User {
    private String username;
    private String passwordHash;

    public User(String username, String password) {
        this.username = username;
        this.passwordHash = hashPassword(password);
    }

    String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder(2 * hashBytes.length);

            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}

class UserDatabase {
    private List<User> users = new ArrayList<>();
    private static final String USER_DATA_FILE = "userData.txt";

    public UserDatabase() {
        loadUsers();
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public boolean authenticateUser(String username, String inputPassword) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPasswordHash().equals(user.hashPassword(inputPassword))) {
                return true;
            }
        }
        return false;
    }

    private void loadUsers() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(USER_DATA_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.add(new User(parts[0], parts[1]));
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(USER_DATA_FILE));
            for (User user : users) {
                bw.write(user.getUsername() + "," + user.getPasswordHash());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
