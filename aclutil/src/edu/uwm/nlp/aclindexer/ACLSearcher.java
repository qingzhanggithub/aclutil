package edu.uwm.nlp.aclindexer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;


public class ACLSearcher {
	
	protected QueryParser parser = new QueryParser(Version.LUCENE_29, ACLIndexer.CONTENT_FIELD, new StandardAnalyzer(Version.LUCENE_29));
	protected IndexSearcher searcher;
	
	public ACLSearcher(String indexDir)
	{
		NIOFSDirectory directory;
		try {
			directory = new NIOFSDirectory(new File(indexDir));
			searcher = new IndexSearcher(directory,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> searchFileSimple(String queryStr, int returnSize)
	{
		ArrayList<String> ids = new ArrayList<String>();
		try {
			Query q = parser.parse(queryStr);
			TopDocs hits = searcher.search(q, returnSize);
			ScoreDoc[] docs = hits.scoreDocs;
			int size = docs.length;
			for(int i=0; i<size; i++)
			{
				Document doc=searcher.getIndexReader().document(docs[i].doc);
				String fileid = doc.get(ACLIndexer.FILE_ID_FIELD);
				ids.add(fileid);
			}
			System.out.println("Outputted file id. requested size="+returnSize+"\treturned size="+size);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ids;
	}
	
}
