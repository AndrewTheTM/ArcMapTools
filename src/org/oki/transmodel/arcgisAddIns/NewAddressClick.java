package org.oki.transmodel.arcgisAddIns;

import java.awt.event.MouseEvent;

import com.esri.arcgis.addins.desktop.Tool;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IActiveView;
import com.esri.arcgis.carto.IIdentify;
import com.esri.arcgis.carto.ILayer;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.display.IScreenDisplay;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geometry.Envelope;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryProxy;
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

			for(int x=0;x<focusMap.getLayerCount();x++){
				//if(focusMap.getLayer(x).getName().equals("GeocodedLocations")){
				if(focusMap.getLayer(x).getName().equals("Origin Locations")){ //
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
					if(result!=null){
						for(int i=0;i<result.getCount();i++){
							System.out.println(result.getElement(i).toString());
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
