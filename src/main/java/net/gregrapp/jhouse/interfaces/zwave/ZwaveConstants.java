/**
 * 
 */
package net.gregrapp.jhouse.interfaces.zwave;

/**
 * @author Greg Rapp
 * 
 */
public class ZwaveConstants
{
  // Definition of Z-Wave constants;
  public static final int SOF = 0x01;

  public static final int ACK = 0x06;
  public static final int NAK = 0x15;
  public static final int CAN = 0x18;

  public static final int MAGIC_LEN = 29;

  public static final int REQUEST = 0x00;
  public static final int RESPONSE = 0x01;

  public static final int TRANSMIT_OPTION_NONE = 0x00;
  public static final int TRANSMIT_OPTION_ACK = 0x01;
  public static final int TRANSMIT_OPTION_LOW_POWER = 0x02;
  public static final int TRANSMIT_OPTION_AUTO_ROUTE = 0x04;
  public static final int TRANSMIT_OPTION_FORCE_ROUTE = 0x08;
  public static final int TRANSMIT_OPTION_NO_ROUTE = 0x10;
  public static final int TRANSMIT_OPTION_NO_RETRANSMIT = 0x40;

  public static final int TRANSMIT_COMPLETE_OK = 0x00;
  public static final int TRANSMIT_COMPLETE_NO_ACK = 0x01;
  public static final int TRANSMIT_COMPLETE_FAIL = 0x02;
  public static final int TRANSMIT_COMPLETE_NOROUTE = 0x04;

  public static final int RECEIVE_STATUS_TYPE_BROAD = 0x04;

  public static final int NODE_BROADCAST = 0xff;

