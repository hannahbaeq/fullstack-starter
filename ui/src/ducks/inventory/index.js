import axios from 'axios'
import { createAction, handleActions } from 'redux-actions'

const actions = {
  INVENTORY_GET_ALL: 'inventory/get_all',
  INVENTORY_GET_ALL_PENDING: 'inventory/get_all_PENDING',
  INVENTORY_SAVE: 'inventory/save',
  INVENTORY_RETRIEVE: 'inventory/retrieve',
  INVENTORY_UPDATE: 'inventory/update',
  INVENTORY_DELETE: 'inventory/delete',
  INVENTORY_REFRESH: 'inventory/refresh'
}

export let defaultState = {
  all: [],
  fetched: false,
}

export const findInventory = createAction(actions.INVENTORY_GET_ALL, () =>
  (dispatch, getState, config) => axios
    .get(`${config.restAPIUrl}/inventory`)
    .then((suc) => dispatch(refreshInventory(suc.data)))
)

export const saveInventory = createAction(actions.INVENTORY_SAVE, (id, inventory) =>
  (dispatch, getState, config) => axios
    .post(`${config.restAPIUrl}/inventory`, id)
    .then((suc) => {
      const invs = []
      getState().inventory.all.forEach(inv => {
        if (inv.id !== suc.data.id) {
          invs.push(inv)
        }
      })
      invs.push(suc.data)
      dispatch(refreshInventory(invs))
    })
)

export const retrieveInventory = createAction(actions.INVENTORY_RETRIEVE, (id) =>
  (dispatch, getState, config) => axios
    .get(`${config.restAPIUrl}/inventory/retrieve`, id)
    .then((suc) => dispatch(refreshInventory(suc.data)))
)

export const updateInventory = createAction(actions.INVENTORY_UPDATE, (inventory) =>
  (dispatch, getState, config) => axios
    .post(`${config.restAPIUrl}/inventory/update`, inventory,
      { params: { id: inventory.id } })
    .then((suc) => {
      const invs = []
      getState().inventory.all.forEach(inv => {
        if (inv.id !== suc.data.id) {
          invs.push(inv)
        }
      })
      invs.push(suc.data)
      dispatch(refreshInventory(invs))
    })
)

export const removeInventory = createAction(actions.INVENTORY_DELETE, (ids) =>
  (dispatch, getState, config) => axios
    .delete(`${config.restAPIUrl}/inventory`, { data: ids }) // Put the id list into the InventoryController
    .then((suc) => { // Result List<String> of deleted IDs in suc
      const invs = [] // Create a list
      getState().inventory.all.forEach(inv => { // For every inventory
        if (!ids.includes(inv.id)) { // If the current ID is not in the deleted list,
          invs.push(inv) // Add the inventory item to the list
        }
      })
      dispatch(refreshInventory(invs)) // Refresh using the new list
    })
)

export const refreshInventory = createAction(actions.INVENTORY_REFRESH, (payload) =>
  (dispatcher, getState, config) =>
    payload.sort((invA, invB) => invA.name < invB.name ? -1 : invA.name > invB.name ? 1 : 0)
)

export default handleActions({
  [actions.INVENTORY_GET_ALL_PENDING]: (state) => ({
    ...state,
    fetched: false
  }),
  [actions.INVENTORY_REFRESH]: (state, action) => ({
    ...state,
    all: action.payload,
    fetched: true
  })
}, defaultState)
