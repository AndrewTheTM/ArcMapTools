package org.oki.transmodel.arcgisAddIns;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.esri.arcgis.addins.desktop.Button;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.FeatureLayer;
import com.esri.arcgis.carto.IFeatureSelection;
import com.esri.arcgis.carto.IMap;
import com.esri.arcgis.carto.Map;
import com.esri.arcgis.datasourcesGDB.ScratchWorkspaceFactory;
import com.esri.arcgis.display.IColor;
import com.esri.arcgis.display.RgbColor;
import com.esri.arcgis.display.SimpleMarkerSymbol;
import com.esri.arcgis.display.esriSimpleMarkerStyle;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.Feature;
import com.esri.arcgis.geodatabase.FeatureClass;
import com.esri.arcgis.geodatabase.FeatureClassDescription;
import com.esri.arcgis.geodatabase.Field;
import com.esri.arcgis.geodatabase.Fields;
import com.esri.arcgis.geodatabase.GeometryDef;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.IFeatureClassDescription;
import com.esri.arcgis.geodatabase.IFeatureWorkspace;
import com.esri.arcgis.geodatabase.IField;
import com.esri.arcgis.geodatabase.IFieldEdit;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.geodatabase.IFieldsEdit;
import com.esri.arcgis.geodatabase.IGeometryDef;
import com.esri.arcgis.geodatabase.IGeometryDefEdit;
import com.esri.arcgis.geodatabase.ILocator;
import com.esri.arcgis.geodatabase.IObjectClassDescription;
import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.IScratchWorkspaceFactory;
import com.esri.arcgis.geodatabase.ISelectionSet;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.geodatabase.esriFeatureType;
import com.esri.arcgis.geodatabase.esriFieldType;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.geometry.IGeometryProxy;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IProjectedCoordinateSystem;
import com.esri.arcgis.geometry.ISpatialReferenceFactory;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.geoprocessing.GeoProcessor;
import com.esri.arcgis.geoprocessing.tools.datamanagementtools.AddField;
import com.esri.arcgis.geoprocessing.tools.datamanagementtools.CreateFeatureclass;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.location.IAddressCandidates;
import com.esri.arcgis.location.IAddressGeocoding;
import com.esri.arcgis.location.ILocalLocatorWorkspace;
import com.esri.arcgis.location.ILocalLocatorWorkspaceProxy;
import com.esri.arcgis.location.LocatorManager;
import com.esri.arcgis.system.IArray;
import com.esri.arcgis.system.IPropertySet;
import com.esri.arcgis.system.PropertySet;
import com.esri.arcgis.location.ILocatorManager;

public class reGeocode extends Button{
	private IApplication app;
	private GeoProcessor gp=null;
	
