package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import bots.SimpleBot;

public class GameManager implements Serializable {
    private static final long serialVersionUID = 1L; 
    private List<Player> players;
    private Deck deck;
    private int currentPlayerIndex;
    private boolean isClockwise;
    private String wildColor;
    
    
    public List<Player> getPlayers() {
		return players;
	}

	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	public boolean isClockwise() {
		return isClockwise;
	}

	public String getWildColor() {
		return wildColor;
	}

    public GameManager(int numberOfPlayers) {
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.currentPlayerIndex = 0; // Start with the first player
        this.isClockwise = true;
        initializePlayers(numberOfPlayers);
        startGame();
    }

    public Deck getDeck() {
        return deck;
    }

    private void initializePlayers(int numberOfPlayers) {
        players.add(new HumanPlayer("Human Player", this)); // Add the human player
        for (int i = 1; i < numberOfPlayers; i++) {
            players.add(new SimpleBot("Bot " + i, this)); // Add AI players
        }
    }

    private void startGame() {
        for (Player player : players) {
            for (int i = 0; i < 7; i++) { // Each player gets 7 cards
                player.drawCard(deck);
            }
        }
        // Ensure the discard pile starts with a non-wild card
        deck.prepareDiscardPile();
        // Let the first player start
        players.get(currentPlayerIndex).takeTurn(deck);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Card getTopCard() {
        return deck.getTopCard();
    }

    public boolean playTurn(Player player, Card card) {
        if (player.playCard(card, getTopCard())) {
            deck.discardCard(card);
            applyCardEffect(card); // Apply card effect if it is a special card
            nextTurn();
            return true;
        }
        return false;
    }

    public void nextTurn() {
        if (!checkIfGameOver()) {
            currentPlayerIndex = (currentPlayerIndex + (isClockwise ? 1 : -1) + players.size()) % players.size();
            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer instanceof SimpleBot) {
                currentPlayer.takeTurn(deck);
            } else {
                // GUI updates for human player (assuming a method exists to update the UI for the human player)
                // updatePlayerUI(currentPlayer);
            }
        }
    }

    public void applyCardEffect(Card card) {
        switch (card.getValue()) {
            case "Wild":
            case "Wild Draw Four":
                // Ensure a color is chosen before moving to next turn
                chooseColor();
                if (card.getValue().equals("Wild Draw Four")) {
                    nextTurn();
                    drawMultipleCards(4); // Next player draws four cards
                }
                break;
            case "Draw Two":
                nextTurn();
                drawMultipleCards(2);
                break;
            case "Skip":
                nextTurn();
                break;
            case "Reverse":
                changeDirection();
                break;
            default:
                // Handle normal number cards here
                break;
        }
    }


    public void changeDirection() {
        isClockwise = !isClockwise;
    }

    public void drawMultipleCards(int count) {
        Player nextPlayer = players.get((currentPlayerIndex + (isClockwise ? 1 : -1) + players.size()) % players.size());
        for (int i = 0; i < count; i++) {
            nextPlayer.drawCard(deck);
        }
    }

    public boolean checkIfGameOver() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                displayGameOverScreen();
                return true;
            }
        }
        return false;
    }

    private void displayGameOverScreen() {
        calculateScores();  // Calculate final scores if needed
        JFrame gameOverFrame = new JFrame("Game Over");
        gameOverFrame.setLayout(new BorderLayout());
        gameOverFrame.setSize(300, 200);
        gameOverFrame.setLocationRelativeTo(null);
        
        JTextArea results = new JTextArea("Game Over! Here are the results:\n");
        for (Player player : players) {
            results.append(player.getName() + ": " + player.getHand().size() + " cards left\n");
        }
        gameOverFrame.add(results, BorderLayout.CENTER);
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            gameOverFrame.dispose();
            startGame();  // Assuming startGame resets everything and starts new
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);
        gameOverFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        gameOverFrame.setVisible(true);
    }


    public void calculateScores() {
        int score = 0;
        for (Player player : players) {
            for (Card card : player.getHand()) {
                score += card.getPoints(); // Sum up points based on remaining cards
            }
        }
        // Display scores or store them as needed
        System.out.println("Game Over! Final score: " + score);
    }

    private void chooseColor() {
        // This is a simplified example for setting the wild color
        // In a real game, you'd likely get input from the player or AI
        String[] colors = {"Red", "Green", "Blue", "Yellow"};
        String chosenColor = colors[new Random().nextInt(colors.length)]; // Random for bot, user input for human
        setWildColor(chosenColor);
    }

    public void setWildColor(String color) {
        this.wildColor = color;
    }
    
    public void saveGame(GameManager gameManager, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(gameManager);
            System.out.println("Game has been saved successfully.");
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }



    public static GameManager loadGame(String filePath) {
        GameManager gameManager = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            gameManager = (GameManager) ois.readObject();
            System.out.println("Game has been loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
        }
        return gameManager;
    }
}
