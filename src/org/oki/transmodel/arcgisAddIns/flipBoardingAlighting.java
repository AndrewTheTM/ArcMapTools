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

public class flipBoardingAlighting extends Button {
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
						int boardXField=table.findField("BX");
						int boardYField=table.findField("BY_");
						int boardCodeField=table.findField("BRDCODE");

						
						int alightXField=table.findField("AX");
						int alightYField=table.findField("AY");
						int alightCodeField=table.findField("ALTCODE");

						
						IEnumIDs selIds=selSet.getIDs();
						if(selSet.getCount()>1){
							int reply=JOptionPane.showConfirmDialog(null, "This will flip the origin/destination locations for ALL SELECTED items.  Is this what you want?","Confirm",JOptionPane.YES_NO_OPTION);
							if(reply==JOptionPane.NO_OPTION){
								return;
							}
						}
						int iId=selIds.next();
						while(iId>0){
							double boardX=0;
							double boardY=0;
							double alightX=0;
							double alightY=0;
							Object oName, oGeocode, oAddress, oCrossStreet1, oCrossStreet2, oCity, oState, oZip, oAVStatus, oAVAddress, oAVZone;
							Object boardCode, alightCode;
							IRow row=table.getRow(iId);
							boardX=(Double) row.getValue(boardXField);
							boardY=(Double) row.getValue(boardYField);
							boardCode=row.getValue(boardCodeField);
							alightX=(Double) row.getValue(alightXField);
							alightY=(Double) row.getValue(alightYField);
							alightCode=row.getValue(alightCodeField);
							
							row.setValue(boardCodeField, alightCode);
							row.setValue(boardXField, alightX);
							row.setValue(boardYField, alightY);
							row.setValue(alightCodeField, alightCode);
							row.setValue(alightXField, boardX);
							row.setValue(alightYField, boardY);
							
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
