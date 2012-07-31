package org.oki.transmodel.arcgisAddIns;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.Map;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.ISelectionSet;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.interop.AutomationException;

public class flipOriginDestination extends Button {
	private IApplication app;
	@Override
	public void onClick() throws IOException, AutomationException{
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
						int originXField=table.findField("OXCORD");
						int originYField=table.findField("OYCORD");
						int destXField=table.findField("DXCORD");
						int destYField=table.findField("DYCORD");
						IEnumIDs selIds=selSet.getIDs();
						if(selSet.getCount()>1){
							int reply=JOptionPane.showConfirmDialog(null, "This will flip the boarding/alighting locations for ALL SELECTED items.  Is this what you want?","Confirm",JOptionPane.YES_NO_OPTION);
							if(reply==JOptionPane.NO_OPTION){
								return;
							}
						}
						int iId=selIds.next();
						while(iId>0){
							double originX=0;
							double originY=0;
							double destX=0;
							double destY=0;
							IRow row=table.getRow(iId);
							originX=(Double) row.getValue(originXField);
							originY=(Double) row.getValue(originYField);
							destX=(Double) row.getValue(destXField);
							destY=(Double) row.getValue(destYField);
							row.setValue(destXField, originX);
							row.setValue(destYField, originY);
							row.setValue(originXField, destX);
							row.setValue(originYField, destY);
							row.store();
							iId=selIds.next();
						}
					}
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
