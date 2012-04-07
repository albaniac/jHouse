/**
 * 
 */
package net.gregrapp.jhouse.models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * User role model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "user_roles")
public class UserRole
{

  private Long id;
  private String name;
  private Set<User> users;

  /**
   * Unique ID
   * 
   * @return the id
   */
  @Id
  @GeneratedValue
  public Long getId()
  {
    return id;
  }

  /**
   * Role name
   * 
   * @return the role name
   */
  @Column(nullable = false, unique = true)
  public String getName()
  {
    return name;
  }

  /**
   * Users who are members of this role
   * 
   * @return the users
   */
  @ManyToMany(mappedBy = "roles")
  public Set<User> getUsers()
  {
    return users;
  }

  /**
   * @param id
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * Role name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Users who are members of this role
   * 
   * @param users
   *          the users to set
   */
  public void setUsers(Set<User> users)
  {
    this.users = users;
  }
}
