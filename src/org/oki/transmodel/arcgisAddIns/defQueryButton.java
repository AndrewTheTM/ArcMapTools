package org.oki.transmodel.arcgisAddIns;

import java.io.IOException;

//import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IFeatureLayerDefinition;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.ISelectionSet;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.interop.AutomationException;

public class defQueryButton extends Button {

	private IApplication app;
	/**
	 * Called when the button is clicked.
	 * 
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	
	@Override
	public void onClick() throws IOException, AutomationException {
		
		System.out.println("Hello World!");
		//JOptionPane.showMessageDialog(null, "Hello World from the OKI Modeling Tools!");
		try{
			IMxDocument mxDoc = new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap = mxDoc.getFocusMap();
			int selectedCount=focusMap.getSelectionCount();
			if(selectedCount>0){
				for(int x=0;x<focusMap.getLayerCount();x++){
					if(focusMap.getLayer(x).getName().equals("Origin Locations")){
						FeatureLayer layer=(FeatureLayer) focusMap.getLayer(x);
						IFeatureSelection featSel=layer;
						ISelectionSet selSet=featSel.getSelectionSet();
						ITable table=selSet.getTarget();
						int snFieldN=table.findField("SAMPN");
						IEnumIDs selIds=selSet.getIDs();
						int iId=selIds.next();
						while(iId>0){
							System.out.println(iId);
							IRow row=table.getRow(iId);
							System.out.println(row.getValue(snFieldN));
							iId=selIds.next();
						}
					}
				}
			}

			for(int x=0;x<focusMap.getLayerCount();x++){
				if(focusMap.getLayer(x).getName().equals("Destination Locations")){
					ILayer layer=focusMap.getLayer(x);
					IFeatureLayerDefinition ld=new com.esri.arcgis.carto.IFeatureLayerDefinitionProxy (layer);
					ld.setDefinitionExpression("\"SAMPN\"=999");
				}
			}
			
			//Alighting Locations
			//Boarding Locations
			
			
			
			
			
			/*
			JOptionPane.showMessageDialog(null, topLayer.getName());
			*/
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		

	}
	@Override
	public void init(IApplication app) throws IOException, AutomationException {
		this.app = app;
		super.init(app);
	}

}
