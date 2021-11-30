package edu.claflin.finder.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.claflin.finder.logic.Condition;

/**
 * Used to supply arguments to Algorithms in a generalized manner. The caller is
 * required to put arguments on the bundle prior to passing it to the
 * Algorithm's constructor. The algorithm, then, can interpret the arguments it
 * wants to and ignore others. This generalizes the constructor of the abstract
 * Algorithm object so that algorithms that take different arguments may be
 * created without complicating the constructors of others.
 * 
 * Specifically, the bundle supports the following kinds of arguments: - Enums -
 * Integers - Floats - Objects - a special Condition object list
 * 
 * @author Charles Allen Schultz II
 * @version 3.0 February 2, 2016
 */
public class ArgumentsBundle
{

	/**
	 * The List of conditions to apply to the Algorithm.
	 */
	private final ArrayList<Condition> conditionsList;
	/**
	 * The Mapping of String keywords to Notable Integer values.
	 */
	private final HashMap<String, Integer> integerList;
	/**
	 * The Mapping of String keywords to Notable Double values.
	 */
	private final HashMap<String, Double> doubleList;
	/**
	 * The Mapping of String keywords to Notable Boolean values.
	 */
	private final HashMap<String, Boolean> booleanList;
	/**
	 * The Mapping of String keywords to Notable Enum values.
	 */
	private final HashMap<String, Enum> enumList;
	/**
	 * The Mapping of String keywords to Notable objects.
	 */
	private final HashMap<String, Object> objectsList;

	/**
	 * Constructs the ArgumentsBundle.
	 */
	public ArgumentsBundle()
	{
		conditionsList = new ArrayList<>();
		integerList = new HashMap<>();
		doubleList = new HashMap<>();
		booleanList = new HashMap<>();
		enumList = new HashMap<>();
		objectsList = new HashMap<>();
	}

	/**
	 * Adds a Condition to the conditionsList.
	 * 
	 * @param condition the Condition object to add.
	 */
	public void addCondition(Condition condition)
	{
		conditionsList.add(condition);
	}

	/**
	 * Removes a Condition from the conditionsList.
	 * 
	 * @param condition the Condition object to remove.
	 */
	public void removeCondition(Condition condition)
	{
		conditionsList.remove(condition);
	}

	/**
	 * Gets a copy of the conditionsList that will not structurally modify the one
	 * owned by the bundle.
	 * 
	 * @return a List of Conditions held in this ArgumentsBundle.
	 */
	public List<Condition> getConditionsList()
	{
		return conditionsList.subList(0, conditionsList.size());
	}

	/**
	 * Puts an integer in the integerList.
	 * 
	 * @param key the String keyword which maps to the integer.
	 * @param val the Integer to put.
	 */
	public void putInteger(String key, Integer val)
	{
		integerList.put(key, val);
	}

	/**
	 * Removes an integer from the integerList.
	 * 
	 * @param key the String keyword which maps to the integer.
	 */
	public void removeInteger(String key)
	{
		integerList.remove(key);
	}

	/**
	 * Gets an integer from the integerList.
	 * 
	 * @param key the String keyword which maps to the integer.
	 * @return the Integer in the list or null if not present.
	 */
	public Integer getInteger(String key)
	{
		return integerList.get(key);
	}

	/**
	 * Puts a double in the doubleList.
	 * 
	 * @param key the String keyword which maps to the double.
	 * @param val the Double to put.
	 */
	public void putDouble(String key, Double val)
	{
		doubleList.put(key, val);
	}

	/**
	 * Removes a double from the doubleList.
	 * 
	 * @param key the String keyword which maps to the double.
	 */
	public void removeDouble(String key)
	{
		doubleList.remove(key);
	}

	/**
	 * Gets a double from the doubleList.
	 * 
	 * @param key the String keyword which maps to the double.
	 * @return the Double in the list or null if not present.
	 */
	public Double getDouble(String key)
	{
		return doubleList.get(key);
	}

	/**
	 * Puts a boolean in the booleanList.
	 * 
	 * @param key the String keyword which maps to the boolean.
	 * @param val the Boolean to put.
	 */
	public void putBoolean(String key, Boolean val)
	{
		booleanList.put(key, val);
	}

	/**
	 * Removes a boolean from the booleanList.
	 * 
	 * @param key the String keyword which maps to the boolean.
	 */
	public void removeBoolean(String key)
	{
		booleanList.remove(key);
	}

	/**
	 * Gets a boolean from the booleanList.
	 * 
	 * @param key the String keyword which maps to the boolean.
	 * @return the Boolean in the list or null if not present.
	 */
	public Boolean getBoolean(String key)
	{
		return booleanList.get(key);
	}

	/**
	 * Puts an enum in the enumList.
	 * 
	 * @param key the String keyword which maps to the enum.
	 * @param val the Enum to put.
	 */
	public void putEnum(String key, Enum val)
	{
		enumList.put(key, val);
	}

	/**
	 * Removes an enum from the enumList.
	 * 
	 * @param key the String keyword which maps to the enum.
	 */
	public void removeEnum(String key)
	{
		enumList.remove(key);
	}

	/**
	 * Gets an enum from the enumList.
	 * 
	 * @param key the String keyword which maps to the enum.
	 * @return the Enum in the list or null if not present.
	 */
	public Enum getEnum(String key)
	{
		return enumList.get(key);
	}

	/**
	 * Puts an object in the enumList.
	 * 
	 * @param key the String keyword which maps to the object.
	 * @param val the Object to put.
	 */
	public void putObject(String key, Object val)
	{
		objectsList.put(key, val);
	}

	/**
	 * Removes an object from the enumList.
	 * 
	 * @param key the String keyword which maps to the object.
	 */
	public void removeObject(String key)
	{
		objectsList.remove(key);
	}

	/**
	 * Gets an object from the enumList.
	 * 
	 * @param key the String keyword which maps to the object.
	 * @return the Object in the list or null if not present.
	 */
	public Object getObject(String key)
	{
		return objectsList.get(key);
	}

	/**
	 * An enumeration containing common arguments used by the program.
	 */
	public static enum COMMON_ARGS
	{
		EDGE_PRESERVATION("edge_preservation"), EDGE_WEIGHT_COMPARATOR("edge_weight_comparator");

		/**
		 * A String description of the enum.
		 */
		private String desc;

		COMMON_ARGS(String desc)
		{
			this.desc = desc;
		}

		@Override
		public String toString()
		{
			return desc;
		}
	}

	@Override
	public String toString()
	{
		String result = "";
		
		result += conditionsList + "\n";
		
		result += "Integers: ";
		for (Map.Entry<String, Integer> x : integerList.entrySet())
		{
			result += x.getKey() + ": " + x.getValue();
		}
		result += "\n";
		
		result += "Doubles: ";
		for (Map.Entry<String, Double> x : doubleList.entrySet())
		{
			result += x.getKey() + ": " + x.getValue();			
		}
		result += "\n";
		
		result += "Booleans: ";
		for (Map.Entry<String, Boolean> x : booleanList.entrySet())
		{
			result += x.getKey() + ": " + x.getValue();
		}
		result += "\n";
		
		result += "Enums: ";
		for (Map.Entry<String, Enum> x : enumList.entrySet())
		{
			result += x.getKey() + ": " + x.getValue();
		}
		result += "\n";
		
		result += "Objects: ";
		for (Map.Entry<String, Object> x : objectsList.entrySet())
		{
			result += x.getKey() + ": " + x.getValue();
		}
		result += "\n";
		
		return result;
	}
}
