package com.beigu.yunbeiuc.api.mapper;

/** Maps shared block definitions to a version-specific block instance. */
public interface BlockMapper<S, B> {
    B create(String id, S settings);
}
