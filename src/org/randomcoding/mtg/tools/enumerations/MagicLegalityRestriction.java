/*
 * (c) QinetiQ Limited, 2009
 *
 * Copyright in this library belongs to:
 *
 * QinetiQ Limited,
 * St. Andrews Road,
 * Malvern,
 * Worcestershire.
 * WR14 3RJ
 * UK
 *
 * This software may not be used, sold, licensed, transferred, copied
 * or reproduced in whole or in part in any manner or form or in or
 * on any media by any person other than has been explicitly granted in the 
 * relevant licence terms.
 *
 * The licence allows "Access Rights needed for the execution of the Project"
 * and specifically excludes "Access Rights for Use". You may not assign or
 * transfer this licence. You may not sublicense the Software.
 *
 * This software is distributed WITHOUT ANY WARRANTY, without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * Created By: Tim Sheppard
 *
 * Created for Project: MagicLegalityChecker
 */
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