  public static final int FUNC_ID_SERIAL_API_GET_INIT_DATA = 0x02;
  public static final int FUNC_ID_SERIAL_API_APPL_NODE_INFORMATION = 0x03;
  public static final int FUNC_ID_APPLICATION_COMMAND_HANDLER = 0x04;
  public static final int FUNC_ID_ZW_GET_CONTROLLER_CAPABILITIES = 0x05;
  public static final int FUNC_ID_SERIAL_API_SET_TIMEOUTS = 0x06;
  public static final int FUNC_ID_SERIAL_API_GET_CAPABILITIES = 0x07;
  public static final int FUNC_ID_SERIAL_API_SOFT_RESET = 0x08;
  public static final int FUNC_ID_ZW_SET_R_F_RECEIVE_MODE = 0x10;
  public static final int FUNC_ID_ZW_SET_SLEEP_MODE = 0x11;
  public static final int FUNC_ID_ZW_SEND_NODE_INFORMATION = 0x12;
  public static final int FUNC_ID_ZW_SEND_DATA = 0x13;
  public static final int FUNC_ID_ZW_SEND_DATA_MULTI = 0x14;
  public static final int FUNC_ID_ZW_GET_VERSION = 0x15;
  public static final int FUNC_ID_ZW_SEND_DATA_ABORT = 0x16;
  public static final int FUNC_ID_ZW_R_F_POWER_LEVEL_SET = 0x17;
  public static final int FUNC_ID_ZW_SEND_DATA_META = 0x18;
  public static final int FUNC_ID_MEMORY_GET_ID = 0x20;
  public static final int FUNC_ID_MEMORY_GET_BYTE = 0x21;
  public static final int FUNC_ID_MEMORY_PUT_BYTE = 0x22;
  public static final int FUNC_ID_MEMORY_GET_BUFFER = 0x23;
  public static final int FUNC_ID_MEMORY_PUT_BUFFER = 0x24;
  public static final int FUNC_ID_CLOCK_SET = 0x30;
  public static final int FUNC_ID_CLOCK_GET = 0x31;
  public static final int FUNC_ID_CLOCK_COMPARE = 0x32;
  public static final int FUNC_ID_RTC_TIMER_CREATE = 0x33;
  public static final int FUNC_ID_RTC_TIMER_READ = 0x34;
  public static final int FUNC_ID_RTC_TIMER_DELETE = 0x35;
  public static final int FUNC_ID_RTC_TIMER_CALL = 0x36;
  public static final int FUNC_ID_ZW_GET_NODE_PROTOCOL_INFO = 0x41;
  public static final int FUNC_ID_ZW_SET_DEFAULT = 0x42;
  public static final int FUNC_ID_ZW_REPLICATION_COMMAND_COMPLETE = 0x44;
  public static final int FUNC_ID_ZW_REPLICATION_SEND_DATA = 0x45;
  public static final int FUNC_ID_ZW_ASSIGN_RETURN_ROUTE = 0x46;
  public static final int FUNC_ID_ZW_DELETE_RETURN_ROUTE = 0x47;
  public static final int FUNC_ID_ZW_REQUEST_NODE_NEIGHBOR_UPDATE = 0x48;
  public static final int FUNC_ID_ZW_APPLICATION_UPDATE = 0x49;
  public static final int FUNC_ID_ZW_ADD_NODE_TO_NETWORK = 0x4A;
  public static final int FUNC_ID_ZW_REMOVE_NODE_FROM_NETWORK = 0x4B;
  public static final int FUNC_ID_ZW_CREATE_NEW_PRIMARY = 0x4C;
  public static final int FUNC_ID_ZW_CONTROLLER_CHANGE = 0x4D;
  public static final int FUNC_ID_ZW_SET_LEARN_MODE = 0x50;
  public static final int FUNC_ID_ZW_ASSIGN_SUC_RETURN_ROUTE = 0x51;
  public static final int FUNC_ID_ZW_ENABLE_SUC = 0x52;
  public static final int FUNC_ID_ZW_REQUEST_NETWORK_UPDATE = 0x53;
  public static final int FUNC_ID_ZW_SET_SUC_NODE_ID = 0x54;
  public static final int FUNC_ID_ZW_DELETE_SUC_RETURN_ROUTE = 0x55;
  public static final int FUNC_ID_ZW_GET_SUC_NODE_ID = 0x56;
  public static final int FUNC_ID_ZW_SEND_SUC_ID = 0x57;
  public static final int FUNC_ID_ZW_REDISCOVERY_NEEDED = 0x59;
  public static final int FUNC_ID_ZW_REQUEST_NODE_INFO = 0x60;
  public static final int FUNC_ID_ZW_REMOVE_FAILED_NODE_ID = 0x61;
  public static final int FUNC_ID_ZW_IS_FAILED_NODE = 0x62;
  public static final int FUNC_ID_ZW_REPLACE_FAILED_NODE = 0x63;
  public static final int FUNC_ID_TIMER_START = 0x70;
  public static final int FUNC_ID_TIMER_RESTART = 0x71;
  public static final int FUNC_ID_TIMER_CANCEL = 0x72;
  public static final int FUNC_ID_TIMER_CALL = 0x73;
  public static final int FUNC_ID_GET_ROUTING_TABLE_LINE = 0x80;
  public static final int FUNC_ID_GET_T_X_COUNTER = 0x81;
  public static final int FUNC_ID_RESET_T_X_COUNTER = 0x82;
  public static final int FUNC_ID_STORE_NODE_INFO = 0x83;
  public static final int FUNC_ID_STORE_HOME_ID = 0x84;
  public static final int FUNC_ID_LOCK_ROUTE_RESPONSE = 0x90;
  public static final int FUNC_ID_ZW_SEND_DATA_ROUTE_DEMO = 0x91;
  public static final int FUNC_ID_SERIAL_API_TEST = 0x95;
  public static final int FUNC_ID_SERIAL_API_SLAVE_NODE_INFO = 0xA0;
  public static final int FUNC_ID_APPLICATION_SLAVE_COMMAND_HANDLER = 0xA1;
  public static final int FUNC_ID_ZW_SEND_SLAVE_NODE_INFO = 0xA2;
  public static final int FUNC_ID_ZW_SEND_SLAVE_DATA = 0xA3;
  public static final int FUNC_ID_ZW_SET_SLAVE_LEARN_MODE = 0xA4;
  public static final int FUNC_ID_ZW_GET_VIRTUAL_NODES = 0xA5;
  public static final int FUNC_ID_ZW_IS_VIRTUAL_NODE = 0xA6;
  public static final int FUNC_ID_ZW_GET_NEIGHBOR_COUNT = 0xBB;
  public static final int FUNC_ID_ZW_ARE_NODES_NEIGHBOURS = 0xBC;
  public static final int FUNC_ID_ZW_TYPE_LIBRARY = 0xBD;
  public static final int FUNC_ID_ZW_SET_PROMISCUOUS_MODE = 0xD0;

