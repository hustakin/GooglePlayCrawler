package com.google.play.crawler;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;

/**
 * @author FanJie
 */
public class MyTagFilter implements NodeFilter {
	private static final long serialVersionUID = -1453871394285314477L;

	private List<NodeFilter> predicates = new ArrayList<NodeFilter>();

	public MyTagFilter() {
	}

	public MyTagFilter(String tagType) {
		if (tagType != null)
			predicates.add(new TagNameFilter(tagType));
	}

	public void addAttributeFilter(String attribute, String value) {
		predicates.add(new HasAttributeFilter(attribute, value));
	}

	@Override
	public boolean accept(Node node) {
		boolean ret = true;
		for (int i = 0; ret && (i < predicates.size()); i++)
			if (!predicates.get(i).accept(node))
				ret = false;
		return ret;
	}
}
