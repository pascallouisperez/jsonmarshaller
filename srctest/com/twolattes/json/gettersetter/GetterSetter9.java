package com.twolattes.json.gettersetter;

import com.twolattes.json.Email;
import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class GetterSetter9 {
  private Email email;

  @Value(views = {"1", "2"})
  public Email getEmail() {
    return email;
  }

  @Value(views = {"1", "2", "3"})
  public void setEmail(Email email) {
    this.email = email;
  }
}
