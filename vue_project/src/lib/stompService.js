// src/lib/stompService.js
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

let client = null

/**
 * 单例订阅 STOMP 服务
 * @param {string} endpoint  SockJS 端点，如 '/ws/orders'
 * @param {string} topic     Broker 目标，如 '/topic/orders'
 * @param {Function} onMessage 回调(msgObj)
 */
export function subscribe(endpoint, topic, onMessage) {
    if (!client) {
        client = new Client({
            webSocketFactory: () => new SockJS(`http://localhost:8080${endpoint}`),
            reconnectDelay: 5000,
            debug: () => {}
        })
        client.onStompError = frame => console.error('STOMP 错误：', frame)
        client.activate()
    }
    client.onConnect = () => {
        client.subscribe(topic, msg => onMessage(JSON.parse(msg.body)))
    }
    return client
}