  public static final int MODE_NODE_ANY = 0x01; // to add/remove any node;
  public static final int MODE_NODE_CONTROLLER = 0x02; // to add/remove/create new
                                   // primary/controller change;
  public static final int MODE_NODE_SLAVE = 0x03; // to add remove slave node;
  public static final int MODE_NODE_EXISTING = 0x04; // for add/remove node;
  public static final int MODE_NODE_STOP = 0x05; // to stop learn mode without error;
  public static final int MODE_NODE_STOP_FAILED = 0x06; // for add node/create new orimary;

  public static final int MODE_NODE_OPTION_HIGH_POWER = 0x80;

  public static final int NODE_STATUS_LEARN_READY = 0x01;
  public static final int NODE_STATUS_NODE_FOUND = 0x02;
  public static final int NODE_STATUS_ADDING_SLAVE = 0x03;
  public static final int NODE_STATUS_ADDING_CONTROLLER = 0x04;
  public static final int NODE_STATUS_PROTOCOL_DONE = 0x05;
  public static final int NODE_STATUS_DONE = 0x06;
  public static final int NODE_STATUS_FAILED = 0x07;

  // FUNC_ID_ZW_REMOVE_FAILED_NODE_ID and FUNC_ID_ZW_REPLACE_FAILED_NODE;
  public static final int FAILED_NODE_REMOVE_STARTED = 0x00;
  public static final int FAILED_NODE_REMOVE_NOT_PRIMARY_CONTROLLER = 0x02;
  public static final int FAILED_NODE_REMOVE_NO_CALLBACK_FUNCTION = 0x04;
  public static final int FAILED_NODE_REMOVE_NODE_NOT_FOUND = 0x08;
  public static final int FAILED_NODE_REMOVE_PROCESS_BUSY = 0x10;
  public static final int FAILED_NODE_REMOVE_FAIL = 0x20;
  // return code;
  public static final int FAILED_NODE_OK = 0x00;
  public static final int FAILED_NODE_REMOVED = 0x01;
  public static final int FAILED_NODE_NOT_REMOVED = 0x02;
  public static final int FAILED_NODE_REPLACE = 0x03;
  public static final int FAILED_NODE_REPLACE_DONE = 0x04;
  public static final int FAILED_NODE_REPLACE_FAILED = 0x00;

  public static final int SLAVE_LEARN_MODE_DISABLE = 0x00;
  public static final int SLAVE_LEARN_MODE_ENABLE = 0x01;
  public static final int SLAVE_LEARN_MODE_ADD = 0x02;
  public static final int SLAVE_LEARN_MODE_REMOVE = 0x04;

  public static final int ZW_SUC_FUNC_BASIC_SUC = 0x00;
  public static final int ZW_SUC_FUNC_NODEID_SERVER = 0x01;

  public static final int ZW_SUC_UPDATE_DONE = 0x00;
  public static final int ZW_SUC_UPDATE_ABORT = 0x01;
  public static final int ZW_SUC_UPDATE_WAIT = 0x02;
  public static final int ZW_SUC_UPDATE_DISABLED = 0x03;
  public static final int ZW_SUC_UPDATE_OVERFLOW = 0x04;
  public static final int ZW_SUC_SET_SUCCEEDED = 0x05;
  public static final int ZW_SUC_SET_FAILED = 0x06;

  public static final int UPDATE_STATE_SUC_ID = 0x10;
  public static final int UPDATE_STATE_DELETE_DONE = 0x20;
  public static final int UPDATE_STATE_ADD_DONE = 0x40;

  public static final int UPDATE_STATE_NODE_INFO_RECEIVED = 0x84;
  public static final int UPDATE_STATE_NODE_INFO_REQ_FAILED = 0x81;

