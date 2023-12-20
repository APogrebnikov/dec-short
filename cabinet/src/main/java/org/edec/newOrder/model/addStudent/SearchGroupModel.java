package org.edec.newOrder.model.addStudent;

/**
 * Created by antonskripacev on 03.01.17.
 */
public class SearchGroupModel {
    private long id;
    private String name;

    public SearchGroupModel () {

    }

    public SearchGroupModel (long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
}
