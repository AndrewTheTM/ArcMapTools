package org.oki.transmodel.arcgisAddIns;

import java.io.IOException;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IFeatureLayerDefinition;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.Map;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.ISelectionSet;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.interop.AutomationException;

public class defQueryButtonDestination extends Button {
	private IApplication app;
	@Override
	public void onClick() throws IOException, AutomationException {
		try{
			IMxDocument mxDoc = new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap = mxDoc.getFocusMap();
			int selectedCount=focusMap.getSelectionCount();
			String sqlString="";
			if(selectedCount>0){
				for(int x=0;x<focusMap.getLayerCount();x++){
					if(focusMap.getLayer(x).getName().equals("Destination Locations")){
						FeatureLayer layer=(FeatureLayer) focusMap.getLayer(x);
						IFeatureSelection featSel=layer;
						ISelectionSet selSet=featSel.getSelectionSet();
						ITable table=selSet.getTarget();
						int snFieldN=table.findField("SAMPN");
						IEnumIDs selIds=selSet.getIDs();
						int iId=selIds.next();
						int iaId[]=new int[selSet.getCount()];
						int a=0;
						while(iId>0){
							IRow row=table.getRow(iId);
							iaId[a]=((Number)row.getValue(snFieldN)).intValue();
							a++;
							iId=selIds.next();
						}
						for(int b=0;b<iaId.length;b++){
							sqlString+="\"SAMPN\"="+iaId[b];
						}
					}
				}
			}
			for(int x=0;x<focusMap.getLayerCount();x++){
				if(focusMap.getLayer(x).getName().equals("Origin Locations") || 
						focusMap.getLayer(x).getName().equals("Alighting Locations") || 
						focusMap.getLayer(x).getName().equals("Boarding Locations")){
					ILayer layer=focusMap.getLayer(x);
					IFeatureLayerDefinition ld=new com.esri.arcgis.carto.IFeatureLayerDefinitionProxy (layer);
					ld.setDefinitionExpression(sqlString);
				}
			}
			Map focusMap2=(Map) focusMap;
			focusMap2.refresh();
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
