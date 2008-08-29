/*
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
 */
package org.exoplatform.mail.webui.popup;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.mail.MailUtils;
import org.exoplatform.mail.service.Account;
import org.exoplatform.mail.service.MailService;
import org.exoplatform.mail.service.MailSetting;
import org.exoplatform.mail.service.Utils;
import org.exoplatform.mail.webui.UIMailPortlet;
import org.exoplatform.mail.webui.UIMessageArea;
import org.exoplatform.mail.webui.UIMessageList;
import org.exoplatform.mail.webui.UIMessagePreview;
import org.exoplatform.mail.webui.UINavigationContainer;
import org.exoplatform.mail.webui.UISelectAccount;
import org.exoplatform.portal.webui.util.SessionProviderFactory;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputWithActions;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Nam Phung
 *          phunghainam@gmail.com
 * Sep 18, 2007  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "app:/templates/mail/webui/popup/UIAccountSetting.gtmpl",
    events = {
        @EventConfig(listeners = UIAccountSetting.SelectAccountActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIAccountSetting.AddNewAccountActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIAccountSetting.DeleteAccountActionListener.class, phase = Phase.DECODE, confirm="UIAccountSetting.msg.confirm-remove-account"),
        @EventConfig(listeners = UIAccountSetting.SaveActionListener.class),
        @EventConfig(listeners = UIAccountSetting.CancelActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIAccountSetting.ChangeServerTypeActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIAccountSetting.ChangeSSLActionListener.class, phase = Phase.DECODE)
    }
)

