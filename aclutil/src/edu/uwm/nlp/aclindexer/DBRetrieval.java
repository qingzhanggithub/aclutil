/**
 * 
 */
package edu.uwm.nlp.aclindexer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import bibtex.storage.BibViews;
import bibtex.storage.CitationData;
import bibtex.storage.CitationKey;
import bibtex.storage.DBManager;

import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.DatabaseException;

/**
 * @author qing
 *
 */
public class DBRetrieval {
	
	private DBManager db;
	private BibViews views;
	private StoredMap<CitationKey, CitationData> bibMap;
	private HashMap<String,CitationData> id_citation_mapping;

	public DBRetrieval(String dbLocation){
		try {
			db = new DBManager(dbLocation);
			views = new BibViews(db); 
			bibMap = views.getBibMap();
			
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String,CitationData> retrieveDataFromDB(ArrayList<String> ids)
	{
		DBRetrievor dbr = new DBRetrievor();
		dbr.setIds(ids);
		TransactionRunner runner = new TransactionRunner(db.getEnvironment());
		try {
			runner.run(dbr);
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
		return id_citation_mapping;
		// after above step, id_citation_mapping, id_set, author_set are ready.
	}
	
	private class DBRetrievor implements TransactionWorker
	{

		private ArrayList<String> ids;
		@Override
		public void doWork() throws Exception {
			CitationData data = null;
			id_citation_mapping = new HashMap<String, CitationData>();
			for(String id:ids){
				data = bibMap.get(new CitationKey(id));
				if(data != null){// if data is null, it will be ignored.
					id_citation_mapping.put(id, data);
				}
			}
		}
		public void setIds(ArrayList<String> ids) {
			this.ids = ids;
		}
	}
}