  // FUNC_ID_ZW_SET_SLAVE_LEARN_MODE;
  public static final int ASSIGN_COMPLETE = 0x00;
  public static final int ASSIGN_NODEID_DONE = 0x01;
  public static final int ASSIGN_RANGE_INFO_UPDATE = 0x02;

  public static final int BASIC_TYPE_CONTROLLER = 0x01;
  public static final int BASIC_TYPE_STATIC_CONTROLLER = 0x02;
  public static final int BASIC_TYPE_SLAVE = 0x03;
  public static final int BASIC_TYPE_ROUTING_SLAVE = 0x04;

  public static final int GENERIC_TYPE_GENERIC_CONTROLLER = 0x01;
  public static final int GENERIC_TYPE_STATIC_CONTROLLER = 0x02;
  public static final int GENERIC_TYPE_AV_CONTROL_POINT = 0x03;
  public static final int GENERIC_TYPE_DISPLAY = 0x06;
  public static final int GENERIC_TYPE_GARAGE_DOOR = 0x07;
  public static final int GENERIC_TYPE_THERMOSTAT = 0x08;
  public static final int GENERIC_TYPE_WINDOW_COVERING = 0x09;
  public static final int GENERIC_TYPE_REPEATER_SLAVE = 0x0F;
  public static final int GENERIC_TYPE_SWITCH_BINARY = 0x10;
  public static final int GENERIC_TYPE_SWITCH_MULTILEVEL = 0x11;
  public static final int GENERIC_TYPE_SWITCH_REMOTE = 0x12;
  public static final int GENERIC_TYPE_SWITCH_TOGGLE = 0x13;
  public static final int GENERIC_TYPE_SENSOR_BINARY = 0x20;
  public static final int GENERIC_TYPE_SENSOR_MULTILEVEL = 0x21;
  public static final int GENERIC_TYPE_WATER_CONTROL = 0x22;
  public static final int GENERIC_TYPE_METER_PULSE = 0x30;
  public static final int GENERIC_TYPE_ENTRY_CONTROL = 0x40;
  public static final int GENERIC_TYPE_SEMI_INTEROPERABLE = 0x50;
  public static final int GENERIC_TYPE_NON_INTEROPERABLE = 0xFF;

  public static final int COMMAND_CLASS_MARK = 0xef;

  public static final int COMMAND_CLASS_BASIC = 0x20;
  public static final int BASIC_SET = 0x01;
  public static final int BASIC_GET = 0x02;
  public static final int BASIC_REPORT = 0x03;

  public static final int COMMAND_CLASS_VERSION = 0x86;
  public static final int VERSION_GET = 0x11;
  public static final int VERSION_REPORT = 0x12;

  public static final int COMMAND_CLASS_BATTERY = 0x80;
  public static final int BATTERY_GET = 0x02;
  public static final int BATTERY_REPORT = 0x03;

  public static final int COMMAND_CLASS_WAKE_UP = 0x84;
  public static final int WAKE_UP_INTERVAL_SET = 0x04;
  public static final int WAKE_UP_NOTIFICATION = 0x07;
  public static final int WAKE_UP_NO_MORE_INFORMATION = 0x08;

  public static final int COMMAND_CLASS_CONTROLLER_REPLICATION = 0x21;
  public static final int CTRL_REPLICATION_TRANSFER_GROUP = 0x31;

  public static final int COMMAND_CLASS_SWITCH_MULTILEVEL = 0x26;
  public static final int SWITCH_MULTILEVEL_SET = 0x01;
  public static final int SWITCH_MULTILEVEL_GET = 0x02;
  public static final int SWITCH_MULTILEVEL_REPORT = 0x03;
  public static final int SWITCH_MULTILEVEL_REPORT_BEGIN = 0x04;
  public static final int SWITCH_MULTILEVEL_REPORT_BEGIN_UP = 0x20;
  public static final int SWITCH_MULTILEVEL_REPORT_BEGIN_DOWN = 0x60;
  public static final int SWITCH_MULTILEVEL_REPORT_END = 0x05;

  public static final int COMMAND_CLASS_SWITCH_ALL = 0x27;

