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
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.system.IArray;
import com.esri.arcgis.geometry.IEnvelope;

public class NewAddressClick extends Tool{
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
	
	public void mousePressed(MouseEvent me){
		try{
			mxDoc=new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap=mxDoc.getFocusMap();
			IActiveView activeView=(IActiveView) focusMap;
			this.screenDisplay=activeView.getScreenDisplay();

			for(int x=0;x<focusMap.getLayerCount();x++){
				if(focusMap.getLayer(x).getName().equals("GeocodeLocations")){
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
					double newX=0, newY=0;
					if(result!=null){
						for(int i=0;i<result.getCount();i++){
							Object obj=result.getElement(i);
							@SuppressWarnings("deprecation")
							SimpleIdentifyObject sio = new SimpleIdentifyObject(obj);
							IRow idRow=sio.getRow();
							sio.flash(screenDisplay);
							if(result.getCount()==1){
								Object selectedValue=JOptionPane.showConfirmDialog(null, "This will update the XY coordinates of the selected selected origin or destination.  Is this okay?", "Question", JOptionPane.OK_CANCEL_OPTION);
								if(selectedValue.equals(JOptionPane.CANCEL_OPTION))
									return;
								else{
									newX=(Double)idRow.getValue(idRow.getFields().findField("X"));
									newY=(Double)idRow.getValue(idRow.getFields().findField("Y"));
								}
							}else{
								int remaining=result.getCount()-i-1;
								Object selectedValue=JOptionPane.showConfirmDialog(null, "This will update the XY coordinates of the selected selected origin or destination.  Do you want this one?  There are "+remaining+" choices remaining.", "Question", JOptionPane.OK_CANCEL_OPTION);
								if(selectedValue.equals(JOptionPane.OK_OPTION)){
									newX=(Double)idRow.getValue(idRow.getFields().findField("X"));
									newY=(Double)idRow.getValue(idRow.getFields().findField("Y"));
									i=result.getCount(); //this kills this loop - break was breaking too far!
								}
							}
							
							for(int y=0;y<focusMap.getLayerCount();y++){
								if(focusMap.getLayer(y).getName().equals("Origin Locations")){
									FeatureLayer layer=(FeatureLayer) focusMap.getLayer(y);	
									IFeatureSelection featSel=layer;
									ISelectionSet selSet=featSel.getSelectionSet();
									IEnumIDs rowIds=selSet.getIDs();
									int rowId=rowIds.next();
									while(rowId>0){
										if(selSet.getCount()>0){
											IRow ssRow=selSet.getTarget().getRow(rowId);
											ssRow.setValue(ssRow.getFields().findField("OXCORD"), newX);
											ssRow.setValue(ssRow.getFields().findField("OYCORD"), newY);
											ssRow.store();
											Map focusMap2=(Map) focusMap;
											focusMap2.refresh();
											return;
										}
									}
								}else if(focusMap.getLayer(y).getName().equals("Destination Locations")){
									FeatureLayer layer=(FeatureLayer) focusMap.getLayer(y);	
									IFeatureSelection featSel=layer;
									ISelectionSet selSet=featSel.getSelectionSet();
									IEnumIDs rowIds=selSet.getIDs();
									int rowId=rowIds.next();
									while(rowId>0){
										if(selSet.getCount()>0){
											IRow ssRow=selSet.getTarget().getRow(rowId);
											ssRow.setValue(ssRow.getFields().findField("DXCORD"), newX);
											ssRow.setValue(ssRow.getFields().findField("DYCORD"), newY);
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
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public void init(IApplication app){
		this.app=app;
	}
	
}
