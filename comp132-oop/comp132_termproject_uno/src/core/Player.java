package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Player implements Serializable {
    private static final long serialVersionUID = 1L; 
    protected List<Card> hand;
    protected String name;
    protected GameManager gameManager;

    public Player(String name, GameManager gameManager) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.gameManager = gameManager;
    }

    
    public abstract boolean playCard(Card card, Card topCard);

    public void drawCard(Deck deck) {
        Card card = deck.drawCard();
        if (card != null) {
            hand.add(card);
        }
    }
    
    public boolean isValidMove(Card card, Card topCard) {
        if (topCard.getColor().equals("Wild")) {
            return card.getColor().equals(gameManager.getWildColor()); // Check against the chosen wild color
        }
        return card.getColor().equals(topCard.getColor()) ||
               card.getValue().equals(topCard.getValue()) ||
               card.getColor().equals("Wild");
    }


    public List<Card> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

    public void takeTurn(Deck deck) {
    }
}
