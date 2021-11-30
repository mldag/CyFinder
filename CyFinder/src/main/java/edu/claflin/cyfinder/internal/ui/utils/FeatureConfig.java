package edu.claflin.cyfinder.internal.ui.utils;

public class FeatureConfig
{
	private boolean ordered;
	private boolean weightSelect;
	private boolean minNodeCount;
	private boolean tiedNodeCount;
	
	public FeatureConfig () 
	{
		ordered = true;
		weightSelect = false;
		minNodeCount = false;
		tiedNodeCount = false;
	}

	/**
	 * @return the ordered
	 */
	public boolean isOrdered()
	{
		return ordered;
	}

	/**
	 * @param ordered the ordered to set
	 */
	public void setOrdered(boolean ordered)
	{
		this.ordered = ordered;
	}

	/**
	 * @return the weightSelect
	 */
	public boolean isWeightSelect()
	{
		return weightSelect;
	}

	/**
	 * @param weightSelect the weightSelect to set
	 */
	public void setWeightSelect(boolean weightSelect)
	{
		this.weightSelect = weightSelect;
	}

	/**
	 * @return the minNodeCount
	 */
	public boolean isMinNodeCount()
	{
		return minNodeCount;
	}

	/**
	 * @param minNodeCount the minNodeCount to set
	 */
	public void setMinNodeCount(boolean minNodeCount)
	{
		this.minNodeCount = minNodeCount;
	}

	/**
	 * @return the tiedNodeCount
	 */
	public boolean isTiedNodeCount()
	{
		return tiedNodeCount;
	}

	/**
	 * @param tiedNodeCount the tiedNodeCount to set
	 */
	public void setTiedNodeCount(boolean tiedNodeCount)
	{
		this.tiedNodeCount = tiedNodeCount;
	}
}
