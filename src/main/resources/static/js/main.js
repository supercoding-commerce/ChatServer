
const seller = {sellerId: 2, shopName: "테스트판매자2"}
const user = {userId: 3, userName: "테스트유저3"}
const product = {productId: 4, productName: "테스트상품4"}
const customRoomId = createCustomRoomId(seller.sellerId, product.productId, user.userId)

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
let role = null;
let eventSource = null;

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    if (sellerRadio.checked) { // 수정: 판매자 라디오 버튼이 선택된 경우
        role = 'seller';
    } else if (userRadio.checked) { // 수정: 사용자 라디오 버튼이 선택된 경우
        role = 'user';
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
    const socket = new SockJS('http://localhost:8080/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);

    role === "user" ? listenForUserEvents() : listenForSellerEvents()

    event.preventDefault();
}


function onConnected() {
    const message = {
        customRoomId : customRoomId,
        shopName: seller.shopName,
        userName: user.userName,
        role: role, //웹소켓 세션 연결이 브라우저마다 각자 독립적으로 메모리를 다루기 때문에 //role : seller or user
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
            customRoomId: customRoomId,
            sender: role === 'user' ? user.userName : seller.shopName,
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
            message.content = (message.role === 'user') ? (message.userName + ' joined!') : (message.shopName + ' joined!');
            break;

        case 'LEAVE':
            messageElement.classList.add('event-message');
            message.content = (message.role === 'user') ? (message.userName + ' left!') : (message.shopName + ' left!');
            break;

        case 'TERMINATE':
            //stompClient.send(`/app/chat.disconnect/${seller.sellerId}/${product.productId}/${user.userId}`);
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

function createCustomRoomId(sellerId, productId, userId) {
    // userId, sellerId, productId를 6자리 문자열로 변환
    const userIdStr = String(userId).padStart(6, '0');
    const sellerIdStr = String(sellerId).padStart(6, '0');
    const productIdStr = String(productId).padStart(6, '0');

    // customRoomId를 조합
    const customRoomId = sellerIdStr + productIdStr + userIdStr;

    return customRoomId;
}

startButton.addEventListener('click', connect, true)
messageForm.addEventListener('submit', sendMessage, true)


function listenForUserEvents() {

    const eventSource = new EventSource(
       // `http://43.201.22.31:8081/chat-alarm/user/${seller.sellerId}/${user.userId}`
        `http://localhost:8081/chat-alarm/user/${seller.sellerId}/${user.userId}`
    );
    console.log('EventSource opened');

    eventSource.addEventListener('sse', function(event) {
        const message = JSON.parse(event.data);
        console.log('새로운 채팅 알람: ', message);
    });

    eventSource.onerror = function(error) {
        console.error("EventSource failed:", error);
        eventSource.close();
    };

    window.addEventListener('unload', function() {
        if (eventSource) {
            eventSource.close();
            console.log('EventSource closed');
        }
    });
}

function listenForSellerEvents() {

    const eventSource = new EventSource(
        // `http://43.201.22.31:8081/chat-alarm/seller/${seller.sellerId}`
        `http://localhost:8081/chat-alarm/seller/${seller.sellerId}`
    );

    eventSource.addEventListener('sse', function(event) {
        console.log('New chat alarm:', event.data);
        const message = JSON.parse(event.data);
        console.log('새로운 채팅 알람: ', message);
    });

    eventSource.onerror = function(error) {
        console.error("EventSource failed:", error);
        eventSource.close();
    };

    window.addEventListener('unload', function() {
        if (eventSource) {
            eventSource.close();
            console.log('EventSource closed');
        }
    });
}


