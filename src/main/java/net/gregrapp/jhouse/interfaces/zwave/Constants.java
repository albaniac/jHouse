//////////////////////////////////////////////////////////////////////////////////////////////// 
//
//          #######
//          #   ##    ####   #####    #####  ##  ##   #####
//             ##    ##  ##  ##  ##  ##      ##  ##  ##
//            ##  #  ######  ##  ##   ####   ##  ##   ####
//           ##  ##  ##      ##  ##      ##   #####      ##
//          #######   ####   ##  ##  #####       ##  #####
//                                           #####
//          Z-Wave, the wireless language.
//
//          Copyright Zensys A/S, 2005
//
//          All Rights Reserved
//
//          Description:   
//
//          Author:   Morten Damsgaard, Linkage A/S
//
//          Last Changed By:  $Author: jrm $
//          Revision:         $Revision: 1.9 $
//          Last Changed:     $Date: 2007/03/02 12:12:21 $
//
//////////////////////////////////////////////////////////////////////////////////////////////

package net.gregrapp.jhouse.interfaces.zwave;

/**
 * Z-Wave constants
 * 
 * @author Greg Rapp
 * 
 */
public class Constants
{
  /**
   * Z-Wave hosting Chip types
   * 
   * @author Greg Rapp
   * 
   */
  public enum ChipType
  {
    Unknown(0), ZW0102(1), ZW0201(2), ZW0301(3);

    public static ChipType getByVal(int value)
    {
      for (ChipType t : ChipType.class.getEnumConstants())
        if (t.get() == value)
          return t;
      return Unknown;
    }

    private int value;