  public static final int SWITCH_ALL_EXCLUDE_ON_OFF = 0x00;
  public static final int SWITCH_ALL_SET = 0x01;

  public static final int SWITCH_ALL_GET = 0x02;
  public static final int SWITCH_ALL_REPORT = 0x03;

  public static final int SWITCH_ALL_ON = 0x04;
  public static final int SWITCH_ALL_OFF = 0x05;
  public static final int SWITCH_ALL_ENABLE_ON_OFF = 0xFF;

  public static final int COMMAND_CLASS_SENSOR_BINARY = 0x30;

  public static final int SENSOR_BINARY_GET = 0x02;
  public static final int SENSOR_BINARY_REPORT = 0x03;

  public static final int COMMAND_CLASS_SENSOR_MULTILEVEL = 0x31;

  public static final int SENSOR_MULTILEVEL_VERSION = 0x01;
  public static final int SENSOR_MULTILEVEL_GET = 0x04;
  public static final int SENSOR_MULTILEVEL_REPORT = 0x05;

  public static final int SENSOR_MULTILEVEL_REPORT_TEMPERATURE = 0x01;
  public static final int SENSOR_MULTILEVEL_REPORT_GENERAL_PURPOSE_VALUE = 0x02;
  public static final int SENSOR_MULTILEVEL_REPORT_LUMINANCE = 0x03;
  public static final int SENSOR_MULTILEVEL_REPORT_POWER = 0x04;
  public static final int SENSOR_MULTILEVEL_REPORT_RELATIVE_HUMIDITY = 0x05;
  public static final int SENSOR_MULTILEVEL_REPORT_CO2_LEVEL = 0x11;
  public static final int SENSOR_MULTILEVEL_REPORT_SIZE_MASK = 0x07;
  public static final int SENSOR_MULTILEVEL_REPORT_SCALE_MASK = 0x18;
  public static final int SENSOR_MULTILEVEL_REPORT_SCALE_SHIFT = 0x03;
  public static final int SENSOR_MULTILEVEL_REPORT_PRECISION_MASK = 0xe0;
  public static final int SENSOR_MULTILEVEL_REPORT_PRECISION_SHIFT = 0x05;

  public static final int COMMAND_CLASS_ALARM = 0x71;
  public static final int ALARM_REPORT = 0x05;

  public static final int COMMAND_CLASS_MULTI_CMD = 0x8F;
  public static final int MULTI_CMD_VERSION = 0x01;
  public static final int MULTI_CMD_ENCAP = 0x01;
  public static final int MULTI_CMD_RESPONSE_ENCAP = 0x02;

  public static final int COMMAND_CLASS_CLIMATE_CONTROL_SCHEDULE = 0x46;
  public static final int SCHEDULE_SET = 0x01;
  public static final int SCHEDULE_GET = 0x02;
  public static final int SCHEDULE_CHANGED_GET = 0x04;
  public static final int SCHEDULE_CHANGED_REPORT = 0x05;
  public static final int SCHEDULE_OVERRIDE_GET = 0x07;
  public static final int SCHEDULE_OVERRIDE_REPORT = 0x08;

  public static final int COMMAND_CLASS_CLOCK = 0x81;
  public static final int CLOCK_GET = 0x05;
  public static final int CLOCK_SET = 0x04;
  public static final int CLOCK_REPORT = 0x06;

  public static final int COMMAND_CLASS_ASSOCIATION = 0x85;
  public static final int ASSOCIATION_SET = 0x01;
  public static final int ASSOCIATION_GET = 0x02;
  public static final int ASSOCIATION_REPORT = 0x03;
  public static final int ASSOCIATION_REMOVE = 0x04;

  public static final int COMMAND_CLASS_CONFIGURATION = 0x70;
  public static final int CONFIGURATION_SET = 0x04;
  public static final int CONFIGURATION_GET = 0x05;
  public static final int CONFIGURATION_REPORT = 0x06;
  public static final int CONFIGURATION_SIZE_MASK = 0x07;

  public static final int COMMAND_CLASS_MANUFACTURER_SPECIFIC = 0x72;
  public static final int MANUFACTURER_SPECIFIC_GET = 0x04;
  public static final int MANUFACTURER_SPECIFIC_REPORT = 0x05;

