// vue_project/src/stores/cart.js
import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
    state: () => ({
        items: []
    }),
    getters: {
        totalPrice: (state) =>
            state.items.reduce((sum, i) => sum + i.price * i.quantity, 0)
    },
    actions: {
        addItem(dish) {
            const exist = this.items.find(i => i.id === dish.id)
            if (exist) {
                exist.quantity++
            } else {
                this.items.push({ ...dish, quantity: 1 })
            }
        },
        removeItem(dishId) {
            this.items = this.items.filter(i => i.id !== dishId)
        },
        updateQuantity(dishId, qty) {
            const item = this.items.find(i => i.id === dishId)
            if (item) item.quantity = qty
        },
        clearCart() {
            this.items = []
        }
    }
})
