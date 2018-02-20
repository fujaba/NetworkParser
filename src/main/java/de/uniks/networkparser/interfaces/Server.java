package de.uniks.networkparser.interfaces;

public interface Server {
	public static final String BROADCAST = "BROADCAST";
	public static final String TIME = "Time";
	public static final String TCP = "TCP";
	public static final String REST = "REST";

	public boolean close();
	public void run();
	public boolean isRun();
}
