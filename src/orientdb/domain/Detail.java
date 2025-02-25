package orientdb.domain;

public class Detail {
    private String name;
    private String type;

    Detail() {}

    Detail(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String typeName() { return type; }
    public void setType(String type) { this.type = type; }
}
