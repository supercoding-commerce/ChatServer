
const seller = {sellerId: 1, shopName: "testShop"}
const user = {userId: 2, userName: "testUser"}
const product = {productId: 1, productName: "testProduct"}

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var startButton = document.querySelector('#open-chat');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var sellerRadio = document.querySelector('#seller-radio'); // 추가: 판매자 라디오 버튼 요소
var userRadio = document.querySelector('#user-radio');
var stompClient = null;
var chatName = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    if (sellerRadio.checked) { // 수정: 판매자 라디오 버튼이 선택된 경우
        chatName = seller.shopName;
    } else if (userRadio.checked) { // 수정: 사용자 라디오 버튼이 선택된 경우
        chatName = user.userName;
    } else {
        alert('Please select a user type.'); // 라디오 버튼이 선택되지 않은 경우 알림 표시
        return;
    }

    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');


    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);

    event.preventDefault();
}


function onConnected() {
    const message = {
        shopName: seller.shopName,
        userName: user.userName,
        chatName: chatName,
        type: "JOIN"
    }

    // Subscribe to the Public Topic
    stompClient.subscribe(`/topic/${seller.sellerId}/${product.productId}/${user.userId}`, onMessageReceived);

    // Tell your username to the server
    stompClient.send(`/app/chat.addUser/${seller.sellerId}/${product.productId}/${user.userId}`,
        {},
        JSON.stringify(message)
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: chatName,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send(`/app/chat.sendMessage/${seller.sellerId}/${product.productId}/${user.userId}`, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    const messageElement = document.createElement('li');

    switch (message.type){
        case 'JOIN':
            messageElement.classList.add('event-message');
            message.content = message.chatName + ' joined!';
            break;

        case 'LEAVE':
            messageElement.classList.add('event-message');
            message.content = message.chatName + ' left!';
            break;

        case 'TERMINATE':
            disconnectChatRoom();
            break;

        default :
            messageElement.classList.add('chat-message');
            var avatarElement = document.createElement('i');
            var avatarText = document.createTextNode(message.sender[0]);
            avatarElement.appendChild(avatarText);
            avatarElement.style['background-color'] = getAvatarColor(message.sender);

            messageElement.appendChild(avatarElement);

            var usernameElement = document.createElement('span');
            var usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
    }


    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function disconnectChatRoom() {
    // 웹소켓 연결을 종료하는 로직을 추가합니다.
    // 채팅방에 사용자가 모두 나갔을 때 호출됩니다.
    if (stompClient) {
        stompClient.disconnect();
        stompClient = null;
    }
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

startButton.addEventListener('click', connect, true)
messageForm.addEventListener('submit', sendMessage, true)