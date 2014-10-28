package edu.uwm.nlp.aclindexer;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import bibtex.storage.CitationData;

public class ACLIndexer {
	
	private IndexWriter indexWriter; 
	public static String FILE_ID_FIELD = "file-id";
	public static String CONTENT_FIELD = "content";
	public static String TITLE_FIELD  = "title";
	public static String AUTHOR_FIELD = "author";
	public static String YEAR_FIELD = "year";
	private static int DEFAULT_YEAR = 30;
	private static NumberFormat formatter = new DecimalFormat("00"); 
	public ACLIndexer(String indexLoc)
	{
		try {
			NIOFSDirectory directory = new NIOFSDirectory(new File(indexLoc));
			indexWriter = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_29),true, IndexWriter.MaxFieldLength.UNLIMITED);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void indexFile(String fileID, CitationData citation, String text)
	{
		Document doc = new Document();
		doc.add(new Field(FILE_ID_FIELD, fileID, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(CONTENT_FIELD, (text==null)?"null":text, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(TITLE_FIELD, citation.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
		String year = fileID.substring(1, 3);
		int num = -1;
		try{
		num = Integer.parseInt(year);}
		catch(NumberFormatException e)
		{
			num = DEFAULT_YEAR;
		}
		String yearStr = null;
		if(num<12)
			yearStr = "20"+formatter.format(num);
		else
			yearStr = "19"+formatter.format(num);
		doc.add(new Field(YEAR_FIELD,yearStr,Field.Store.YES, Field.Index.ANALYZED));
		try {
			indexWriter.addDocument(doc);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeIndexWriter()
	{
		try {
			indexWriter.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}

}
