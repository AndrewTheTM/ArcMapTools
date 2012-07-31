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
import com.esri.arcgis.geodatabase.IDataset;
import com.esri.arcgis.geodatabase.IDatasetProxy;
import com.esri.arcgis.geodatabase.IEnumIDs;
import com.esri.arcgis.geodatabase.IField;
import com.esri.arcgis.geodatabase.IFields;
import com.esri.arcgis.geodatabase.ILocator;
import com.esri.arcgis.geodatabase.ILocatorWorkspace;
import com.esri.arcgis.geodatabase.IRow;
import com.esri.arcgis.geodatabase.ISelectionSet;
import com.esri.arcgis.geodatabase.ITable;
import com.esri.arcgis.geodatabase.esriFieldType;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.location.IAddressGeocoding;
import com.esri.arcgis.location.IAddressGeocodingProxy;
import com.esri.arcgis.location.ILocalLocatorWorkspace;
import com.esri.arcgis.location.ILocalLocatorWorkspaceProxy;
import com.esri.arcgis.location.LocalLocatorWorkspace;
import com.esri.arcgis.location.LocatorManager;
import com.esri.arcgis.system.IPropertySet;
import com.esri.arcgis.system.PropertySet;
import com.esri.arcgis.location.ILocatorManager;

public class reGeocode extends Button{
	private IApplication app;
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
			
			for(int x=0;x<focusMap.getLayerCount();x++){
				
				FeatureLayer layer=(FeatureLayer) focusMap.getLayer(x);
				IFeatureSelection featSel=layer;
				ISelectionSet selSet=featSel.getSelectionSet();
				if(selSet.getCount()>0){
					//This is the selected item
					ITable table=selSet.getTarget();
					Object[] possibleValues={"Origins","Destinations"};
					Object selectedValue=JOptionPane.showInputDialog(null, "Do you want to re-geocode the origin or destination?", "Question", JOptionPane.QUESTION_MESSAGE, null, possibleValues, possibleValues[0]);
					
					String Address="", PlaceName="", City="", State="", Zip="";
					
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
							Zip=(String) row.getValue(table.findField("OZIP"));
						}else{
							PlaceName=(String) row.getValue(table.findField("DNAME"));
							Address=(String) row.getValue(table.findField("DADDR"));
							if(Address.equals("")){
								Address=(String) row.getValue(table.findField("DXSTRT1"))+" & "+row.getValue(table.findField("DXSTRT2"));
							}
							City=(String) row.getValue(table.findField("DCITY"));
							State=(String) row.getValue(table.findField("DSTATE"));
							Zip=(String) row.getValue(table.findField("DZIP"));
						}
						iId=selIds.next();
					}
					//This is the experimental part taken from a Microsoft.net howto
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
					addressMatched=addressGeocoding.matchAddress(addressToMatch); //IPropertySet address
					//works to here
					IFields fields=addressGeocoding.getMatchFields();
					IField field=null;
					IPoint point=null;
					for(int matchedIdx=0;matchedIdx<8;matchedIdx++){ //fields.getFieldCount()
						field=fields.getField(matchedIdx);
						String fieldName=field.getName();
						Object objMatched=addressMatched.getProperty(fieldName);
						if(field.getType()==esriFieldType.esriFieldTypeGeometry){
							point=(IPoint)objMatched;
							if(!point.isEmpty()){
								System.out.println("X: "+point.getX() + "Y: "+point.getY());
								
							}
						}else{
							System.out.println(field.getAliasName()+": "+objMatched.toString());
							
						}
					}
					
					int aa=1;
					
					
					
					break;
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
