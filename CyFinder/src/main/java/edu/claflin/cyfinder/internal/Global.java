/* 
 * Copyright 2015 Charles Allen Schultz II.
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
package edu.claflin.cyfinder.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.work.TaskManager;

/**
 * Contains Global Constants.
 * 
 * @author Charles Allen Schultz II
 * @version 1.1 June 17, 2015
 */
public class Global
{
	static CyApplicationManager cyApplicationManagerService = null;	
	static CyNetworkManager networkManagerService = null;
	static CyRootNetworkManager rootNetworkService = null;
	static CySwingApplication desktopService = null;
	static TaskManager taskManagerService = null;	
	
	public static CyApplicationManager getApplicationManagerService()
	{
		return cyApplicationManagerService;
	}
	
	public static CyNetworkManager getNetworkManagerService()
	{
		return networkManagerService;
	}
	
	public static CyRootNetworkManager getRootNetworkService()
	{
		return rootNetworkService;
	}
	
	public static CySwingApplication getDesktopService()
	{
		return desktopService;
	}

	public static TaskManager getTaskManagerService()
	{
		return taskManagerService;
	}
}
