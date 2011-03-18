/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.contact;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.jcr.RepositoryException;

import org.exoplatform.contact.service.AddressBook;
import org.exoplatform.contact.service.Contact;
import org.exoplatform.contact.service.ContactService;
import org.exoplatform.contact.service.impl.NewUserListener;
import org.exoplatform.contact.webui.popup.UISelectComponent;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.mail.service.Account;
import org.exoplatform.mail.service.MailService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.mail.Message;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputWithActions;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormInputWithActions.ActionData;

/**
 * Created by The eXo Platform SARL
 * Author : Hung Hoang Quang
 *          hung.hoang@exoplatform.com
 * Jul 11, 2007  
 */
public class ContactUtils {
  //private static String AKONG = "@" ;
  final public static String COMMA = ",".intern() ;
  final public static String SEMI_COMMA = ";".intern() ;
  public static final String HTTP = "http://" ; 
  public static String[] specialString = {"!", "#", "%", "&"
                                            , ":", ">", "<", "~", "`", "]", "'", "/", "-"} ;
//can't use String.replaceAll() ;
  public static String[] specialString2 = {"?", "[", "(", "|", ")", "*", "\\", "+", "}", "{", "^", "$", "\""
    ,"!", "#", "%", "&", ":", ">", "<", "~", "`", "]", "'", "/", "-"} ;
  final static public String FIELD_USER = "user".intern() ;
  final static public String FIELD_GROUP = "group".intern() ;  
  final static public String FIELD_EDIT_PERMISSION = "canEdit".intern() ;
  
  
  public static String getDisplayAdddressShared(String sharedUserId, String addressName) {
    return sharedUserId + " - " + addressName ;
  }
  /*
  public static boolean isNameLong(String text) {
    if (text == null) return false ;
    if (text.length() > 40) return true ;
    return false ;
  }
  */
  public static String encodeJCRText(String str) {
    return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").
    replaceAll("'", "&apos;").replaceAll("\"", "&quot;") ;
  }
  
  public static boolean isNameValid(String name, String[] regex) {
    if (isEmpty(name)) return true ;
    for(String c : regex){ if(name.contains(c)) return false ;}
    return true ;
  }
  
  // add
  public static String encodeHTML(String str) {
    if (str == null) return "" ;
    return str.replaceAll("<", "&lt;").replaceAll(">", "&gt;") ;
    
    /*return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").
      replaceAll("'", "&apos;").replaceAll("\"", "&quot;") ;*/
  }
//not use
  /*public static String filterString(String text, boolean isEmail) {
    if (text == null || text.trim().length() == 0) return "" ;
    for (String str : specialString) {
      text = text.replaceAll(str, "") ;
    }
    if (!isEmail) text = text.replaceAll(AKONG, "") ;
    int i = 0 ;
    while (i < text.length()) {
      if (Arrays.asList(specialString2).contains(text.charAt(i) + "")) {
        text = text.replace((text.charAt(i)) + "", "") ;
      } else {
        i ++ ;
      }
    }
    return text ;
  }*/
  static public String getCurrentUser() throws Exception {
    return Util.getPortalRequestContext().getRemoteUser() ; 
  }
  
  static public ContactService getContactService() throws Exception {
    return (ContactService)PortalContainer.getComponent(ContactService.class) ;
  }
  
  public static boolean isEmpty(String s) {
    if (s == null || s.trim().length() == 0) return true ;
    return false ;    
  }
  
  public static List<String> getUserGroups() throws Exception {
    OrganizationService organizationService = 
      (OrganizationService)PortalContainer.getComponent(OrganizationService.class) ;
    Object[] objGroupIds = organizationService.getGroupHandler().findGroupsOfUser(getCurrentUser()).toArray() ;
    List<String> groupIds = new ArrayList<String>() ;
    for (Object object : objGroupIds) {
      groupIds.add(((Group)object).getId()) ;
    }
    return groupIds ;
  }
  
