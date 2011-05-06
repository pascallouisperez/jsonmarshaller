package com.twolattes.json.gettersetter;

import com.twolattes.json.Email;
import com.twolattes.json.Entity;
import com.twolattes.json.Value;

@Entity
public class GetterSetter8 {
  private Email email;

  @Value
  public Email getEmail() {
    return email;
  }

  @Value
  public void setEmail(String email) {
    this.email = new Email(email);
  }
}
