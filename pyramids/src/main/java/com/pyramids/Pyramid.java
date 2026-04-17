package com.pyramids;

// pyramid class, that corresponds to the information in the json file
public class Pyramid {

    protected Integer id;
    protected String name;
    protected String[] contributors;

    // constructor
    public Pyramid(
            Integer pyramidId,
            String pyramidName,
            String[] pyramidContributors
    ) {
        id = pyramidId;
        name = pyramidName;
        contributors = pyramidContributors;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getContributors() {
        return contributors;
    }
}
