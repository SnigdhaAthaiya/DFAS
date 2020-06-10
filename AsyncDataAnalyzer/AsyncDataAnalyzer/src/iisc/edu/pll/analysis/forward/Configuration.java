package iisc.edu.pll.analysis.forward;

import java.util.Arrays;

public class Configuration {

	byte[] conf;

	public byte[] getConf() {
		return conf;
	}
	
	public byte getConfAt(int index){
		if(index >= conf.length)
			return -1;
		
		return conf[index];
	}

	public void setConf(byte[] conf) {
		this.conf = conf;
	}

	public Configuration(byte[] conf) {
		super();
		this.conf = conf;
	}
	
	public Configuration(int size) {
		this.conf = new byte[size];
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(conf);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Configuration other = (Configuration) obj;
		if (!Arrays.equals(conf, other.conf))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[conf=" + Arrays.toString(conf) + "]";
	}

	public boolean isZeroVector() {
		
		return false;
	}
	
	
}
