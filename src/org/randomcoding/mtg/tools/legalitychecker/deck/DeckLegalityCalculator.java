package org.randomcoding.mtg.tools.legalitychecker.deck;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.randomcoding.mtg.tools.enumerations.MagicDeckFormat;
import org.randomcoding.mtg.tools.enumerations.MagicLegalityRestriction;
import org.randomcoding.mtg.tools.legalitychecker.scraper.GathererDataScraper;

/**
 * Class to calculate the legality of a whole deck. If a deck has all legal cards for a given format, then it is legal.
 * If there are restricted cards, then if there is only one of the card in the deck it is legal otherwise it is not. If
 * there are any banned cards the the deck is flagged as banned.
 * <p>
 * For any formats were the deck is illegal or banned, then an explanation is given.
 * </p>
 * <p>
 * This class is a singleton. This allows the caching of legality information for all cards checked
 * 
 * @author Tym The Enchanter
 */
public class DeckLegalityCalculator
{
	private static final Log log = LogFactory.getLog(DeckLegalityCalculator.class);

	private GathererDataScraper scraper;
	private static DeckLegalityCalculator legalityCalculatorInstance;
	private final Set<MtgCardData> legalityScrapedCardsCache;

	private DeckLegalityCalculator()
	{
		legalityScrapedCardsCache = new HashSet<MtgCardData>();
	}

	public synchronized static DeckLegalityCalculator getDeckLegalityCalculator()
	{
		if (legalityCalculatorInstance == null)
		{
			legalityCalculatorInstance = new DeckLegalityCalculator();
		}

		return legalityCalculatorInstance;
	}

	private GathererDataScraper getScraper()
	{
		if (scraper == null)
		{
			scraper = new GathererDataScraper();
		}

		return scraper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException("The Singleton " + getClass().getName() + " cannot be cloned");
	}

	/**
	 * @param deck The {@link MtgDeck} to determine the legality of
	 * @return a {@link Map} of the formats and legality restrictions for this deck. If a format is not present then it
	 *         is not legal, restricted or banned in that format
	 */
	public Map<MagicDeckFormat, MagicLegalityRestriction> checkDeckLegality(MtgDeck deck)
	{
		updateCardDataCache(deck);
		Map<MagicDeckFormat, MagicLegalityRestriction> deckLegalities = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();

		return deckLegalities;
	}

	private void updateCardDataCache(MtgDeck deck)
	{
		for (MtgCardData cardData : deck.getCardData())
		{
			if (!legalityScrapedCardsCache.contains(cardData))
			{
				try
				{
					Map<MagicDeckFormat, MagicLegalityRestriction> cardLegalityData = getScraper().getLegality(cardData.getCardName());
					for (Map.Entry<MagicDeckFormat, MagicLegalityRestriction> legalityEntry : cardLegalityData.entrySet())
					{
						cardData.setFormatLegality(legalityEntry.getKey(), legalityEntry.getValue());
					}
					legalityScrapedCardsCache.add(cardData);
				}
				catch (Exception e)
				{
					log.error("Failed to scrape legality data for card: " + cardData, e);
				}
			}
		}
	}
}
