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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.randomcoding.mtg.tools.enumerations.MagicDeckFormat;
import org.randomcoding.mtg.tools.enumerations.MagicLegalityRestriction;

/**
 * Class to scrape various data associated to a given card
 * 
 * @author Tym The Enchanter
 */
public class GathererDataScraper
{
	private static final String GATHERER_URL_BASE = "http://gatherer.wizards.com/";
	private static final String MULTIVERSE_ID_QUERY_URL_BASE = GATHERER_URL_BASE + "Pages/Card/Details.aspx?name=";
	private static final String LEGALITY_QUERY_URL_BASE = GATHERER_URL_BASE + "Pages/Card/Printings.aspx?multiverseid=";

	private HttpClient httpClient;

	public static boolean isNewRestrictionMoreRestrictive(MagicLegalityRestriction newRestriction, MagicLegalityRestriction previousRestriction)
	{
		boolean isMoreRestrictive = false;

		switch (previousRestriction)
		{
			case LEGAL:
			{
				isMoreRestrictive = (!newRestriction.equals(MagicLegalityRestriction.LEGAL));
				break;
			}
			case RESTRICTED:
			{
				isMoreRestrictive = newRestriction.equals(MagicLegalityRestriction.BANNED) || newRestriction.equals(MagicLegalityRestriction.NOT_PRESENT);
				break;
			}
			case BANNED:
			{
				isMoreRestrictive = newRestriction.equals(MagicLegalityRestriction.NOT_PRESENT);
			}
		}

		return isMoreRestrictive;
	}

	public Map<MagicDeckFormat, MagicLegalityRestriction> getLegality(int cardMultiverseId) throws IOException
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> legality = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();
		String queryUrl = LEGALITY_QUERY_URL_BASE + cardMultiverseId;
		GetMethod getMethod = new GetMethod(queryUrl);
		getMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		getHttpClient().executeMethod(getMethod);

		String responseBody = getMethod.getResponseBodyAsString();

		legality = parseLegalityFromResponse(responseBody);