  public static String getPublicGroupName(String groupId) throws Exception {
    OrganizationService organizationService = 
      (OrganizationService)PortalContainer.getComponent(OrganizationService.class) ;
    return organizationService.getGroupHandler().findGroupById(groupId).getGroupName() ;
  }
  
  public static boolean isPublicGroup(String groupId) throws Exception {
    if (getUserGroups().contains(groupId)) return true ;
    return false ;
  }
  
  public static String formatDate(String format, Date date) {
    Format formatter = new SimpleDateFormat(format);
    return formatter.format(date);
  }
  
  static public class SelectComparator implements Comparator{
    public int compare(Object o1, Object o2) throws ClassCastException {
      String name1 = ((SelectItemOption) o1).getLabel() ;
      String name2 = ((SelectItemOption) o2).getLabel() ;
      return name1.compareToIgnoreCase(name2) ;
    }
  }
  
  public static List<Account> getAccounts() throws Exception {
    MailService mailSvr = (MailService)PortalContainer.getComponent(MailService.class) ;
    try {
      return mailSvr.getAccounts(getCurrentUser()) ;
    } catch (RepositoryException e) {
      return null ;
    } catch (IndexOutOfBoundsException ex) {
      return null ;
    }
  }
  
  public static String emptyName() {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    ResourceBundle res = context.getApplicationResourceBundle() ;
    try {
        return  res.getString("ContactUtils.label.emptyName");
    } catch (MissingResourceException e) {      
      e.printStackTrace() ;
      return "(empty name)" ;
    }
  }  
 
  static public String getEmailUser(String userName) throws Exception {
    OrganizationService organizationService = (OrganizationService) PortalContainer.getComponent(OrganizationService.class);
    User user = organizationService.getUserHandler().findUserByName(userName) ;
    String email = user.getEmail() ;
    return email;
  }

  static public String getFullName(String userName) throws Exception {
    OrganizationService organizationService = (OrganizationService) PortalContainer.getComponent(OrganizationService.class);
    User user = organizationService.getUserHandler().findUserByName(userName) ;
    String fullName = user.getFullName() ;
    return fullName ;
  }
  
  public static void sendMessage(Message message) throws Exception {
    org.exoplatform.services.mail.MailService mService = (org.exoplatform.services.mail.MailService)PortalContainer.getComponent(org.exoplatform.services.mail.MailService.class) ;
    mService.sendMessage(message) ;
  }
  
  public static boolean isCollectedAddressBook(String addressId) {
    return addressId.contains(NewUserListener.ADDRESSESGROUP) ;
  }
  
  public static String reduceSpace(String s) {
    if (isEmpty(s)) return "" ;
    String[] words = s.split(" ") ;
    StringBuilder builder = new StringBuilder() ;
    for (String word : words) {
      if (builder.length() > 0 && word.trim().length() > 0) builder.append(" ") ;
      builder.append(word.trim()) ;
    }
    return builder.toString() ;
  }
  
  public static String listToString(List<String> list) {
    if (list == null || list.size() == 0) return ""; 
    StringBuilder builder = new StringBuilder();
    for (String str : list) {
      if (builder.length() > 0) builder.append("; " + str);
      else builder.append(str);
    }
    return builder.toString();
  }
  
  public static int getAge(Contact contact) {
    if (contact == null) return 0;
    Calendar birthday = new GregorianCalendar() ;
    birthday.setTime(contact.getBirthday());
    Calendar now = new GregorianCalendar() ;
    return now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
  }
  
