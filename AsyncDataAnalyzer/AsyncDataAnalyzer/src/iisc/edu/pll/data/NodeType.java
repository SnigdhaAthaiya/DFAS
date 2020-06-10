package iisc.edu.pll.data;

public class NodeType {

	
	public static final byte ENTRY=0;
	public static final byte EXIT=1;
	public static final byte RETSITE=2;
	public static final byte CALLSITE=3;
	public static final byte OTHER = 4;
	
	private byte type;
	private String procName;
	
	
	public NodeType(byte type, String procName) {
		
		this.type = type;
		this.procName = procName;
	}


	public byte getType() {
		return type;
	}


	public void setType(byte type) {
		this.type = type;
	}


	public String getProcName() {
		return procName;
	}


	public void setProcName(String procName) {
		this.procName = procName;
	}


	@Override
	public String toString() {
		return "NodeType [type=" + type + ", procName=" + procName + "]";
	}
	
	
}
