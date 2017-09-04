/**
 * Created by lihaoda.
 */

import Vue from 'vue'
import Vuex from 'vuex'
import * as types from './mutation_types'
import Cookies from 'js-cookie'

Vue.use(Vuex);
const debug = process.env.NODE_ENV !== 'production';

const state = {
  token: "",
  apiUrl: ""
};

const mutations = {
  [types.SET_TOKEN] (state, t) {
    Cookies.set('crowddb_token', t);
    state.token = t;
  },
};

export default new Vuex.Store({
  state,
  mutations,
  strict: debug
    ? [/*createLogger()*/]
    : []
});
