package com.twolattes.json.visibility1;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
class PackagePrivateClassPublicConstuctor {
  @Value
  public int value = 9;

  public PackagePrivateClassPublicConstuctor() {
  }

  public static PackagePrivateClassPublicConstuctor create() {
    return new PackagePrivateClassPublicConstuctor();
  }
}
