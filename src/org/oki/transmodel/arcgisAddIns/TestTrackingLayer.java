package org.oki.transmodel.arcgisAddIns;

import java.io.IOException;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.Map;
import com.esri.arcgis.display.IColor;
import com.esri.arcgis.display.RgbColor;
import com.esri.arcgis.display.SimpleMarkerSymbol;
import com.esri.arcgis.display.esriSimpleMarkerStyle;
import com.esri.arcgis.enginecore.GraphicTracker;
import com.esri.arcgis.enginecore.IGraphicTrackerSymbol;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geometry.IEnvelope;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.interop.AutomationException;
/*
 * Author: Rohne
 * August 3, 2012
 * This is a sample to test the tracking layer.  It does nothing other than animate two
 * dots on the screen.
 */
public class TestTrackingLayer extends Button {
	private IApplication app;

	@Override
	public void onClick() throws IOException, AutomationException{
		try{
			IMxDocument mxDoc = new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap = mxDoc.getFocusMap();
			GraphicTracker graphicTkr=new GraphicTracker();
			graphicTkr.initialize(focusMap);
			SimpleMarkerSymbol Sym2d=new SimpleMarkerSymbol();
			Sym2d.setSize(10);
			Sym2d.setStyle(esriSimpleMarkerStyle.esriSMSCircle);
			IColor color=new RgbColor();
			color.setRGB(0xFF0000);
			Sym2d.setColor(color);
			IGraphicTrackerSymbol symbol1=graphicTkr.createSymbol(Sym2d, null);
			color.setRGB(0x0FFF00);
			Sym2d.setColor(color);
			IGraphicTrackerSymbol symbol2=graphicTkr.createSymbol(Sym2d, null);			
			Map map=(Map) focusMap;
			IEnvelope env=map.getAutoExtentBounds();
			IPoint pointLL=new Point();
			IPoint pointUR=new Point();
			pointLL=env.getLowerLeft();
			pointUR=env.getUpperRight();
			IPoint point1=new Point();
			point1=pointLL;
			IPoint point2=new Point();
			point2.setX(pointUR.getX());
			point2.setY(pointLL.getY());
			graphicTkr.removeAll();
			int point1ID = graphicTkr.add(point1, symbol1);
			int point2ID = graphicTkr.add(point2, symbol2);
			double deltaX=0;
			double deltaY=0;
			deltaX=(pointUR.getX()-pointLL.getX())/200;
			deltaY=(pointUR.getY()-pointLL.getY())/200;
			for(int cnt=0;cnt<200;cnt++){
				System.out.println(cnt);
				graphicTkr.setSuspendUpdate(true);
				graphicTkr.moveTo(point1ID, point1.getX()+(cnt*deltaX), point1.getY()+(cnt*deltaY), 0);
				graphicTkr.moveTo(point2ID, point2.getX()-(cnt*deltaX), point2.getY()+(cnt*deltaY), 0);
				graphicTkr.setSuspendUpdate(false);
				Thread.sleep(100);
			}
			for(int cnt=200;cnt>0;cnt--){
				System.out.println(cnt);
				graphicTkr.setSuspendUpdate(true);
				graphicTkr.moveTo(point1ID, point1.getX()+(cnt*deltaX), point1.getY()+(cnt*deltaY), 0);
				graphicTkr.moveTo(point2ID, point2.getX()-(cnt*deltaX), point2.getY()+(cnt*deltaY), 0);
				graphicTkr.setSuspendUpdate(false);
				Thread.sleep(100);
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	@Override
	public void init(IApplication app) throws IOException, AutomationException {
		this.app = app;
		super.init(app);
	}
}
