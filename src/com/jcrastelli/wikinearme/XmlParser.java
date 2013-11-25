package com.jcrastelli.wikinearme;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlParser {
	// We don't use namespaces
	private static final String ns = null;

	public List<Entry> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Entry> entries = new ArrayList<Entry>();

		parser.require(XmlPullParser.START_TAG, ns, "wikilocation");
		parser.nextTag();
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("article")) {
				entries.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return entries;
	}

	// This class represents a single entry (post) in the XML feed.
	public static class Entry {
		public final String id;
		public final String lat;
		public final String lng;
		public final String type;
		public final String title;
		public final String mobileurl;

		private Entry(String id, String lat, String lng, String type,
				String title, String mobileurl) {
			this.id = id;
			this.lat = lat;
			this.lng = lng;
			this.type = type;
			this.title = title;
			this.mobileurl = mobileurl;
		}
	}

	// Parses the contents of an entry. If it encounters a title, summary, or
	// link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the
	// tag.
	private Entry readEntry(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "article");
		String id = null;
		String lat = null;
		String lng = null;
		String type = null;
		String title = null;
		String mobileurl = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("id")) {
				id = readString(parser, "id");
			} else if (name.equals("lat")) {
				lat = readString(parser, "lat");
			} else if (name.equals("lng")) {
				lng = readString(parser, "lng");
			} else if (name.equals("type")) {
				type = readString(parser, "type");
			} else if (name.equals("title")) {
				title = readString(parser, "title");
			} else if (name.equals("mobileurl")) {
				mobileurl = readString(parser, "mobileurl");
			} else {
				skip(parser);
			}
		}
		return new Entry(id, lat, lng, type, title, mobileurl);
	}

	// Processes title tags in the feed.
	private String readString(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return title;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