		return legality;

	}

	/**
	 * @param cardName The name of the card, exactly as printed.
	 * @return A Map of the legalities to the deck formats
	 * @throws IOException If there is a problem scraping the gatherer
	 */
	public Map<MagicDeckFormat, MagicLegalityRestriction> getLegality(String cardName) throws IOException
	{
		return getLegality(getMultiverseId(cardName));
	}

	public int getMultiverseId(String cardName) throws IOException
	{
		String queryString = MULTIVERSE_ID_QUERY_URL_BASE + cardName.replaceAll("\\s", "%20");
		GetMethod getMethod = new GetMethod(queryString);

		getHttpClient().executeMethod(getMethod);
		String response = getMethod.getResponseBodyAsString();

		return getIdFromResponse(response);
	}

	private void addToLegalities(MagicLegalityRestriction legalityRestriction, MagicDeckFormat deckFormat, Map<MagicDeckFormat, MagicLegalityRestriction> legalities)
	{
		if (legalities.get(deckFormat) == null)
		{
			legalities.put(deckFormat, legalityRestriction);
		}
		else
		{
			if (isNewRestrictionMoreRestrictive(legalityRestriction, legalities.get(deckFormat)))
			{
				legalities.put(deckFormat, legalityRestriction);
			}
		}
	}

	private Map<MagicDeckFormat, MagicLegalityRestriction> extractLegalities(Node tableNode)
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> legalities = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();

		for (Node rowNode : tableNode.getChildren().toNodeArray())
		{
			if (rowNode instanceof TableRow)
			{
				if (isForRelevantFormat(rowNode))
				{
					MagicLegalityRestriction legalityRestriction = getLegalityRestrictionFromRow(rowNode);
					MagicDeckFormat deckFormat = getDeckFormat(rowNode);
					addToLegalities(legalityRestriction, deckFormat, legalities);
				}
			}
		}

		return legalities;
	}

	private MagicDeckFormat getDeckFormat(Node rowNode)
	{
		MagicDeckFormat deckFormat = null;
		locateFormatType: for (Node dataNode : rowNode.getChildren().toNodeArray())
		{
			if (dataNode instanceof TableColumn)
			{
				deckFormat = MagicDeckFormat.getFormatForString(dataNode.getFirstChild().getText().trim());
				if (deckFormat != null)
				{
					break locateFormatType;
				}
			}
		}
		return deckFormat;
	}

	private Node getFirstTableRowNode(Node tableNode)
	{
		Node firstTableRowNode = null;
		locateFirstTableRowNode: for (Node childNode : tableNode.getChildren().toNodeArray())
		{
			if (childNode instanceof TableRow)
			{
				firstTableRowNode = childNode;
				break locateFirstTableRowNode;
			}
		}

		return firstTableRowNode;
	}

	private HttpClient getHttpClient()
	{
		if (httpClient == null)
		{
			httpClient = new HttpClient();
			httpClient.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		}

		return httpClient;
	}

	private int getIdFromResponse(String response)
	{
		int idStartIndex = response.indexOf("multiverseid=") + "multiverseid=".length();
		int idEndIndex = response.indexOf("\"", idStartIndex);

		String idAsString = response.substring(idStartIndex, idEndIndex);

		return Integer.parseInt(idAsString);
	}

	private MagicLegalityRestriction getLegalityRestrictionFromRow(Node rowNode)
	{
		MagicLegalityRestriction legalityRestriction = null;
		locateFormatType: for (Node dataNode : rowNode.getChildren().toNodeArray())
		{
			if (dataNode instanceof TableColumn)
			{
				legalityRestriction = MagicLegalityRestriction.getLegalityRestrictionForString(dataNode.getFirstChild().getText().trim());
				if (legalityRestriction != null)
				{
					break locateFormatType;
				}
			}
		}
		return legalityRestriction;
	}

	private Node getLegalityTable(String responseBody)
	{
		Node legalityTableNode = null;
		try
		{
			legalityTableNode = getLegalityTableNode(getTableElementsIteratorFromHtmlResponse(responseBody));
		}
		catch (ParserException e)
		{
			e.printStackTrace();
		}

		return legalityTableNode;
	}

	private Node getLegalityTableNode(SimpleNodeIterator elements)
	{
		Node legalityTableNode = null;

		locateLegalityTableNode: while (elements.hasMoreNodes())
		{
			Node tableNode = elements.nextNode();

			if (isNodeForLegalityTable(tableNode))
			{
				legalityTableNode = tableNode;
				break locateLegalityTableNode;
			}
		}

		return legalityTableNode;
	}

	private SimpleNodeIterator getTableElementsIteratorFromHtmlResponse(String responseBody) throws ParserException
	{
		return getTableNodesFromHtmlResponse(responseBody).elements();
	}

	private NodeList getTableNodesFromHtmlResponse(String responseBody) throws ParserException
	{
		Parser htmlParser = new Parser();
		htmlParser.setInputHTML(responseBody);

		return htmlParser.parse(new TagNameFilter("table"));
	}

	private boolean isForRelevantFormat(Node rowNode)
	{
		return getDeckFormat(rowNode) != null;
	}

	private boolean isNodeForLegalityTable(Node tableNode)
	{
		boolean formatFound = false;
		boolean legalityFound = false;

		Node rowNode = getFirstTableRowNode(tableNode);

		locateFormatAndLegalityNodes: for (Node rowChildNode : rowNode.getChildren().toNodeArray())
		{
			if (rowChildNode instanceof TableColumn)
			{
				Node textNode = rowChildNode.getFirstChild();
				String nodeText = textNode.getText().trim();
				if (nodeText.equals("Format"))
				{
					formatFound = true;
				}
				if (nodeText.equals("Legality"))
				{
					legalityFound = true;
				}

				if (formatFound && legalityFound)
				{
					break locateFormatAndLegalityNodes;
				}
			}
		}

		return formatFound && legalityFound;
	}

	private Map<MagicDeckFormat, MagicLegalityRestriction> parseLegalityFromResponse(String responseBody)
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> legality = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();

		legality = extractLegalities(getLegalityTable(responseBody));

		return legality;
	}
}
