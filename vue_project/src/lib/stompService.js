// src/lib/stompService.js
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

let client = null

/**
 * Singleton helper to subscribe to STOMP service
 * @param {string} endpoint  SockJS endpoint, e.g. '/ws/orders'
 * @param {string} topic     Broker destination, e.g. '/topic/orders'
 * @param {Function} onMessage callback(msgObj)
 */
export function subscribe(endpoint, topic, onMessage) {
    if (!client) {
        client = new Client({
            webSocketFactory: () => new SockJS(`http://localhost:8080${endpoint}`),
            reconnectDelay: 5000,
            debug: () => {}
        })
        client.onStompError = frame => console.error('STOMP error:', frame)
        client.activate()
    }
    client.onConnect = () => {
        client.subscribe(topic, msg => onMessage(JSON.parse(msg.body)))
    }
    return client
}
