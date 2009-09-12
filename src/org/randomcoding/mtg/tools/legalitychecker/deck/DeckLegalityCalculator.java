package org.randomcoding.mtg.tools.legalitychecker.deck;

import java.util.HashMap;
import java.util.Map;

import org.randomcoding.mtg.tools.legalitychecker.scraper.MagicDeckFormat;
import org.randomcoding.mtg.tools.legalitychecker.scraper.MagicLegalityRestriction;

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
	private Map<MtgCardData, Map<MagicLegalityRestriction, MagicDeckFormat>> cardLegalityCacheByMultiverseId;

	private static DeckLegalityCalculator legalityCalculatorInstance;

	private DeckLegalityCalculator()
	{
		cardLegalityCacheByMultiverseId = new HashMap<MtgCardData, Map<MagicLegalityRestriction, MagicDeckFormat>>();
	}

	public synchronized static DeckLegalityCalculator getDeckLegalityCalculator()
	{
		if (legalityCalculatorInstance == null)
		{
			legalityCalculatorInstance = new DeckLegalityCalculator();
		}

		return legalityCalculatorInstance;
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
		Map<MagicDeckFormat, MagicLegalityRestriction> deckLegalities = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();

		return deckLegalities;
	}
}
