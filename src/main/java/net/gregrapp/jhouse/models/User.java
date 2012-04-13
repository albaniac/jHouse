/**
 * 
 */
package net.gregrapp.jhouse.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * User account model
 * 
 * @author Greg Rapp
 * 
 */
@Entity
@Table(name = "users")
public class User
{

  private Set<ApnsDeviceToken> apnsDeviceTokens;
  private String emailAddress;
  private boolean enabled;
  private String firstName;
  private Long id;
  private Calendar lastLogin;
  private String lastName;
  private List<UserLocation> locations;
  private boolean locked;
  private String password;
  private Set<UserRole> roles;
  private String username;

  /**
   * Add an Apple APNs device token
   * 
   * @param token
   */
  @Transient
  public void addApnsDeviceToken(ApnsDeviceToken token)
  {
    this.apnsDeviceTokens.add(token);
  }

  /**
   * Apple APNs device tokens associated with this user
   * 
   * @return the apnsDeviceTokens
   */
  @OneToMany(mappedBy = "user", targetEntity = ApnsDeviceToken.class, cascade = CascadeType.ALL)
  public Set<ApnsDeviceToken> getApnsDeviceTokens()
  {
    return apnsDeviceTokens;
  }

  /**
   * Email address
   * 
   * @return the emailAddress
   */
  @Column
  public String getEmailAddress()
  {
    return emailAddress;
  }

  /**
   * First name
   * 
   * @return the firstName
   */
  @Column
  public String getFirstName()
  {
    return firstName;
  }

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
   * Last login time
   * 
   * @return the lastLogin
   */
  @Temporal(value = TemporalType.TIMESTAMP)
  public Calendar getLastLogin()
  {
    return lastLogin;
  }

  /**
   * Last name
   * 
   * @return the lastName
   */
  @Column
  public String getLastName()
  {
    return lastName;
  }

  /**
   * Location records associated with user
   * 
   * @return the locations
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  public List<UserLocation> getLocations()
  {
    return locations;
  }

  /**
   * User password
   * 
   * @return the password
   */
  @Column(nullable = false)
  public String getPassword()
  {
    return password;
  }

  /**
   * User role names
   * 
   * @return {@link List} of {@link String} containing role names
   */
  @Transient
  public List<String> getRoleNames()
  {
    List<String> names = new ArrayList<String>();

    Set<UserRole> roles = this.getRoles();

    for (UserRole role : roles)
    {
      names.add(role.getName());
    }

    return names;
  }

  /**
   * User roles
   * 
   * @return the roles
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_role_map")
  public Set<UserRole> getRoles()
  {
    return roles;
  }

  /**
   * Username
   * 
   * @return the username
   */
  @Column(nullable = false, unique = true)
  public String getUsername()
  {
    return username;
  }

  /**
   * User enabled status
   * 
   * @return the enabled
   */
  @Column
  public boolean isEnabled()
  {
    return enabled;
  }

  /**
   * User locked status
   * 
   * @return the locked
   */
  @Column
  public boolean isLocked()
  {
    return locked;
  }

  /**
   * Apple APNs device tokens associated with this user
   * 
   * @param apnsDeviceTokens
   *          the APNs tokens to set
   */
  public void setApnsDeviceTokens(Set<ApnsDeviceToken> apnsDeviceTokens)
  {
    this.apnsDeviceTokens = apnsDeviceTokens;
  }

  /**
   * @param emailAddress
   *          the emailAddress to set
   */
  public void setEmailAddress(String emailAddress)
  {
    this.emailAddress = emailAddress;
  }

  /**
   * User enabled status
   * 
   * @param enabled
   */
  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  /**
   * @param firstName
   *          the firstName to set
   */
  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  /**
   * @param id
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @param lastLogin
   *          the lastLogin to set
   */
  public void setLastLogin(Calendar lastLogin)
  {
    this.lastLogin = lastLogin;
  }

  /**
   * @param lastName
   *          the lastName to set
   */
  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  /**
   * @param locations
   *          the locations to set
   */
  public void setLocations(List<UserLocation> locations)
  {
    this.locations = locations;
  }

  /**
   * User locked status
   * 
   * @param locked
   */
  public void setLocked(boolean locked)
  {
    this.locked = locked;
  }

  /**
   * User password
   * 
   * @param password
   *          the password to set
   */
  public void setPassword(String password)
  {
    this.password = password;
  }

  /**
   * User roles
   * 
   * @param roles
   *          the roles to set
   */
  public void setRoles(Set<UserRole> roles)
  {
    this.roles = roles;
  }

  /**
   * User login name
   * 
   * @param username
   *          the username to set
   */
  public void setUsername(String username)
  {
    this.username = username;
  }

}
