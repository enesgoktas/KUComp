package gui;

import users.User;
import users.UserManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private UserManager userManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame(UserManager userManager) {
        super("Login or Register");
        this.userManager = userManager;
        initializeUI();
        setLocationRelativeTo(null);
        setSize(300, 200);
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        add(loginButton);

        registerButton = new JButton("Register");
        registerButton.addActionListener(e -> register());
        add(registerButton);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        User user = userManager.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful");
            this.dispose();
            openGameInterface(user);
        } else {
            JOptionPane.showMessageDialog(this, "Login Failed");
        }
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (userManager.register(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration Successful");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists");
        }
    }

    private void openGameInterface(User user) {
        EventQueue.invokeLater(() -> {
            JFrame mainMenuFrame = new MainMenuFrame(userManager, user);
            mainMenuFrame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserManager userManager = new UserManager();
            new LoginFrame(userManager).setVisible(true);
        });
    }
}