public class UIAccountSetting extends UIFormTabPane {  
  public static final String TAB_ACCOUNT = "account";
  public static final String TAB_IDENTITY_SETTINGS = "identitySettings";
  public static final String TAB_SERVER_SETTINGS = "serverSettings";
  public static final String FIELD_ACCOUNT_NAME = "accountName";
  public static final String FIELD_DISPLAY_NAME = "display-name".intern();
  public static final String FIELD_INCOMING_USERNAME = "incomingUsername";
  public static final String FIELD_ACCOUNT_DESCRIPTION = "description";
  public static final String FIELD_OUTGOING_NAME = "yourOutgoingName";
  public static final String FIELD_EMAIL_ADDRESS = "yourEmailAddress";
  public static final String FIELD_INCOMING_ACCOUNT = "incomingAccount";
  public static final String FIELD_INCOMING_PASSWORD = "incomingPassword";
  public static final String FIELD_REPLYTO_ADDRESS = "replyToAddress";
  public static final String FIELD_MAIL_SIGNATURE = "mailSignature" ;
  public static final String FIELD_SERVER_TYPE = "serverType";
  public static final String FIELD_INCOMING_SERVER = "incomingServer";
  public static final String FIELD_INCOMING_PORT = "incomingPort";
  public static final String FIELD_OUTGOING_SERVER = "outgoingServer";
  public static final String FIELD_OUTGOING_PORT = "outgoingPort";
  public static final String FIELD_INCOMING_FOLDER = "messageComeInFolder";
  public static final String FIELD_IS_INCOMING_SSL = "isSSL";
  public static final String FIELD_CHECKMAIL_AUTO = "checkMailAutomatically";
  public static final String FIELD_LEAVE_ON_SERVER = "leaveMailOnServer";
//  public static final String FIELD_SKIP_OVER_SIZE = "skipMessageOverMaxSize";
  public static final String FIELD_MARK_AS_DELETED = "markItAsDeleted";
  public static final String FIELD_IS_SAVE_PASSWORD = "isSavePassword" ;
  private String accountId_ = null;
  //TODO don't keep these components
  UIFormCheckBoxInput<Boolean> leaveOnServer_ ;
//  UIFormStringInput skipOverSize_;
  UIFormCheckBoxInput<Boolean> markAsDelete_;
  
  
  public UIAccountSetting() throws Exception {
    super("UIAccountSetting");
    UIFormInputWithActions accountInputSet = new UIFormInputWithActions(TAB_ACCOUNT);
    accountInputSet.addUIFormInput(new UIFormStringInput(FIELD_ACCOUNT_NAME, null, null).addValidator(MandatoryValidator.class)) ;
    accountInputSet.addUIFormInput(new UIFormTextAreaInput(FIELD_ACCOUNT_DESCRIPTION, null, null)) ;
    addUIFormInput(accountInputSet); 
    setSelectedTab(accountInputSet.getId()) ;
    UIFormInputWithActions  identityInputSet = new UIFormInputWithActions(TAB_IDENTITY_SETTINGS);
    
    identityInputSet.addUIFormInput(new UIFormStringInput(FIELD_DISPLAY_NAME, null, null).addValidator(MandatoryValidator.class)) ;
    identityInputSet.addUIFormInput(new UIFormStringInput(FIELD_EMAIL_ADDRESS, null, null).addValidator(MandatoryValidator.class));
    identityInputSet.addUIFormInput(new UIFormStringInput(FIELD_REPLYTO_ADDRESS, null, null));
    identityInputSet.addUIFormInput(new UIFormTextAreaInput(FIELD_MAIL_SIGNATURE, null, null));
    addUIFormInput(identityInputSet); 
    
    UIFormInputWithActions serverInputSet = new UIFormInputWithActions(TAB_SERVER_SETTINGS);
    UIFormSelectBox serverType = new UIFormSelectBox(FIELD_SERVER_TYPE, null, getServerTypeValues()) ;
    serverType.setEditable(false);
    serverType.setEnable(false);
    //serverType.setOnChange("ChangeServerType");
    serverInputSet.addUIFormInput(serverType) ;
    
    serverInputSet.addUIFormInput(new UIFormStringInput(FIELD_INCOMING_SERVER, null, null).addValidator(MandatoryValidator.class));
    serverInputSet.addUIFormInput(new UIFormStringInput(FIELD_INCOMING_PORT, null, null).addValidator(MandatoryValidator.class));
    serverInputSet.addUIFormInput(new UIFormStringInput(FIELD_INCOMING_ACCOUNT, null, null).addValidator(MandatoryValidator.class));
    UIFormStringInput passwordField = new UIFormStringInput(FIELD_INCOMING_PASSWORD, null, null) ;
    passwordField.setType(UIFormStringInput.PASSWORD_TYPE) ;
    passwordField.addValidator(MandatoryValidator.class) ;
    serverInputSet.addUIFormInput(passwordField);
    serverInputSet.addUIFormInput(new UIFormCheckBoxInput<Boolean>(FIELD_IS_SAVE_PASSWORD, null, null));
    UIFormCheckBoxInput<Boolean> ssl = new UIFormCheckBoxInput<Boolean>(FIELD_IS_INCOMING_SSL, null, null);//getFieldIsSSL()
    ssl.setOnChange("ChangeSSL"); 
    serverInputSet.addUIFormInput(ssl);
    
    serverInputSet.addUIFormInput(new UIFormStringInput(FIELD_OUTGOING_SERVER, null, null).addValidator(MandatoryValidator.class));
    serverInputSet.addUIFormInput(new UIFormStringInput(FIELD_OUTGOING_PORT, null, null).addValidator(MandatoryValidator.class));
    
    serverInputSet.addUIFormInput(new UIFormStringInput(FIELD_INCOMING_FOLDER, null, null).addValidator(MandatoryValidator.class));
    serverInputSet.addUIFormInput(new UIFormCheckBoxInput<Boolean>(FIELD_CHECKMAIL_AUTO, null, null));
    
    leaveOnServer_ = new UIFormCheckBoxInput<Boolean>(FIELD_LEAVE_ON_SERVER, null, null) ;
//    skipOverSize_ = new UIFormStringInput(FIELD_SKIP_OVER_SIZE, null, null);
    markAsDelete_ = new UIFormCheckBoxInput<Boolean>(FIELD_MARK_AS_DELETED, null, null);
    
    addUIFormInput(serverInputSet);
  }
  
