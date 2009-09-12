package org.randomcoding.mtg.tools.legalitychecker.deck;

import java.util.HashMap;
import java.util.Map;

/**
 * A Class to represent simple data for a MTG deck
 * 
 * @author Tym The Enchanter
 */
public class MtgDeck
{
	private final String deckName;
	private final Map<MtgCardData, Integer> cardsAndCount;

	public MtgDeck(String deckName)
	{
		this.deckName = deckName;
		cardsAndCount = new HashMap<MtgCardData, Integer>();
	}

	/**
	 * Adds the given number of cards to the deck
	 * 
	 * @param cardName The name of the card, as it is printed
	 * @param cardMultiverseId The Gatherer MultiverseId of the card
	 * @param card
	 */
	public void add(String cardName, int cardMultiverseId, int cardCount)
	{
		MtgCardData cardData = getCardData(cardName);
		if (cardData == null)
		{
			cardData = new MtgCardData(cardName, cardMultiverseId);
		}

		updateCardCount(cardData, cardCount);
	}

	private void updateCardCount(MtgCardData cardData, int cardCount)
	{
		if (cardsAndCount.get(cardData) == null)
		{
			cardsAndCount.put(cardData, cardCount);
		}
		else
		{
			int newCount = cardsAndCount.get(cardData) + cardCount;
			cardsAndCount.put(cardData, newCount);
		}
	}

	private MtgCardData getCardData(String cardName)
	{
		MtgCardData cardData = null;

		locatdCardData: for (MtgCardData mtgCardData : cardsAndCount.keySet())
		{
			if (mtgCardData.getCardName().equals(cardName))
			{
				cardData = mtgCardData;
				break locatdCardData;
			}
		}

		return cardData;
	}

	/**
	 * Adds a single card to the deck
	 * 
	 * @param cardName The name of the card, as it is printed
	 * @param cardMultiverseId The Gatherer MultiverseId of the card
	 * @param card
	 */
	public void add(String cardName, int cardMultiverseId)
	{
		add(cardName, cardMultiverseId, 1);
	}
}
