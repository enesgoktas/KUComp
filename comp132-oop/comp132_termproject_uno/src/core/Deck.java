package core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Stack;

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L; 
    private Stack<Card> cards;
    private Stack<Card> discardPile;

    public Deck() {
        this.cards = new Stack<>();
        this.discardPile = new Stack<>();
        initializeDeck();
        shuffle();
        prepareDiscardPile();
    }

    private void initializeDeck() {
        String[] colors = {"Red", "Yellow", "Green", "Blue"};
        String[] values = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw Two"};
        for (String color : colors) {
            cards.add(new Card(color, "0", 0));
            for (int i = 1; i <= 9; i++) {
                cards.add(new Card(color, String.valueOf(i), i));
                cards.add(new Card(color, String.valueOf(i), i));
            }
            for (String value : values) {
                if (value.equals("Skip") || value.equals("Reverse") || value.equals("Draw Two")) {
                    cards.add(new Card(color, value, 20));
                    cards.add(new Card(color, value, 20));
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            cards.add(new Card("Wild", "Wild", 50));
            cards.add(new Card("Wild", "Wild Draw Four", 50));
        }
    }

    public void shuffle() {
        if (!cards.isEmpty()) {
            Collections.shuffle(cards);
        }
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            reshuffleDiscardIntoDraw();
        }
        return cards.isEmpty() ? null : cards.pop();
    }

    void prepareDiscardPile() {
        while (!cards.isEmpty()) {
            Card topCard = drawCard();
            if (!topCard.getColor().equals("Wild")) {
                discardPile.push(topCard);
                break;
            } else {
                cards.push(topCard);  // Put the wild card back to the deck
            }
        }
    }

    public Card getTopCard() {
        return discardPile.isEmpty() ? null : discardPile.peek();
    }

    public void discardCard(Card card) {
        if (card != null) {
            discardPile.push(card);
        }
    }

    private void reshuffleDiscardIntoDraw() {
        if (discardPile.size() <= 1) {
            System.out.println("No cards left to reshuffle into the draw pile.");
            return;
        }
        Card top = discardPile.pop();
        while (!discardPile.isEmpty()) {
            cards.push(discardPile.pop());
        }
        shuffle();
        discardPile.push(top);
    }
}