  public static Map<String, String> getIMs(Contact contact) {
    Map<String, String> ims = new LinkedHashMap<String, String>();
    if (!isEmpty(contact.getExoId())) ims.put("exoChat", contact.getExoId());
    if (!isEmpty(contact.getGoogleId())) ims.put("google", contact.getGoogleId());
    if (!isEmpty(contact.getMsnId())) ims.put("msn", contact.getMsnId());
    if (!isEmpty(contact.getAolId())) ims.put("aol-aim", contact.getAolId());
    if (!isEmpty(contact.getYahooId())) ims.put("yahoo", contact.getYahooId());
    if (!isEmpty(contact.getIcrId())) ims.put("icr", contact.getIcrId());
    if (!isEmpty(contact.getSkypeId())) ims.put("skype", contact.getSkypeId());
    if (!isEmpty(contact.getIcqId())) ims.put("icq", contact.getIcqId());
    return ims;
  }
  
  public static Map<String, String> getPhones(Contact contact) {
    Map<String, String> phones = new LinkedHashMap<String, String>();
    if (!isEmpty(contact.getWorkPhone1())) phones.put(contact.getWorkPhone1(), "work");
    if (!isEmpty(contact.getWorkPhone2())) phones.put(contact.getWorkPhone2(), "work");
    if (!isEmpty(contact.getHomePhone1())) phones.put(contact.getHomePhone1(), "home");
    if (!isEmpty(contact.getHomePhone2())) phones.put(contact.getHomePhone2(), "home");
    if (!isEmpty(contact.getMobilePhone())) phones.put(contact.getMobilePhone(),"home");
    return phones;
  }
  
  public static boolean isNullArray(String[] array){
    if(array.length < 1) return true;
    return false;
  }
  
  //message warning within null parameter
  public static UIApplication initWarnPopup(UIComponent uicomponent,String messagekey){
    UIApplication windowWarn = uicomponent.getAncestorOfType(UIApplication.class) ;
    windowWarn.addMessage(new ApplicationMessage(messagekey, null, ApplicationMessage.WARNING)) ;
    return windowWarn;
  }
  
  //message warning within parameter
  public static UIApplication initPopup(UIComponent uicomponent,String messagekey,Object[] para,int type){
    UIApplication windowPopup = uicomponent.getAncestorOfType(UIApplication.class) ;
    windowPopup.addMessage(new ApplicationMessage(messagekey, para, type)) ;
    return windowPopup;
  }
  
  public static UIFormInputWithActions initSelectPermissions(UIFormInputWithActions inputset) throws Exception {
    inputset.addUIFormInput(new UIFormStringInput(FIELD_USER, FIELD_USER, null)) ;
    List<ActionData> actionUser = new ArrayList<ActionData>() ;
    actionUser = new ArrayList<ActionData>() ;
    ActionData selectUserAction = new ActionData() ;
    selectUserAction.setActionListener("SelectPermission") ;
    selectUserAction.setActionName("SelectUser") ;    
    selectUserAction.setActionType(ActionData.TYPE_ICON) ;
    selectUserAction.setCssIconClass("SelectUserIcon") ;
    selectUserAction.setActionParameter(UISelectComponent.TYPE_USER) ;
    actionUser.add(selectUserAction) ;
    inputset.setActionField(FIELD_USER, actionUser) ;
    
    UIFormStringInput groupField = new UIFormStringInput(FIELD_GROUP, FIELD_GROUP, null) ;
    inputset.addUIFormInput(groupField) ;
    List<ActionData> actionGroup = new ArrayList<ActionData>() ;
    ActionData selectGroupAction = new ActionData() ;
    selectGroupAction.setActionListener("SelectPermission") ;
    selectGroupAction.setActionName("SelectGroup") ;    
    selectGroupAction.setActionType(ActionData.TYPE_ICON) ;  
    selectGroupAction.setCssIconClass("SelectGroupIcon") ;
    selectGroupAction.setActionParameter(UISelectComponent.TYPE_GROUP) ;
    actionGroup.add(selectGroupAction) ;
    inputset.setActionField(FIELD_GROUP, actionGroup) ;    
    inputset.addChild(new UIFormCheckBoxInput<Boolean>(FIELD_EDIT_PERMISSION, FIELD_EDIT_PERMISSION, null)) ;
    return inputset;
  }
  
