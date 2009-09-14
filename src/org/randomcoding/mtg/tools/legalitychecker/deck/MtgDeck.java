package org.randomcoding.mtg.tools.legalitychecker.deck;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.randomcoding.mtg.tools.legalitychecker.scraper.GathererDataScraper;

/**
 * A Class to represent simple data for a MTG deck
 * 
 * @author Tym The Enchanter
 */
public class MtgDeck
{
	private static final int MAX_COPIES_OF_CARD_IN_DECK = 4;

	private final String deckName;
	private final Map<MtgCardData, Integer> cardsAndCount;
	private GathererDataScraper dataScraper;

	public MtgDeck(String deckName)
	{
		this.deckName = deckName;
		cardsAndCount = new HashMap<MtgCardData, Integer>();
	}

	/**
	 * Adds the named card to the deck. This method will first query the Gatherer for the card's Multiverse Id
	 * <p>
	 * As there cannot be any more that four copies of a given card in a deck, this method will limit the number of
	 * copies to 4.
	 * </p>
	 * 
	 * @param cardName The name of the card to add
	 * @param cardCount The number of copies of the card to add
	 * @throws IOException If there is a problem scraping the data from the Gatherer
	 */
	public void add(String cardName, int cardCount) throws IOException
	{
		add(cardName, getMultiverseId(cardName), cardCount);
	}

	/**
	 * Adds the given number of cards to the deck. If the card has already been added then the current count is added to
	 * the new count.
	 * <p>
	 * As there cannot be any more that four copies of a given card in a deck, this method will limit the number of
	 * copies to 4.
	 * </p>
	 * 
	 * @param cardName The name of the card, as it is printed
	 * @param cardMultiverseId The Gatherer MultiverseId of the card
	 * @param cardCount The number of copies of this card to add to the deck
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

	/**
	 * @param cardName The name of the card to get the count of
	 * @return The number of copies of the given card currently in this Deck
	 */
	public int getCardCount(String cardName)
	{
		int cardCount = -1;
		locateNamedCard: for (Map.Entry<MtgCardData, Integer> cardCountEntry : cardsAndCount.entrySet())
		{
			if (cardCountEntry.getKey().getCardName().equals(cardName))
			{
				cardCount = cardCountEntry.getValue();
				break locateNamedCard;
			}
		}

		return cardCount;
	}

	/**
	 * @return The individual {@link MtgCardData} that currently make up this deck
	 */
	public Set<MtgCardData> getCardData()
	{
		return cardsAndCount.keySet();
	}

	/**
	 * @return The name of this deck
	 */
	public String getDeckName()
	{
		return deckName;
	}

	/**
	 * Gets a {@link MtgCardData} by its name
	 */
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
	 * Lazy loader for the data scraper
	 */
	private GathererDataScraper getDataScraper()
	{
		if (dataScraper == null)
		{
			dataScraper = new GathererDataScraper();
		}

		return dataScraper;
	}

	private int getMultiverseId(String cardName) throws IOException
	{
		return getDataScraper().getMultiverseId(cardName);
	}

	private void updateCardCount(MtgCardData cardData, int cardCount)
	{
		if (cardsAndCount.get(cardData) == null)
		{
			int numberOfCardsToAdd = (cardCount > MAX_COPIES_OF_CARD_IN_DECK ? MAX_COPIES_OF_CARD_IN_DECK : cardCount);
			cardsAndCount.put(cardData, numberOfCardsToAdd);
		}
		else
		{
			int newCount = Math.min(MAX_COPIES_OF_CARD_IN_DECK, cardsAndCount.get(cardData) + cardCount);
			cardsAndCount.put(cardData, newCount);
		}
	}
}