  public static final int COMMAND_CLASS_APPLICATION_STATUS = 0x22;
  public static final int COMMAND_CLASS_ASSOCIATION_COMMAND_CONFIGURATION = 0x9B;
  public static final int COMMAND_CLASS_AV_CONTENT_DIRECTORY_MD = 0x95;
  public static final int COMMAND_CLASS_AV_CONTENT_SEARCH_MD = 0x97;
  public static final int COMMAND_CLASS_AV_RENDERER_STATUS = 0x96;
  public static final int COMMAND_CLASS_AV_TAGGING_MD = 0x99;
  public static final int COMMAND_CLASS_BASIC_WINDOW_COVERING = 0x50;
  public static final int COMMAND_CLASS_CHIMNEY_FAN = 0x2A;
  public static final int COMMAND_CLASS_COMPOSITE = 0x8D;
  public static final int COMMAND_CLASS_DOOR_LOCK = 0x62;
  public static final int COMMAND_CLASS_ENERGY_PRODUCTION = 0x90;
  public static final int COMMAND_CLASS_FIRMWARE_UPDATE_MD = 0x7a;
  public static final int COMMAND_CLASS_GEOGRAPHIC_LOCATION = 0x8C;
  public static final int COMMAND_CLASS_GROUPING_NAME = 0x7B;
  public static final int COMMAND_CLASS_HAIL = 0x82;
  public static final int COMMAND_CLASS_INDICATOR = 0x87;
  public static final int COMMAND_CLASS_IP_CONFIGURATION = 0x9A;
  public static final int COMMAND_CLASS_LANGUAGE = 0x89;
  public static final int COMMAND_CLASS_LOCK = 0x76;
  public static final int COMMAND_CLASS_MANUFACTURER_PROPRIETARY = 0x91;
  public static final int COMMAND_CLASS_METER_PULSE = 0x35;
  public static final int COMMAND_CLASS_METER = 0x32;
  public static final int METER_GET = 0x01;
  public static final int METER_REPORT = 0x02;
  public static final int METER_REPORT_ELECTRIC_METER = 0x01;
  public static final int METER_REPORT_GAS_METER = 0x02;
  public static final int METER_REPORT_WATER_METER = 0x03;
  public static final int METER_REPORT_SIZE_MASK = 0x07;
  public static final int METER_REPORT_SCALE_MASK = 0x18;
  public static final int METER_REPORT_SCALE_SHIFT = 0x03;
  public static final int METER_REPORT_PRECISION_MASK = 0xe0;
  public static final int METER_REPORT_PRECISION_SHIFT = 0x05;

  public static final int COMMAND_CLASS_MTP_WINDOW_COVERING = 0x51;
  public static final int COMMAND_CLASS_MULTI_INSTANCE_ASSOCIATION = 0x8E;
  public static final int COMMAND_CLASS_MULTI_INSTANCE = 0x60;
  public static final int MULTI_INSTANCE_VERSION = 0x01;
  public static final int MULTI_INSTANCE_GET = 0x04;
  public static final int MULTI_INSTANCE_CMD_ENCAP = 0x06;
  public static final int MULTI_INSTANCE_REPORT = 0x05;

  public static final int COMMAND_CLASS_NO_OPERATION = 0x00;
  public static final int COMMAND_CLASS_NODE_NAMING = 0x77;
  public static final int COMMAND_CLASS_NON_INTEROPERABLE = 0xf0;
  public static final int COMMAND_CLASS_POWERLEVEL = 0x73;
  public static final int COMMAND_CLASS_PROPRIETARY = 0x88;
  public static final int COMMAND_CLASS_PROTECTION = 0x75;
  public static final int COMMAND_CLASS_REMOTE_ASSOCIATION_ACTIVATE = 0x7c;
  public static final int COMMAND_CLASS_REMOTE_ASSOCIATION = 0x7d;
  public static final int COMMAND_CLASS_SCENE_ACTIVATION = 0x2b;
  public static final int COMMAND_CLASS_SCENE_ACTUATOR_CONF = 0x2C;
  public static final int COMMAND_CLASS_SCENE_CONTROLLER_CONF = 0x2D;
  public static final int COMMAND_CLASS_SCREEN_ATTRIBUTES = 0x93;
  public static final int COMMAND_CLASS_SCREEN_MD = 0x92;
  public static final int COMMAND_CLASS_SECURITY = 0x98;
  public static final int COMMAND_CLASS_SENSOR_ALARM = 0x9C;
  public static final int SENSOR_ALARM_REPORT = 0x02;