	@SuppressWarnings("unused")
	@Override
	public void onClick() throws IOException, AutomationException{
		try{
			IMxDocument mxDoc = new com.esri.arcgis.arcmapui.IMxDocumentProxy (app.getDocument());
			IMap focusMap = mxDoc.getFocusMap();
			int selectedCount=focusMap.getSelectionCount();
			if(selectedCount!=1){
				JOptionPane.showMessageDialog(null, "You must select ONE AND ONLY ONE item to re-geocode", "Error!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			//Create (temporary) output feature class for geocode candidates
			gp=new GeoProcessor();
			gp.setOverwriteOutput(true);
			IScratchWorkspaceFactory scratchWSF=new ScratchWorkspaceFactory();			
			IWorkspace scratch=scratchWSF.getDefaultScratchWorkspace();
			
			CreateFeatureclass createFC=new CreateFeatureclass(scratch.getPathName(),"GeocodeLocations");
			createFC.setGeometryType("point");
			//Spatial Reference
			ISpatialReferenceFactory spatialReferenceFactory=new SpatialReferenceEnvironment();
			IProjectedCoordinateSystem projectedCoordinateSystem=(IProjectedCoordinateSystem) spatialReferenceFactory.createESRISpatialReferenceFromPRJFile("C:\\Program Files (x86)\\ArcGIS\\Desktop10.0\\Coordinate Systems\\Projected Coordinate Systems\\State Plane\\NAD 1983 (US Feet)\\NAD 1983 StatePlane Ohio South FIPS 3402 (US Feet).prj");
			createFC.setSpatialReference(projectedCoordinateSystem);
			gp.execute(createFC, null);
			AddField newField=new AddField(scratch.getPathName()+File.separator+"GeocodeLocations","X","double");
			newField.setFieldScale(8);
			newField.setFieldPrecision(20);
			gp.execute(newField, null);
			newField=new AddField(scratch.getPathName()+File.separator+"GeocodeLocations","Y","double");
			newField.setFieldScale(8);
			newField.setFieldPrecision(20);
			gp.execute(newField, null);
			Workspace ws=new Workspace(scratch);
			
			
			FeatureClass featClass=new FeatureClass(ws.openFeatureClass("GeocodeLocations"));
			
			
			

			for(int x=0;x<focusMap.getLayerCount();x++){
				FeatureLayer layer=(FeatureLayer) focusMap.getLayer(x);
				IFeatureSelection featSel=layer;
				ISelectionSet selSet=featSel.getSelectionSet();
				if(selSet.getCount()>0){
					//This is the selected item
					ITable table=selSet.getTarget();
					Object[] possibleValues={"Origins","Destinations"};
					Object selectedValue=JOptionPane.showInputDialog(null, "Do you want to re-geocode the origin or destination?", "Question", JOptionPane.QUESTION_MESSAGE, null, possibleValues, possibleValues[0]);
					
					String Address="", PlaceName="", City="", State="";
					double Zip=0;
					
					IEnumIDs selIds=selSet.getIDs();
					int iId=selIds.next();
					while(iId>0){
						IRow row=table.getRow(iId);
						if(selectedValue.equals("Origins")){
							PlaceName=(String) row.getValue(table.findField("ONAME"));
							Address=(String) row.getValue(table.findField("OADDR"));
							if(Address.equals("")){
								Address=(String) row.getValue(table.findField("OXSTRT1"))+" & "+row.getValue(table.findField("OXSTRT2"));
							}
							City=(String) row.getValue(table.findField("OCITY"));
							State=(String) row.getValue(table.findField("OSTATE"));
							if(row.getValue(table.findField("OZIP"))!=null){
								Zip=(Double) row.getValue(table.findField("OZIP"));
							}else{
								Zip=0;
							}
						}else{
							PlaceName=(String) row.getValue(table.findField("DNAME"));
							Address=(String) row.getValue(table.findField("DADDR"));
							if(Address.equals("")){
								Address=(String) row.getValue(table.findField("DXSTRT1"))+" & "+row.getValue(table.findField("DXSTRT2"));
							}
							City=(String) row.getValue(table.findField("DCITY"));
							State=(String) row.getValue(table.findField("DSTATE"));
							if(row.getValue(table.findField("DZIP"))!=null){
								Zip=(Double) row.getValue(table.findField("DZIP"));
							}else{
								Zip=0;
							}
							
						}
						iId=selIds.next();
					}
					ILocatorManager locatorMgr = new LocatorManager();
					ILocalLocatorWorkspace locatorWs=new ILocalLocatorWorkspaceProxy(locatorMgr.getLocatorWorkspaceFromPath("T:\\GIS\\Staff\\Rohne\\TOBS\\OKIData.gdb"));
					ILocator locator=(new com.esri.arcgis.geodatabase.ILocatorWorkspaceProxy (locatorWs)).getLocator("Compositeocator");
					IAddressGeocoding addressGeocoding=new com.esri.arcgis.location.IAddressGeocodingProxy (locator);
					IPropertySet addressToMatch = new PropertySet();
					IPropertySet addressMatched = new PropertySet();
					addressToMatch.setProperty("Street", Address);  
					addressToMatch.setProperty("Zone",Zip); 
					addressToMatch.setProperty("Name",PlaceName);
					addressToMatch.setProperty("City",City);
					addressToMatch.setProperty("State",State);
					addressMatched=addressGeocoding.matchAddress(addressToMatch); 
					IAddressCandidates addressCandidates = new com.esri.arcgis.location.IAddressCandidatesProxy (locator);
					IArray addressCandidateArray=addressCandidates.findAddressCandidates(addressToMatch);
					for(int acao=0;acao<addressCandidateArray.getCount();acao++){
						IPropertySet currentElement=(IPropertySet) addressCandidateArray.getElement(acao);
						currentElement=(IPropertySet) addressCandidateArray.getElement(acao);
						Object[] names = new Object[1];
						Object[] values = new Object[1];
						currentElement.getAllProperties(names, values);
						Object[] nameArray=(Object[]) names[0];
						Object[] valueArray=(Object[]) values[0];
						int[] pointID=new int[currentElement.getCount()];
						
						String shapeFieldName=featClass.getShapeFieldName();
						
						
						for(int cec=0;cec<currentElement.getCount();cec++){
							if(nameArray[cec].toString().equals("Shape")){
								IGeometry geomPoint=new IGeometryProxy(valueArray[cec]);
								IPoint point=new Point(geomPoint);
								
								System.out.println("X: "+point.getX()+" Y: "+point.getY());
								
								Feature newFeature=(Feature) featClass.createFeature();
								newFeature.setShapeByRef(point);
								newFeature.setValue(2, point.getX());
								newFeature.setValue(3, point.getY());
								newFeature.store();
								
								
								
								
								
								/*
								IFeature newFeat=new com.esri.arcgis.geodatabase.IFeatureProxy (new Feature());
								
								
								
								
								newFeat.setShapeByRef(point); //<~ it dies here, and that is probably okay
								//An expected field was not found or could not be retrieved properly
								
								newFeat.store();
								//graphicTkr.setSuspendUpdate(true);
								//IGraphicTrackerSymbol symbol=graphicTkr.createSymbol(Sym2d, null);
								//pointID[cec] = graphicTkr.add(point, symbol);
								//graphicTkr.setSuspendUpdate(false);
								
								
								
								int b=1;
								/*
								 * This is working, but it does not draw anything on the screen, which is what
								 * I really need here.  If I could get it to draw and be selectable or scroll 
								 * through options, THEN this would be cooking with gas!
								 */
								
							}else{
							}
						}
					}
					int aa=1;
						


					
					break; // This breaks where there should be no more features remaining to check
				}
				
			}
			Map focusMap2=(Map) focusMap;
			focusMap2.refresh();
			System.out.println((char)7);
		}
		catch(AutomationException ae){
			System.out.println(ae.getMessage());
			System.out.println(ae.getCode());
			System.out.println(ae.getDescription());
			System.out.println(ae.getSource());
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
