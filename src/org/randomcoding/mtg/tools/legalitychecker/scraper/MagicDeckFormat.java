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
package org.randomcoding.mtg.tools.legalitychecker.scraper;

/**
 * @author Tim Sheppard
 */
public enum MagicDeckFormat
{
	EXTENDED,
	LEGACY,
	STANDARD,
	VINTAGE;

	public static MagicDeckFormat getFormatForString(String formatString)
	{
		MagicDeckFormat format = null;

		locateFormat: for (MagicDeckFormat deckFormat : values())
		{
			if (deckFormat.name().toLowerCase().equals(formatString.toLowerCase()))
			{
				format = deckFormat;
				break locateFormat;
			}
		}

		return format;
	}
}
