/**
 * @author Uoc Nguyen Ba
 */
function UIJoinRoomPopupWindow() {
  this.MAX_ROOM_NAME_DISPLAY = 12;
  this.MAX_ROOM_DESC_DISPLAY = 55;
}

UIJoinRoomPopupWindow.prototype.init = function(rootNode, UIMainChatWindow) {
  this.rootNode = rootNode;
  this.UIMainChatWindow = UIMainChatWindow;
  var DOMUtil = eXo.core.DOMUtil;
  var tmpNode = DOMUtil.findFirstDescendantByClass(this.rootNode, 'table', 'UIGrid');
  if (tmpNode.getElementsByTagName('tbody').length > 0) {
    this.roomListContainerNode = tmpNode.getElementsByTagName('tbody')[0];
  } else {
    this.roomListContainerNode = tmpNode;
  }
  this.joinRoomButtonNode = eXo.core.DOMUtil.findFirstDescendantByClass(this.rootNode, 'a', 'JoinRoomButton');
  this.joinRoomButtonNode.hrefBk = this.joinRoomButtonNode.href;
  this.LocalTemplateEngine = this.UIMainChatWindow.LocalTemplateEngine;
};

UIJoinRoomPopupWindow.prototype.setVisible = function(visible) {
  if (!this.UIMainChatWindow.userStatus ||
      this.UIMainChatWindow.userStatus == this.UIMainChatWindow.OFFLINE_STATUS) {
    return;
  }
  if (visible) {
    this.UIMainChatWindow.jabberGetRoomList();
    if (this.rootNode.style.display != 'block') {
      this.rootNode.style.display = 'block'; 
    }
    this.UIPopupManager.focusEventFire(this);
  } else {
    if (this.rootNode.style.display != 'none') {
      this.rootNode.style.display = 'none'; 
    }
  }
};

UIJoinRoomPopupWindow.prototype.updateRoomList = function(roomList) {
  // Fix bug table innerHTML for ie
  var tmpNode = this.roomListContainerNode.parentNode;
  tmpNode.removeChild(this.roomListContainerNode);
  this.roomListContainerNode = document.createElement('tbody');
  tmpNode.appendChild(this.roomListContainerNode);
  for (var i=0; i<roomList.length; i++) {
    var roomInfo = roomList[i];
    roomInfo.enabled4Add = true;
    this.roomListContainerNode.appendChild(this.createRoomNode(roomInfo, (i%2)));
  }
};

UIJoinRoomPopupWindow.prototype.createRoomNode = function(roomInfo, isAlternate) {
  var DOMUtil = eXo.core.DOMUtil;
  var uiRoomRowNode = document.createElement('tr');
  if (isAlternate) {
    uiRoomRowNode.className = 'UIRoomRowC';
  } else {
    uiRoomRowNode.className = 'UIRoomRow';
  }
  uiRoomRowNode.roomInfo = roomInfo;
  var tdTmpNode = document.createElement('td');
  
  //var selectBoxNode = document.createElement('input');
  
  //selectBoxNode.type = 'checkbox';
  //selectBoxNode.className = 'CheckBox';
  var checkBoxHTML = '<input type="radio" class="CheckBox"';
  if (!roomInfo.enabled4Add) {
    checkBoxHTML += ' checked="true" disabled="true"';
  }
  checkBoxHTML += 'name="roomName" value="' + roomInfo['name'] +
    '" onclick="eXo.communication.chat.webui.UIJoinRoomPopupWindow.selectRoom(event);" style="width: 10px;">';
  
  tdTmpNode.style.width = '10px';
  tdTmpNode.style.textAlign = 'center';
  tdTmpNode.innerHTML = checkBoxHTML;
  uiRoomRowNode.appendChild(tdTmpNode.cloneNode(true));
  
  tdTmpNode.innerHTML = '<span></span>';
  tdTmpNode.style.textAlign = '';
  tdTmpNode.style.width = '';
  
  var tmpRoomName = roomInfo['name'];
  tdTmpNode.setAttribute('title', tmpRoomName);
  if (tmpRoomName.length > this.MAX_ROOM_NAME_DISPLAY) {
    tmpRoomName = tmpRoomName.substr(0, this.MAX_ROOM_NAME_DISPLAY) + '...';
  }
  tdTmpNode.innerHTML = tmpRoomName;//roomInfo['name'];
  uiRoomRowNode.appendChild(tdTmpNode.cloneNode(true));
  var tmpRoomDesc = roomInfo['description'];
  tdTmpNode.setAttribute('title', tmpRoomDesc);
  if (tmpRoomDesc.length > this.MAX_ROOM_DESC_DISPLAY) {
    tmpRoomDesc = tmpRoomDesc.substr(0, this.MAX_ROOM_DESC_DISPLAY) + '...';
  }
  tdTmpNode.innerHTML = tmpRoomDesc;
  uiRoomRowNode.appendChild(tdTmpNode.cloneNode(true));
  
  return uiRoomRowNode;
};

UIJoinRoomPopupWindow.prototype.selectRoom = function(event) {
  var DOMUtil = eXo.core.DOMUtil;
  event = event || window.event;
  var srcElement = event.srcElement || event.target;
  srcElement.checked = true;
};

UIJoinRoomPopupWindow.prototype.joinRoomAction = function() {
  var DOMUtil = eXo.core.DOMUtil;
  var checkBoxList = DOMUtil.findDescendantsByClass(this.roomListContainerNode, 'input', 'CheckBox');
  for (var i=0; i<checkBoxList.length; i++) {
    var currentNode = checkBoxList[i];
    if (currentNode.checked) {
      // Check if target room is protected
      var roomInfoNode = DOMUtil.findAncestorByTagName(currentNode, 'tr');
      if (!roomInfoNode) {
        // window.jsconsole.error('Can not detect room information');
        return;
      }
      var roomInfo = roomInfoNode.roomInfo;
      var joinedRooms = this.UIMainChatWindow.joinedRooms;
      for (var i=0; i<joinedRooms.length; i++) {
        var joinedRoomInfo = joinedRooms[i];
        if (joinedRoomInfo.roomInfo.room == roomInfo.room) {
          this.UIMainChatWindow.UIChatWindow.createNewTab(roomInfo.room, true);
          this.setVisible(false);
          return;
        }
      }
      this.UIMainChatWindow.jabberJoinToRoom(currentNode.value, roomInfo.isPasswordProtected);
      this.setVisible(false);
      return;
    }
  }
};

eXo.communication.chat.webui.UIJoinRoomPopupWindow = new UIJoinRoomPopupWindow();