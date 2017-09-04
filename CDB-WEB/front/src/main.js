// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import VueResource from 'vue-resource'

import store from './store/index'
import * as types from './store/mutation_types'
import * as actions from './store/actions'
import Cookies from 'js-cookie'
import App from './App'

Vue.use(VueResource);

Vue.http.options.xhr = {withCredentials: true};

Vue.http.interceptors.push(
  (req, next) => {
    req.headers.map['Authorization'] = ['JWT ' + store.state.token];
    req.headers.map['Accept'] = ['application/json'];
    console.log(req);
    next((res) => {
      console.log(res);
      if("resetAuthToken" in res.data && res.data.resetAuthToken) {
        store.commit(types.SET_TOKEN, "");
      }
      return res;
    })
  }
);

Vue.filter('len_limit20', function (value) {
  if(value.substring) {
    return value.substring(0, 20);
  } else {
    return value;
  }
});

var crowddb_token = Cookies.get("crowddb_token");
if(crowddb_token) {
  store.commit(types.SET_TOKEN, crowddb_token);
  actions.testToken(store, function(data) {
    new Vue({
      el: '#app',
      template: '<App/>',
      components: { App }
    });
  }, function(data) {
    new Vue({
      el: '#app',
      template: '<App/>',
      components: { App }
    });
  });
} else {
  new Vue({
    el: '#app',
    template: '<App/>',
    components: { App }
  });
}
