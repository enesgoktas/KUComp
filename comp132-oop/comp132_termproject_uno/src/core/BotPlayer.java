package core;

import java.util.Random;

public class BotPlayer extends Player {
    private Random rand = new Random();

    public BotPlayer(String name, GameManager gameManager) {
        super(name, gameManager);
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

	@Override
	public void takeTurn(Deck deck) {
	    Card topCard = deck.getTopCard();
	    Card cardToPlay = null;
	    for (Card card : hand) {
	        if (isValidMove(card, topCard)) {
	            cardToPlay = card;
	            break;
	        }
	    }

	    if (cardToPlay != null) {
	        System.out.println(name + " plays " + cardToPlay);
	        hand.remove(cardToPlay);
	        deck.discardCard(cardToPlay); // Update the discard pile
	        gameManager.applyCardEffect(cardToPlay); // Apply any special effects
	        gameManager.nextTurn();
	    } else {
	        drawCard(deck);
	        System.out.println(name + " draws a card.");
	        // Optionally allow the bot to play the drawn card if it's valid
	        Card drawnCard = hand.get(hand.size() - 1);
	        if (isValidMove(drawnCard, topCard)) {
	            playCard(drawnCard, topCard);
	        } else {
	            gameManager.nextTurn();
	        }
	    }
	}

}
