package com.twolattes.json;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public enum MapType {
  MAP {
    @Override
    <K, V> Map<K, V> newMap() {
      return new HashMap<K, V>();
    }

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends Map> toClass() {
      return Map.class;
    }
  },

  SORTED_MAP {
    @Override
    <K, V> Map<K, V> newMap() {
      return new TreeMap<K, V>();
    }

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends Map> toClass() {
      return SortedMap.class;
    }
  };

  abstract <K, V> Map<K, V> newMap();

  @SuppressWarnings("rawtypes")
  abstract Class<? extends Map> toClass();

  @SuppressWarnings("rawtypes")
  static MapType fromClass(Class<? extends Map> klass) {
    if (SortedMap.class.isAssignableFrom(klass)) {
      return SORTED_MAP;
    } else {
      return MAP;
    }
  }

}
