package org.oki.transmodel.arcgisAddIns;

import java.io.IOException;
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

public class FixDistances extends Button {
	private IApplication app;
	@SuppressWarnings("unused")
	@Override
	public void onClick() throws IOException, AutomationException{
		try{
			IMxDocument mxDoc = new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap = mxDoc.getFocusMap();
			int selectedCount=focusMap.getSelectionCount();
			if(selectedCount>0){
				for(int x=0;x<focusMap.getLayerCount();x++){
					if(focusMap.getLayer(x).getName().equals("Origin Locations") || focusMap.getLayer(x).getName().equals("Destination Locations")){
						FeatureLayer layer=(FeatureLayer) focusMap.getLayer(x);
						IFeatureSelection featSel=layer;
						ISelectionSet selSet=featSel.getSelectionSet();
						ITable table=selSet.getTarget();
						int originXField=table.findField("OXCORD");
						int originYField=table.findField("OYCORD");
						int destXField=table.findField("DXCORD");
						int destYField=table.findField("DYCORD");
						int boardXField=table.findField("BX");
						int boardYField=table.findField("BY_");
						int alightXField=table.findField("AX");
						int alightYField=table.findField("AY");
						int oGetField=table.findField("OGET");
						int dGetField=table.findField("DGET");
						int qcWalkTo=table.findField("QC_WalkToDistance");
						int qcBikeTo=table.findField("QC_BikeToDistance");
						int qcDriveTo=table.findField("QC_DriveToDistance");
						int qcWalkFrom=table.findField("QC_WalkFromDistance");
						int qcBikeFrom=table.findField("QC_BikeFromDistance");
						int qcDriveFrom=table.findField("QC_DriveFromDistance");
						int surveyedBusField=table.findField("RTCODE");
						int bus1Field=table.findField("BUS1");
						int bus2Field=table.findField("BUS2");
						int bus3Field=table.findField("BUS3");
						int bus4Field=table.findField("BUS4");
						IEnumIDs selIds=selSet.getIDs();
						int iId=selIds.next();
						while(iId>0){
							double originX=0;
							double originY=0;
							double destX=0;
							double destY=0;
							IRow row=table.getRow(iId);
							String surveyedBus=row.getValue(surveyedBusField).toString();
							String lastBus="";
							if(row.getValue(bus4Field)!=null)
								lastBus=row.getValue(bus4Field).toString();
							else if(row.getValue(bus3Field)!=null)
								lastBus=row.getValue(bus3Field).toString();
							else if(row.getValue(bus2Field)!=null)
								lastBus=row.getValue(bus2Field).toString();
							else
								lastBus=row.getValue(bus1Field).toString();
							int oGetVal=0;
							if(row.getValue(oGetField)!=null)
								oGetVal=(int)Math.round((Double) row.getValue(oGetField));
							int dGetVal=0;
							if(row.getValue(dGetVal)!=null)
								dGetVal=(int)Double.parseDouble((row.getValue(dGetField).toString()));
							double x1=0,y1=0, x2=0, y2=0;
							if(row.getValue(boardXField)!=null && row.getValue(originXField)!=null && row.getValue(boardYField)!=null && row.getValue(originYField)!=null){
								x1=(Double)row.getValue(boardXField);
								x2=(Double)row.getValue(originXField);
								y1=(Double)row.getValue(boardYField);
								y2=(Double)row.getValue(originYField);
							}
							double o2b=Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2))/5280;
							if(row.getValue(alightXField)!=null && row.getValue(alightYField)!=null && row.getValue(destXField)!=null && row.getValue(destYField)!=null){
								x1=(Double)row.getValue(alightXField);
								x2=(Double)row.getValue(destXField);
								y1=(Double)row.getValue(alightYField);
								y2=(Double)row.getValue(destYField);
							}
							double a2d=Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2))/5280;
							if(row.getValue(bus1Field).toString().equals(surveyedBus)){
								switch(oGetVal){
								case 1:
									row.setValue(qcWalkTo, o2b);
									break;
								case 2:
									row.setValue(qcBikeTo, o2b);
									break;
								default:
									row.setValue(qcDriveTo, o2b);
									break;
								}
							}else{
								row.setValue(qcWalkTo, 0);
								row.setValue(qcBikeTo, 0);
								row.setValue(qcDriveTo, 0);
							}
							if(lastBus.equals(surveyedBus)){
								switch(dGetVal){
								case 1:
									row.setValue(qcWalkFrom,a2d);
									break;
								case 2:
									row.setValue(qcBikeFrom, a2d);
									break;
								default:
									row.setValue(qcDriveFrom, a2d);
									break;
								}
							}else{
								row.setValue(qcWalkFrom, 0);
								row.setValue(qcBikeFrom, 0);
								row.setValue(qcDriveFrom, 0);
							}
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
