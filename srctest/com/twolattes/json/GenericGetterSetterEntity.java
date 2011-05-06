package com.twolattes.json;

import static java.util.Collections.emptySet;

import java.util.Set;

@Entity
public class GenericGetterSetterEntity {

  @Value
	<T extends C & Comparable<? super T>> Set<T> getOops() {
	  return emptySet();
	}

	@Value
  <T extends C & Comparable<? super T>> void setOops(T t) {
  }

	@Entity
	static class C {}

}
