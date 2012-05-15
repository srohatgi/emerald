package net.service;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import net.content.Group;

/***
 * wrapper class for api, does not contain any BL
 * @author sumeet
 *
 */
@XmlRootElement(name = "grouplist")
public class GroupList implements Serializable
{
  private Group[] groups;
  private Long prev;
  private Long next;
  
  public GroupList() {}
  
  public Group[] getGroups()
  {
    return groups;
  }

  public void setGroups(Group[] groups)
  {
    this.groups = groups;
  }

  public Long getPrev()
  {
    return prev;
  }

  public void setPrev(Long prev)
  {
    this.prev = prev;
  }

  public Long getNext()
  {
    return next;
  }

  public void setNext(Long next)
  {
    this.next = next;
  }
}
