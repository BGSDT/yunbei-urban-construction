package com.beigu.yunbeiuc.api.mapper;

public interface BlockMapper<S, B> {
    B create(String id, S settings);
}
