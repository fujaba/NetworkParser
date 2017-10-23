package de.uniks.networkparser.ext;

import java.math.BigDecimal;

import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.list.SimpleKeyValueList;


public class StartData implements SendableEntityCreatorNoIndex
{
	private SimpleKeyValueList<String, Object> values = new SimpleKeyValueList<String, Object>();

	@Override
	public String[] getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public boolean set(String attrName, Object value)
//	{
//		if (PROPERTY_NODEPORT.equals(attrName))
//		{
//			setNodePort(Integer.valueOf(""+value));
//			return true;
//		}
//		if (PROPERTY_ROLE.equals(attrName))
//		{
//			setRole((String) value);
//			return true;
//		}
//		if (PROPERTY_FIRST_PEER_ADDRESS.equals(attrName))
//		{
//			setFirstPeerAddress((String) value);
//			return true;
//		}
//		if (PROPERTY_LOCAL_CONF_DIR_PATH.equals(attrName))
//		{
//			setLocalConfDirPath((String) value);
//			return true;
//		}
//		if (PROPERTY_CONFNAME.equals(attrName))
//		{
//			setConfName((String) value);
//			return true;
//		}
//		if (PROPERTY_ROOMNAME.equals(attrName))
//		{
//			setRoomName((String) value);
//			return true;
//		}
//		if (PROPERTY_NODEFILESERVERPORT.equals(attrName))
//		{
//			if(value instanceof BigDecimal){
//				setNodeFileServerPort(((BigDecimal)value).intValue());
//			}else{
//				setNodeFileServerPort((Integer)value);
//			}
//			return true;
//		}
//		if (PROPERTY_DEBUG.equals(attrName))
//		{
//			setEnableDebug((String)value);
//			return true;
//		}
//		if (PROPERTY_OVERRIDE.equals(attrName))
//		{
//			setOverride(Boolean.valueOf(""+value));
//			return true;
//		}
//		if (PROPERTY_NICKNAME.equals(attrName))
//		{
//			setNickname((String)value);
//			return true;
//		}
//		if (PROPERTY_UPDATETIMERTASK.equals(attrName)){
//			setUpdateTimerTask(Integer.valueOf(""+value));
//			return true;
//		}
//		return super.set(attrName, value);
//	}
//
//	@Override
//	public Object get(String attrName)
//	{
//		if (PROPERTY_NODEPORT.equalsIgnoreCase(attrName))
//		{
//			return getNodePort();
//		}
//		else if (PROPERTY_ROLE.equalsIgnoreCase(attrName))
//		{
//			return getRole();
//		}
//		else if (PROPERTY_FIRST_PEER_ADDRESS.equalsIgnoreCase(attrName))
//		{
//			return getFirstPeerAddress();
//		}
//		else if (PROPERTY_LOCAL_CONF_DIR_PATH.equalsIgnoreCase(attrName))
//		{
//			return getLocalConfDirPath();
//		}
//		else if (PROPERTY_CONFNAME.equalsIgnoreCase(attrName))
//		{
//			return getConfName();
//		}
//		else if (PROPERTY_ROOMNAME.equalsIgnoreCase(attrName))
//		{
//			return getRoomName();
//		}
//		else if (PROPERTY_NODEFILESERVERPORT.equalsIgnoreCase(attrName))
//		{
//			return getNodeFileServerPort();
//		}
//		else if (PROPERTY_DEBUG.equalsIgnoreCase(attrName))
//		{
//			return getEnableDebug();
//		}
//		else if (PROPERTY_OVERRIDE.equalsIgnoreCase(attrName))
//		{
//			return isOverride();
//		}
//		else if (PROPERTY_NICKNAME.equalsIgnoreCase(attrName))
//		{
//			return getNickname();
//		}
//		else if (PROPERTY_UPDATETIMERTASK.equalsIgnoreCase(attrName))
//		{
//			return getUpdateTimerTask();
//		}
//		return super.get(attrName);
//	}
//	
//	
//	public void setNodePort(int value) 
//	{
//		int oldValue=this.nodeIpPort;
//		this.nodeIpPort = value;
//		firePropertyChange(PROPERTY_NODEPORT, oldValue, value);
//	}
//
//	public int getNodePort() 
//	{
//		return this.nodeIpPort;
//	}
//
//
//	public void setRole(String value) 
//	{
//		String oldValue=this.role;
//		this.role = value;
//		firePropertyChange(PROPERTY_ROLE, oldValue, value);
//	}
//
//	public String getRole() 
//	{
//		return this.role;
//	}
//
//	
//	public void setFirstPeerAddress(String value) 
//	{
//		String oldValue=this.firstPeerAddress;
//		this.firstPeerAddress = value;
//		firePropertyChange(PROPERTY_FIRST_PEER_ADDRESS, oldValue, value);
//	}
//
//	public String getFirstPeerAddress() 
//	{
//		return this.firstPeerAddress;
//	}
//
//	public void setConfName(String value) 
//	{
//		String oldValue=this.confName;
//		this.confName = value;
//		firePropertyChange(PROPERTY_CONFNAME, oldValue, value);
//	}
//
//	public String getConfName() 
//	{
//		return this.confName;
//	}
//
//	
//	public void setRoomName(String value) 
//	{
//		String oldValue=this.roomName;
//		this.roomName = value;
//		firePropertyChange(PROPERTY_ROOMNAME, oldValue, value);
//	}
//
//	public String getRoomName() 
//	{
//		return this.roomName;
//	}
//
//	public void setNodeFileServerPort(int value) 
//	{
//		int oldValue=this.nodeFileServerPort;
//		this.nodeFileServerPort = value;
//		firePropertyChange(PROPERTY_NODEFILESERVERPORT, oldValue, value);
//	}
//
//	public int getNodeFileServerPort() 
//	{
//		return this.nodeFileServerPort;
//	}
//	public int getFileServerPort() 
//	{
//		if(nodeFileServerPort==-1){
//			return nodeIpPort+FILESERVERADD;
//		}
//		return this.nodeFileServerPort;
//	}
//
//	public void setLocalConfDirPath(String value) 
//	{
//		String oldValue=this.localConfDirPath;
//		value = ""+value;
//		if(!value.endsWith("/")&&!value.endsWith("\\")){
//			value+="/";
//		}
//		this.localConfDirPath = value;
//		firePropertyChange(PROPERTY_LOCAL_CONF_DIR_PATH, oldValue, value);
//	}
//
//	public String getLocalConfDirPath() 
//	{
//		return this.localConfDirPath;
//	}
//
//	
//	public void setWorld(String value) 
//	{
//		String oldValue=this.world;
//		this.world = value;
//		firePropertyChange(PROPERTY_ROLE, oldValue, value);
//	}
//
//	public String getWorld() 
//	{
//		return this.role;
//	}
//	public void setNodeIp(String ip){
//		super.withName(ip);
//	}
//	public String getNodeIp() {
//		return getName();
//	}
//	public static String getHelpText() {
//		StringBuffer text=new StringBuffer();
////		text.append("Help for the commandline - ConfNet "+version+"\n\n");
//		text.append(PROPERTY_NODEPORT +"= Port for the communication\n");
//		text.append(PROPERTY_ROLE +"= Role for this Node<");
//		String[] roles=getRoleList();
//		for(int z=0;z<roles.length;z++){
//			if(z>0){
//				text.append(", "+roles[z]);
//			}else{
//				text.append(roles[z]);
//			}
//		}
//		text.append(">\n");
//		text.append(PROPERTY_FIRST_PEER_ADDRESS +"= Adress for the first communication");
//		text.append(PROPERTY_LOCAL_CONF_DIR_PATH +"= Local Base Directory\n");
//		text.append(PROPERTY_CONFNAME +"= Name of the conference\n");
//		text.append(PROPERTY_ROOMNAME +"= Name of the room (if this a room node)\n");
//		text.append(PROPERTY_NODEFILESERVERPORT +"= true or false for get the file contents\n");
//		return text.toString();
//	}
//	public String getEnableDebug() {
//		return enableDebug;
//	}
//	public void setEnableDebug(String enableDebug) {
//		this.enableDebug = enableDebug;
//	}
//	public boolean isAutoStart() {
//		return isAutoStart;
//	}
//	public void setAutoStart(boolean isAutoStart) {
//		this.isAutoStart = isAutoStart;
//	}
//	public static String[] getRoleList()
//	{
//		return new String[] {ROLE_REGISTRATION_DESK, ROLE_ROOM_NODE, ROLE_ADMIN_DESK, ROLE_REGISTRATION_VIEW}; 
//	}
//	public boolean isOverride() {
//		return override;
//	}
//	public void setOverride(boolean override) {
//		this.override = override;
//	}
//	public String getNickname() {
//		return nickname;
//	}
//	public void setNickname(String value) {
//		String oldValue=this.nickname;
//		this.nickname = value;
//		firePropertyChange(PROPERTY_NICKNAME, oldValue, value);
//	}
//	public int getUpdateTimerTask() {
//		return updateTimerTask;
//	}
//	public void setUpdateTimerTask(Integer value) {
//		Integer oldValue=this.updateTimerTask;
//		this.updateTimerTask = value;
//		firePropertyChange(PROPERTY_UPDATETIMERTASK, oldValue, value);
//	}
}
