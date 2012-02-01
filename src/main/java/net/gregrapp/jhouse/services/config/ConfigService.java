package net.gregrapp.jhouse.services.config;

public interface ConfigService
{

  /**
   * Get configuration option
   * 
   * @param namespace
   *          option namespace
   * @param opt
   *          option name
   * @return
   */
  public String get(String namespace, String opt);

  /**
   * Set configuration option
   * 
   * @param namespace
   *          option namespace
   * @param opt
   *          option name
   * @param val
   *          option value
   */
  public void set(String namespace, String opt, String val);
}