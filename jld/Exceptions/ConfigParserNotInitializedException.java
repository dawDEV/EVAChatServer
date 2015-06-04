package jld.Exceptions;

public class ConfigParserNotInitializedException extends Exception {
	private static final long serialVersionUID = 1L;

	public ConfigParserNotInitializedException(){
		super("Config parser has not been initialized!");
	}
}
