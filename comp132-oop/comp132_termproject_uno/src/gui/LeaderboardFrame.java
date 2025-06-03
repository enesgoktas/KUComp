package gui;

import javax.swing.*;
import java.awt.*;
import users.UserManager;

public class LeaderboardFrame extends JFrame {
    private UserManager userManager;

    public LeaderboardFrame(UserManager userManager) {
        super("Leaderboard");
        this.userManager = userManager;
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        String[] columnNames = {"Username", "Games Played", "Wins", "Losses", "Total Score"};
        Object[][] data = {}; // This should be dynamically filled based on user data

        // For demonstration, populate with placeholder data
        data = new Object[][] {
            {"Player1", 10, 5, 5, 150},
            {"Player2", 20, 10, 10, 300},
            {"Player3", 15, 7, 8, 210}
        };

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        add(scrollPane);
    }

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        EventQueue.invokeLater(() -> {
            new LeaderboardFrame(userManager).setVisible(true);
        });
    }
}
