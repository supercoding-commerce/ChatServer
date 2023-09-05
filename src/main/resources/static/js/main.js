
const seller = {sellerId: 1, shopName: "테스트판매자"}
const user = {userId: 2, userName: "테스트유저"}
const product = {productId: 1, productName: "테스트상품"}

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const startButton = document.querySelector('#open-chat');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const sellerRadio = document.querySelector('#seller-radio'); // 추가: 판매자 라디오 버튼 요소
const userRadio = document.querySelector('#user-radio');
let stompClient = null;
let chatName = null;

const colors = [
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

    /**
     * SockJS와 StompJS 연결부분
     * SockJS : 웹 소켓을 사용한 실시간 통신의 폴백(fallback)을 제공하는 JavaScript 라이브러리입니다. 주요 목적은 브라우저에서 웹 소켓을 지원하지 않는 경우에도 실시간 통신을 가능하게 하는 것입니다.
     * STOMP(Simple Text Oriented Messaging Protocol): 사실상 요놈이 본체 같습니다. 간단한 텍스트 기반의 메시징 프로토콜로, 메시지 브로커와 클라이언트 간의 통신을 단순화하는 데 사용됩니다. 주로 웹 소켓과 함께 사용되며, 메시지 큐, 채팅 애플리케이션, 실시간 업데이트 등의 시나리오에 적합합니다.
     */
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
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        const chatMessage = {
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
            const avatarElement = document.createElement('i');
            const avatarText = document.createTextNode(message.sender[0]);
            avatarElement.appendChild(avatarText);
            avatarElement.style['background-color'] = getAvatarColor(message.sender);

            messageElement.appendChild(avatarElement);

            const usernameElement = document.createElement('span');
            const usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
    }


    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
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
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    let index = Math.abs(hash % colors.length);
    return colors[index];
}

startButton.addEventListener('click', connect, true)
messageForm.addEventListener('submit', sendMessage, true)