  public static final int COMMAND_CLASS_SENSOR_CONFIGURATION = 0x9E;
  public static final int COMMAND_CLASS_SILENCE_ALARM = 0x9d;
  public static final int COMMAND_CLASS_SIMPLE_AV_CONTROL = 0x94;
  public static final int COMMAND_CLASS_SWITCH_BINARY = 0x25;
  public static final int COMMAND_CLASS_SWITCH_TOGGLE_BINARY = 0x28;
  public static final int COMMAND_CLASS_SWITCH_TOGGLE_MULTILEVEL = 0x29;
  public static final int COMMAND_CLASS_THERMOSTAT_FAN_MODE = 0x44;
  public static final int THERMOSTAT_FAN_MODE_VERSION = 0x01;
  public static final int THERMOSTAT_FAN_MODE_GET = 0x02;
  public static final int THERMOSTAT_FAN_MODE_REPORT = 0x03;
  public static final int THERMOSTAT_FAN_MODE_SET = 0x01;
  public static final int THERMOSTAT_FAN_MODE_SUPPORTED_GET = 0x04;
  public static final int THERMOSTAT_FAN_MODE_SUPPORTED_REPORT = 0x05;
  public static final int THERMOSTAT_FAN_MODE_REPORT_FAN_MODE_MASK = 0x0F;
  public static final int THERMOSTAT_FAN_MODE_REPORT_RESERVED_MASK = 0xf0;
  public static final int THERMOSTAT_FAN_MODE_REPORT_RESERVED_SHIFT = 0x04;
  public static final int THERMOSTAT_FAN_MODE_SET_FAN_MODE_MASK = 0x0F;
  public static final int THERMOSTAT_FAN_MODE_SET_RESERVED_MASK = 0xF0;
  public static final int THERMOSTAT_FAN_MODE_SET_RESERVED_SHIFT = 0x04;

  public static final int COMMAND_CLASS_THERMOSTAT_FAN_STATE = 0x45;
  public static final int COMMAND_CLASS_THERMOSTAT_HEATING = 0x38;
  public static final int COMMAND_CLASS_THERMOSTAT_MODE = 0x40;
  public static final int THERMOSTAT_MODE_VERSION = 0x01;
  public static final int THERMOSTAT_MODE_GET = 0x02;
  public static final int THERMOSTAT_MODE_REPORT = 0x03;
  public static final int THERMOSTAT_MODE_SET = 0x01;
  public static final int THERMOSTAT_MODE_SUPPORTED_GET = 0x04;
  public static final int THERMOSTAT_MODE_SUPPORTED_REPORT = 0x05;