  private List<SelectItemOption<String>> getServerTypeValues(){
    List<SelectItemOption<String>> options = new ArrayList<SelectItemOption<String>>() ;
    options.add(new SelectItemOption<String>(Utils.POP3, Utils.POP3));
    options.add(new SelectItemOption<String>(Utils.IMAP, Utils.IMAP)) ;
    return options ;
  }
  
  public String getSelectedAccountId() throws Exception { return accountId_; }
  public void setSelectedAccountId(String accountId) throws Exception { this.accountId_ = accountId; }  

  public String getFieldAccountNameValue() { 
    UIFormInputWithActions uiInput = getChildById(TAB_ACCOUNT);
    return uiInput.getUIStringInput(FIELD_ACCOUNT_NAME).getValue();
  }
  
  public String getDisplayName() { 
    UIFormInputWithActions uiInput = getChildById(TAB_IDENTITY_SETTINGS);
    return uiInput.getUIStringInput(FIELD_DISPLAY_NAME).getValue();
  }
  
  public String getFieldAccountDescription() {
    UIFormInputWithActions uiInput = getChildById(TAB_ACCOUNT);
    return uiInput.getUIStringInput(FIELD_ACCOUNT_DESCRIPTION).getValue();
  }
  
  public String getFieldMailAddress() {
    UIFormInputWithActions uiInput = getChildById(TAB_IDENTITY_SETTINGS);
    return uiInput.getUIStringInput(FIELD_EMAIL_ADDRESS).getValue();
  }
  
  public String getFieldProtocol() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIFormSelectBox(FIELD_SERVER_TYPE).getValue();
  }
  
  public String getFieldIncomingAccount() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIStringInput(FIELD_INCOMING_ACCOUNT).getValue();
  }
  
  public String getFieldIncomingPassword() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIStringInput(FIELD_INCOMING_PASSWORD).getValue() ;
  }  
  
  public String getFieldIncomingServer() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIStringInput(FIELD_INCOMING_SERVER).getValue();
  }
  
  public String getFieldIncomingPort() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIStringInput(FIELD_INCOMING_PORT).getValue();
  }
  
  public String getFieldOutgoingServer() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIStringInput(FIELD_OUTGOING_SERVER).getValue();
  }
  
  public String getFieldOutgoingPort() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIStringInput(FIELD_OUTGOING_PORT).getValue();
  }
  
  public String getFieldMailSignature() {
    UIFormInputWithActions uiInput = getChildById(TAB_IDENTITY_SETTINGS);
    return uiInput.getUIStringInput(FIELD_MAIL_SIGNATURE).getValue();
  }
  
  public boolean isSavePassword() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIFormCheckBoxInput(FIELD_IS_SAVE_PASSWORD).isChecked();
  }
  
  public String getFieldReplyAddress() {
    UIFormInputWithActions uiInput = getChildById(TAB_IDENTITY_SETTINGS);
    return uiInput.getUIStringInput(FIELD_REPLYTO_ADDRESS).getValue();
  }
  
  public String getFieldIncomingFolder() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIStringInput(FIELD_INCOMING_FOLDER).getValue();
  }
  
  public boolean getFieldIsSSL() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIFormCheckBoxInput(FIELD_IS_INCOMING_SSL).isChecked();
  }
  
  public boolean getFieldCheckMailAuto() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIFormCheckBoxInput(FIELD_CHECKMAIL_AUTO).isChecked();
  }
  
  public boolean getFieldLeaveOnServer() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIFormCheckBoxInput(FIELD_LEAVE_ON_SERVER).isChecked();
  }
  
