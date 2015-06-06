package jld.Exceptions;

public class InvalidPacketException extends Exception {
	/**
	 * Damit die Warnings weg sind.
	 */
	private static final long serialVersionUID = -5472293598446795484L;

	public InvalidPacketException() {
		super("Packet invalid!");
	}
}
