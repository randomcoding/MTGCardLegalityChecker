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
