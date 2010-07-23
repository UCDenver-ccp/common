package edu.ucdenver.ccp.common.lucene;

import java.util.Iterator;

import org.apache.lucene.document.Document;

/**
 * Abstract class for a Lucene Document feeder class.
 * 
 * @author Bill Baumgartner
 * 
 */
public abstract class DocumentFeeder implements Iterator<Document> {

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Remove() is not supported by the DocumentFeeder Iterator implementation.");
	}
	
	public abstract void close();

}
