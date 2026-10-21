package com.beigu.yunbeiuc.api.mapper;

public interface ItemMapper<S, I> {
    I create(String id, S settings);
}
