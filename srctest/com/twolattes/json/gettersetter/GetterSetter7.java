package com.twolattes.json.gettersetter;

import com.twolattes.json.Email;
import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class GetterSetter7 {
  private Email email;

  @Value
  public Email getEmail() {
    return email;
  }

  @Value
  public GetterSetter7 setEmail(Email email) {
    this.email = email;
    return this;
  }
}
