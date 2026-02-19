package com.dev.quikkkk.parser.domain.search;

import com.dev.quikkkk.parser.domain.model.SearchResult;

import java.util.Set;

public interface ISearchEngine {
    String getName();

    Set<SearchResult> search(String dork, int limit) throws InterruptedException;
}
