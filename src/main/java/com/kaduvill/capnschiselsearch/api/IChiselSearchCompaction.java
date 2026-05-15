package com.kaduvill.capnschiselsearch.api;

public interface IChiselSearchCompaction {
    void capnschiselsearch$setSearchQuery(String query);

    String capnschiselsearch$getSearchQuery();

    void capnschiselsearch$scrollSearchOffset(int delta);

    int capnschiselsearch$getSearchOffset();

    boolean capnschiselsearch$isCompacting();
}