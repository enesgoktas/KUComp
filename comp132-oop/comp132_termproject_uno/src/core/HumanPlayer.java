package core;

import java.util.List;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, GameManager gameManager) {
        super(name, gameManager);
    }

    @Override
    public void takeTurn(Deck deck) {

        System.out.println(name + "'s turn. Please play a card.");
    }
    
    public boolean playCard(Card card, Card topCard) {
        if (isValidMove(card, topCard)) {
            hand.remove(card);
            System.out.println(name + " plays " + card);
            return true;
        } else {
            System.out.println("Invalid move. Try again.");
            return false;
        }
    }

    public List<Card> getHand() {
        return hand;
    }
    
}
