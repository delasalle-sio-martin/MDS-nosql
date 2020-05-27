package com.mydigitalschool.user;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mydigitalschool.configurations.Operators;
import com.mydigitalschool.mongodb.MongoService;
import com.mydigitalschool.tools.Scan;

public class MongoUserManager {

  private final MongoDatabase db;
  private MongoService mongoService;

  public MongoUserManager(final MongoDatabase db) {
    this.db = db;
  }

  public void run() {
    while (true) {
      String collection = this.selectCollection();
      Integer option = this.selectionCollectionOption();

      switch (option) {
      case 1:
        FindIterable<Document> datas = this.findIncollection(collection);
        break;
      case 2:
        this.insertInCollection(collection);
        break;
      default:
        break;
      }
    }
  }

  private void insertInCollection(String collection) {
    // TODO Auto-generated method stub
    System.out.println("insert");
  }

  private int loop;

  private FindIterable<Document> findIncollection(String collection) {
    FindIterable<Document> result = null;
    final FindIterable<Document> documents = this.db.getCollection(collection).find();

    final Map<Integer, String> fields = new HashMap<>();
    final Map<String, String> fieldsType = new HashMap<>();
    final List<String> fieldsChoice = new ArrayList<>();

    loop = 1;

    this.ExtractDocumentsFields(documents, fields, fieldsType, fieldsChoice);

    String choice = Scan.getInstance().selectStringFromIntChoice("Sélection un champ", fieldsChoice.toArray(),
        fields);

    int choiceOperator = Scan.getInstance().selectInt("Sélection un opérateur",
        Operators.getDisplayChoices().toArray(), 0, Operators.getDisplayChoices().size() - 1);

    System.out.println(String.format("Insérer une valeur pour %s avec l'opérateur %s", choice,
        Operators.getDisplayList().get(choiceOperator)));

    Bson filter = this.ActionsForFieldAndOperator(fieldsType, choice, choiceOperator);

    result = this.db.getCollection(collection).find(filter);

    for (Document document : result) {
      System.out.println(document.toJson());
    }

    System.out.println("find");
    
    Integer res = Scan.getInstance().selectInt("Delete/Update", new String[] {
    		"Delete all documents", "Delete only the document you filtered", 
    		"Update all documents", "Update only the document you filtered"
    }, 0, 3);
    
    switch(res) {
    case 0:
    	mongoService.deleteAll(result);
    	break;
    case 1:
    	System.out.println("Id from the doc you want to delete");
    	mongoService.deleteOne(Scan.getInstance().inputString());
    	break;
    case 2:
    	System.out.println("Which field do you want to update?");
    	mongoService.updateDocuments(result, filter);
    	break;
    case 3:
    	mongoService.updateOneDocument(Scan.getInstance().inputString(), filter);
    }
    return result;
  }

  private Bson ActionsForFieldAndOperator(final Map<String, String> fieldsType, String choice, int choiceOperator) {
    Class<?> cls = null;
    try {
      cls = Class.forName(fieldsType.get(choice));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      cls = String.class;
    }

    Bson filter = null;
    if (cls == String.class) {
      switch (choiceOperator) {
      case 0:
        filter = Filters.eq(choice, Scan.getInstance().inputString());
        break;
      case 5:
        filter = Filters.ne(choice, Scan.getInstance().inputString());
        break;

      default:
        System.out.println(String.format("Opération non autorisé pour l'opérateur %s",
            Operators.getDisplayList().get(choiceOperator)));
        break;
      }

    } else if (cls == Integer.class) {
      switch (choiceOperator) {
      case 0:
        filter = Filters.eq(choice, Scan.getInstance().inputInt());
        break;

      case 1:
        filter = Filters.gt(choice, Scan.getInstance().inputInt());
        break;

      case 2:
        filter = Filters.lt(choice, Scan.getInstance().inputInt());
        break;

      case 3:
        filter = Filters.gte(choice, Scan.getInstance().inputInt());
        break;

      case 4:
        filter = Filters.lte(choice, Scan.getInstance().inputInt());
        break;

      case 5:
        filter = Filters.ne(choice, Scan.getInstance().inputInt().intValue());
        break;

      default:
        System.out.println(String.format("Opération non autorisé pour l'opérateur %s",
            Operators.getDisplayList().get(choiceOperator)));
        break;
      }
    }
    return filter;
  }

  private void ExtractDocumentsFields(final FindIterable<Document> documents, final Map<Integer, String> fields,
      final Map<String, String> fieldsType, final List<String> fieldsChoice) {
	  	documents.forEach(x -> x.keySet().forEach(y -> {
	  		if (!fields.containsValue(y)) {
	  			String typeString = x.get(y) == null ? "null" : x.get(y).getClass().getName();
	  			if (!typeString.equals(ArrayList.class.getName())) {
	  				fieldsType.put(y, typeString);
	  				fields.put(loop, y);
	  				fieldsChoice.add(String.format("%s : %s", loop, y));
	  				MongoUserManager.this.loop++;
	  			} else {
          // System.out.println("Array");
	  			}
	  		}
	  	}));
  }

  private Integer selectionCollectionOption() {
    final List<String> datas = new ArrayList<>();
    datas.add("1 : rechercher");
    datas.add("2 : insérer");
    return Scan.getInstance().selectInt("Sélectionner une option", datas.toArray(), 1, 2);
  }

  private String selectCollection() {
    final List<String> datas = new ArrayList<>();
    final Map<Integer, String> choicesDatas = new HashMap<Integer, String>();
    int loop = 1;
    	for (String collectionName : db.listCollectionNames()) {
    		choicesDatas.put(loop, collectionName);
    		datas.add(String.format("%s : %s", loop, collectionName));
    		loop++;
    	}

    String choicedCollection = Scan.getInstance().selectStringFromIntChoice("Sélectionner une collection",
        datas.toArray(), choicesDatas);
    System.out.println(String.format("Now in %s collection", choicedCollection));

    return choicedCollection;
  }
  
}
