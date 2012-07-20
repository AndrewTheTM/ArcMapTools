package org.oki.transmodel.arcgisAddIns;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.interop.AutomationException;

public class defQueryButton extends Button {

	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void onClick() throws IOException, AutomationException {
		// TODO Auto-generated method stub
		
		JOptionPane.showMessageDialog(null, "Hello World from the OKI Modeling Tools!");

	}
	@Override
	public void init(IApplication app) throws IOException, AutomationException {
		// TODO Andrew generated method stub
		super.init(app);
	}

}
