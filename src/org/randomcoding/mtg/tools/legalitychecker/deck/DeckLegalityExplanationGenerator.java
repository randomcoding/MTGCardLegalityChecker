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

import org.randomcoding.mtg.tools.enumerations.MagicDeckFormat;
import org.randomcoding.mtg.tools.enumerations.MagicLegalityRestriction;

/**
 * Class to explain why a deck is not legal in different formats
 * <p>
 * The basic rules are
 * <ol>
 * <li>Only one copy of a Restricted card is allowed</li>
 * <li>No Banned cards are allowed</li>
 * <li>A maximum of four copies of a single, non Restricted, card are allowed in a deck</li>
 * </ol>
 * 
 * @author Tym The Enchanter
 */
public class DeckLegalityExplanationGenerator
{

	/**
	 * @param deck The deck that for which illegality explanations are required
	 * @return The reasons, indexed by deck format, why each card that is not legal in that format
	 */
	public Map<MagicDeckFormat, Map<MtgCardData, Set<String>>> getExplanationForDeckIllegality(MtgDeck deck)
	{
		Map<MagicDeckFormat, Map<MtgCardData, Set<String>>> legalityExplanations = new HashMap<MagicDeckFormat, Map<MtgCardData, Set<String>>>();

		for (MagicDeckFormat deckFormat : MagicDeckFormat.values())
		{
			for (MtgCardData cardData : deck.getCardData())
			{
				if (!MagicLegalityRestriction.LEGAL.equals(cardData.getCardLegality().get(deckFormat)))
				{
					Map<MtgCardData, Set<String>> newIllegalityExplanation = getIllegalityExplanation(deckFormat, cardData, deck);
					addNewExplanationToExplanations(deckFormat, newIllegalityExplanation, legalityExplanations);
				}
			}
		}

		return legalityExplanations;
	}

	private void addNewExplanationToExplanations(MagicDeckFormat deckFormat, Map<MtgCardData, Set<String>> newIllegalityExplanation,
			Map<MagicDeckFormat, Map<MtgCardData, Set<String>>> legalityExplanations)
	{
		Map<MtgCardData, Set<String>> currentExplanationsForFormat = legalityExplanations.get(deckFormat);
		if (currentExplanationsForFormat == null)
		{
			currentExplanationsForFormat = new HashMap<MtgCardData, Set<String>>();
		}

		for (Map.Entry<MtgCardData, Set<String>> newIllegalityEntry : newIllegalityExplanation.entrySet())
		{
			Set<String> explanationsForCard = currentExplanationsForFormat.get(newIllegalityEntry.getKey());

			if (explanationsForCard == null)
			{
				explanationsForCard = new HashSet<String>();
			}
			explanationsForCard.addAll(newIllegalityEntry.getValue());

			currentExplanationsForFormat.put(newIllegalityEntry.getKey(), explanationsForCard);
		}

		legalityExplanations.put(deckFormat, currentExplanationsForFormat);
	}

	private Map<MtgCardData, Set<String>> getIllegalityExplanation(MagicDeckFormat deckFormat, MtgCardData cardData, MtgDeck deck)
	{
		Map<MtgCardData, Set<String>> illegalityExplanationsForCard = new HashMap<MtgCardData, Set<String>>();

		if (deck.getCardCount(cardData.getCardName()) > 4)
		{
			addIllegalityExplanation(cardData, getExplanationForTooManyCardsIndeck(cardData), illegalityExplanationsForCard);
		}

		switch (cardData.getCardLegality().get(deckFormat))
		{
			case BANNED:
				addIllegalityExplanation(cardData, cardData.getCardName() + " is Banned in " + deckFormat.name(), illegalityExplanationsForCard);
				break;
			case RESTRICTED:
				if (deck.getCardCount(cardData.getCardName()) > 1)
				{
					addIllegalityExplanation(cardData, "Only one copy of " + cardData.getCardName() + " is permitted in " + deckFormat.name() + " as it is Restricted", illegalityExplanationsForCard);
				}
				break;
			case NOT_PRESENT:
				addIllegalityExplanation(cardData, cardData.getCardName() + " is not in the legal sets for " + deckFormat.name(), illegalityExplanationsForCard);
				break;
		}

		return illegalityExplanationsForCard;
	}

	private void addIllegalityExplanation(MtgCardData cardData, String explanation, Map<MtgCardData, Set<String>> illegalityExplanationsForCard)
	{
		Set<String> explanations = illegalityExplanationsForCard.get(cardData);
		if (explanations == null)
		{
			explanations = new HashSet<String>();
		}

		explanations.add(explanation);

		illegalityExplanationsForCard.put(cardData, explanations);
	}

	private String getExplanationForTooManyCardsIndeck(MtgCardData cardData)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("There are more than four copies of ");
		builder.append(cardData.getCardName());
		builder.append(" present in the deck.");

		return builder.toString();
	}
}
