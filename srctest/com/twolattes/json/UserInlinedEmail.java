package com.twolattes.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class UserInlinedEmail {
  @Value(views = "1")
  public EmailInline email;

  @Value(views = "2", inline = false)  // "inline = false" ignored (false is default value)
  public EmailInline inlineFalse;

  @Value(views = "3", inline = true)  // redundant with "inline = true" on @Entity
  public EmailInline inlineTrue;

  @Value(views = "4")
  public Map<String, EmailInline> emails = new HashMap<String, EmailInline>();

  @Value(views = "5")
  public EmailInline[] emailsArray;

  @Value(views = "6")
  public List<EmailInline> emailsList = new ArrayList<EmailInline>();
}
