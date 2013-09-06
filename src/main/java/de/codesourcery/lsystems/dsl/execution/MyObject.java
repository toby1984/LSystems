package de.codesourcery.lsystems.dsl.execution;

import java.util.HashMap;
import java.util.Map;

import de.codesourcery.lsystems.dsl.nodes.TermNode.TermType;
import de.codesourcery.lsystems.dsl.symbols.Identifier;

public class MyObject {

	private final Map<Identifier,Object> properties = new HashMap<>();
	
	private final boolean isPrimitive;
	private TermType type;
	private  Object primitiveValue;
	
	public MyObject(TermType type, boolean isPrimitive) 
	{
		if ( type == null ) {
			throw new IllegalArgumentException("type must not be NULL");
		}
		this.type = type;
		this.isPrimitive = isPrimitive;
	}
	
	public TermType getType() {
		return type;
	}
	
	public Object getPrimitiveValue() {
		return primitiveValue;
	}
	
	public void setPrimitiveValue(Object primitiveValue) {
		this.primitiveValue = primitiveValue;
	}
	
	public boolean hasType(TermType t) 
	{
		return t.equals( this.type );
	}	
	
	public void setType(TermType type) 
	{
		if (type == null) {
			throw new IllegalArgumentException("type must not be NULL");
		}
		this.type = type;
	}
	
	public Map<Identifier, Object> getProperties() {
		if ( isPrimitive() ) {
			throw new UnsupportedOperationException("Primitive objects don't have properties");
		}				
		return properties;
	}
	
	public boolean isDefined(Identifier key) 
	{
		if ( isPrimitive() ) {
			throw new UnsupportedOperationException("Primitive objects don't have properties");
		}		
		if (key == null) {
			throw new IllegalArgumentException("key must not be NULL");
		}
		return properties.containsKey( key );
	}
	
	public boolean isPrimitive() {
		return isPrimitive;
	}
	
	public Object setProperty(Identifier key,Object value) 
	{
		if ( isPrimitive() ) {
			throw new UnsupportedOperationException("Cannot set value on primitive object");
		}
		
		if ( key == null ) {
			throw new IllegalArgumentException("key must not be NULL");
		}
		return properties.put(key,value);
	}
	
	public Object getProperty(Identifier key) 
	{
		if ( isPrimitive() ) {
			throw new UnsupportedOperationException("Primitive object don't have properties");
		}
		
		if ( key == null ) {
			throw new IllegalArgumentException("key must not be NULL");
		}
		if ( ! isDefined( key ) ) {
			throw new RuntimeException("Property '"+key+"' is not defined");
		}
		final Object result = properties.get( key );
		return result;
	}
	
	@Override
	public String toString() 
	{
		if ( isPrimitive() ) {
			return "primitive[ value="+primitiveValue+" , type="+type+"]";
		}
		return "object[ type="+type+", properties= "+properties+" ]";
	}
}