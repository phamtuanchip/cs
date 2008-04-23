/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 **/
package org.exoplatform.calendar.webui.popup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.exoplatform.calendar.CalendarUtils;
import org.exoplatform.calendar.service.EventQuery;
import org.exoplatform.calendar.webui.UIFormComboBox;
import org.exoplatform.portal.webui.util.SessionProviderFactory;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputWithActions;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Tuan
 *          tuan.pham@exoplatform.com
 * Aug 29, 2007  
 */
@ComponentConfig(template = "app:/templates/calendar/webui/UIPopup/UIEventAttenderTab.gtmpl")
public class UIEventAttenderTab extends UIFormInputWithActions {
  final public static String FIELD_FROM_TIME = "timeFrom".intern() ;
  final public static String FIELD_TO_TIME = "timeTo".intern();
  final public static String FIELD_CHECK_TIME = "checkTime".intern();

  final public static String FIELD_DATEALL = "dateAll".intern();
  final public static String FIELD_CURRENTATTENDER = "currentAttender".intern() ;

  protected Map<String, String> parMap_ = new HashMap<String, String>() ;
  public Calendar calendar_ ;
  public UIEventAttenderTab(String arg0) {
    super(arg0);
    setComponentConfig(getClass(), null) ;
    calendar_ = CalendarUtils.getInstanceTempCalendar() ;
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    Map<String, String> fromJsActions = new HashMap<String, String>() ;
    fromJsActions.put(UIFormComboBox.ON_BLUR, "eXo.calendar.UICombobox.synchronize(this)") ;
    addUIFormInput(new UIFormComboBox(UIEventAttenderTab.FIELD_FROM_TIME, UIEventAttenderTab.FIELD_FROM_TIME, options , fromJsActions)) ;
    addUIFormInput(new UIFormComboBox(UIEventAttenderTab.FIELD_TO_TIME, UIEventAttenderTab.FIELD_TO_TIME, options, fromJsActions)) ;

    addUIFormInput(new UIFormCheckBoxInput<Boolean>(FIELD_DATEALL, FIELD_DATEALL, false)) ;
    UIFormCheckBoxInput<Boolean> checkFreeInput = new UIFormCheckBoxInput<Boolean>(FIELD_CHECK_TIME, FIELD_CHECK_TIME, false) ;
    checkFreeInput.setOnChange("OnChange") ;
    addUIFormInput(checkFreeInput) ;
  }
  protected UIFormComboBox getUIFormComboBox(String id) {
    return findComponentById(id) ;
  }

  protected void updateParticipants(String values) throws Exception{
    parMap_.clear() ;
    Map<String, String> tmpMap = new HashMap<String, String>() ;
    List<String> newPars = new ArrayList<String>() ;
    if(!CalendarUtils.isEmpty(values)) {
      for(String par : values.split(CalendarUtils.COMMA)) {
        String vl = tmpMap.get(par) ;
        parMap_.put(par.trim(), vl) ;
        if(vl == null) newPars.add(par.trim()) ;  			
      }
    }
    boolean isCheckFreeTime = getUIFormCheckBoxInput(FIELD_CHECK_TIME).isChecked() ;
    if(newPars.size() > 0 && isCheckFreeTime) {
      EventQuery eventQuery = new EventQuery() ;
      eventQuery.setFromDate(CalendarUtils.getBeginDay(calendar_)) ;
      eventQuery.setToDate(CalendarUtils.getEndDay(calendar_)) ;
      eventQuery.setParticipants(newPars.toArray(new String[]{})) ;
      eventQuery.setNodeType("exo:calendarPublicEvent") ;
      Map<String, String> parsMap = 
        CalendarUtils.getCalendarService().checkFreeBusy(SessionProviderFactory.createSystemProvider(), eventQuery) ;
      parMap_.putAll(parsMap) ;
    }
  }


  protected Map<String, String> getMap(){ 
    for(String id : parMap_.keySet()) {
      if(getUIFormCheckBoxInput(id) == null) addUIFormInput(new UIFormCheckBoxInput<Boolean>(id, id, false)) ;
    }
    return parMap_ ; 
    
  }

  protected String[] getParticipants() { return parMap_.keySet().toArray(new String[]{}) ; } 

  protected String getDateValue() {
    DateFormat df = new SimpleDateFormat(CalendarUtils.DATEFORMAT) ;
    df.setCalendar(CalendarUtils.getInstanceTempCalendar()) ;
    return df.format(calendar_.getTime()) ;
  }
  protected void moveNextDay() throws Exception{
    calendar_.add(Calendar.DATE, 1) ;
    StringBuilder values = new StringBuilder(); 
    for(String par : parMap_.keySet()) {
      if(values != null && values.length() > 0) values.append(",") ;
      values.append(par) ;    	
    }
    parMap_.clear() ;
    updateParticipants(values.toString()) ;
  }
  protected void movePreviousDay() throws Exception{
    calendar_.add(Calendar.DATE, -1) ;
    StringBuilder values = new StringBuilder(); 
    for(String par : parMap_.keySet()) {
      if(values != null && values.length() > 0) values.append(",") ;
      values.append(par) ;    	
    }
    parMap_.clear() ;
    updateParticipants(values.toString()) ;
  }

  protected UIForm getParentFrom() {
    return getAncestorOfType(UIForm.class) ;
  }
  protected String getFormName() { 
    UIForm uiForm = getAncestorOfType(UIForm.class);
    return uiForm.getId() ; 
  }

  protected String getFromFieldValue() {
    return getUIFormComboBox(FIELD_FROM_TIME).getValue() ;
  }
  protected void setEventFromDate(Date date, String timeFormat) {
    UIFormComboBox timeField = getChildById(FIELD_FROM_TIME) ;
    DateFormat df = new SimpleDateFormat(timeFormat) ;
    df.setCalendar(CalendarUtils.getInstanceTempCalendar()) ;
    timeField.setValue(df.format(date)) ;
    calendar_.setTime(date) ;
  }
  protected boolean getEventAllDate() {
    return false;
  }
  protected void setEventToDate(Date date, String timeFormat) {
    UIFormComboBox timeField = getChildById(FIELD_TO_TIME) ;
    DateFormat df = new SimpleDateFormat(timeFormat) ;
    df.setCalendar(CalendarUtils.getInstanceTempCalendar()) ;
    timeField.setValue(df.format(date)) ;
  }  

  protected boolean isAllDateFieldChecked() {
    return getUIFormCheckBoxInput(FIELD_DATEALL).isChecked() ;
  }
  @Override
  public void processRender(WebuiRequestContext arg0) throws Exception {
    super.processRender(arg0);
  }
  public String getUserTimeZone(String username) throws Exception {
     String timeZone = CalendarUtils.getCalendarService().getCalendarSetting(SessionProviderFactory.createSystemProvider(), CalendarUtils.getCurrentUser()).getTimeZone() ;
     return CalendarUtils.getTimeZone(timeZone) ;
  }
  
  public String getServerTimeZone() {
    return CalendarUtils.getTimeZone(TimeZone.getDefault().getID()) ;
  }
}
