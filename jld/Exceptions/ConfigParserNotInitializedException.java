package jld.Exceptions;

public class ConfigParserNotInitializedException extends Exception {
	public ConfigParserNotInitializedException(){
		super("Config parser has not been initialized!");
	}
}
