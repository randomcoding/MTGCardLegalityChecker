package org.randomcoding.mtg.tools.legalitychecker.deck;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.randomcoding.mtg.tools.enumerations.MagicDeckFormat;
import org.randomcoding.mtg.tools.enumerations.MagicLegalityRestriction;

/**
 * Class to contain data about a MTG card for the purposes of legality checking.
 * <p>
 * This encapsulates its name, the various multiverse associated to the name and the legality restrictions in the
 * different deck formats.
 * </p>
 * 
 * @author Tym The Enchanter
 */
public class MtgCardData
{
	private final String cardName;
	private final Set<Integer> multiverseIds;
	private final Map<MagicDeckFormat, MagicLegalityRestriction> cardLegality;

	/**
	 * @param cardName The name of the card. THis should be the same as it is printed on the actual card
	 * @param cardMultiverseId The Gatherer Multiverse Id of this card. A named card will have a different Multiverse Id
	 *            for each printing of the card.
	 */
	public MtgCardData(String cardName, int cardMultiverseId)
	{
		this.cardName = cardName;
		multiverseIds = new HashSet<Integer>();
		cardLegality = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();
		add(cardMultiverseId);
	}

	public void add(int multiverseId)
	{
		if (multiverseId > 0)
		{
			multiverseIds.add(multiverseId);
		}
	}

	public void remove(int multiverseId)
	{
		multiverseIds.remove(multiverseId);
	}

	public String getCardName()
	{
		return cardName;
	}

	public Set<Integer> getMultiverseIds()
	{
		return multiverseIds;
	}

	public void setFormatLegality(MagicDeckFormat deckFormat, MagicLegalityRestriction legality)
	{
		cardLegality.put(deckFormat, legality);
	}

	public void clearFormatLegality(MagicDeckFormat deckFormat)
	{
		cardLegality.remove(deckFormat);
	}

	public Map<MagicDeckFormat, MagicLegalityRestriction> getCardLegality()
	{
		return cardLegality;
	}

	/**
	 * Base level equals method for all {@link MtgCardData}s. This is safe to call with {@code super.equals()} as class
	 * ownership is correctly checked.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other)
	{
		boolean isEquals = true;

		if (other == null || !other.getClass().equals(this.getClass()))
		{
			isEquals = false;
		}
		else if (other == this)
		{
			isEquals = true;
		}
		else
		{
			MtgCardData otherCardData = (MtgCardData) other;

			if (!(getCardName() == otherCardData.getCardName() || getCardName() != null && getCardName().equals(otherCardData.getCardName())))
			{
				isEquals = false;
			}
			else if (!(getMultiverseIds() == otherCardData.getMultiverseIds() || getMultiverseIds() != null && getMultiverseIds().equals(otherCardData.getMultiverseIds())))
			{
				isEquals = false;
			}
			else if (!(getCardLegality() == otherCardData.getCardLegality() || getCardLegality() != null && getCardLegality().equals(otherCardData.getCardLegality())))
			{
				isEquals = false;
			}
		}

		return isEquals;
	}

	@Override
	public int hashCode()
	{
		int hash = getClass().getName().hashCode();

		if (getCardName() != null)
		{
			hash += getCardName().hashCode();
		}

		if (getMultiverseIds() != null)
		{
			hash += getMultiverseIds().hashCode();
		}

		if (getCardLegality() != null)
		{
			hash += getCardLegality().hashCode();
		}

		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("MTG Card - ");
		builder.append(getCardName());
		builder.append(", Multiverse Ids: ");
		builder.append(getMultiverseIds().toString().replaceAll("[\\[\\]]", "").trim());

		return builder.toString();
	}
}
