package net.gregrapp.jhouse.services;

public interface BeanLifecycleService
{
  /**
   * Prefix for Device Spring bean names
   */
  public static final String DEVICE_BEAN_NAME_PREFIX = "DEVICE:";

  /**
   * Prefix for Driver Spring bean names
   */
  public static final String DRIVER_BEAN_NAME_PREFIX = "DRIVER:";

  /**
   * Prefix for Interface Spring bean names
   */
  public static final String INTERFACE_BEAN_NAME_PREFIX = "INTERFACE:";

  /**
   * Initialize the bean lifecycle service
   */
  public void init();

  /**
   * Instantiate Device beans
   */
  public abstract void instantiateDeviceBeans();

  /**
   * Instantiate Driver beans
   */
  public abstract void instantiateDriverBeans();

  /**
   * Instantiate Interface beans
   */
  public abstract void instantiateInterfaceBeans();
}