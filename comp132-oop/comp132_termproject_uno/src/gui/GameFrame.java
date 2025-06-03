package gui;

import core.Card;
import core.GameManager;
import core.HumanPlayer;
import core.Player;
import users.User;
import users.UserManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class GameFrame extends JFrame {
    private GameManager gameManager;
    private User currentUser;
    private UserManager userManager;
    private String sessionName;
    private JPanel cardsPanel;
    private JLabel topCardLabel;
    private JButton drawButton;
    private JButton unoButton;
    private JLabel gameInfoLabel;  // Label to display session name and direction


    public GameFrame(User currentUser, UserManager userManager, int numberOfPlayers, String sessionName) {
        super("Uno Game - " + currentUser.getUsername());
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.sessionName = sessionName;
        this.gameManager = new GameManager(numberOfPlayers);
        initializeUI();
        setLocationRelativeTo(null);
        setSize(800, 600);
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        topCardLabel = new JLabel("Top Card: ");
        add(topCardLabel, BorderLayout.NORTH);
        
        gameInfoLabel = new JLabel();
        add(gameInfoLabel, BorderLayout.SOUTH);

        cardsPanel = new JPanel(new FlowLayout());
        add(cardsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        drawButton = new JButton("Draw Card");
        drawButton.addActionListener(e -> handleDrawCard());
        buttonPanel.add(drawButton);

        unoButton = new JButton("Declare UNO");
        unoButton.addActionListener(e -> handleDeclareUno());
        buttonPanel.add(unoButton);

        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> {
            String fileName = "UnoGame_" + System.currentTimeMillis() + ".ser";
            gameManager.saveGame(gameManager, fileName);
            // Optionally return to the main menu or confirm the save
            JOptionPane.showMessageDialog(this, "Game saved successfully.", "Save Game", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); // Closes the game window, potentially returning to the main menu
        });

        buttonPanel.add(drawButton);
        buttonPanel.add(unoButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        updateUI();
    }


    private void updateUI() {
        // Update top card
        Card topCard = gameManager.getTopCard();
        String wildColor = gameManager.getWildColor();
        if (topCard != null) {
            if (topCard.getColor().equals("Wild")) {
                topCardLabel.setText("Top Card: " + topCard.toString() + " (Wild Color: " + wildColor + ")");
            } else {
                topCardLabel.setText("Top Card: " + topCard.toString());
            }
        } else {
            topCardLabel.setText("No top card present.");
        }
        
        gameInfoLabel.setText("Session: " + sessionName + " | Direction: " + (gameManager.isClockwise() ? "Clockwise" : "Counter-clockwise"));

        // Clear existing content
        cardsPanel.removeAll();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS)); // Align components vertically

        // Update player cards and show card counts for all players
        for (Player player : gameManager.getPlayers()) {
            JLabel playerLabel = new JLabel(player.getName() + ": " + player.getHand().size() + " cards");
            playerLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set a larger font size for better visibility
            cardsPanel.add(playerLabel);

            if (player == gameManager.getCurrentPlayer()) {
                for (Card card : player.getHand()) {
                    JButton cardButton = new JButton(card.toString());
                    cardButton.setFont(new Font("Arial", Font.PLAIN, 16)); // Slightly smaller font for cards
                    cardButton.addActionListener(e -> handleCardPlay(card));
                    cardsPanel.add(cardButton);
                }
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }


    private void handleDrawCard() {
        Card drawnCard = gameManager.getDeck().drawCard();
        if (drawnCard != null) {
            gameManager.getCurrentPlayer().getHand().add(drawnCard);
            updateUI();
        } else {
            JOptionPane.showMessageDialog(this, "No more cards to draw. Wait for other players to play.");
        }
    }

    private void handleDeclareUno() {
        // Implement Uno declaration logic
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (currentPlayer.getHand().size() == 2) {  // before the last card is played
            JOptionPane.showMessageDialog(this, currentUser.getUsername() + " declares UNO!");
        } else {
            JOptionPane.showMessageDialog(this, "You can only declare UNO when you have 1 card left!");
        }
    }

    private void handleCardPlay(Card card) {
        if (gameManager.playTurn(gameManager.getCurrentPlayer(), card)) {
            if (gameManager.getCurrentPlayer().getHand().size() == 1) {
                // Automatically declare UNO for simplicity
                handleDeclareUno();
            }
            if (card.getColor().equals("Wild")) {
                chooseColor(); // If the card played is a Wild card, choose a color
            }
            updateUI();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid move!");
        }
    }

    private void chooseColor() {
        String[] options = new String[]{"Red", "Green", "Blue", "Yellow"};
        String color = (String) JOptionPane.showInputDialog(null, "Choose a color:",
                "Color Selection", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        gameManager.setWildColor(color);
    }
    
    private void drawAndMaybePlayCard() {
        Player currentPlayer = gameManager.getCurrentPlayer();
        if (currentPlayer instanceof HumanPlayer) {
            Card drawnCard = gameManager.getDeck().drawCard();
            currentPlayer.getHand().add(drawnCard); // Adding the drawn card to the hand
            updateUI(); // Update UI to show the new card

            if (((HumanPlayer) currentPlayer).isValidMove(drawnCard, gameManager.getTopCard())) {
                int play = JOptionPane.showConfirmDialog(null, "You drew a playable card. Would you like to play it now?", "Play Card", JOptionPane.YES_NO_OPTION);
                if (play == JOptionPane.YES_OPTION) {
                    handleCardPlay(drawnCard);
                } else {
                    gameManager.nextTurn(); // Skip to the next player if the card is not played
                }
            } else {
                JOptionPane.showMessageDialog(null, "The drawn card cannot be played. Skipping to next player.");
                gameManager.nextTurn(); // Skip to the next player
            }
        }
    }

    private void checkForNoValidMoves() {
        boolean hasValidMove = false;
        for (Card card : gameManager.getCurrentPlayer().getHand()) {
            if (((HumanPlayer) gameManager.getCurrentPlayer()).isValidMove(card, gameManager.getTopCard())) {
                hasValidMove = true;
                break;
            }
        }

        if (!hasValidMove) {
            JOptionPane.showMessageDialog(null, "No valid moves. You need to draw a card.");
            drawAndMaybePlayCard();
        }
    }

    
}
