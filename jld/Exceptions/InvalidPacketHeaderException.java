package jld.Exceptions;

public class InvalidPacketHeaderException extends Exception {
	/**
	 * Damit die Warnings weg sind
	 */
	private static final long serialVersionUID = -210450484382649404L;

	public InvalidPacketHeaderException() {
		super("Packetheader invalid!");
	}
}
