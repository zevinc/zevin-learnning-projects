package com.zevin.serialization;

/** Node in the path from root to a visited object. */
public record Entry(Object object, String segment) {}
