package com.mytest;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import static org.elasticsearch.index.query.QueryBuilders.fieldQuery;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.elasticsearch.search.SearchHit;


/**
 *
 * @author hakdogan
 */

public class JavaAPIMain {
    
    public void test(String content) throws IOException{

    	
        Node node     = nodeBuilder().node();
        Client client = node.client();
        
        client.prepareIndex("test", "myarticle", "1")
              .setSource(putJsonDocument(""+content+"test",new Date())).execute().actionGet();
        
        getDocument(client, "test", "myarticle", "1");
        node.close();
    }
    
    public static Map<String, Object> putJsonDocument(String content, Date postDate){
        
        Map<String, Object> jsonDocument = new HashMap<String, Object>();
        
        jsonDocument.put("content", content);
        //jsonDocument.put("Name","testES");
        jsonDocument.put("Date",postDate);
        return jsonDocument;
    }
    
    public static void getDocument(Client client, String index, String type, String id){
        
        GetResponse getResponse = client.prepareGet(index, type, id)
                                        .execute()
                                        .actionGet();
        Map<String, Object> source = getResponse.getSource();
        
        System.out.println("------------------------------");
        System.out.println("Index: " + getResponse.getIndex());
        System.out.println("Type: " + getResponse.getType());
        System.out.println("Id: " + getResponse.getId());
        System.out.println("Version: " + getResponse.getVersion());
        System.out.println(source);
        System.out.println("------------------------------");
        
    }
        public static void searchDocument(Client client, String index, String type,
                                      String field, String value){
        
        SearchResponse response = client.prepareSearch(index)
                                        .setTypes(type)
                                        .setSearchType(SearchType.QUERY_AND_FETCH)
                                        .setQuery(fieldQuery(field, value))
                                        .setFrom(0).setSize(60).setExplain(true)
                                        .execute()
                                        .actionGet();
        
        SearchHit[] results = response.getHits().getHits();
        
        System.out.println("Current results: " + results.length);
        for (SearchHit hit : results) {
            System.out.println("------------------------------");
            Map<String,Object> result = hit.getSource();   
            System.out.println(result);
        }
    }
        public static void main(String[] args) throws IOException {
			JavaAPIMain ma=new JavaAPIMain();
			ma.test("ne");
		}
    
    public static void deleteDocument(Client client, String index, String type, String id){
        
        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
        System.out.println("Information on the deleted document:");
        System.out.println("Index: " + response.getIndex());
        System.out.println("Type: " + response.getType());
        System.out.println("Id: " + response.getId());
        System.out.println("Version: " + response.getVersion());
    }
}
