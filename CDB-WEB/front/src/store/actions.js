/**
 * Created by lihaoda.
 */

import Vue from 'vue'
import * as types from './mutation_types'

export const testToken = (store, succall, errcall) => {
  Vue.http.post(store.state.apiUrl + '/api/test_auth', {}).then(function(res)  {
    if(res.data.success) {
      succall(res.data);
    } else {
      errcall(res);
    }
  }, errcall)
};

export const register = (store, obj, succall, errcall) => {
  Vue.http.post(store.state.apiUrl + '/api/register', obj).then(function(res)  {
    if(res.data.success) {
      succall(res.data);
    } else {
      errcall(res);
    }
  }, errcall)
};

export const login = (store, obj, succall, errcall) => {
  Vue.http.post(store.state.apiUrl + '/api/auth', obj).then(function(res)  {
    if(res.data.access_token) {
      store.commit(types.SET_TOKEN, res.data.access_token);
      succall(res.data);
    } else {
      errcall(res);
    }
  }, errcall)
};

export const runNormalSql = (store, obj, succall, errcall) => {
  Vue.http.post(store.state.apiUrl + '/api/run_simple_sql', obj).then(function(res)  {
    if(res.data.success) {
      succall(res.data);
    } else {
      errcall(res);
    }
  }, errcall)
};

export const runCrowdSql = (store, obj, succall, errcall) => {
  Vue.http.post(store.state.apiUrl + '/api/run_crowd_sql', obj).then(function(res)  {
    if(res.data.success) {
      succall(res.data);
    } else {
      errcall(res);
    }
  }, errcall)
};

export const removeCrowdSql = (store, obj, succall, errcall) => {
  Vue.http.post(store.state.apiUrl + '/api/delete_crowd_sql', obj).then(function(res)  {
    if(res.data.success) {
      succall(res.data);
    } else {
      errcall(res);
    }
  }, errcall)
};

export const get_crowd_sqls = (store, obj, succall, errcall) => {
  Vue.http.post(store.state.apiUrl + '/api/get_all_crowd_sql', obj).then(function(res)  {
    if(res.data.success) {
      succall(res.data);
    } else {
      errcall(res);
    }
  }, errcall)
};







