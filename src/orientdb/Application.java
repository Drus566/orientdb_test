package orientdb;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.object.db.OrientDBObject;
import orientdb.domain.Detail;

import java.util.HashMap;
import java.util.Map;

public class Application {
    public static void main(String[] args) {
        new Application();
    }

    Application() {
        OrientDB orient = new OrientDB("remote:172.17.125.80", OrientDBConfig.defaultConfig());
        ODatabaseSession db = orient.open("test","root", "3123");

        OClass c = db.createClassIfNotExist("Fire");
//        OClass i = db.createClassIfNotExist("Ice");

//        c.createProperty("name", OType.STRING);

        OElement element = db.newInstance("Fire");
        System.out.println(element.getRecord());

        element.setProperty("surname", "Kovalsky");
        System.out.println(element.getIdentity()); //this will print a temporary RID (negative cluster position)
        element.save();
        System.out.println(element.getIdentity()); //this will print a temporary RID (negative cluster position)
        db.commit();
        System.out.println(element.getIdentity()); //this will print a temporary RID (negative cluster position)

        c = db.getClass("Fire");
        if (c.existsProperty("surname")) {
            System.out.println("EXISTS");
        }
        for (String s: element.getPropertyNames()) {
            System.out.println(s);
        };


//        OrientDBObject orientDB = new OrientDBObject(orient);
//        ODatabaseObject db = orientDB.open("test","root", "3123");
//
//        db.getEntityManager().registerEntityClass(Detail.class);
//
//
//        Detail detail;
//
//        for (int i = 0; i < 10000; i++) {
//            detail = db.newInstance(Detail.class);
//            detail.setName("Болт" + i);
//            detail.setType("Крепеж");
//            db.save(detail);
//        }
//        for (Detail o: db.browseClass(Detail.class)){
//            System.out.println(o.getName());
//        }

//        System.out.println("HELLO");
//        OrientDB orient = new OrientDB("remote:172.17.125.80", OrientDBConfig.defaultConfig());
//        ODatabaseSession db = orient.open("test","root", "3123");
//
//        createSchema(db);
//
//        createPeople(db);
//
//        executeAQuery(db);
//
//        executeAnotherQuery(db);
//        db.close();
//        orient.close();
    }

    private static void createSchema(ODatabaseSession db) {
        OClass person = db.getClass("Person");

        if (person == null) {
            person = db.createVertexClass("Person");
        }

        if (person.getProperty("name") == null) {
            person.createProperty("name", OType.STRING);
            person.createIndex("Person_name_index", OClass.INDEX_TYPE.NOTUNIQUE, "name");
        }

        if (db.getClass("FriendOf") == null) {
            db.createEdgeClass("FriendOf");
        }

    }
    private static OVertex createPerson(ODatabaseSession db, String name, String surname) {
        OVertex result = db.newVertex("Person");
        result.setProperty("name", name);
        result.setProperty("surname", surname);
        result.save();
        return result;
    }

    private static void createPeople(ODatabaseSession db){
        OVertex alice = createPerson(db, "Alice", "Foo");
        OVertex bob = createPerson(db, "Bob", "Bar");
        OVertex jim = createPerson(db, "Jim", "Baz");

        OEdge edge1 = alice.addEdge(bob, "FriendOf");
        edge1.save();
        OEdge edge2 = bob.addEdge(jim, "FriendOf");
        edge2.save();
    }

    private static void executeAQuery(ODatabaseSession db) {
        String query = "SELECT expand(out('FriendOf').out('FriendOf')) from Person where name = ?";
        OResultSet rs = db.query(query, "Alice");
        rs.stream().forEach(x -> System.out.println("friend: " + x.getProperty("name")));
        rs.close();
    }

    private static void executeAnotherQuery(ODatabaseSession db) {
        String query =
                " MATCH                                           " +
                        "   {class:Person, as:a, where: (name = :name1)}, " +
                        "   {class:Person, as:b, where: (name = :name2)}, " +
                        "   {as:a} -FriendOf-> {as:x} -FriendOf-> {as:b}  " +
                        " RETURN x.name as friend                         ";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name1", "Alice");
        params.put("name2", "Jim");

        OResultSet rs = db.query(query, params);

        while (rs.hasNext()) {
            OResult item = rs.next();
            System.out.println("friend: " + item.getProperty("name"));
        }

        rs.close();
    }
}
