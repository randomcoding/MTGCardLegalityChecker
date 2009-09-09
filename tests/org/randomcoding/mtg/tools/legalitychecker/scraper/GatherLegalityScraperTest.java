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

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.randomcoding.mtg.tools.legalitychecker.scraper.GathererLegalityScraper;
import org.randomcoding.mtg.tools.legalitychecker.scraper.MagicDeckFormat;
import org.randomcoding.mtg.tools.legalitychecker.scraper.MagicLegalityRestriction;

/**
 * @author Tim Sheppard
 */
public class GatherLegalityScraperTest
{
	@Test
	public void testGetMultiverseId() throws Exception
	{
		assertEquals(135199, getScraper().getMultiverseId("Terror"));
	}

	@Test
	public void testGetLegalityReturnsCorrectLegalitySet() throws Exception
	{
		assertEquals(getExpectedLegalityForTerror(), getScraper().getLegality("Terror"));
		assertEquals(getExpectedLegalityForMegrim(), getScraper().getLegality("Megrim"));
		assertEquals(getExpectedLegalityForWindfall(), getScraper().getLegality("Windfall"));
	}

	private Map<MagicDeckFormat, MagicLegalityRestriction> getExpectedLegalityForTerror()
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> legality = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();
		legality.put(MagicDeckFormat.EXTENDED, MagicLegalityRestriction.LEGAL);
		legality.put(MagicDeckFormat.LEGACY, MagicLegalityRestriction.LEGAL);
		legality.put(MagicDeckFormat.VINTAGE, MagicLegalityRestriction.LEGAL);

		return legality;
	}

	private Map<MagicDeckFormat, MagicLegalityRestriction> getExpectedLegalityForMegrim()
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> legality = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();
		legality.put(MagicDeckFormat.EXTENDED, MagicLegalityRestriction.LEGAL);
		legality.put(MagicDeckFormat.LEGACY, MagicLegalityRestriction.LEGAL);
		legality.put(MagicDeckFormat.VINTAGE, MagicLegalityRestriction.LEGAL);
		legality.put(MagicDeckFormat.STANDARD, MagicLegalityRestriction.LEGAL);

		return legality;
	}

	private Map<MagicDeckFormat, MagicLegalityRestriction> getExpectedLegalityForWindfall()
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> legality = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();

		legality.put(MagicDeckFormat.VINTAGE, MagicLegalityRestriction.RESTRICTED);
		legality.put(MagicDeckFormat.LEGACY, MagicLegalityRestriction.BANNED);
		return legality;
	}

	private GathererLegalityScraper getScraper()
	{
		return new GathererLegalityScraper();
	}
}
