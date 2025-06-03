package bots;

import core.Card;
import core.Deck;
import core.GameManager;
import core.Player;

public class SimpleBot extends Player {

    public SimpleBot(String name, GameManager gameManager) {
        super(name, gameManager);
    }

    @Override
    public void takeTurn(Deck deck) {
        Card topCard = gameManager.getTopCard();
        Card cardToPlay = chooseCardToPlay(topCard);  // This should choose a valid card if available

        if (cardToPlay != null) {
            System.out.println(name + " plays " + cardToPlay);
            hand.remove(cardToPlay);
            deck.discardCard(cardToPlay);  // Discard the played card
            gameManager.applyCardEffect(cardToPlay);  // Apply any special effects
        } else {
            System.out.println(name + " has no valid moves, drawing a card.");
            drawCard(deck);
            Card drawnCard = hand.get(hand.size() - 1);  // Get the last card, which is the drawn card
            if (isValidMove(drawnCard, topCard)) {
                System.out.println(name + " plays " + drawnCard + " immediately after drawing.");
                hand.remove(drawnCard);
                deck.discardCard(drawnCard);
                gameManager.applyCardEffect(drawnCard);
            } else {
                System.out.println(name + " cannot play the drawn card.");
            }
        }
        gameManager.nextTurn();  // Move to the next player
    }

    private Card chooseCardToPlay(Card topCard) {
        for (Card card : hand) {
            if (isValidMove(card, topCard)) {
                return card;
            }
        }
        return null; // No valid card to play, needs to draw
    }

    public void drawCard(Deck deck) {
        Card drawnCard = deck.drawCard();
        hand.add(drawnCard);
        System.out.println(name + " draws a card.");
    }

    public boolean isValidMove(Card card, Card topCard) {
        return card.getColor().equals(topCard.getColor()) ||
               card.getValue().equals(topCard.getValue()) ||
               card.getColor().equals("Wild");
    }

	@Override
    public boolean playCard(Card card, Card topCard) {
        // AI logic to play a valid card
        for (Card c : hand) {
            if (c.getColor().equals(topCard.getColor()) || c.getValue().equals(topCard.getValue()) ||
                c.getColor().equals("Wild")) {
                hand.remove(c);
                System.out.println(name + " plays " + c);
                return true;
            }
        }
        return false;
    }


}
