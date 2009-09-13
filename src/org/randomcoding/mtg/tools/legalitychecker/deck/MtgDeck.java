package org.randomcoding.mtg.tools.legalitychecker.deck;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.randomcoding.mtg.tools.legalitychecker.scraper.GathererDataScraper;

/**
 * A Class to represent simple data for a MTG deck
 * 
 * @author Tym The Enchanter
 */
public class MtgDeck
{
	private static final Log log = LogFactory.getLog(MtgDeck.class);

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
		if (cardCount > 0 && cardMultiverseId > 0)
		{
			MtgCardData cardData = getCardData(cardName);

			if (cardData == null)
			{
				cardData = new MtgCardData(cardName, cardMultiverseId);
			}

			updateCardCount(cardData, cardCount);
		}
	}

	public void add(String cardName, int cardCount)
	{
		add(cardName, getMultiverseId(cardName), cardCount);
	}

	private int getMultiverseId(String cardName)
	{
		int multiverseId = -1;
		try
		{
			multiverseId = new GathererDataScraper().getMultiverseId(cardName);
		}
		catch (IOException e)
		{
			log.error(e);
		}

		return multiverseId;
	}

	public Set<MtgCardData> getCardData()
	{
		return cardsAndCount.keySet();
	}

	public int getCardCount(MtgCardData cardData)
	{
		Integer cardCount = cardsAndCount.get(cardData);
		return (cardCount != null ? cardCount : -1);
	}

	public int getCardCount(String cardName)
	{
		return getCardCount(getCardData(cardName));
	}

	public String getDeckName()
	{
		return deckName;
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
}
