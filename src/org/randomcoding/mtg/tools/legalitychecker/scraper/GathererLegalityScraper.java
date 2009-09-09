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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.InvalidRedirectLocationException;
import org.apache.commons.httpclient.NameValuePair;
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

/**
 * Class to scrape the legality list of a given card
 * 
 * @author Tim Sheppard
 */
public class GathererLegalityScraper
{
	private final String GATHERER_URL_BASE = "http://gatherer.wizards.com/";
	private final String GATHERER_SEARCH_URL_BASE = GATHERER_URL_BASE + "Pages/Search/Default.aspx";

	private HttpClient httpClient;

	public Map<MagicDeckFormat, MagicLegalityRestriction> getLegality(int cardMultiverseId) throws IOException
	{
		Map<MagicDeckFormat, MagicLegalityRestriction> legality = new HashMap<MagicDeckFormat, MagicLegalityRestriction>();
		String queryUrl = GATHERER_URL_BASE + "Pages/Card/Printings.aspx?multiverseid=" + cardMultiverseId;
		GetMethod getMethod = new GetMethod(queryUrl);
		getMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		getHttpClient().executeMethod(getMethod);

		String responseBody = getMethod.getResponseBodyAsString();

		try
		{
			Parser htmlParser = new Parser();
			htmlParser.setInputHTML(responseBody);
			NodeList tableNodes = htmlParser.parse(new TagNameFilter("table"));

			SimpleNodeIterator elements = tableNodes.elements();

			while (elements.hasMoreNodes())
			{
				Node tableNode = elements.nextNode();

				if (isNodeForLegalityTable(tableNode))
				{
					legality = extractLegalities(tableNode);
				}
			}

		}
		catch (ParserException e)
		{
			e.printStackTrace();
		}

		return legality;

	}

	/**
	 * @param cardName
	 * @return A Map of the legalities to the deck formats
	 * @throws IOException
	 */
	public Map<MagicDeckFormat, MagicLegalityRestriction> getLegality(String cardName) throws IOException
	{
		return getLegality(getMultiverseId(cardName));
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

	private boolean isForRelevantFormat(Node rowNode)
	{
		return getDeckFormat(rowNode) != null;
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

	public int getMultiverseId(String cardName) throws IOException
	{
		int id = -1;
		GetMethod getMethod = new GetMethod();
		getMethod.setPath(GATHERER_SEARCH_URL_BASE);
		getMethod.setQueryString(new NameValuePair[] { new NameValuePair("name", "[m/^" + URLEncoder.encode(cardName, "UTF-8") + "$/]") });
		try
		{
			getHttpClient().executeMethod(getMethod);
			String response = getMethod.getResponseBodyAsString();
			id = getIdFromNormalResponse(response);
		}
		catch (InvalidRedirectLocationException e)
		{
			id = extractIdFromExceptionMessage(e.getMessage());
		}

		return id;
	}

	/**
	 * @param response
	 * @return
	 */
	private int getIdFromNormalResponse(String response)
	{
		int id = -1;
		Pattern pattern = Pattern.compile("multiverseid=(\\d+)");
		Matcher matcher = pattern.matcher(response);

		if (matcher.matches())
		{
			id = Integer.parseInt(matcher.group(1));
		}

		return id;
	}

	private int extractIdFromExceptionMessage(String exceptionMessage)
	{
		int equalsIndex = exceptionMessage.lastIndexOf("=");

		int id = Integer.parseInt(exceptionMessage.substring(equalsIndex + 1));

		return id;
	}
}
