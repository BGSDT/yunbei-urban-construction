package com.beigu.yunbeiuc.api.mapper;

/** Maps shared item definitions to a version-specific item instance. */
public interface ItemMapper<S, I> {
    I create(String id, S settings);
}
