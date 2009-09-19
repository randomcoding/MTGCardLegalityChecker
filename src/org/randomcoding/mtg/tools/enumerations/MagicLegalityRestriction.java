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

package org.randomcoding.mtg.tools.enumerations;

/**
 * @author Tym The Enchanter
 */
public enum MagicLegalityRestriction
{
	/**
	 * Indicates that the card/deck is legal in a deck format
	 */
	LEGAL,
	/**
	 * Indicates that the card is restricted in a deck format or that a deck is not legal due to restricted cards
	 */
	RESTRICTED,
	/**
	 * Indicates that the card is banned in a deck format or that a deck is not legal due to containing banned cards
	 */
	BANNED,
	/**
	 * Indicates that the card is not legal in a deck format or that a deck is not legal due to containing cards that
	 * are not in the current legal set of expansions (usually only for Standard and Extended)
	 */
	NOT_PRESENT,
	/**
	 * Indicates that the card is simply illegal or that a deck is illegal for a reason other than those covered by
	 * Restricted, Banned and Not Present.
	 * <p>
	 * Can be used when a deck contains more than 4 copies of a single card.
	 * </p>
	 */
	ILLEGAL;

	public static MagicLegalityRestriction getLegalityRestrictionForString(String restrictionString)
	{
		MagicLegalityRestriction legalityRestriction = null;

		locateRestriction: for (MagicLegalityRestriction restriction : values())
		{
			if (restriction.name().toLowerCase().equals(restrictionString.toLowerCase()))
			{
				legalityRestriction = restriction;
				break locateRestriction;
			}
		}

		return legalityRestriction;
	}

	public boolean isMoreRestrictiveThan(MagicLegalityRestriction newRestriction)
	{
		return (newRestriction.compareTo(this) < 0);
	}
}