    ChipType(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * COMMAND_CLASS_BASIC
   * 
   * @author Greg Rapp
   * 
   */
  public enum CommandBasic
  {
    /**
     * BASIC_GET - 0x02
     */
    BASIC_GET(0x02),
    /**
     * BASIC_OFF - 0x00
     */
    BASIC_OFF(0x00),
    /**
     * BASIC_ON - 0xFF
     */
    BASIC_ON(0xFF),
    /**
     * BASIC_REPORT - 0x03
     */
    BASIC_REPORT(0x03),
    /**
     * BASIC_SET - 0x01
     */
    BASIC_SET(0x01);
    /**
     * BASIC_VERSION - 0x01
     */
    // BASIC_VERSION(0x01);

    private int value;

    CommandBasic(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * Z-Wave command classes
   * 
   * @author Greg Rapp
   * 
   */
  public enum CommandClass
  {

    /**
     * COMMAND_CLASS_ACTUATOR_MULTILEVEL - 0x78
     */
    COMMAND_CLASS_ACTUATOR_MULTILEVEL(0x78),

    /**
     * COMMAND_CLASS_ALARM - 0x71
     */
    COMMAND_CLASS_ALARM(0x71),

    /**
     * COMMAND_CLASS_APPLICATION_STATUS - 0x22
     */
    COMMAND_CLASS_APPLICATION_STATUS(0x22),

    /**
     * COMMAND_CLASS_ASSOCIATION - 0x85
     */
    COMMAND_CLASS_ASSOCIATION(0x85),

    /**
     * COMMAND_CLASS_AV_CONTENT_DIRECTORY_MD - 0x95
     */
    COMMAND_CLASS_AV_CONTENT_DIRECTORY_MD(0x95),

    /**
     * COMMAND_CLASS_AV_CONTENT_SEARCH_MD - 0x96
     */
    COMMAND_CLASS_AV_CONTENT_SEARCH_MD(0x96),

    /**
     * COMMAND_CLASS_AV_RENDERER_STATUS - 0x97
     */
    COMMAND_CLASS_AV_RENDERER_STATUS(0x97),

    /**
     * COMMAND_CLASS_BASIC - 0x20
     */
    COMMAND_CLASS_BASIC(0x20),

    /**
     * COMMAND_CLASS_BASIC_WINDOW_COVERING - 0x50
     */
    COMMAND_CLASS_BASIC_WINDOW_COVERING(0x50),

    /**
     * COMMAND_CLASS_BATTERY - 0x80
     */
    COMMAND_CLASS_BATTERY(0x80),

    /**
     * COMMAND_CLASS_CHIMNEY_FAN - 0x2A
     */
    COMMAND_CLASS_CHIMNEY_FAN(0x2A),

    /**
     * COMMAND_CLASS_CLOCK - 0x81
     */
    COMMAND_CLASS_CLOCK(0x81),

    /**
     * COMMAND_CLASS_CONFIGURATION - 0x70
     */
    COMMAND_CLASS_CONFIGURATION(0x70),

    /**
     * COMMAND_CLASS_CONTROLLER_REPLICATION - 0x21
     */
    COMMAND_CLASS_CONTROLLER_REPLICATION(0x21),

    /**
     * COMMAND_CLASS_DISPLAY - 0x61
     */
    COMMAND_CLASS_DISPLAY(0x61),

    /**
     * COMMAND_CLASS_ENERGY_PRODUCTION - 0x90
     */
    COMMAND_CLASS_ENERGY_PRODUCTION(0x90),

    /**
     * COMMAND_CLASS_GARAGE_DOOR - 0x64
     */
    COMMAND_CLASS_GARAGE_DOOR(0x64),

    /**
     * COMMAND_CLASS_GEOGRAPHICAL_LOCATION - 0x8C
     */
    COMMAND_CLASS_GEOGRAPHICAL_LOCATION(0x8C),

    /**
     * COMMAND_CLASS_HAIL - 0x82
     */
    COMMAND_CLASS_HAIL(0x82),

    /**
     * COMMAND_CLASS_INDICATOR - 0x87
     */
    COMMAND_CLASS_INDICATOR(0x87),

    /**
     * COMMAND_CLASS_KICK - 0x79
     */
    COMMAND_CLASS_KICK(0x79),

    /**
     * COMMAND_CLASS_LANGUAGE - 0x89
     */
    COMMAND_CLASS_LANGUAGE(0x89),

    /**
     * COMMAND_CLASS_LOCK - 0x76
     */
    COMMAND_CLASS_LOCK(0x76),

    /**
     * COMMAND_CLASS_MANUFACTURER_PROPRIETARY - 0x91
     */
    COMMAND_CLASS_MANUFACTURER_PROPRIETARY(0x91),

    /**
     * COMMAND_CLASS_MANUFACTURER_SPECIFIC - 0x72
     */
    COMMAND_CLASS_MANUFACTURER_SPECIFIC(0x72),

    /**
     * COMMAND_CLASS_MARK - 0xEF
     */
    COMMAND_CLASS_MARK(0xEF),

    /**
     * COMMAND_CLASS_METER_PULSE - 0x35
     */
    COMMAND_CLASS_METER_PULSE(0x35),

    /**
     * COMMAND_CLASS_MTP_WINDOW_COVERING - 0x51
     */
    COMMAND_CLASS_MTP_WINDOW_COVERING(0x51),

    /**
     * COMMAND_CLASS_MULTI_INSTANCE - 0x60
     */
    COMMAND_CLASS_MULTI_INSTANCE(0x60),

    /**
     * COMMAND_CLASS_NETWORK_STAT - 0x83
     */
    COMMAND_CLASS_NETWORK_STAT(0x83),

    /**
     * COMMAND_CLASS_NO_OPERATION - 0x00
     */
    COMMAND_CLASS_NO_OPERATION(0x00),

    /**
     * COMMAND_CLASS_NODE_NAMING - 0x77
     */
    COMMAND_CLASS_NODE_NAMING(0x77),

    /**
     * COMMAND_CLASS_NON_INTEROPERABLE - 0xF0
     */
    COMMAND_CLASS_NON_INTEROPERABLE(0xF0),

    /**
     * COMMAND_CLASS_POWERLEVEL - 0x73
     */
    COMMAND_CLASS_POWERLEVEL(0x73),

    /**
     * COMMAND_CLASS_PROPRIETARY - 0x88
     */
    COMMAND_CLASS_PROPRIETARY(0x88),

    /**
     * COMMAND_CLASS_PROTECTION - 0x75
     */
    COMMAND_CLASS_PROTECTION(0x75),

    /**
     * COMMAND_CLASS_SCENE_ACTIVATION - 0x2B
     */
    COMMAND_CLASS_SCENE_ACTIVATION(0x2B),

    /**
     * COMMAND_CLASS_SCENE_ACTUATOR_CONF - 0x2C
     */
    COMMAND_CLASS_SCENE_ACTUATOR_CONF(0x2C),

    /**
     * COMMAND_CLASS_SCENE_CONTROLLER_CONF - 0x2D
     */
    COMMAND_CLASS_SCENE_CONTROLLER_CONF(0x2D),

    /**
     * COMMAND_CLASS_SCREEN_ATTRIBUTES - 0x93
     */
    COMMAND_CLASS_SCREEN_ATTRIBUTES(0x93),

    /**
     * COMMAND_CLASS_SCREEN_MD - 0x92
     */
    COMMAND_CLASS_SCREEN_MD(0x92),

    /**
     * COMMAND_CLASS_SENSOR_BINARY - 0x30
     */
    COMMAND_CLASS_SENSOR_BINARY(0x30),

    /**
     * COMMAND_CLASS_SENSOR_MULTILEVEL - 0x31
     */
    COMMAND_CLASS_SENSOR_MULTILEVEL(0x31),

    /**
     * COMMAND_CLASS_SIMPLE_AV_CONTROL - 0x94
     */
    COMMAND_CLASS_SIMPLE_AV_CONTROL(0x94),

    /**
     * COMMAND_CLASS_SWITCH_ALL - 0x27
     */
    COMMAND_CLASS_SWITCH_ALL(0x27),

    /**
     * COMMAND_CLASS_SWITCH_BINARY - 0x25
     */
    COMMAND_CLASS_SWITCH_BINARY(0x25),

    /**
     * COMMAND_CLASS_SWITCH_MULTILEVEL - 0x26
     */
    COMMAND_CLASS_SWITCH_MULTILEVEL(0x26),

    /**
     * COMMAND_CLASS_SWITCH_TOGGLE_BINARY - 0x28
     */
    COMMAND_CLASS_SWITCH_TOGGLE_BINARY(0x28),

    /**
     * COMMAND_CLASS_SWITCH_TOGGLE_MULTILEVEL - 0x29
     */
    COMMAND_CLASS_SWITCH_TOGGLE_MULTILEVEL(0x29),

    /**
     * COMMAND_CLASS_THERMOSTAT_FAN_MODE - 0x44
     */
    COMMAND_CLASS_THERMOSTAT_FAN_MODE(0x44),

    /**
     * COMMAND_CLASS_THERMOSTAT_FAN_STATE - 0x45
     */
    COMMAND_CLASS_THERMOSTAT_FAN_STATE(0x45),

    /**
     * COMMAND_CLASS_THERMOSTAT_HEATING - 0x38
     */
    COMMAND_CLASS_THERMOSTAT_HEATING(0x38),

    /**
     * COMMAND_CLASS_THERMOSTAT_MODE - 0x40
     */
    COMMAND_CLASS_THERMOSTAT_MODE(0x40),

    /**
     * COMMAND_CLASS_THERMOSTAT_OPERATING_STATE - 0x42
     */
    COMMAND_CLASS_THERMOSTAT_OPERATING_STATE(0x42),

    /**
     * COMMAND_CLASS_THERMOSTAT_SETPOINT - 0x43
     */
    COMMAND_CLASS_THERMOSTAT_SETPOINT(0x43),

    /**
     * COMMAND_CLASS_TIME - 0x8A
     */
    COMMAND_CLASS_TIME(0x8A),

    /**
     * COMMAND_CLASS_TIME_PARAMETERS - 0x8B
     */
    COMMAND_CLASS_TIME_PARAMETERS(0x8B),

    /**
     * COMMAND_CLASS_VERSION - 0x86
     */
    COMMAND_CLASS_VERSION(0x86),

    /**
     * COMMAND_CLASS_WAKE_UP - 0x84
     */
    COMMAND_CLASS_WAKE_UP(0x84);

    public static CommandClass getByVal(int value)
    {
      for (CommandClass c : CommandClass.class.getEnumConstants())
        if (c.get() == value)
          return c;
      return null;
    }

    private int value;

    CommandClass(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * COMMAND_CLASS_MANUFACTURER_SPECIFC
   * 
   * @author Greg Rapp
   * 
   */
  public enum CommandManufacturerSpecific
  {
    MANUFACTURER_SPECIFIC_GET(0x04), MANUFACTURER_SPECIFIC_REPORT(0x05);

    private int value;

    CommandManufacturerSpecific(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * COMMAND_CLASS_SENSOR_BINARY
   * 
   * @author Greg Rapp
   * 
   */
  public enum CommandSensorBinary
  {
    SENSOR_BINARY_GET(0x02), SENSOR_BINARY_REPORT(0x03);

    private int value;

    CommandSensorBinary(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  public enum CommandSwitchMultilevel
  {
    // <summary>
    // SWITCH_MULTILEVEL_DOWN_BIT(0x40),
    // </summary>
    SWITCH_MULTILEVEL_DOWN_BIT(0x40),
    // <summary>
    // SWITCH_MULTILEVEL_GET(0x02),
    // </summary>
    SWITCH_MULTILEVEL_GET(0x02),
    // <summary>
    // SWITCH_MULTILEVEL_LEVEL_CHANGE_MASK(0xC0),
    // </summary>
    SWITCH_MULTILEVEL_LEVEL_CHANGE_MASK(0xC0),
    // <summary>
    // SWITCH_MULTILEVEL_REPORT(0x03),
    // </summary>
    SWITCH_MULTILEVEL_REPORT(0x03),
    // <summary>
    // SWITCH_MULTILEVEL_SET(0x01),
    // </summary>
    SWITCH_MULTILEVEL_SET(0x01),
    // <summary>
    // SWITCH_MULTILEVEL_START_LEVEL_CHANGE(0x04),
    // </summary>
    SWITCH_MULTILEVEL_START_LEVEL_CHANGE(0x04),
    // <summary>
    // SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE(0x05),
    // </summary>
    SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE(0x05);

    private int value;

    CommandSwitchMultilevel(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * COMMAND_CLASS_THERMOSTAT_MODE
   * 
   * @author Greg Rapp
   * 
   */
  public enum CommandThermostatMode
  {
    /**
     * THERMOSTAT_MODE_GET - 0x02
     */
    THERMOSTAT_MODE_GET(0x02),
    /**
     * THERMOSTAT_MODE_REPORT - 0x03
     */
    THERMOSTAT_MODE_REPORT(0x03),
    /**
     * THERMOSTAT_MODE_SET - 0x01
     */
    THERMOSTAT_MODE_SET(0x01),
    /**
     * THERMOSTAT_MODE_SUPPORTED_GET - 0x04
     */
    THERMOSTAT_MODE_SUPPORTED_GET(0x04),
    /**
     * THERMOSTAT_MODE_SUPPORTED_REPORT - 0x05
     */
    THERMOSTAT_MODE_SUPPORTED_REPORT(0x05),
    /**
     * THERMOSTAT_MODE_VERSION - 0x01
     */
    THERMOSTAT_MODE_VERSION(0x01);

    private int value;

    CommandThermostatMode(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * COMMAND_CLASS_THERMOSTAT_FAN_MODE
   * 
   * @author Greg Rapp
   * 
   */
  public enum CommandThermostatFanMode
  {
    /**
     * THERMOSTAT_FAN_MODE_GET - 0x02
     */
    THERMOSTAT_FAN_MODE_GET(0x02),
    /**
     * THERMOSTAT_FAN_MODE_REPORT - 0x03
     */
    THERMOSTAT_FAN_MODE_REPORT(0x03),
    /**
     * THERMOSTAT_FAN_MODE_SET - 0x01
     */
    THERMOSTAT_FAN_MODE_SET(0x01),
    /**
     * THERMOSTAT_FAN_MODE_SUPPORTED_GET - 0x04
     */
    THERMOSTAT_FAN_MODE_SUPPORTED_GET(0x04),
    /**
     * THERMOSTAT_FAN_MODE_SUPPORTED_REPORT - 0x05
     */
    THERMOSTAT_FAN_MODE_SUPPORTED_REPORT(0x05),
    /**
     * THERMOSTAT_FAN_MODE_VERSION - 0x01
     */
    THERMOSTAT_FAN_MODE_VERSION(0x01);

    private int value;

    CommandThermostatFanMode(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  /**
   * COMMAND_CLASS_THERMOSTAT_SETPOINT
   * 
   * @author Greg Rapp
   * 
   */
  public enum CommandThermostatSetpoint
  {
    /**
     * THERMOSTAT_SETPOINT_GET - 0x02
     */
    THERMOSTAT_SETPOINT_GET(0x02),
    /**
     * THERMOSTAT_SETPOINT_REPORT - 0x03
     */
    THERMOSTAT_SETPOINT_REPORT(0x03),
    /**
     * THERMOSTAT_SETPOINT_SET - 0x01
     */
    THERMOSTAT_SETPOINT_SET(0x01),
    /**
     * THERMOSTAT_SETPOINT_SUPPORTED_GET - 0x04
     */
    THERMOSTAT_SETPOINT_SUPPORTED_GET(0x04),
    /**
     * THERMOSTAT_SETPOINT_SUPPORTED_REPORT - 0x05
     */
    THERMOSTAT_SETPOINT_SUPPORTED_REPORT(0x05),
    /**
     * THERMOSTAT_SETPOINT_VERSION - 0x01
     */
    THERMOSTAT_SETPOINT_VERSION(0x01);

    private int value;

    CommandThermostatSetpoint(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  // <summary>
  // Modes available for Controller change
  // </summary>
  public enum ControllerChangeMode
  {
    // <summary>
    // Start Change
    // </summary>
    Start(Mode.NodeController),
    // <summary>
    // Stop Change
    // </summary>
    Stop(Mode.NodeStop),
    // <summary>
    // Stop Change. Indicate failed
    // </summary>
    StopFailed(Mode.NodeStopFailed);

    private Mode value;

    ControllerChangeMode(Mode value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value.get();
    }

  }

  // <summary>
  // Modes available for Create new primary ctrl.
  // </summary>
  public enum CreateNewPrimaryControllerMode
  {
    // <summary>
    // Start Change
    // </summary>
    Start(Mode.NodeController),
    // <summary>
    // Stop Change
    // </summary>
    Stop(Mode.NodeStop),
    // <summary>
    // Stop Change. Indicate failed
    // </summary>
    StopFailed(Mode.NodeStopFailed);

    private Mode value;

    CreateNewPrimaryControllerMode(Mode value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value.get();
    }

  }

  // <summary>
  // SUC capabilities used in ZW_EnableSUC and ZW_SetSUCNodeID
  // </summary>
  public enum EnableSUCCapabilities
  {
    // ZW_SUC_FUNC_BASIC_SUC(0x00),
    SucFuncBasicSuc(0x00),
    // ZW_SUC_FUNC_NODEID_SERVER(0x01
    SucFuncNodeIdServer(0x01);

    private int value;

    EnableSUCCapabilities(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // LearnMode
  // </summary>
  public enum LearnMode
  {
    // <summary>
    // Callback states from ZWaveSetLearnMode
    // </summary>
    Deleted(0x80),
    // <summary>
    // Callback states from ZWaveSetLearnMode
    // </summary>
    Done(0x06),
    // <summary>
    // Callback states from ZWaveSetLearnMode
    // </summary>
    Failed(0x07),
    // <summary>
    // Callback states from ZWaveSetLearnMode
    // </summary>
    Started(0x01),
    // <summary>
    //
    // </summary>
    Unknown(0x00);

    public static LearnMode getByVal(int value)
    {
      for (LearnMode m : LearnMode.class.getEnumConstants())
        if (m.get() == value)
          return m;
      return null;
    }

    private int value;

    LearnMode(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  // <summary>
  // LibraryType
  // </summary>
  public enum Library
  {
    // <summary>
    // A controller bridge library was found
    // </summary>
    ControllerBridgeLib(0x07),
    // <summary>
    // A Controller library was found
    // </summary>
    ControllerLib(0x02),
    // <summary>
    // A Static Controller library was found
    // </summary>
    ControllerStaticLib(0x01),
    // <summary>
    // An Installer Library was found
    // </summary>
    InstallerLib(0x05),
    // <summary>
    // No Library was found
    // </summary>
    NoLib(0x00),
    // <summary>
    // A Slave enhanced library was found
    // </summary>
    SlaveEnhancedLib(0x03),
    // <summary>
    // A Slave library was found
    // </summary>
    SlaveLib(0x04),
    // <summary>
    // A Routing slave library was found
    // </summary>
    SlaveRoutingLib(0x06);

    public static Library getByVal(int value)
    {
      for (Library l : Library.class.getEnumConstants())
        if (l.get() == value)
          return l;
      return null;
    }

    private int value;

    Library(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  public enum Manufacturer
  {
    _2B_Electronics(0x28), _3e_Technologies(0x2a), A1_Components(0x22), ACT(
        0x01), Aeon_Labs(0x86), Alarm_DotCom(0x94), Asia_Heading(0x29), Aspalis(
        0x5d), Atech(0x2b), Balboa_Instruments(0x18), BeSafer(0x2c), Boca_Devices(
        0x23), Broadband_Energy(0x2d), BuLogics(0x26), Carrier(0x2e), CasaWorks(
        11), Color_Kinetics(0x2f), ControlThink(0x19), ConvergeX(0x0F), Cooper_Wiring(
        0x1a), Coventive_Tech(0x9d), Cyberhouse(0x14), Cytech_Tech(0x30), Danfoss(
        0x02), Destiny_Networks(0x31), Digital_5(0x32), Eka_Systems(0x87), El_Gev_Electronics(
        0x34), Electronic_Solutions(0x33), ELK_Products(0x1b), Embedit_A_S(0x35), Everspring(
        0x60), Exceptional_Innovations(0x36), Exhausto(0x04), Exigent_Sensors(
        0x9f), Express_Controls(0x1e), Fakro(0x85), Foard_Systems(0x37), FortrezZ(
        0x84), GEJasco(0x63), GreenWave_Reality(0x99), HAI(0x5b), HiTech_Automation(
        0x17), Home_Automation_Europe(0x9a), Home_Director(0x38), HomeMangeables(
        0x6f), Homepro(0x50), HomeSeer(0x0c), Honeywell(0x39), Horstmann_Controls(
        0x59), iCOM_Technology(0x11), Inlon_Srl(0x3a), INNOVUS(0x77), Intel(
        0x06), IntelliCon(0x1c), Intermatic(0x05), Internet_Dom(0x13), Kamstrup_AS(
        0x91), Kwikset(0x90), Lagotek_Corp(0x51), Leviton(0x1d), Lifestyle_Networks(
        0x3c), Logitech(0x7f), Loudwater_Technologies(0x25), LS_Control(0x71), Marmitek_BV(
        0x3d), Martec_Access(0x3e), MB_Turnkey_Design(0x8f), Merten(0x7a), Monster_Cable(
        0x7e), Motorola(0x3f), MTC_Maintronic(0x83), Novar_EDS(0x40), OpenPeak(
        0x41), PowerLynx(0x16), Pragmatic_Consulting(0x42), Qees(0x95), Raritan(
        0x8e), RCS(0x10), Reitz_Group(0x64), Remotec(0x5254), RS_Scene_Automation(
        0x65), Schlage(0x3b), Scientia_Technologies(0x1f), Seluxit(0x69), Senmatic_A_S(
        0x43), Sequoia_Technology(0x44), Sine_Wireless(0x45), Smart_Products(
        0x46), Somfy(0x47), Sylvania(0x09), Techniku(0x0a), Tell_It_Online(0x12), Telsey(
        0x48), Trane_Corp(0x8b), Tricklestar(0x6b), Twisthink(0x49), TwoGig_Technologies(
        0x98), UEI(0x20), Unknown(-1), Vero_Duco(0x80), ViewSonic(0x5e), Vimar_CRS(
        0x07), Visualize(0x4a), Watt_Stopper(0x4b), Wayne_Dalton(0x08), Woodward_Labs(
        0x4c), Wrap(0x03), Xanboo(0x4d), Yale(0x109), Z_Wave_Technologia(0x4f), Zdata_LLC(
        0x4e), Zensys(0x00);

    public static Manufacturer getByVal(int value)
    {
      for (Manufacturer v : Manufacturer.class.getEnumConstants())
        if (v.get() == value)
          return v;
      return Manufacturer.Unknown;
    }

    private int value;

    Manufacturer(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

    @Override
    public String toString()
    {
      // Replace underscores in the name with a space
      return this.name().replaceAll("_", " ");
    }
  }

  // <summary>
  // Mode
  // </summary>
  public enum Mode
  {
    // <summary>
    // Mode parameters to ZWaveAddNodeToNetwork and ZWaveRemoveNodeFromNetwork -
    // Add / Remove any node to the network.
    // </summary>
    NodeAny(0x01),
    // <summary>
    // Mode parameters to ZWaveAddNodeToNetwork), ZWaveRemoveNodeFromNetwork),
    // ZWaveCreateNewPrimary), ZWaveControllerChange - Add a controller to the
    // network
    // </summary>
    NodeController(0x02),
    // <summary>
    // Mode parameters to ZWaveAddNodeToNetwork and ZWaveRemoveNodeFromNetwork
    // </summary>
    NodeExisting(0x04),
    // <summary>
    // Mode parameters to ZWaveAddNodeToNetwork ),ZWaveRemoveNodeFromNetwork-
    // Add/Remove a slave node to/from the network
    // </summary>
    NodeSlave(0x03),
    // <summary>
    // Stop learn mode without reporting an error.ZWaveAddNodeToNetwork),
    // ZWaveRemoveNodeFromNetwork and ZWaveCreateNewPrimary
    // </summary>
    NodeStop(0x05),
    // <summary>
    // Mode parameters to ZWaveAddNodeToNetwork), ZWaveCreateNewPrimary
    // </summary>
    NodeStopFailed(0x06),
    // <summary>
    //
    // </summary>
    None(0x00);

    private int value;

    Mode(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // NodeFailedReturnValue
  // </summary>
  public enum NodeFailedReturnValue
  {
    // <summary>
    // ZWaveFAILED_NODE_NOT_FOUND
    // </summary>
    FailedNodeNotFound(0x08),
    // <summary>
    // ZWaveFAILED_NODE_REMOVE_FAIL
    // </summary>
    FailedNodeRemoveFail(0x20),
    // <summary>
    // ZWaveFAILED_NODE_REMOVE_PROCESS_BUSY
    // </summary>
    FailedNodeRemoveProcessBusy(0x10),
    // <summary>
    //
    // </summary>
    FailedNodeRemoveStarted(0x00),
    // <summary>
    // ZWaveNO_CALLBACK_FUNCTION
    // </summary>
    NoCallbackFunction(0x04),
    // <summary>
    // ZWaveNOT_PRIMARY_CONTROLLER
    // </summary>
    NotPrimaryController(0x02);

    public static NodeFailedReturnValue getByVal(int value)
    {
      for (NodeFailedReturnValue v : NodeFailedReturnValue.class
          .getEnumConstants())
        if (v.get() == value)
          return v;
      return null;
    }

    private int value;

    NodeFailedReturnValue(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  // <summary>
  // NodeFailedStatus
  // </summary>
  public enum NodeFailedStatus
  {
    // <summary>
    // NODE_NOT_REMOVED
    // </summary>
    NodeNotRemoved(0x02),
    // <summary>
    // Callback functions parameters for ZWaveRemoveFailedNodeId
    // </summary>
    NodeOk(0x00),
    // <summary>
    // NODE_REMOVED
    // </summary>
    NodeRemoved(0x01),
    // <summary>
    // Callback function parameters for ZWaveReplaceFailedNode
    //
    // The failed node are ready to be replaced and controller
    // is ready to add new node with nodeID of the failed node
    // </summary>
    NodeReplace(0x03),
    // <summary>
    // The failed node has been replaced
    // </summary>
    NodeReplaceDone(0x04),
    // <summary>
    // The failed node has not been replaced
    // </summary>
    NodeReplaceFailed(0x05);

    public static NodeFailedStatus getByVal(int value)
    {
      for (NodeFailedStatus s : NodeFailedStatus.class.getEnumConstants())
        if (s.get() == value)
          return s;
      return null;
    }

    private int value;

    NodeFailedStatus(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  // <summary>
  //
  // </summary>
  public enum NodeStatus
  {
    // <summary>
    // Callback states from ZWaveAddNodeToNetwork
    // </summary>
    AddingRemovingController(0x04),
    // <summary>
    // Callback states from ZWaveAddNodeToNetwork
    // </summary>
    AddingRemovingSlave(0x03),
    // <summary>
    // Callback states from ZWaveAddNodeToNetwork
    // </summary>
    Done(0x06),
    // <summary>
    // Callback states from ZWaveAddNodeToNetwork
    // </summary>
    Failed(0x07),
    // <summary>
    // Callback states from ZWaveAddNodeToNetwork
    // </summary>
    LearnReady(0x01),
    // <summary>
    // Callback states from ZWaveAddNodeToNetwork
    // </summary>
    NodeFound(0x02),
    // <summary>
    // Callback states from ZWaveAddNodeToNetwork
    // </summary>
    ProtocolDone(0x05),
    // <summary>
    //
    // </summary>
    Unknown(0x00);

    public static NodeStatus getByVal(int value)
    {
      for (NodeStatus s : NodeStatus.class.getEnumConstants())
        if (s.get() == value)
          return s;
      return null;
    }

    private int value;

    NodeStatus(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  // <summary>
  // ReceiveStatus
  // </summary>
  public enum ReceiveStatus
  {
    // <summary>
    // Rece’ved Z-wave Received frame is broadcast frame - (status == xxxx01xx)
    // </summary>
    Broad(0x04),
    // <summary>
    // Received Z-Wave frame status flag telling that received frame was sent
    // with low power
    // </summary>
    LowPower(0x02),
    // <summary>
    // Received Z-Wave frame status mask used to mask Z-Wave frame type flag
    // bits out
    // </summary>
    Mask(0x0C),
    // <summary>
    // Rece’ved Z-wave Received frame is multicast frame - (status == xxxx10xx)
    // </summary>
    Multi(0x08),
    // <summary>
    // Received Z-Wave frame status flags - as received in
    // ApplicationCommandHandler
    // This flag is set when....
    // </summary>
    RoutedBusy(0x01),
    // <summary>
    // Rece’ved Z-wave Received frame is singlecast frame - (status == xxxx00xx)
    // </summary>
    Single(0x00);

    private int value;

    ReceiveStatus(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // RequestNeighbor
  // </summary>
  public enum RequestNeighbor
  {
    // <summary>
    //
    // </summary>
    None(0x00),
    // <summary>
    // UPDATE_DONE
    // </summary>
    UpdateDone(0x22),
    // <summary>
    // UPDATE_FAILED
    // </summary>
    UpdateFailed(0x23),
    // <summary>
    // UPDATE_STARTED
    // </summary>
    UpdateStarted(0x21);

    public static RequestNeighbor getByVal(int value)
    {
      for (RequestNeighbor n : RequestNeighbor.class.getEnumConstants())
        if (n.get() == value)
          return n;
      return null;
    }

    private int value;

    RequestNeighbor(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }

  // <summary>
  // ZWaveSetSUCReturnValue
  // </summary>
  public enum SetSucReturnValue
  {
    // <summary>
    // ZWaveSUC_SET_FAILED
    // </summary>
    SucSetFailed(0x06),
    // <summary>
    // ZWaveSUC_SET_SUCCEEDED
    // </summary>
    SucSetSucceeded(0x05),
    // <summary>
    //
    // </summary>
    SucUndefined(0x00);

    private int value;

    SetSucReturnValue(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // slaveLearnMode
  // </summary>
  public enum SlaveLearnMode
  {
    // <summary>
    // Add new Virtual Slave node if possible
    // Allowed when bridge is a primary or an inclusion controller
    // Slave Learn function done when Callback function returns
    // ASSIGN_NODEID_DONE
    // </summary>
    VirtualSlaveLearnModeAdd(0x02),
    // <summary>
    // Disable SlaveLearnMode (disable possibility to add/remove Virtual Slave
    // nodes)
    // Allowed when bridge is a primary controller), an inclusion controller or
    // a secondary controller
    // </summary>
    VirtualSlaveLearnModeDisable(0x00),
    // <summary>
    // Enable SlaveLearnMode - Enable possibility for including/excluding a
    // Virtual Slave node by an external primary/inclusion controller
    // Allowed when bridge is an inclusion controller or a secondary controller
    // </summary>
    VirtualSlaveLearnModeEnable(0x01),
    // <summary>
    // Remove existing Virtual Slave node
    // Allowed when bridge is a primary or an inclusion controller
    // Slave Learn function done when Callback function returns
    // ASSIGN_NODEID_DONE
    // </summary>
    VirtualSlaveLearnModeRemove(0x03);

    private int value;

    SlaveLearnMode(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // slaveLearnStatus
  // </summary>
  public enum SlaveLearnStatus
  {
    // <summary>
    // Slave Learn Complete
    // </summary>
    AssignComplete(0x00),
    // <summary>
    // Node ID have been assigned
    // </summary>
    AssignNodeIdDone(0x01),
    // <summary>
    // Node is doing Neighbor discovery
    // </summary>
    AssignRangeInfoUpdate(0x02);

    private int value;

    SlaveLearnStatus(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // ZWaveSUC
  // </summary>
  public enum Suc
  {
    // <summary>
    // UPDATE_ABORT
    // </summary>
    UpdateAbort(1),
    // <summary>
    // UPDATE_DISABLED
    // </summary>
    UpdateDisabled(3),
    // <summary>
    // UPDATE_DONE
    // </summary>
    UpdateDone(0),
    // <summary>
    // UPDATE_OVERFLOW
    // </summary>
    UpdateOverflow(4),
    // <summary>
    // UPDATE_WAIT
    // </summary>
    UpdateWait(2);

    private int value;

    Suc(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // TxOption
  // </summary>
  public enum TXOption
  {
    // <summary>
    // Request acknowledge from destination node.
    // </summary>
    TransmitOptionAcknowledge(0x01),
    // <summary>
    // Request retransmission via repeater nodes (at normal output power level).
    // </summary>
    TransmitOptionAutoRoute(0x04),
    // <summary>
    // TRANSMIT_OPTION_LOW_POWER
    // </summary>
    TransmitOptionLowPower(0x02),
    // <summary>
    // No Request
    // </summary>
    TransmitOptionNone(0x00),
    // #if ERRT
    // <summary>
    // Is used for ERTT
    // </summary>
    TransmitOptionNoRetransmit(0x40),
    // <summary>
    // TRANSMIT_OPTION_NO_ROUTE
    // </summary>
    TransmitOptionNoRoute(0x10);
    // #endif

    private int value;

    TXOption(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

  }

  // <summary>
  // Text Status
  // </summary>
  public enum TXStatus
  {
    // <summary>
    // Not possible to transmit data because the Z-Wave network is busy
    // (jammed).
    // </summary>
    CompleteFail(0x02),
    // <summary>
    // No acknowledge is received before timeout from the destination node.
    // Acknowledge is discarded in case it is received after the timeout.
    // </summary>
    CompleteNoAcknowledge(0x01),
    // <summary>
    // no route found in Assign Route
    // </summary>
    CompleteNoRoute(0x04),
    // <summary>
    // Successfully
    // </summary>
    CompleteOk(0x00),
    // <summary>
    // No Communication ACK received
    // </summary>
    NoAcknowledge(0x05),
    // <summary>
    // No response received
    // </summary>
    ResMissing(0x06);

    public static TXStatus getByVal(int value)
    {
      for (TXStatus s : TXStatus.class.getEnumConstants())
        if (s.get() == value)
          return s;
      return null;
    }

    private DataPacket[] responses = null;

    private int value;

    TXStatus(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }

    public DataPacket getResponse()
    {
      return this.responses[0];
    }

    public DataPacket[] getResponses()
    {
      return this.responses;
    }

    public void setResponse(DataPacket response)
    {
      this.responses = new DataPacket[1];
      this.responses[0] = response;
    }

    public void setResponses(DataPacket[] responses)
    {
      this.responses = responses;
    }
  }

  public enum ZWaveRediscoveryNeededReturnValue
  {
    Abort(0x01), Done(0x00), LostAccepted(0x05), LostFailed(0x04);

    public static ZWaveRediscoveryNeededReturnValue getByVal(int value)
    {
      for (ZWaveRediscoveryNeededReturnValue v : ZWaveRediscoveryNeededReturnValue.class
          .getEnumConstants())
        if (v.get() == value)
          return v;
      return null;
    }

    private int value;

    ZWaveRediscoveryNeededReturnValue(int value)
    {
      this.value = value;
    }

    public int get()
    {
      return this.value;
    }
  }
}
