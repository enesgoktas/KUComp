package gui;

import users.User;
import users.UserManager;

import javax.swing.*;

import core.GameManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

public class MainMenuFrame extends JFrame {
    private UserManager userManager;
    private User currentUser;
    private JTable leaderboardTable;

    public MainMenuFrame(UserManager userManager, User currentUser) {
        super("Main Menu");
        this.userManager = userManager;
        this.currentUser = currentUser;
        initializeUI();
        setLocationRelativeTo(null);
        setSize(600, 400);
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        leaderboardTable = new JTable();
        updateLeaderboard();
        add(new JScrollPane(leaderboardTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> startNewGame());
        buttonPanel.add(newGameButton);

        JButton loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(e -> displaySavedGames());
        buttonPanel.add(loadGameButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void displaySavedGames() {
        File[] savedGames = getSavedGames();
        JList<File> list = new JList<>(savedGames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Select a Game to Load", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            File selectedFile = list.getSelectedValue();
            if (selectedFile != null) {
                GameManager gameManager = GameManager.loadGame(selectedFile.getAbsolutePath());
                GameFrame gameFrame = new GameFrame(currentUser, userManager, result, getTitle());
                gameFrame.setVisible(true);
                this.dispose(); // Optionally close the main menu
            }
        }
    }

    private File[] getSavedGames() {
        File dir = new File(".");  // Directory where files are saved, adjust if using a specific directory
        return dir.listFiles((dir1, name) -> name.startsWith("UnoGame_") && name.endsWith(".ser"));
    }

    private void updateLeaderboard() {
        String[] columnNames = {"Username", "Total Score", "Games Played", "Wins", "Losses", "Win/Loss Ratio", "Avg Score/Game"};
        Object[][] data = new Object[userManager.getAllUsers().size()][columnNames.length];
        int i = 0;
        for (Map.Entry<String, User> entry : userManager.getAllUsers().entrySet()) {
            User user = entry.getValue();
            data[i][0] = user.getUsername();
            data[i][1] = user.getTotalScore();
            data[i][2] = user.getGamesPlayed();
            data[i][3] = user.getWins();
            data[i][4] = user.getLosses();
            data[i][5] = String.format("%.2f", user.getWinLossRatio());
            data[i][6] = String.format("%.2f", user.getAverageScorePerGame());
            i++;
        }
        leaderboardTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void startNewGame() {
        String sessionName = JOptionPane.showInputDialog(this, "Enter session name:");
        int numberOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of players (2-10):"));
        new GameFrame(currentUser, userManager, numberOfPlayers, sessionName).setVisible(true);
        this.dispose();
    }

    private void loadGame() {
        // Load game logic to be implemented
        JOptionPane.showMessageDialog(this, "Load Game feature to be implemented");
    }
}