  public static void updateSelect(UIFormStringInput fieldInput, String selectField, String value) throws Exception {
    
    
    System.out.println("\n\n upppppppppp \n\n\n");
    
    StringBuilder sb = new StringBuilder("") ;
    if (!ContactUtils.isEmpty(fieldInput.getValue())) sb.append(fieldInput.getValue()) ;
    if (sb.indexOf(value) == -1) {
      if (sb.length() == 0) sb.append(value) ;
      else sb.append("," + value) ;
      fieldInput.setValue(sb.toString()) ;
    }
  }
  
  public static AddressBook setViewPermissionAddress(AddressBook contactGroup, Map<String, String> receiveUsers, Map<String, String> receiveGroups) {
    String[] viewPerUsers = contactGroup.getViewPermissionUsers() ;
    Map<String, String> viewMapUsers = new LinkedHashMap<String, String>() ; 
    if (viewPerUsers != null)
      for (String view : viewPerUsers) viewMapUsers.put(view, view) ; 
    for (String user : receiveUsers.keySet()) viewMapUsers.put(user, user) ;
    contactGroup.setViewPermissionUsers(viewMapUsers.keySet().toArray(new String[] {})) ;
    
    String[] viewPerGroups = contactGroup.getViewPermissionGroups() ;
    Map<String, String> viewMapGroups = new LinkedHashMap<String, String>() ; 
    if (viewPerGroups != null)
      for (String view : viewPerGroups) viewMapGroups.put(view, view) ; 
    for (String user : receiveGroups.keySet()) viewMapGroups.put(user, user) ;
    contactGroup.setViewPermissionGroups(viewMapGroups.keySet().toArray(new String[] {})) ;
    return contactGroup;
  }
  
  public static AddressBook removeEditPermissionAddress(AddressBook contactGroup, Map<String, String> receiveUsers, Map<String, String> receiveGroups) {
    List<String> newPerUsers = new ArrayList<String>() ; 
    if (contactGroup.getEditPermissionUsers() != null)
      for (String edit : contactGroup.getEditPermissionUsers())
        if(!receiveUsers.keySet().contains(edit)) {
          newPerUsers.add(edit) ;
      }
    contactGroup.setEditPermissionUsers(newPerUsers.toArray(new String[newPerUsers.size()])) ;
    
    List<String> newPerGroups = new ArrayList<String>() ; 
    if (contactGroup.getEditPermissionGroups() != null)
      for (String edit : contactGroup.getEditPermissionGroups())
        if(!receiveGroups.keySet().contains(edit)) {
          newPerGroups.add(edit) ;
      }
    contactGroup.setEditPermissionGroups(newPerGroups.toArray(new String[newPerGroups.size()])) ; 
    return contactGroup;
  }
  
  public static AddressBook setEditPermissionAddress(AddressBook contactGroup, Map<String, String> receiveUsers, Map<String, String> receiveGroups) {
    String[] editPerUsers = contactGroup.getEditPermissionUsers() ;
    Map<String, String> editMapUsers = new LinkedHashMap<String, String>() ; 
    if (editPerUsers != null)
      for (String edit : editPerUsers) editMapUsers.put(edit, edit) ;
    for (String user : receiveUsers.keySet()) editMapUsers.put(user, user) ;
    contactGroup.setEditPermissionUsers(editMapUsers.keySet().toArray(new String[] {})) ;
    
    String[] editPerGroups = contactGroup.getEditPermissionGroups() ;
    Map<String, String> editMapGroups = new LinkedHashMap<String, String>() ; 
    if (editPerGroups != null)
      for (String edit : editPerGroups) editMapGroups.put(edit, edit) ;
    for (String group : receiveGroups.keySet()) editMapGroups.put(group, group) ;
    contactGroup.setEditPermissionGroups(editMapGroups.keySet().toArray(new String[] {})) ;
    return contactGroup;
  }
  
}