  public static final int COMMAND_CLASS_THERMOSTAT_OPERATING_STATE = 0x42;
  public static final int COMMAND_CLASS_THERMOSTAT_SETBACK = 0x47;
  public static final int COMMAND_CLASS_THERMOSTAT_SETPOINT = 0x43;
  public static final int THERMOSTAT_SETPOINT_VERSION = 0x01;
  public static final int THERMOSTAT_SETPOINT_GET = 0x02;
  public static final int THERMOSTAT_SETPOINT_REPORT = 0x03;
  public static final int THERMOSTAT_SETPOINT_SET = 0x01;
  public static final int THERMOSTAT_SETPOINT_SUPPORTED_GET = 0x04;
  public static final int THERMOSTAT_SETPOINT_SUPPORTED_REPORT = 0x05;
  public static final int THERMOSTAT_SETPOINT_GET_SETPOINT_TYPE_MASK = 0x0F;
  public static final int THERMOSTAT_SETPOINT_GET_RESERVED_MASK = 0xf0;
  public static final int THERMOSTAT_SETPOINT_GET_RESERVED_SHIFT = 0x04;
  public static final int THERMOSTAT_SETPOINT_REPORT_SETPOINT_TYPE_MASK = 0x0F;
  public static final int THERMOSTAT_SETPOINT_REPORT_RESERVED_MASK = 0xf0;
  public static final int THERMOSTAT_SETPOINT_REPORT_RESERVED_SHIFT = 0x04;
  public static final int THERMOSTAT_SETPOINT_REPORT_SIZE_MASK = 0x07;
  public static final int THERMOSTAT_SETPOINT_REPORT_SCALE_MASK = 0x18;
  public static final int THERMOSTAT_SETPOINT_REPORT_SCALE_SHIFT = 0x03;
  public static final int THERMOSTAT_SETPOINT_REPORT_PRECISION_MASK = 0xe0;
  public static final int THERMOSTAT_SETPOINT_REPORT_PRECISION_SHIFT = 0x05;
  public static final int THERMOSTAT_SETPOINT_SET_SETPOINT_TYPE_MASK = 0x0F;
  public static final int THERMOSTAT_SETPOINT_SET_RESERVED_MASK = 0xF0;
  public static final int THERMOSTAT_SETPOINT_SET_RESERVED_SHIFT = 0x04;
  public static final int THERMOSTAT_SETPOINT_SET_SIZE_MASK = 0x07;
  public static final int THERMOSTAT_SETPOINT_SET_SCALE_MASK = 0x18;
  public static final int THERMOSTAT_SETPOINT_SET_SCALE_SHIFT = 0x03;
  public static final int THERMOSTAT_SETPOINT_SET_PRECISION_MASK = 0xE0;
  public static final int THERMOSTAT_SETPOINT_SET_PRECISION_SHIFT = 0x05;

  public static final int COMMAND_CLASS_TIME_PARAMETERS = 0x8B;
  public static final int COMMAND_CLASS_TIME = 0x8a;
  public static final int COMMAND_CLASS_USER_CODE = 0x63;
  public static final int COMMAND_CLASS_ZIP_ADV_CLIENT = 0x34;
  public static final int COMMAND_CLASS_ZIP_ADV_SERVER = 0x33;
  public static final int COMMAND_CLASS_ZIP_ADV_SERVICES = 0x2F;
  public static final int COMMAND_CLASS_ZIP_CLIENT = 0x2e;
  public static final int COMMAND_CLASS_ZIP_SERVER = 0x24;
  public static final int COMMAND_CLASS_ZIP_SERVICES = 0x23;
  // FUNC_ID_ZW_REQUEST_NODE_NEIGHBOR_UPDATE;
  public static final int NODE_NEIGHBOR_UPDATE_STARTED = 0x21;
  public static final int NODE_NEIGHBOR_UPDATE_DONE = 0x22;
  public static final int NODE_NEIGHBOR_UPDATE_FAILED = 0x23;

  // FUNC_ID_ZW_GET_CONTROLLER_CAPABILITIES;
  public static final int CONTROLLER_IS_SECONDARY = 0x01;
  public static final int CONTROLLER_ON_OTHER_NETWORK = 0x02;
  public static final int CONTROLLER_NODEID_SERVER_PRESENT = 0x04;
  public static final int CONTROLLER_IS_REAL_PRIMARY = 0x08;
  public static final int CONTROLLER_IS_SUC = 0x10;

  // FUNC_ID_ZW_TYPE_LIBRARY;
  public static final int ZW_LIB_CONTROLLER_STATIC = 0x01;
  public static final int ZW_LIB_CONTROLLER = 0x02;
  public static final int ZW_LIB_CONTROLLER_BRIDGE = 0x07;
  public static final int ZW_LIB_SLAVE_ENHANCED = 0x03;
  public static final int ZW_LIB_SLAVE_ROUTING = 0xFF; // correct this value
                                   // !!!!!!!!!!!!!!!!!!!!!!!!!!;
  public static final int ZW_LIB_SLAVE = 0x04;
  public static final int ZW_LIB_INSTALLER = 0x05;
  public static final int ZW_NO_INTELLIGENT_LIFE = 0x06;

}
