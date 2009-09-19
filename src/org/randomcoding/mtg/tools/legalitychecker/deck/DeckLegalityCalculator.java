/*******************************************************************************
 * Copyright (c) 08/09/2009 Tym The Enchanter - tymtheenchanter@randomcoding.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tym The Enchanter - initial API and implementation
 *******************************************************************************/
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

		if (getMaxCardCountInDeck(deck) <= 4)
		{
			for (MtgCardData cardData : deck.getCardData())
			{
				updateDeckLegalitiesFromCardData(cardData, deckLegalities);
			}

			fixRestrictedFormats(deck, deckLegalities);
		}
		else
		{
			deckLegalities.put(MagicDeckFormat.STANDARD, MagicLegalityRestriction.ILLEGAL);
			deckLegalities.put(MagicDeckFormat.EXTENDED, MagicLegalityRestriction.ILLEGAL);
			deckLegalities.put(MagicDeckFormat.LEGACY, MagicLegalityRestriction.ILLEGAL);
			deckLegalities.put(MagicDeckFormat.VINTAGE, MagicLegalityRestriction.ILLEGAL);
		}
		return deckLegalities;
	}

	private int getMaxCardCountInDeck(MtgDeck deck)
	{
		int maxCardCount = 0;

		for (MtgCardData cardData : deck.getCardData())
		{
			maxCardCount = Math.max(maxCardCount, deck.getCardCount(cardData.getCardName()));
		}

		return maxCardCount;
	}

	private void fixRestrictedFormats(MtgDeck deck, Map<MagicDeckFormat, MagicLegalityRestriction> deckLegalities)
	{
		for (Map.Entry<MagicDeckFormat, MagicLegalityRestriction> deckLegalityEntry : deckLegalities.entrySet())
		{
			if (deckLegalityEntry.getValue().equals(MagicLegalityRestriction.RESTRICTED))
			{
				if (getMaxCountOfRestrictedCardsInDeck(deck) == 1)
				{
					deckLegalities.put(deckLegalityEntry.getKey(), MagicLegalityRestriction.LEGAL);
				}
			}
		}

	}

	private int getMaxCountOfRestrictedCardsInDeck(MtgDeck deck)
	{
		int maxRestrictedCount = 0;
		for (MtgCardData cardData : deck.getCardData())
		{
			if (cardData.getCardLegality().values().contains(MagicLegalityRestriction.RESTRICTED))
			{
				maxRestrictedCount = Math.max(maxRestrictedCount, deck.getCardCount(cardData.getCardName()));
			}
		}

		return maxRestrictedCount;
	}

	private void updateDeckLegalitiesFromCardData(MtgCardData cardData, Map<MagicDeckFormat, MagicLegalityRestriction> deckLegalities)
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> cardLegalities = cardData.getCardLegality();
		for (Map.Entry<MagicDeckFormat, MagicLegalityRestriction> deckLegalityEntry : deckLegalities.entrySet())
		{
			if (!cardLegalities.keySet().contains(deckLegalityEntry.getKey()))
			{
				deckLegalities.put(deckLegalityEntry.getKey(), MagicLegalityRestriction.NOT_PRESENT);
			}
		}

		for (Map.Entry<MagicDeckFormat, MagicLegalityRestriction> cardLegalityEntry : cardLegalities.entrySet())
		{
			MagicDeckFormat deckFormat = cardLegalityEntry.getKey();
			MagicLegalityRestriction cardLegalityRestriction = cardLegalityEntry.getValue();
			if (!deckLegalities.containsKey(deckFormat))
			{
				deckLegalities.put(deckFormat, cardLegalityRestriction);
			}
			else
			{
				if (cardLegalityRestriction.isMoreRestrictiveThan(deckLegalities.get(deckFormat)))
				{
					deckLegalities.put(deckFormat, cardLegalityRestriction);
				}
			}
		}
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
