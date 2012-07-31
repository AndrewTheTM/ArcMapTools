package org.oki.transmodel.arcgisAddIns;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.ISelectionSet;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.interop.AutomationException;

public class flipBoardAlight extends Button{
	private IApplication app;
	@SuppressWarnings("unused")
	@Override
	public void onClick() throws IOException, AutomationException{
		try{
			IMxDocument mxDoc = new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap = mxDoc.getFocusMap();
			int selectedCount=focusMap.getSelectionCount();
			String sqlString="";
			if(selectedCount>0){
				for(int x=0;x<focusMap.getLayerCount();x++){
					if(focusMap.getLayer(x).getName().equals("Origin Locations")){
						FeatureLayer layer=(FeatureLayer) focusMap.getLayer(x);
						IFeatureSelection featSel=layer;
						ISelectionSet selSet=featSel.getSelectionSet();
						ITable table=selSet.getTarget();
						
						int boardXField=table.findField("BX");
						int boardYField=table.findField("BY_");
						int alightXField=table.findField("AX");
						int alightYField=table.findField("AY");
														
						IEnumIDs selIds=selSet.getIDs();
						int iId=selIds.next();
						int iaId[]=new int[selSet.getCount()];
						if(selSet.getCount()>1){
							int reply=JOptionPane.showConfirmDialog(null, "This will flip the boarding/alighting locations for ALL SELECTED items.  Is this what you want?","Confirm",JOptionPane.YES_NO_OPTION);
							if(reply==JOptionPane.NO_OPTION){
								System.exit(0); //<~ NOT the right command to use!  Exits ArcMap!
							}
						}
						/*
						int a=0;
						while(iId>0){
							IRow row=table.getRow(iId);
							iaId[a]=((Number)row.getValue(snFieldN)).intValue(); //<~~ java.lang.Double cannot be cast to java.lang.Integer
							a++;
							iId=selIds.next();
						}*/
						
					}
				}
			}
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