//  public String getFieldSkipOverSize() {
//    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
//    return uiInput.getUIStringInput(FIELD_SKIP_OVER_SIZE).getValue();
//  }
  
  public boolean getFieldMaxAsDeleted() {
    UIFormInputWithActions uiInput = getChildById(TAB_SERVER_SETTINGS);
    return uiInput.getUIFormCheckBoxInput(FIELD_MARK_AS_DELETED).isChecked();
  }
  
  public void fillField() throws Exception {
    MailService mailSrv = getApplicationComponent(MailService.class) ;
    String username = Util.getPortalRequestContext().getRemoteUser() ;
    Account account = mailSrv.getAccountById(SessionProviderFactory.createSystemProvider(), username, getSelectedAccountId());
    UIFormInputWithActions uiAccountInput = getChildById(TAB_ACCOUNT) ;
    uiAccountInput.getUIStringInput(FIELD_ACCOUNT_NAME).setValue(account.getLabel()) ;
    uiAccountInput.getUIStringInput(FIELD_ACCOUNT_DESCRIPTION).setValue(account.getDescription()) ;
    
    UIFormInputWithActions uiIdentityInput = getChildById(TAB_IDENTITY_SETTINGS) ;
    uiIdentityInput.getUIStringInput(FIELD_DISPLAY_NAME).setValue(account.getUserDisplayName()) ;
    uiIdentityInput.getUIStringInput(FIELD_EMAIL_ADDRESS).setValue(account.getEmailAddress()) ;
    uiIdentityInput.getUIStringInput(FIELD_REPLYTO_ADDRESS).setValue(account.getEmailReplyAddress()) ;
    uiIdentityInput.getUIStringInput(FIELD_MAIL_SIGNATURE).setValue(account.getSignature()) ;
    
    UIFormInputWithActions uiServerInput = getChildById(TAB_SERVER_SETTINGS) ;
    uiServerInput.getUIStringInput(FIELD_INCOMING_SERVER).setValue(account.getIncomingHost()) ;
    uiServerInput.getUIStringInput(FIELD_INCOMING_PORT).setValue(account.getIncomingPort()) ;
    uiServerInput.getUIStringInput(FIELD_INCOMING_ACCOUNT).setValue(account.getIncomingUser()) ;
    uiServerInput.getUIStringInput(FIELD_INCOMING_PASSWORD).setValue(account.getIncomingPassword()) ;
    uiServerInput.getUIFormCheckBoxInput(FIELD_IS_SAVE_PASSWORD).setChecked(account.isSavePassword()) ;
    
    uiServerInput.getUIStringInput(FIELD_OUTGOING_SERVER).setValue(account.getOutgoingHost()) ;
    uiServerInput.getUIStringInput(FIELD_OUTGOING_PORT).setValue(account.getOutgoingPort()) ;
    
    uiServerInput.getUIStringInput(FIELD_INCOMING_FOLDER).setValue(account.getIncomingFolder()) ;
    uiServerInput.getUIFormSelectBox(FIELD_SERVER_TYPE).setValue(account.getProtocol()) ;
    uiServerInput.getUIFormCheckBoxInput(FIELD_IS_INCOMING_SSL).setChecked(account.isIncomingSsl()) ;
    uiServerInput.getUIFormCheckBoxInput(FIELD_CHECKMAIL_AUTO).setChecked(account.checkedAuto()) ;
    if(getFieldProtocol().equals(Utils.POP3)) {
      if(uiServerInput.getChildById(FIELD_LEAVE_ON_SERVER) == null)
        uiServerInput.addUIFormInput(leaveOnServer_) ;
      if(uiServerInput.getChildById(FIELD_MARK_AS_DELETED) != null)
        uiServerInput.removeChildById(FIELD_MARK_AS_DELETED) ;
//      uiServerInput.addUIFormInput(skipOverSize_) ;
    } else {
      if(uiServerInput.getChildById(FIELD_MARK_AS_DELETED) == null)
        uiServerInput.addUIFormInput(markAsDelete_) ;
      if(uiServerInput.getChildById(FIELD_LEAVE_ON_SERVER) != null)
        uiServerInput.removeChildById(FIELD_LEAVE_ON_SERVER) ;
    }
    
    if(account.getPopServerProperties() != null) {
      if (uiServerInput.getChildById(FIELD_LEAVE_ON_SERVER) != null)
        uiServerInput.getUIFormCheckBoxInput(FIELD_LEAVE_ON_SERVER).setChecked(Boolean.valueOf(account.getPopServerProperties().get(Utils.SVR_POP_LEAVE_ON_SERVER))) ;
//      if (uiServerInput.getChildById(FIELD_SKIP_OVER_SIZE) != null)
//        uiServerInput.getUIStringInput(FIELD_SKIP_OVER_SIZE).setValue(account.getPopServerProperties().get(Utils.SVR_POP_SKIP_OVER_SIZE)) ;
    }
    if(account.getImapServerProperties() != null && uiServerInput.getChildById(FIELD_MARK_AS_DELETED) != null) {
      uiServerInput.getUIFormCheckBoxInput(FIELD_MARK_AS_DELETED).setChecked(Boolean.valueOf(account.getImapServerProperties().get(Utils.SVR_IMAP_MARK_AS_DELETE))) ;
    }
  } 
  
  public void setDefaultValue(String serverType, boolean isSSL) {
    if(serverType.equals(Utils.POP3)) {
      if(isSSL) {
        getUIStringInput(FIELD_INCOMING_PORT).setValue(UIAccountCreation.DEFAULT_POPSSL_PORT) ;
        getUIStringInput(FIELD_OUTGOING_PORT).setValue(UIAccountCreation.DEFAULT_SMTPSSL_PORT) ;
      } else {
        getUIStringInput(FIELD_INCOMING_PORT).setValue(UIAccountCreation.DEFAULT_POP_PORT) ;
        getUIStringInput(FIELD_OUTGOING_PORT).setValue(UIAccountCreation.DEFAULT_SMTP_PORT) ;
      }
    } else {
      if(isSSL) {
        getUIStringInput(FIELD_INCOMING_PORT).setValue(UIAccountCreation.DEFAULT_IMAPSSL_PORT) ;
        getUIStringInput(FIELD_OUTGOING_PORT).setValue(UIAccountCreation.DEFAULT_SMTP_PORT) ;
      } else {
        getUIStringInput(FIELD_INCOMING_PORT).setValue(UIAccountCreation.DEFAULT_IMAP_PORT) ;
        getUIStringInput(FIELD_OUTGOING_PORT).setValue(UIAccountCreation.DEFAULT_SMTP_PORT) ;
      }
    }
  }
  
  public String[] getActions() {return new String[]{"Save", "Cancel"};}
  
  public List<Account> getAccounts() throws Exception {
    MailService mailSrv = getApplicationComponent(MailService.class);
    String username = Util.getPortalRequestContext().getRemoteUser();
    return mailSrv.getAccounts(SessionProviderFactory.createSystemProvider(), username);
  }
  
  static  public class SelectAccountActionListener extends EventListener<UIAccountSetting> {
    public void execute(Event<UIAccountSetting> event) throws Exception {
      UIAccountSetting uiAccountSetting = event.getSource() ;
      String accountId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      uiAccountSetting.setSelectedAccountId(accountId) ;
      uiAccountSetting.fillField();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiAccountSetting.getParent()) ;
    }
  }
  
  static  public class AddNewAccountActionListener extends EventListener<UIAccountSetting> {
    public void execute(Event<UIAccountSetting> event) throws Exception {
      UIAccountSetting uiAccountSetting = event.getSource() ;
      UIPopupActionContainer uiActionContainer = uiAccountSetting.getAncestorOfType(UIPopupActionContainer.class) ;
      UIPopupAction uiChildPopup = uiActionContainer.getChild(UIPopupAction.class) ;
      UIAccountCreation uiAccountCreation = uiChildPopup.activate(UIAccountCreation.class, 700) ;
      uiAccountCreation.setChildPopup(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiActionContainer) ;
    }
  }
  
  static  public class DeleteAccountActionListener extends EventListener<UIAccountSetting> {
    public void execute(Event<UIAccountSetting> event) throws Exception {
      UIAccountSetting uiAccSetting = event.getSource() ;
      UIMailPortlet uiPortlet = uiAccSetting.getAncestorOfType(UIMailPortlet.class) ;
      UIMessageList uiMsgList = uiPortlet.findFirstComponentOfType(UIMessageList.class) ;
      UIMessagePreview uiMsgPreview = uiPortlet.findFirstComponentOfType(UIMessagePreview.class) ;
      UISelectAccount uiSelectAccount = uiPortlet.findFirstComponentOfType(UISelectAccount.class) ;
      String username = uiPortlet.getCurrentUser();
      MailService mailSvr = uiPortlet.getApplicationComponent(MailService.class) ;
      try {
        String removedAccId = uiAccSetting.getSelectedAccountId() ; 
        mailSvr.removeAccount(SessionProviderFactory.createSystemProvider(), username, removedAccId) ;
        MailSetting mailSetting = mailSvr.getMailSetting(SessionProviderFactory.createSystemProvider(), username) ;
        if (uiAccSetting.getAccounts().size() > 0) {
          String newSelectedAcc = uiAccSetting.getAccounts().get(0).getId() ;
          uiAccSetting.setSelectedAccountId(newSelectedAcc) ;
          uiSelectAccount.updateAccount() ;
          if (removedAccId.equals(uiSelectAccount.getSelectedValue()))
            uiSelectAccount.setSelectedValue(newSelectedAcc) ;
          String defaultAcc = mailSetting.getDefaultAccount();
          if (removedAccId.equals(defaultAcc)) {
            mailSetting.setDefaultAccount(newSelectedAcc) ;
            mailSvr.saveMailSetting(SessionProviderFactory.createSystemProvider(), username, mailSetting) ;
          }
          uiAccSetting.fillField() ;
          uiMsgList.setMessageFilter(null);
          uiMsgList.init(newSelectedAcc);
          uiMsgPreview.setMessage(null);
          event.getRequestContext().addUIComponentToUpdateByAjax(uiAccSetting.getAncestorOfType(UIPopupActionContainer.class)) ;
        } else {
          uiSelectAccount.updateAccount() ;
          uiSelectAccount.setSelectedValue(null) ;
          mailSetting.setDefaultAccount(null) ;
          mailSvr.saveMailSetting(SessionProviderFactory.createSystemProvider(), username, mailSetting) ;
          event.getSource().getAncestorOfType(UIMailPortlet.class).cancelAction() ;
          uiMsgList.init(null);
        }
        event.getRequestContext().addUIComponentToUpdateByAjax(uiSelectAccount) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiMsgList.getAncestorOfType(UIMessageArea.class)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiSelectAccount.getAncestorOfType(UINavigationContainer.class)) ;
      } catch(Exception e) {
        e.printStackTrace() ;
      } 
    }
  }
  
  static  public class SaveActionListener extends EventListener<UIAccountSetting> {
    public void execute(Event<UIAccountSetting> event) throws Exception {
      UIAccountSetting uiSetting = event.getSource() ;
      UIMailPortlet uiPortlet = uiSetting.getAncestorOfType(UIMailPortlet.class) ;
      UIApplication uiApp = uiSetting.getAncestorOfType(UIApplication.class) ;
      MailService mailSrv = uiSetting.getApplicationComponent(MailService.class) ;
      String username = Util.getPortalRequestContext().getRemoteUser() ;
      String editedAccountId = uiSetting.getSelectedAccountId() ;
      Account acc = mailSrv.getAccountById(SessionProviderFactory.createSystemProvider(), username, editedAccountId) ;
      String userName = uiSetting.getFieldIncomingAccount() ;
      String email = uiSetting.getFieldMailAddress() ;
      String reply = uiSetting.getFieldReplyAddress() ;
      String incomingPort = uiSetting.getFieldIncomingPort() ;
      String outgoingPort = uiSetting.getFieldOutgoingPort() ;
      String password = uiSetting.getFieldIncomingPassword() ;
      
      if (!MailUtils.isValidEmailAddresses(email)) {
        uiApp.addMessage(new ApplicationMessage("UIAccountSetting.msg.email-address-is-invalid", null, ApplicationMessage.WARNING)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      } 
      if (!MailUtils.isFieldEmpty(reply) && !MailUtils.isValidEmailAddresses(reply)) {
        uiApp.addMessage(new ApplicationMessage("UIAccountSetting.msg.reply-address-is-invalid", null, ApplicationMessage.WARNING)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      }
      if (!Utils.isNumber(incomingPort)) {
        uiApp.addMessage(new ApplicationMessage("UIAccountSetting.msg.incoming-port-is-not-number", null, ApplicationMessage.WARNING)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      }
      if (!Utils.isNumber(outgoingPort)) {
        uiApp.addMessage(new ApplicationMessage("UIAccountSetting.msg.outgoing-port-is-not-number", null, ApplicationMessage.WARNING)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      }
      
      if (MailUtils.isFieldEmpty(password)) {
        uiApp.addMessage(new ApplicationMessage("UIAccountSetting.msg.field-password-is-required", null, ApplicationMessage.WARNING)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        return ;
      }
      
      acc.setProtocol(uiSetting.getFieldProtocol()) ;
      acc.setLabel(uiSetting.getFieldAccountNameValue()) ;
      acc.setUserDisplayName(uiSetting.getDisplayName()) ;
      acc.setDescription(uiSetting.getFieldAccountDescription()) ;
      acc.setEmailAddress(email) ;
      acc.setEmailReplyAddress(reply) ;
      acc.setSignature(uiSetting.getFieldMailSignature()) ;
      acc.setCheckedAuto(uiSetting.getFieldCheckMailAuto()) ;
      acc.setIncomingUser(userName) ; 
      if (uiSetting.isSavePassword()) acc.setIncomingPassword(password) ;
      else acc.setIncomingPassword("") ;
      acc.setIncomingHost(uiSetting.getFieldIncomingServer()) ;
      acc.setIncomingPort(incomingPort) ;  
      acc.setIncomingSsl(uiSetting.getFieldIsSSL()) ;
      acc.setIncomingFolder(uiSetting.getFieldIncomingFolder()) ;
      acc.setOutgoingHost(uiSetting.getFieldOutgoingServer()) ;
      acc.setOutgoingPort(outgoingPort) ;
      acc.setIsSavePassword(uiSetting.isSavePassword()) ;
      acc.setServerProperty(Utils.SVR_SMTP_USER, userName) ;
      
      if(uiSetting.getFieldProtocol().equals(Utils.POP3)){
        boolean leaveOnServer = uiSetting.getFieldLeaveOnServer() ;
        //String skipOverSize = uiSetting.getFieldSkipOverSize() ;
        acc.setPopServerProperty(Utils.SVR_POP_LEAVE_ON_SERVER, String.valueOf(leaveOnServer)) ;
        //acc.setPopServerProperty(Utils.SVR_POP_SKIP_OVER_SIZE, skipOverSize) ;
      } else {
        boolean markAsDelete = uiSetting.getFieldMaxAsDeleted() ;
        acc.setImapServerProperty(Utils.SVR_IMAP_MARK_AS_DELETE, String.valueOf(markAsDelete)) ;
      }
      
      try {
        mailSrv.updateAccount(SessionProviderFactory.createSystemProvider(), username, acc) ;
        UISelectAccount uiSelectAccount = uiPortlet.findFirstComponentOfType(UISelectAccount.class) ;
        String accountId = uiSelectAccount.getSelectedValue();
        uiSelectAccount.updateAccount() ;
        uiSelectAccount.setSelectedValue(accountId) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiSelectAccount) ;
        
        uiApp.addMessage(new ApplicationMessage("UIAccountSetting.msg.edit-acc-successfully", null)) ;
        event.getSource().getAncestorOfType(UIMailPortlet.class).cancelAction();
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
      } catch(Exception e) {
        uiApp.addMessage(new ApplicationMessage("UIAccountSetting.msg.edit-acc-unsuccessfully", null)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
        e.printStackTrace() ;
        return ;
      }
    }
  }
  //TODO this actionlistener not use anymore
  static	public class ChangeServerTypeActionListener extends EventListener<UIAccountSetting> {
  	public void execute(Event<UIAccountSetting> event) throws Exception {
  		UIAccountSetting uiSetting = event.getSource() ; 
  		uiSetting.setDefaultValue(uiSetting.getFieldProtocol(),uiSetting.getFieldIsSSL());
  		UIFormInputWithActions uiInput = uiSetting.getChildById(TAB_SERVER_SETTINGS);
  		String serverTypeValue = uiSetting.getFieldProtocol() ;
  		if(serverTypeValue.equals(Utils.IMAP)) {
  		  if (uiInput.getChildById(FIELD_LEAVE_ON_SERVER) != null ) {
          uiSetting.leaveOnServer_ = uiInput.getChildById(FIELD_LEAVE_ON_SERVER) ; 
          uiInput.removeChildById(FIELD_LEAVE_ON_SERVER) ;
  		  }
  		  
//  		  if (uiInput.getChildById(FIELD_SKIP_OVER_SIZE) != null ) {
//          uiSetting.skipOverSize_ = uiInput.getChildById(FIELD_SKIP_OVER_SIZE) ; 
//          uiInput.removeChildById(FIELD_SKIP_OVER_SIZE) ;
//        }
  		  
  		  if (uiInput.getChildById(FIELD_MARK_AS_DELETED) == null )
  		    uiInput.addUIFormInput(uiSetting.markAsDelete_) ;
  		} else {
  		  if (uiInput.getChildById(FIELD_MARK_AS_DELETED) != null ) {
          uiSetting.markAsDelete_ = uiInput.getChildById(FIELD_MARK_AS_DELETED) ; 
          uiInput.removeChildById(FIELD_MARK_AS_DELETED) ;
        } 
  		  
  		  if (uiInput.getChildById(FIELD_LEAVE_ON_SERVER) == null ) 
          uiInput.addUIFormInput(uiSetting.leaveOnServer_) ;
        
//        if (uiInput.getChildById(FIELD_SKIP_OVER_SIZE) == null ) 
//          uiInput.addUIFormInput(uiSetting.skipOverSize_) ;
  		}
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSetting.getParent()) ;
  	}
  }
  
  static	public class ChangeSSLActionListener extends EventListener<UIAccountSetting> {
  	public void execute(Event<UIAccountSetting> event) throws Exception {
  		UIAccountSetting uiSetting = event.getSource() ; 
  		uiSetting.setDefaultValue(uiSetting.getFieldProtocol(),uiSetting.getFieldIsSSL());
      event.getRequestContext().addUIComponentToUpdateByAjax(uiSetting.getParent()) ;
  	}
  }
   
  static  public class CancelActionListener extends EventListener<UIAccountSetting> {
    public void execute(Event<UIAccountSetting> event) throws Exception {
      event.getSource().getAncestorOfType(UIMailPortlet.class).cancelAction();
    }
  }
}