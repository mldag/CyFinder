/*  
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.claflin.cyfinder.internal.tasks;

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;

import edu.claflin.cyfinder.internal.logic.ConfigurationBundle;
import edu.claflin.finder.algo.Algorithm;

public class BronKersbochTask extends GeneralAlgorithmTask
{
	/**
	 * look for max biclique or clique?
	 */
	private final boolean bipartite;

	/**
	 * Constructs the Task.
	 * 
	 * @param network   the Network to analyze.
	 * @param config    the Configuration to use.
	 * @param bipartite whether we are looking for bipartite or cliques.
	 */
	public BronKersbochTask(CyNetwork network, ConfigurationBundle config, HashMap<String, String> messages, boolean bipartite)
	{
		super(network, config, messages);
		this.bipartite = bipartite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preconfigure()
	{
		Algorithm algo = config.getAlgo();
		algo.args.putBoolean("bipartite", bipartite);
	}
}
