package org.oki.transmodel.arcgisAddIns;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Tool;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IActiveView;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.carto.IIdentify;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.Map;
import com.esri.arcgis.carto.SimpleIdentifyObject;
import com.esri.arcgis.display.IScreenDisplay;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.ISelectionSet;
import com.esri.arcgis.geometry.Envelope;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.system.IArray;

public class FixAlightingStop extends Tool{
	IScreenDisplay screenDisplay;
	IApplication app;
	IMxDocument mxDoc;
	
	@Override
	public void activate(){
		
	}
	
	public boolean isChecked(){
		return true;
	}
	
	public boolean isEnabled(){
		return true;
	}
	
	/*
	 * @author arohne
	 * @see com.esri.arcgis.addins.desktop.Tool#mousePressed(java.awt.event.MouseEvent)
	 * Class that handles when the mouse was pressed to fix the alighting stop
	 */
	public void mousePressed(MouseEvent me){
		try{
			mxDoc=new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap=mxDoc.getFocusMap();
			IActiveView activeView=(IActiveView) focusMap;
			this.screenDisplay=activeView.getScreenDisplay();

			for(int x=0;x<focusMap.getLayerCount();x++){
				//Layer name for bus stop locations below
				if(focusMap.getLayer(x).getName().equals("Stops")){
					FeatureLayer featLayer=(FeatureLayer) focusMap.getLayer(x);
					IIdentify ident=featLayer;
					IEnvelope envelope = new Envelope();
					IPoint envLL=new Point();
					IPoint envUR=new Point();
					envLL=activeView.getScreenDisplay().getDisplayTransformation().toMapPoint(me.getX()-5, me.getY()+7);
					envUR=activeView.getScreenDisplay().getDisplayTransformation().toMapPoint(me.getX()+5, me.getY()-7);
					envelope.setLowerLeft(envLL);
					envelope.setUpperRight(envUR);
					IArray result=ident.identify(envelope);
					double brdCode=0, newX=0, newY=0;
					if(result!=null){
						for(int i=0;i<result.getCount();i++){
							Object obj=result.getElement(i);
							@SuppressWarnings("deprecation")
							SimpleIdentifyObject sio = new SimpleIdentifyObject(obj);
							IRow idRow=sio.getRow();
							//Field name for bus route id below from the bus stop layer
							String routeName=(String) idRow.getValue(idRow.getFields().findField("BusNum"));
							Object selectedValue=JOptionPane.showConfirmDialog(null, "This will update the XY coordinates of the selected selected boarding stop for Route "+routeName+".  This stop.  Is this okay?", "Question", JOptionPane.YES_NO_OPTION);
							if(selectedValue.equals(JOptionPane.YES_OPTION)){
								//Field names for longitude and latitude and stop id below FROM the bus stops layer
								newX=(Double)idRow.getValue(idRow.getFields().findField("StopLon"));
								newY=(Double)idRow.getValue(idRow.getFields().findField("StopLat"));
								brdCode=(Double)idRow.getValue(idRow.getFields().findField("StopID"));
								for(int y=0;y<focusMap.getLayerCount();y++){
									if(focusMap.getLayer(y).getName().equals("Origin Locations") || focusMap.getLayer(y).getName().equals("Destination Locations")){
										FeatureLayer layer=(FeatureLayer) focusMap.getLayer(y);	
										IFeatureSelection featSel=layer;
										ISelectionSet selSet=featSel.getSelectionSet();
										IEnumIDs ssIds=	selSet.getIDs();
										int rowId=0;
										rowId=ssIds.next();
										while(rowId>0){
											IRow ssRow=selSet.getTarget().getRow(rowId);
											ssRow.setValue(ssRow.getFields().findField("ALTCODE"), brdCode);
											ssRow.setValue(ssRow.getFields().findField("AX"), newX);
											ssRow.setValue(ssRow.getFields().findField("AY"), newY);
											ssRow.store();
											Map focusMap2=(Map) focusMap;
											focusMap2.refresh();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void init(IApplication app){
		this.app=app;
	}
}
