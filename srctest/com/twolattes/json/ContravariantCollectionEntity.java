package com.twolattes.json;

import java.util.Set;

@Entity
public class ContravariantCollectionEntity {
	@Value
	Set<? super String> oupsy;
}
