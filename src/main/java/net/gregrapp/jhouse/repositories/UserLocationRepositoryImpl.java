/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.gregrapp.jhouse.models.UserLocation;

/**
 * @author Greg Rapp
 *
 */
public class UserLocationRepositoryImpl implements UserLocationRepositoryCustom
{

  @PersistenceContext
  private EntityManager em;

  /**
   * Configure the entity manager to be used.
   * 
   * @param em the {@link EntityManager} to set.
   */
  public void setEntityManager(EntityManager em) {

      this.em = em;
  }
  
  /* (non-Javadoc)
   * @see net.gregrapp.jhouse.repositories.UserLocationRepositoryCustom#getNewestLocationForEachUser()
   */
  @Override
  public List<UserLocation> getNewestLocationForEachUser()
  {
    //Query query = em.createNativeQuery("SELECT ul.* FROM user_locations ul JOIN (SELECT user_id,MAX(timestamp) AS timestamp FROM user_locations GROUP BY user_id) ul2 ON ul.user_id = ul2.user_id AND ul.timestamp = ul2.timestamp", UserLocation.class);
    Query query = em.createNativeQuery("SELECT ul.* FROM user_locations ul JOIN (SELECT MAX(ul1.id) as id,ul1.user_id FROM user_locations ul1 JOIN (SELECT user_id,MAX(timestamp) AS timestamp FROM user_locations GROUP BY user_id) ul2 ON ul1.user_id = ul2.user_id AND ul1.timestamp = ul2.timestamp GROUP BY user_id) new ON ul.id=new.id", UserLocation.class);
    @SuppressWarnings("unchecked")
    List<UserLocation> results = query.getResultList();
    return results;
  }

}
